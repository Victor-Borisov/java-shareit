package ru.practicum.shareit.bookingtests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.booking.StatusType.APPROVED;
import static ru.practicum.shareit.booking.StatusType.WAITING;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingControllerTests {
    @Autowired
    private BookingController bookingController;

    @Autowired
    private UserController userController;

    @Autowired
    private ItemController itemController;

    private ItemInDto itemInDto;

    private UserDto userDto;

    private UserDto userDto1;

    private BookingRequestDto bookingInDto;

    @BeforeEach
    void init() {
        itemInDto = ItemInDto
                .builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        userDto = UserDto
                .builder()
                .name("name")
                .email("user@email.com")
                .build();

        userDto1 = UserDto
                .builder()
                .name("name")
                .email("user1@email.com")
                .build();

        bookingInDto = BookingRequestDto
                .builder()
                .start(LocalDateTime.of(2023, 10, 24, 12, 30))
                .end(LocalDateTime.of(2023, 11, 10, 13, 0))
                .itemId(1L).build();
    }

    @Test
    void createTest() {
        UserDto user = userController.create(userDto);
        ItemDto item = itemController.create(user.getId(), itemInDto);
        UserDto user1 = userController.create(userDto1);
        BookingDto booking = bookingController.create(bookingInDto, user1.getId());
        assertEquals(1L, bookingController.getById(booking.getId(), user1.getId()).getId());
    }

    @Test
    void createByWrongUserTest() {
        assertThrows(NotFoundException.class, () -> bookingController.create(bookingInDto, 1L));
    }

    @Test
    void createForWrongItemTest() {
        UserDto user = userController.create(userDto);
        assertThrows(NotFoundException.class, () -> bookingController.create(bookingInDto, 1L));
    }

    @Test
    void createByOwnerTest() {
        UserDto user = userController.create(userDto);
        ItemDto item = itemController.create(user.getId(), itemInDto);
        assertThrows(NotFoundException.class, () -> bookingController.create(bookingInDto, 1L));
    }

    @Test
    void createToUnavailableItemTest() {
        UserDto user = userController.create(userDto);
        itemInDto.setAvailable(false);
        ItemDto item = itemController.create(user.getId(), itemInDto);
        UserDto user1 = userController.create(userDto1);
        assertThrows(BadRequestException.class, () -> bookingController.create(bookingInDto, 2L));
    }

    @Test
    void approveTest() {
        UserDto user = userController.create(userDto);
        ItemDto item = itemController.create(user.getId(), itemInDto);
        UserDto user1 = userController.create(userDto1);
        BookingDto booking = bookingController.create(BookingRequestDto
                .builder()
                .start(LocalDateTime.of(2023, 10, 24, 12, 30))
                .end(LocalDateTime.of(2023, 11, 10, 13, 0))
                .itemId(item.getId()).build(), user1.getId());
        assertEquals(WAITING, bookingController.getById(booking.getId(), user1.getId()).getStatus());
        bookingController.approve(booking.getId(), user.getId(), true);
        assertEquals(APPROVED, bookingController.getById(booking.getId(), user1.getId()).getStatus());
    }

    @Test
    void approveToWrongBookingTest() {
        assertThrows(NotFoundException.class, () -> bookingController.approve(1L, 1L, true));
    }

    @Test
    void approveByWrongUserTest() {
        UserDto user = userController.create(userDto);
        ItemDto item = itemController.create(user.getId(), itemInDto);
        UserDto user1 = userController.create(userDto1);
        BookingDto booking = bookingController.create(bookingInDto, user1.getId());
        assertThrows(NotFoundException.class, () -> bookingController.approve(1L, 2L, true));
    }

    @Test
    void approveToBookingWithWrongStatus() {
        UserDto user = userController.create(userDto);
        ItemDto item = itemController.create(user.getId(), itemInDto);
        UserDto user1 = userController.create(userDto1);
        BookingDto booking = bookingController.create(bookingInDto, user1.getId());
        bookingController.approve(1L, 1L, true);
        assertThrows(BadRequestException.class, () -> bookingController.approve(1L, 1L, true));
    }

    @Test
    void getAllByUserTest() {
        UserDto user = userController.create(userDto);
        ItemDto item = itemController.create(user.getId(), itemInDto);
        UserDto user1 = userController.create(userDto1);
        BookingDto booking = bookingController.create(bookingInDto, user1.getId());
        assertEquals(1, bookingController.getAllByUser(user1.getId(), "WAITING", 0, 10).size());
        assertEquals(1, bookingController.getAllByUser(user1.getId(), "ALL", 0, 10).size());
        assertEquals(0, bookingController.getAllByUser(user1.getId(), "PAST", 0, 10).size());
        assertEquals(0, bookingController.getAllByUser(user1.getId(), "CURRENT", 0, 10).size());
        assertEquals(1, bookingController.getAllByUser(user1.getId(), "FUTURE", 0, 10).size());
        assertEquals(0, bookingController.getAllByUser(user1.getId(), "REJECTED", 0, 10).size());
        bookingController.approve(booking.getId(), user.getId(), true);
        assertEquals(0, bookingController.getAllByOwner(user.getId(), "CURRENT", 0, 10).size());
        assertEquals(1, bookingController.getAllByOwner(user.getId(), "ALL", 0, 10).size());
        assertEquals(0, bookingController.getAllByOwner(user.getId(), "WAITING", 0, 10).size());
        assertEquals(1, bookingController.getAllByOwner(user.getId(), "FUTURE", 0, 10).size());
        assertEquals(0, bookingController.getAllByOwner(user.getId(), "REJECTED", 0, 10).size());
        assertEquals(0, bookingController.getAllByOwner(user.getId(), "PAST", 0, 10).size());
    }

    @Test
    void getAllByWrongUserTest() {
        assertThrows(NotFoundException.class, () -> bookingController.getAllByUser(1L, "ALL", 0, 10));
        assertThrows(NotFoundException.class, () -> bookingController.getAllByOwner(1L, "ALL", 0, 10));
    }

    @Test
    void getByWrongIdTest() {
        assertThrows(NotFoundException.class, () -> bookingController.getById(1L, 1L));
    }

    @Test
    void getByWrongUser() {
        UserDto user = userController.create(userDto);
        ItemDto item = itemController.create(user.getId(), itemInDto);
        UserDto user1 = userController.create(userDto1);
        BookingDto booking = bookingController.create(bookingInDto, user1.getId());
        assertThrows(NotFoundException.class, () -> bookingController.getById(1L, 10L));
    }
}
