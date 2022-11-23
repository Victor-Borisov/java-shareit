package ru.practicum.shareit.bookingtests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.StatusType;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.booking.StatusType.APPROVED;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryTests {
    @Autowired
    private BookingDao bookingRepository;

    @Autowired
    private UserDao userRepository;

    @Autowired
    private ItemDao itemRepository;

    private User user;

    private Item item;

    private User user2;

    private Booking booking;

    private Sort sort;

    @BeforeEach
    void init() {
        user = User.builder()
                   .name("name")
                   .email("email@email.com")
                   .build();

        item = Item.builder()
                   .name("name")
                   .description("description")
                   .available(true)
                   .owner(user)
                   .build();

        user2 = User.builder()
                    .name("name2")
                    .email("email2@email.com")
                    .build();

        booking = Booking.builder()
                         .start(LocalDateTime.of(2023, 1, 10, 10, 30))
                         .end(LocalDateTime.of(2023, 2, 10, 10, 30))
                         .item(item)
                         .booker(user2)
                         .status(APPROVED)
                         .build();

        sort = Sort.by(Sort.Direction.DESC, "start");
    }

    @Test
    void findAllByItemIdOrderByStartAscTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(item.getId(),
                                            LocalDateTime.of(2023, 1, 9, 10, 30),
                                            List.of(StatusType.APPROVED))
                                    .get()
                                    .getStart(),
                equalTo(LocalDateTime.of(2023, 1, 10, 10, 30)));
    }

    @Test
    void findAllByBookerTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByBookerId(user2.getId(),
                PageRequest.of(0, 10, sort)
        ).stream().count(), equalTo(1L));
    }

    @Test
    void findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBeforeTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        booking.setStatus(APPROVED);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(user2.getId(),
                        item.getId(), APPROVED,
                        LocalDateTime.of(2023, 3, 10, 10, 10)).size(),
                equalTo(1));
    }

    @Test
    void findAllByBookerAndStartBeforeAndEndAfterTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(user2.getId(),
                LocalDateTime.of(2023, 2, 1, 10, 10),
                LocalDateTime.of(2023, 1, 1, 10, 30),
                PageRequest.of(0, 10, sort)
        ).stream().count(), equalTo(1L));
    }

    @Test
    void findAllByItemOwnerAndEndBeforeTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByItemOwnerIdAndEndBefore(user.getId(),
                LocalDateTime.of(2023, 4, 10, 10, 10),
                PageRequest.of(0, 10, sort)).stream().count(), equalTo(1L));
    }

    @Test
    void findAllByItemOwnerAndStartAfterTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByItemOwnerIdAndStartAfter(user.getId(),
                LocalDateTime.now(),
                PageRequest.of(0, 10, sort)).stream().count(), equalTo(1L));
    }

    @Test
    void findAllByItemOwnerAndStatusEqualsTest() {
        userRepository.save(user);
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking);
        assertThat(bookingRepository.findAllByItemOwnerIdAndStatusEquals(user.getId(), APPROVED,
                                            PageRequest.of(0, 10, sort))
                                    .stream().count(), equalTo(1L));
    }

}
