package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingMapper.bookToDto;
import static ru.practicum.shareit.booking.BookingMapper.bookToShortDto;
import static ru.practicum.shareit.booking.model.State.validateState;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Override
    public BookingDto addBooking(Long userId, BookingShortDto bookingShortDto) {
        Item item = itemRepository.findById(bookingShortDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Предмет с id= " + userId + " не найден"));
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Невозможно создать бронирование - " +
                        "Не найден пользователь с id " + userId));
        if (bookingShortDto.getStart().isEqual(bookingShortDto.getEnd()) ||
                bookingShortDto.getStart().isAfter(bookingShortDto.getEnd())) {
            throw new NotAvailableException("Дата окончания бронирования не может быть раньше даты начала");
        }
        if (userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("Невозможно забронировать собственный предмет");
        }
        if (!item.getAvailable()) {
            throw new NotAvailableException("Предмет с id= " + userId + " недоступен для бронирования");
        }

        LocalDateTime bookingStart = bookingShortDto.getStart();
        LocalDateTime bookingEnd = bookingShortDto.getEnd();

        List<Booking> conflictingBookings = bookingRepository.findAllByItemIdAndStartBeforeAndEndAfter(
                bookingShortDto.getItemId(), bookingEnd, bookingStart, Sort.by(Sort.Direction.ASC, "start"));

        if (!conflictingBookings.isEmpty()) {
            throw new NotAvailableException("Предмет с id= " + bookingShortDto.getItemId() +
                    " уже забронирован на указанные даты");
        }

        Booking booking = bookToShortDto(bookingShortDto);
        booking.setBooker(userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь с id= " + userId + " не найден")));
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        return bookToDto(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getBooking(Long id, Long bookingId) {
        Booking booking = validateBooking(bookingId);
        if (!booking.getBooker().getId().equals(id) && !booking.getItem().getOwner().getId().equals(id)) {
            throw new NotFoundException("Пользователь с id= " + id +
                    " не является собственником или арендатором предмета");
        }
        return bookToDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllBookingByState(Long id, String stateString, Pageable page) {
        validateUser(id);
        List<Booking> bookingList;
        LocalDateTime time = LocalDateTime.now();
        Pageable pageableWithSort = PageRequest.of(page.getPageNumber(), page.getPageSize(), sort);

        State state = validateState(stateString);
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByBookerId(id, pageableWithSort);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(id,
                        time, time, pageableWithSort);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByBookerIdAndEndBefore(id, time, pageableWithSort);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByBookerIdAndStartAfter(id, time, pageableWithSort);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(id, Status.WAITING, pageableWithSort);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(id, Status.REJECTED, pageableWithSort);
                break;
            default:
                bookingList = Collections.emptyList();
        }

        log.info("Получены следующие сведения о состоянии бронирования  = {}", state);
        return bookingList.stream()
                .map(BookingMapper::bookToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllOwnersBookingByState(Long id, String stateString, Pageable page) {
        validateUser(id);

        List<Booking> bookingList;
        LocalDateTime now = LocalDateTime.now();
        Pageable pageableWithSort = PageRequest.of(page.getPageNumber(), page.getPageSize(), sort);

        State state = validateState(stateString);
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByItemOwnerId(id, pageableWithSort);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByItemOwnerIdAndEndBefore(id, now, pageableWithSort);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(id,
                        now, now, pageableWithSort);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStartAfter(id, now, pageableWithSort);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(id, Status.WAITING, pageableWithSort);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(id, Status.REJECTED, pageableWithSort);
                break;
            default:
                bookingList = Collections.emptyList();
        }

        log.info("Получены сведения о владельце предмета и состоянии бронирования  = {}", state);
        return bookingList.stream()
                .map(BookingMapper::bookToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDto approveBooking(Long id, Long bookingId, Boolean approved) {
        Booking booking = validateBooking(bookingId);

        if (!booking.getItem().getOwner().getId().equals(id)) {
            throw new NotFoundException("Пользователь с id= " + id + " не является собственником предмета");
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new NotAvailableException("Бронирование предмета с id= " + bookingId + " уже подтверждено");
        }
        if (booking.getStatus().equals(Status.REJECTED)) {
            throw new NotAvailableException("Бронирование предмета с id= " + bookingId + " уже было отклонено вами");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        log.info("Получено подтверждение бронирования предмета с id  = {}", bookingId);
        return bookToDto(booking);
    }

    private Booking validateBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Номер бронирования с id= " + bookingId + " не найден"));
    }

    private void validateUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id= " + id + " не найден");
        }
    }
}
