package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.DataAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createItemTest_whenCreateValidItem_shouldCreateItem() {
        Item item = new Item();
        long userId = 0L;
        Long itemRequestId = null;
        User user = new User();
        when(itemRepository.save(item)).thenReturn(item);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Item actualItem = itemService.createItem(item, userId, itemRequestId);

        assertEquals(item, actualItem);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void createItemTest_whenOwnerNotExists_shouldThrowNotFoundException() {
        Item item = new Item();
        long userId = 0L;
        Long itemRequestId = null;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(item, userId, itemRequestId));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void createItemTest_whenItemRequestNotExists_shouldThrowNotFoundException() {
        Item item = new Item();
        User user = new User();
        long userId = 0L;
        Long itemRequestId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(item, userId, itemRequestId));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItemTest_whenUpdateValidName_shouldUpdateName() {
        User owner = new User();
        owner.setId(1L);
        Item oldItem = new Item(
                1L,
                "oldName",
                "oldDescription",
                true,
                owner,
                null
        );
        Item expectedItem = new Item(
                1L,
                "newName",
                "oldDescription",
                true,
                owner,
                null
        );
        UpdateItemRequest newItem = new UpdateItemRequest("newName", null, null);
        when(itemRepository.findById(oldItem.getId())).thenReturn(Optional.of(expectedItem));
        when(itemRepository.save(expectedItem)).thenReturn(expectedItem);

        Item actualItem = itemService.updateItem(owner.getId(), oldItem.getId(), newItem);

        assertEquals(expectedItem, actualItem);
        verify(itemRepository, times(1)).save(expectedItem);
    }

    @Test
    void updateItemTest_whenUpdateValidDescriptionAndAvailable_shouldUpdateDescriptionAndAvailable() {
        User owner = new User();
        owner.setId(1L);
        Item oldItem = new Item(
                1L,
                "oldName",
                "oldDescription",
                true,
                owner,
                null
        );
        Item expectedItem = new Item(
                1L,
                "oldName",
                "newDescription",
                false,
                owner,
                null
        );
        UpdateItemRequest newItem = new UpdateItemRequest(null, "newDescription", false);
        when(itemRepository.findById(oldItem.getId())).thenReturn(Optional.of(expectedItem));
        when(itemRepository.save(expectedItem)).thenReturn(expectedItem);

        Item actualItem = itemService.updateItem(owner.getId(), oldItem.getId(), newItem);

        assertEquals(expectedItem, actualItem);
        verify(itemRepository, times(1)).save(expectedItem);
    }

    @Test
    void updateItemTest_whenItemNotExists_shouldThrowNotFoundException() {
        long itemId = 1L;
        long userId = 1L;
        UpdateItemRequest newItem = new UpdateItemRequest();
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(userId, itemId, newItem));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItemTest_whenUserNotOwner_shouldThrowNotFoundException() {
        long itemId = 1L;
        long userId = 1L;
        long ownerId = 2L;
        User owner = new User();
        owner.setId(ownerId);
        Item item = new Item();
        item.setOwner(owner);
        UpdateItemRequest newItem = new UpdateItemRequest();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(userId, itemId, newItem));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItemTest_whenUpdateItemRequestNull_shouldThrowBadRequestException() {
        long itemId = 1L;
        long ownerId = 1L;
        User owner = new User();
        owner.setId(ownerId);
        Item item = new Item();
        item.setOwner(owner);
        UpdateItemRequest newItem = null;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> itemService.updateItem(ownerId, itemId, newItem));
        verify(itemRepository, never()).save(any(Item.class));
    }

    // Успешно когда есть вещи с бронированиями и комментариями
    @Test
    void getItemsByUserIdTest_whenItemsExistWithBookingsAndComments_shouldReturnItems() {
        User booker = new User(1L, "userName1", "email1@email.com");
        // Создаем вещи пользователя
        Item item1 = new Item(1L, "itemName", "itemDescription", true, booker, null);
        Item item2 = new Item(2L, "itemName2", "itemDescription2", true, booker, null);
        List<Item> items = List.of(item1, item2);
        // Создаем бронирования пользователя
        ZonedDateTime time1 = ZonedDateTime.now().minusMinutes(8);
        ZonedDateTime time2 = ZonedDateTime.now().minusMinutes(7);
        ZonedDateTime time3 = ZonedDateTime.now().plusMinutes(5);
        ZonedDateTime time4 = ZonedDateTime.now().plusMinutes(6);
        Booking booking1 = new Booking(1L, time1, time2, item1, booker, BookingState.WAITING);
        Booking booking2 = new Booking(2L, time3, time4, item2, booker, BookingState.WAITING);
        List<Booking> bookings = List.of(booking1, booking2);
        // Создаем комментарии
        Comment comment1 = new Comment(1L, "comment1Text", item1, booker, ZonedDateTime.now());
        Comment comment2 = new Comment(2L, "comment2Text", item2, booker, ZonedDateTime.now());
        List<Comment> comments = List.of(comment1, comment2);
        // Создаем то, что должен вернуть метод
        List<ItemDto> expectedItems = List.of(
                ItemMapper.mapToItemDto(item1),
                ItemMapper.mapToItemDto(item2)
        );
        expectedItems.get(0).setLastBooking(time2.toLocalDateTime());
        expectedItems.get(1).setNextBooking(time3.toLocalDateTime());
        expectedItems.get(0).setComments(List.of(CommentMapper.mapToCommentDto(comment1)));
        expectedItems.get(1).setComments(List.of(CommentMapper.mapToCommentDto(comment2)));

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findAllByOwnerId(booker.getId())).thenReturn(items);
        when(bookingRepository.findAllByItemOwnerId(booker.getId())).thenReturn(bookings);
        when(commentRepository.findAll()).thenReturn(comments);

        List<ItemDto> actualItems = itemService.getItemsByUserId(booker.getId());

        assertEquals(expectedItems, actualItems);
    }

    // Успешно когда есть вещи без бронирований и комментариев
    @Test
    void getItemsByUserIdTest_whenItemsExistWithoutBookingsAndComments_shouldReturnItems() {
        User booker = new User(1L, "userName1", "email1@email.com");
        // Создаем вещи пользователя
        Item item1 = new Item(1L, "itemName", "itemDescription", true, booker, null);
        Item item2 = new Item(2L, "itemName2", "itemDescription2", true, booker, null);
        List<Item> items = List.of(item1, item2);
        List<ItemDto> expectedItems = List.of(
                ItemMapper.mapToItemDto(item1),
                ItemMapper.mapToItemDto(item2)
        );
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findAllByOwnerId(booker.getId())).thenReturn(items);
        when(bookingRepository.findAllByItemOwnerId(booker.getId())).thenReturn(List.of());
        when(commentRepository.findAll()).thenReturn(List.of());

        List<ItemDto> actualItems = itemService.getItemsByUserId(booker.getId());

        assertEquals(expectedItems, actualItems);
    }

    // Исключение когда пользователь не найден
    @Test
    void getItemsByUserIdTest_whenUserNotExists_shouldThrowNotFoundException() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemsByUserId(userId));
    }

    // Исключение когда у пользователя нет вещей
    @Test
    void getItemsByUserIdTest_whenItemsNotExist_shouldThrowNotFoundException() {
        long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(userId)).thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> itemService.getItemsByUserId(userId));
    }

    // Успешно когда есть вещь с комментариями
    @Test
    void getItemByIdTest_whenItemExistsWithComments_shouldReturnItem() {
        long itemId = 1L;
        User owner = new User(1L, "userName1", "email1@email.com");
        User author = new User(2L, "userName2", "email2@email.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, owner, null);
        Comment comment1 = new Comment(1L, "comment1Text", item, author, ZonedDateTime.now());
        Comment comment2 = new Comment(2L, "comment2Text", item, author, ZonedDateTime.now());
        ItemDto expectedDto = ItemMapper.mapToItemDto(item);
        expectedDto.setComments(List.of(
                CommentMapper.mapToCommentDto(comment1),
                CommentMapper.mapToCommentDto(comment2)
        ));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(List.of(comment1, comment2));

        ItemDto actualDto = itemService.getItemById(itemId);
        assertEquals(expectedDto, actualDto);
    }

    // Успешно когда есть вещь без комментариев
    @Test
    void getItemByIdTest_whenItemExistsWithoutComments_shouldReturnItem() {
        long itemId = 1L;
        User owner = new User(1L, "userName1", "email1@email.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, owner, null);
        ItemDto expectedDto = ItemMapper.mapToItemDto(item);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(List.of());

        ItemDto actualDto = itemService.getItemById(itemId);

        assertEquals(expectedDto, actualDto);
        verify(commentRepository, times(1)).findAllByItemId(itemId);
    }

    // Исключение когда вещь не найдена
    @Test
    void getItemByIdTest_whenItemNotExists_shouldThrowNotFoundException() {
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(itemId));
        verify(commentRepository, never()).findAllByItemId(anyLong());
    }

    @Test
    void deleteItemTest_whenItemExists_shouldDeleteItem() {
        User owner = new User(1L, "userName1", "email1@email.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, owner, null);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        itemService.deleteItem(owner.getId(), item.getId());

        verify(itemRepository, times(1)).delete(item);
    }

    @Test
    void deleteItemTest_whenItemNotExists_shouldThrowNotFoundException() {
        long userId = 1L;
        long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.deleteItem(userId, itemId));
        verify(itemRepository, never()).delete(any(Item.class));
    }

    @Test
    void deleteItemTest_whenUserNotOwner_shouldThrowDataAccessException() {
        long userId = 2L;
        User owner = new User(1L, "userName1", "email1@email.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, owner, null);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(DataAccessException.class, () -> itemService.deleteItem(userId, item.getId()));

        verify(itemRepository, never()).delete(any(Item.class));
    }

    @Test
    void getItemsByTextTest_whenTextExistsAndItemsAvailable_shouldReturnItems() {
        User owner = new User();
        String text = "text";
        Item item1 = new Item(1L, "itemTeXtName", "itemDescription", true, owner, null);
        Item item2 = new Item(1L, "itemName", "itemDescriptionteXT", false, owner, null);
        Item item3 = new Item(1L, "itemName", "itemDescriptionteXT", true, owner, null);
        List<Item> items = List.of(item1, item2, item3);
        List<Item> expectedItems = List.of(item1, item3);
        when(itemRepository.findItemsByText(text)).thenReturn(items);

        List<Item> actualItems = itemService.getItemsByText(text);

        assertEquals(expectedItems, actualItems);
        verify(itemRepository, times(1)).findItemsByText(text);
    }

    @Test
    void getItemsByTextTest_whenTextNotExists_shouldReturnEmptyList() {
        String text = "text";
        List<Item> expectedItems = List.of();
        when(itemRepository.findItemsByText(text)).thenReturn(List.of());

        List<Item> actualItems = itemService.getItemsByText(text);

        assertEquals(expectedItems, actualItems);
        verify(itemRepository, times(1)).findItemsByText(text);
    }

    @Test
    void getItemsByTextTest_whenTextBlank_shouldReturnEmptyList() {
        String text = "";
        List<Item> expectedItems = List.of();

        List<Item> actualItems = itemService.getItemsByText(text);

        assertEquals(expectedItems, actualItems);
        verify(itemRepository, never()).findItemsByText(anyString());
    }



    @Test
    void createCommentTest_whenUserNotExists_shouldThrowNotFoundException() {
        User user = new User(1L, "userName1", "email1@email.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Comment comment = new Comment(1L, "comment1Text", item, user, time);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createComment(user.getId(), item.getId(), comment));
        verify(commentRepository, never()).save(comment);
    }

    @Test
    void createCommentTest_whenItemNotExists_shouldThrowNotFoundException() {
        User user = new User(1L, "userName1", "email1@email.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Comment comment = new Comment(1L, "comment1Text", item, user, time);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createComment(user.getId(), item.getId(), comment));
        verify(commentRepository, never()).save(comment);
    }

    @Test
    void createCommentTest_whenBookingNotExists_shouldThrowBadRequestException() {
        User user = new User(1L, "userName1", "email1@email.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Comment comment = new Comment(1L, "comment1Text", item, user, time);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerId(user.getId())).thenReturn(List.of());

        assertThrows(BadRequestException.class, () -> itemService.createComment(user.getId(), item.getId(), comment));
        verify(commentRepository, never()).save(comment);
    }

    @Test
    void createCommentTest_whenBookingInFuture_shouldThrowBadRequestException() {
        User user = new User(1L, "userName1", "email1@email.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Booking booking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3), item, user, BookingState.WAITING);
        Comment comment = new Comment(1L, "comment1Text", item, user, time);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerId(user.getId())).thenReturn(List.of(booking));

        assertThrows(BadRequestException.class, () -> itemService.createComment(user.getId(), item.getId(), comment));
        verify(commentRepository, never()).save(comment);
    }

    @Test
    void createCommentTest_whenValidData_shouldCreateComment() {
        User user = new User(1L, "userName1", "email1@email.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Booking booking = new Booking(1L, time.minusSeconds(2), time.minusSeconds(1), item, user, BookingState.WAITING);
        Comment expectedComment = new Comment(1L, "comment1Text", item, user, time);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerId(user.getId())).thenReturn(List.of(booking));
        when(commentRepository.save(expectedComment)).thenReturn(expectedComment);

        Comment actualComment = itemService.createComment(user.getId(), item.getId(), expectedComment);

        assertEquals(expectedComment, actualComment);
        verify(commentRepository, times(1)).save(expectedComment);
    }
}