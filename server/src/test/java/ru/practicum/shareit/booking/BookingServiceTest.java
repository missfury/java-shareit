package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.booking.model.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private User user;
    private User userWithBooking;
    private Item item;
    private Booking booking;
    private final LocalDateTime start = LocalDateTime.now();
    private final LocalDateTime end = LocalDateTime.now().plusDays(2);

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("John_White")
                .email("john@test.com")
                .build();

        userWithBooking = User.builder()
                .id(2L)
                .name("Mary_Sue")
                .email("mary@test.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .owner(user)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(userWithBooking)
                .status(Status.WAITING)
                .build();
    }

    @Test
    public void createBookingTest() {
        BookingShortDto shortDto = new BookingShortDto(userWithBooking.getId(), start, end, item.getId());
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(userWithBooking.getId())).thenReturn(Optional.of(userWithBooking));
        when(bookingRepository.save(any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        BookingDto bookingdto = bookingService.addBooking(userWithBooking.getId(), shortDto);

        assertThat(bookingdto).hasFieldOrProperty("id");
    }

    @Test
    void createBookingsWithFalseAvailableTest() {
        Long userId = userWithBooking.getId();
        Long itemId = item.getId();
        item.setAvailable(false);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(any())).thenReturn(Optional.of(userWithBooking));
        BookingShortDto shortDto = new BookingShortDto(userId, start, end, itemId);
        assertThrows(NotAvailableException.class, () -> bookingService.addBooking(userId, shortDto));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBookingReturnItemNotFoundExceptionTest() {
        Long userId = userWithBooking.getId();
        Long itemId = 9999L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        BookingShortDto shortDto = new BookingShortDto(userId, start, end, itemId);

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(userId, shortDto));
    }

    @Test
    void createBookingReturnItemNotAvailableExceptionDateTest() {
        Long userId = userWithBooking.getId();
        Long itemId = item.getId();
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().minusDays(2);

        when(itemRepository.findById(itemId)).thenThrow(NotAvailableException.class);
        BookingShortDto shortDto = new BookingShortDto(userId, start, end, itemId);

        assertThrows(NotAvailableException.class, () -> bookingService.addBooking(userId, shortDto));
    }

    @Test
    void createBookingReturnItemNotAvailableExceptionItemIdTest() {
        Long userId = userWithBooking.getId();
        Long itemId = 999L;

        when(itemRepository.findById(itemId)).thenThrow(NotAvailableException.class);
        BookingShortDto shortDto = new BookingShortDto(userId, start, end, itemId);

        assertThrows(NotAvailableException.class, () -> bookingService.addBooking(userId, shortDto));
    }

    @Test
    void getBookingTest() {
        Long userId = user.getId();
        Long bookingId = booking.getId();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        BookingDto result = bookingService.getBooking(userId, bookingId);

        assertEquals(1L, result.getId());
        assertEquals(Status.WAITING, result.getStatus());
        assertEquals(start, result.getStart());
        assertEquals(end, result.getEnd());
        assertEquals(1L, result.getItem().getId());
        assertEquals("Item name", result.getItem().getName());
        assertEquals(2L, result.getBooker().getId());
        assertEquals("Mary_Sue", result.getBooker().getName());
    }

    @Test
    void getBookingByIdWithWrongBookingIdTest() {
        Long bookingId = 9999L;
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(user.getId(), bookingId));
    }

    @Test
    void getBookingByIdWithWrongUserIdTest() {
        Long bookingId = 1L;
        Long userId = 9999L;
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(userId, bookingId));
    }

    @Test
    void getAllOwnersBookingByStateIfStateIsAllTest() {
        Long userId = userWithBooking.getId();
        String state = String.valueOf(State.ALL);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItemOwnerId(anyLong(), any())).thenReturn(List.of(booking));
        List<BookingDto> result = bookingService.getAllOwnersBookingByState(userId, state,
                PageRequest.of(0, 10));

        assertEquals(1, result.size());
    }

    @Test
    void getAllOwnersBookingByStateIfStateIsAllStateIsCurrentTest() {
        Long userId = user.getId();
        String state = String.valueOf(State.CURRENT);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));
        List<BookingDto> result = bookingService.getAllOwnersBookingByState(userId, state,
                PageRequest.of(0, 10));

        assertEquals(1, result.size());
    }

    @Test
    void getAllOwnersBookingByStateIfStateIsPastTest() {
        Long userId = user.getId();
        String state = String.valueOf(State.PAST);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdAndEndBefore(anyLong(), any(), any())).thenReturn(List.of(booking));
        List<BookingDto> result = bookingService.getAllOwnersBookingByState(userId, state,
                PageRequest.of(0, 10));

        assertEquals(1, result.size());
    }

    @Test
    void getAllOwnersBookingByStateIfStateIsFutureTest() {
        Long userId = user.getId();
        String state = String.valueOf(State.FUTURE);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdAndStartAfter(anyLong(), any(), any())).thenReturn(List.of(booking));
        List<BookingDto> result = bookingService.getAllOwnersBookingByState(userId, state,
                PageRequest.of(0, 10));

        assertEquals(1, result.size());
    }

    @Test
    void getAllOwnersBookingByStateIfStateIsWaitingTest() {
        Long userId = user.getId();
        String state = String.valueOf(State.WAITING);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        List<BookingDto> result = bookingService.getAllOwnersBookingByState(userId, state,
                PageRequest.of(0, 10));

        assertEquals(1, result.size());
    }


    @Test
    void getAllOwnersBookingByStateIfStateIsRejectedTest() {
        Long userId = user.getId();
        String state = String.valueOf(State.REJECTED);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));
        List<BookingDto> result = bookingService.getAllOwnersBookingByState(userId, state,
                PageRequest.of(0, 10));

        assertEquals(1, result.size());
    }

    @Test
    void getAllOwnersBookingByStateIfUserNotFoundExceptionTest() {
        Long userId = 999L;
        String state = String.valueOf(State.ALL);
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.getAllOwnersBookingByState(userId,
                state, PageRequest.of(0, 10)));
    }

    @Test
    void getAllOwnersBookingByStateWithWrongStatusExceptionTest() {
        Long userId = user.getId();
        String state = "Wrong State";
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(NotSupportedStateException.class, () -> bookingService.getAllOwnersBookingByState(userId,
                state, PageRequest.of(0, 10)));
    }

    @Test
    void getAllBookingByStateIfStateIsAllTest() {
        Long userId = user.getId();
        String state = String.valueOf(State.ALL);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(List.of(booking));
        List<BookingDto> result = bookingService.getAllBookingByState(userId, state,
                PageRequest.of(0, 10));

        assertEquals(1, result.size());
    }

    @Test
    void getAllBookingByStateIfStateIsCurrentTest() {
        Long userId = user.getId();
        String state = String.valueOf(State.CURRENT);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));
        List<BookingDto> result = bookingService.getAllBookingByState(userId, state,
                PageRequest.of(0, 10));

        assertEquals(1, result.size());
    }

    @Test
    void getAllBookingByStateIfStateIsPastTest() {
        Long userId = user.getId();
        String state = String.valueOf(State.PAST);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndEndBefore(anyLong(), any(), any())).thenReturn(List.of(booking));
        List<BookingDto> result = bookingService.getAllBookingByState(userId, state,
                PageRequest.of(0, 10));

        assertEquals(1, result.size());
    }


    @Test
    void getAllBookingByStateIfStateIsFutureTest() {
        Long userId = user.getId();
        String state = String.valueOf(State.FUTURE);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStartAfter(anyLong(), any(), any())).thenReturn(List.of(booking));
        List<BookingDto> result = bookingService.getAllBookingByState(userId, state,
                PageRequest.of(0, 10));

        assertEquals(1, result.size());
    }

    @Test
    void getAllBookingByStateIfStateIsWaitingTest() {
        Long userId = user.getId();
        String state = String.valueOf(State.WAITING);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));
        List<BookingDto> result = bookingService.getAllBookingByState(userId, state,
                PageRequest.of(0, 10));

        assertEquals(1, result.size());
    }

    @Test
    void getAllBookingByStateIfStateIsRejectedTest() {
        Long userId = user.getId();
        String state = String.valueOf(State.REJECTED);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));
        List<BookingDto> result = bookingService.getAllBookingByState(userId, state,
                PageRequest.of(0, 10));

        assertEquals(1, result.size());
    }

    @Test
    void getAllBookingByStateWithWrongStatusTest() {
        Long userId = user.getId();
        String state = "Wrong State";
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(NotSupportedStateException.class, () -> bookingService.getAllBookingByState(userId,
                state, PageRequest.of(0, 10)));
    }

    @Test
    void getAllBookingByStateWithUserNotFoundExceptionTest() {
        Long userId = 999L;
        String state = String.valueOf(State.ALL);
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingByState(userId,
                state, PageRequest.of(0, 10)));
    }

    @Test
    void getApproveTest() {
        Long userId = user.getId();
        Long bookingId = booking.getId();
        BookingDto bookingDto = BookingMapper.bookToDto(booking);
        BookingDto bookingDtoActual;
        when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking));
        bookingDtoActual = bookingService.approveBooking(userId, bookingId, true);
        bookingDto.setStatus(Status.APPROVED);

        assertEquals(bookingDto.getStatus(), bookingDtoActual.getStatus());
    }

    @Test
    void getApproveWithRejectedStatusTest() {
        Long userId = user.getId();
        Long bookingId = booking.getId();
        BookingDto bookingDto = BookingMapper.bookToDto(booking);
        BookingDto bookingDtoActual;
        when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking));
        bookingDtoActual = bookingService.approveBooking(userId, bookingId, false);
        bookingDto.setStatus(Status.REJECTED);

        assertEquals(bookingDto.getStatus(), bookingDtoActual.getStatus());
    }

    @Test
    void getApproveWithWrongUserIdTest() {
        Long userId = 999L;
        Long bookingId = booking.getId();
        when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking));

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(userId, bookingId, true));
    }

    @Test
    void getApproveWithNotAvailableExceptionTest() {
        Long userId = user.getId();
        Long bookingId = booking.getId();
        when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking));
        booking.setStatus(Status.APPROVED);
        assertThrows(NotAvailableException.class, () -> bookingService.approveBooking(userId,
                bookingId, false));
    }
}
