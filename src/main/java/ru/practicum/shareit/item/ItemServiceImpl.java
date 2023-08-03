package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static ru.practicum.shareit.item.CommentMapper.commentToDto;
import static ru.practicum.shareit.item.ItemMapper.*;
import static ru.practicum.shareit.user.UserMapper.userToModel;
import static ru.practicum.shareit.user.UserMapper.userToOwner;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;


    public ItemDto addItem(ItemShortDto itemShortDto, Long userId) {
        ItemDto itemDto = itemToShortDto(itemShortDto);
        itemDto.setOwner(userToOwner(userService.getUserById(userId)));
        log.info("Предмес с id = {} создан", itemDto.getId());
        return itemToDto(itemRepository.save(itemToModel(itemDto)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAllItems(Long userId) {
        Collection<Item> items = itemRepository.findAllByOwnerId(userId);
        log.info("Получен список предметов");
        return getItemList(items);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        ItemDto item = itemToDto(itemRepository.findById(itemId).orElseThrow(()
                -> new NotFoundException("Предмет не найден")));
        item.setComments(getComments(item.getId()));
        setBookings(item, userId);
        log.info("Информация о предмете с id = {} получена", itemId);
        return item;
    }

    @Override
    public ItemDto updateItem(ItemShortDto itemShortDto, Long itemId, Long userId) {
        Optional<Item> updatedItem = itemRepository.findById(itemId);
        if (!updatedItem.get().getOwner().getId().equals(userId)) {
            log.info("Предмета с id = {} не существует", itemId);
            throw new NotFoundException("Предмет с id " + itemId + " не найден");
        }
        if (itemShortDto.getName() != null && !itemShortDto.getName().isBlank()) {
            updatedItem.get().setName(itemShortDto.getName());
        }
        if (itemShortDto.getDescription() != null && !itemShortDto.getDescription().isBlank()) {
            updatedItem.get().setDescription(itemShortDto.getDescription());
        }
        if (itemShortDto.getAvailable() != null) {
            updatedItem.get().setAvailable(itemShortDto.getAvailable());
        }
        log.info("Информация о предмете с id = {} обновлена", itemId);
        return itemToDto(itemRepository.save(updatedItem.get()));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> searchItemByText(String stringText) {
        String text = stringText.toLowerCase();
        if (text.isBlank()) {
            return List.of();
        }
        Collection<Item> items = itemRepository.findByNameOrDescriptionAndAvailable(text);
        log.info("Выполнен поиск по тексту {}", text);
        return getItemList(items);
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Long userId, Long itemId) {
        Comment comment = Comment
                .builder()
                .text(commentDto.getText())
                .build();
        comment.setAuthor(userToModel(userService.getUserById(userId)));

        comment.setItem((itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет не найден"))));

        comment.setAuthor(userToModel(userService.getUserById(userId)));
        if (!bookingRepository.existsByBookerIdAndEndBeforeAndStatus(userId, LocalDateTime.now(), Status.APPROVED)) {
            throw new NotAvailableException("Комментарий не может быть создан");
        }
        comment.setCreated(LocalDateTime.now());
        log.info("Комментарий добавлен");
        return commentToDto(commentRepository.save(comment));
    }

    private List<ItemDto> getItemList(Collection<Item> items) {
        List<ItemDto> itemDtoList = items.stream().map(ItemMapper::itemToDto).collect(Collectors.toList());
        List<Long> idItems = itemDtoList.stream().map(ItemDto::getId).collect(Collectors.toList());
        Map<Long, BookingItemDto> lastBookings = bookingRepository.findFirstByItemIdInAndStartLessThanEqualAndStatus(
                        idItems, LocalDateTime.now(), Status.APPROVED, Sort.by(DESC, "start"))
                .stream()
                .map(BookingMapper::bookToItem)
                .collect(Collectors.toMap(BookingItemDto::getItemId, Function.identity()));
        itemDtoList.forEach(i -> i.setLastBooking(lastBookings.get(i.getId())));
        Map<Long, BookingItemDto> nextBookings = bookingRepository.findFirstByItemIdInAndStartAfterAndStatus(
                        idItems, LocalDateTime.now(), Status.APPROVED, Sort.by(Sort.Direction.ASC, "start"))
                .stream()
                .map(BookingMapper::bookToItem)
                .collect(Collectors.toMap(BookingItemDto::getItemId, Function.identity()));
        itemDtoList.forEach(i -> i.setNextBooking(nextBookings.get(i.getId())));

        Map<Long, List<CommentDto>> comments = commentRepository.findByItemIdIn(idItems,
                        Sort.by(DESC, "created"))
                .stream()
                .map(CommentMapper::commentToDto)
                .collect(Collectors.groupingBy(CommentDto::getId));

        itemDtoList.forEach(i -> i.setComments(comments.get(i.getId())));
        itemDtoList.sort(comparing(ItemDto::getId));
        return itemDtoList;
    }

    private List<CommentDto> getComments(Long itemId) {
        List<Comment> comments = commentRepository.findByItemId(itemId);
        log.info("Получен списко комментариев");
        return comments.stream()
                .map(CommentMapper::commentToDto)
                .collect(Collectors.toList());
    }

    private ItemDto setBookings(ItemDto itemDto, Long userId) {
        if (itemDto.getOwner().getId().equals(userId)) {
            itemDto.setLastBooking(
                    bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(itemDto.getId(),
                                    LocalDateTime.now(), Status.APPROVED).map(BookingMapper::bookToItem)
                            .orElse(null));

            itemDto.setNextBooking(
                    bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemDto.getId(),
                                    LocalDateTime.now(), Status.APPROVED).map(BookingMapper::bookToItem)
                            .orElse(null));

            return itemDto;
        }
        log.info("Предмет забронирован");
        return itemDto;
    }

}