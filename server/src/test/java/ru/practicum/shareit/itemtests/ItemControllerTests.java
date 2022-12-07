package ru.practicum.shareit.itemtests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemControllerTests {
    @Autowired
    private ItemController itemController;

    @Autowired
    private UserController userController;

    @Autowired
    private BookingController bookingController;

    @Autowired
    private ItemRequestController itemRequestController;

    private ItemInDto itemInDto;

    private ItemInDto itemInDto2;

    private UserDto userDto;

    private UserDto userDto2;

    private ItemRequestDto itemRequestDto;

    private CommentDto comment;

    @BeforeEach
    void init() {
        itemInDto = ItemInDto
                .builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        itemInDto2 = ItemInDto
                .builder()
                .name("new name")
                .description("new description")
                .available(true)
                .build();

        userDto = UserDto
                .builder()
                .name("name")
                .email("user@email.com")
                .build();

        userDto2 = UserDto
                .builder()
                .name("name")
                .email("user2@email.com")
                .build();

        itemRequestDto = ItemRequestDto
                .builder()
                .description("item request description")
                .build();

        comment = CommentDto
                .builder()
                .text("first comment")
                .build();
    }

    @Test
    void createTest() {
        UserDto user = userController.create(userDto);
        ItemDto item = itemController.create(1L, itemInDto);
        assertEquals(item.getId(), itemController.getById(item.getId(), user.getId()).getId());
    }

    @Test
    void createWithRequestTest() {
        UserDto user = userController.create(userDto);
        ItemRequestDto itemRequest = itemRequestController.create(user.getId(), itemRequestDto);
        itemInDto.setRequestId(1L);
        UserDto user2 = userController.create(userDto2);
        ItemDto item = itemController.create(2L, itemInDto);
        assertEquals(item, itemController.getById(2L, 1L));
    }

    @Test
    void createByWrongUser() {
        assertThrows(NotFoundException.class, () -> itemController.create(1L, itemInDto));
    }

    @Test
    void createWithWrongItemRequest() {
        itemInDto.setRequestId(10L);
        UserDto user = userController.create(userDto);
        assertThrows(NotFoundException.class, () -> itemController.create(1L, itemInDto));
    }

    @Test
    void updateTest() {
        userController.create(userDto);
        itemController.create(1L, itemInDto);
        itemController.update(1L, 1L, itemInDto2);
        assertEquals(itemInDto2.getDescription(), itemController.getById(1L, 1L).getDescription());
    }

    @Test
    void updateForWrongItemTest() {
        assertThrows(NotFoundException.class, () -> itemController.update(1L, 1L, itemInDto));
    }

    @Test
    void updateByWrongUserTest() {
        userController.create(userDto);
        itemController.create(1L, itemInDto);
        assertThrows(ForbiddenException.class, () -> itemController.update(10L, 1L, itemInDto2));
    }

    @Test
    void deleteTest() {
        userController.create(userDto);
        itemController.create(1L, itemInDto);
        assertEquals(1, itemController.getAllByOwner(1L, 0, 10).size());
        itemController.delete(1L, 1L);
        assertEquals(0, itemController.getAllByOwner(1L, 0, 10).size());
    }

    @Test
    void searchTest() {
        userController.create(userDto);
        itemController.create(1L, itemInDto);
        assertEquals(1, itemController.search("Desc", 0, 10).size());
    }

    @Test
    void searchEmptyTextTest() {
        userController.create(userDto);
        itemController.create(1L, itemInDto);
        assertEquals(new ArrayList<ItemDto>(), itemController.search("", 0, 10));
    }

    @Test
    void createCommentTest() throws InterruptedException {
        UserDto user = userController.create(userDto);
        ItemDto item = itemController.create(user.getId(), itemInDto);
        UserDto user2 = userController.create(userDto2);
        bookingController.create(BookingRequestDto
                .builder()
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .itemId(item.getId()).build(), user2.getId());
        bookingController.approve(1L, user.getId(), true);
        TimeUnit.SECONDS.sleep(2);
        itemController.createComment(item.getId(), user2.getId(), comment);
        assertEquals(1, itemController.getById(1L, 1L).getComments().size());
    }

    @Test
    void createCommentByWrongUser() {
        assertThrows(NotFoundException.class, () -> itemController.createComment(1L, 1L, comment));
    }

    @Test
    void createCommentToWrongItem() {
        UserDto user = userController.create(userDto);
        assertThrows(NotFoundException.class, () -> itemController.createComment(1L, 1L, comment));
        ItemDto item = itemController.create(1L, itemInDto);
        assertThrows(BadRequestException.class, () -> itemController.createComment(1L, 1L, comment));
    }
}
