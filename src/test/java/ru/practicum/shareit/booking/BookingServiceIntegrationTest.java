package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {
    private LocalDateTime now = LocalDateTime.now();
    private final UserRepository userRepository;
    private final BookingService bookingService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private User user;
    private User altUser;
    private User savedUser;
    private User savedAltUser;
    private Item item;
    private Item savedItem;
    private Booking booking;
    private Booking savedBooking;
    private Booking altBooking;
    private Booking savedAltBooking;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("John_White")
                .email("john@test.com")
                .build();

        savedUser = userRepository.save(user);

        altUser = User.builder()
                .id(2L)
                .name("Mary_Sue")
                .email("mary@test.com")
                .build();

        savedAltUser = userRepository.save(altUser);

        item = Item.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .owner(savedUser)
                .build();

        savedItem = itemRepository.save(item);

        booking = Booking.builder()
                .id(1L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .item(savedItem)
                .booker(savedAltUser)
                .status(Status.APPROVED)
                .build();

        savedBooking = bookingRepository.save(booking);

        altBooking = Booking.builder()
                .id(5L)
                .start(now.minusDays(3))
                .end(now.minusDays(5))
                .item(savedItem)
                .booker(savedAltUser)
                .status(Status.APPROVED)
                .build();

        savedAltBooking = bookingRepository.save(altBooking);
    }

    @Test
    public void getAllOwnersBookingByStateTest() {
        long userId = savedUser.getId();
        int from = 0;
        int size = 10;
        List<BookingDto> bookingDtoList =
                bookingService.getAllOwnersBookingByState(userId, String.valueOf(State.ALL),
                        PageRequest.of(from, size));

        assertEquals(2, bookingDtoList.size());
        assertEquals(savedBooking.getId(), bookingDtoList.get(0).getId());
        assertEquals(savedAltBooking.getId(), bookingDtoList.get(1).getId());
        assertEquals(savedBooking.getStart(), bookingDtoList.get(0).getStart());
        assertEquals(savedBooking.getEnd(), bookingDtoList.get(0).getEnd());
        assertEquals(savedAltBooking.getStart(), bookingDtoList.get(1).getStart());
        assertEquals(savedAltBooking.getEnd(), bookingDtoList.get(1).getEnd());
    }


}
