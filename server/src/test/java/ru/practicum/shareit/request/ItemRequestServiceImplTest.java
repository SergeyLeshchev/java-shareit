package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.dto.ItemResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void createItemRequestTest_whenValidRequest_shouldCreateItemRequest() {
        long userId = 1L;
        User user = new User(1L, "userName", "email@email.com");
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        ItemRequest expectedItemRequest = new ItemRequest(1L, "description", user, time);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(expectedItemRequest)).thenReturn(expectedItemRequest);

        ItemRequest actualitemRequest = itemRequestService.createItemRequest(userId, expectedItemRequest);

        assertEquals(expectedItemRequest, actualitemRequest);
        verify(itemRequestRepository, times(1)).save(expectedItemRequest);
    }

    @Test
    void createItemRequestTest_whenUserNotExists_shouldNotFoundException() {
        long userId = 1L;
        ItemRequest itemRequest = new ItemRequest();

        assertThrows(NotFoundException.class, () -> itemRequestService.createItemRequest(userId, itemRequest));
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void getItemRequestByIdTest_whenItemRequestHasItems_shouldReturnItemRequestWithItems() {
        Long itemRequestId = 1L;
        User user = new User(1L, "userName", "email@email.com");
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        ItemRequest itemRequest = new ItemRequest(1L, "description", user, time);
        Item item1 = new Item(1L, "itemName", "itemDescription", true, user, itemRequest);
        Item item2 = new Item(2L, "itemName2", "itemDescription2", true, user, itemRequest);
        List<ItemResponse> itemResponses = List.of(
                ItemRequestMapper.mapToItemResponse(item1),
                ItemRequestMapper.mapToItemResponse(item2)
        );
        ItemRequestOutDto expectedItemRequestOutDto = ItemRequestMapper.maptoItemRequestOutDto(itemRequest);
        expectedItemRequestOutDto.setItems(itemResponses);
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(itemRequestId)).thenReturn(List.of(item1, item2));

        ItemRequestOutDto actualItemsRequests = itemRequestService.getItemRequestById(itemRequestId);

        assertEquals(expectedItemRequestOutDto, actualItemsRequests);
        verify(itemRequestRepository, times(1)).findById(itemRequestId);
        verify(itemRepository, times(1)).findAllByRequestId(itemRequestId);
    }

    @Test
    void getItemRequestByIdTest_whenItemRequestHasNotItems_shouldReturnItemRequestWithoutItems() {
        Long itemRequestId = 1L;
        User user = new User(1L, "userName", "email@email.com");
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        ItemRequest itemRequest = new ItemRequest(1L, "description", user, time);
        ItemRequestOutDto expectedItemRequestOutDto = ItemRequestMapper.maptoItemRequestOutDto(itemRequest);
        expectedItemRequestOutDto.setItems(List.of());
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(itemRequestId)).thenReturn(List.of());

        ItemRequestOutDto actualItemsRequests = itemRequestService.getItemRequestById(itemRequestId);

        assertEquals(expectedItemRequestOutDto, actualItemsRequests);
        verify(itemRequestRepository, times(1)).findById(itemRequestId);
        verify(itemRepository, times(1)).findAllByRequestId(itemRequestId);
    }

    @Test
    void getItemRequestByIdTest_whenItemRequestNotExists_shouldThrowNotFoundException() {
        Long itemRequestId = 1L;
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(itemRequestId));
        verify(itemRequestRepository, times(1)).findById(itemRequestId);
        verify(itemRepository, never()).findAllByRequestId(anyLong());
    }

    @Test
    void getAllItemRequestsTest() {
        ItemRequest itemRequest = new ItemRequest();
        List<ItemRequest> expectedItemRequests = List.of(itemRequest);
        when(itemRequestRepository.findAll()).thenReturn(expectedItemRequests);

        List<ItemRequest> actualItemRequests = itemRequestService.getAllItemRequests();

        assertEquals(expectedItemRequests, actualItemRequests);
        verify(itemRequestRepository, times(1)).findAll();
    }

    @Test
    void getItemRequestsByUserTest_whenItemRequestsHaveItems_shouldReturnItemRequestWithItems() {
        long userId = 1L;
        User user = new User(1L, "userName", "email@email.com");
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        ItemRequest itemRequest = new ItemRequest(1L, "description", user, time);
        Item item1 = new Item(1L, "itemName", "itemDescription", true, user, itemRequest);
        Item item2 = new Item(2L, "itemName2", "itemDescription2", true, user, itemRequest);
        List<ItemResponse> itemResponses = List.of(
                ItemRequestMapper.mapToItemResponse(item1),
                ItemRequestMapper.mapToItemResponse(item2)
        );
        ItemRequestOutDto itemRequestOutDto = ItemRequestMapper.maptoItemRequestOutDto(itemRequest);
        itemRequestOutDto.setItems(itemResponses);
        List<ItemRequestOutDto> expectedItemsRequests = List.of(itemRequestOutDto);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorId(userId)).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestId(itemRequest.getId())).thenReturn(List.of(item1, item2));

        List<ItemRequestOutDto> actualItemsRequests = itemRequestService.getItemRequestsByUser(userId);

        assertEquals(expectedItemsRequests, actualItemsRequests);
        verify(itemRequestRepository, times(1)).findAllByRequestorId(userId);
        verify(itemRepository, times(1)).findAllByRequestId(itemRequest.getId());
    }

    @Test
    void getItemRequestsByUserTest_whenItemRequestsNotHaveItems_shouldReturnListWithoutItems() {
        long userId = 1L;
        User user = new User(1L, "userName", "email@email.com");
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        ItemRequest itemRequest = new ItemRequest(1L, "description", user, time);
        ItemRequestOutDto itemRequestOutDto = ItemRequestMapper.maptoItemRequestOutDto(itemRequest);
        itemRequestOutDto.setItems(List.of());
        List<ItemRequestOutDto> expectedItemsRequests = List.of(itemRequestOutDto);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorId(userId)).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestId(itemRequest.getId())).thenReturn(List.of());

        List<ItemRequestOutDto> actualItemsRequests = itemRequestService.getItemRequestsByUser(userId);

        assertEquals(expectedItemsRequests, actualItemsRequests);
        verify(itemRequestRepository, times(1)).findAllByRequestorId(userId);
        verify(itemRepository, times(1)).findAllByRequestId(itemRequest.getId());
    }

    @Test
    void getItemRequestsByUserTest_whenUserNotHasItemRequests_shouldThrowNotFoundException() {
        long userId = 1L;
        User user = new User(1L, "userName", "email@email.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestsByUser(userId));
        verify(itemRequestRepository, times(1)).findAllByRequestorId(userId);
        verify(itemRepository, never()).findAllByRequestId(anyLong());
    }

    @Test
    void getItemRequestsByUserTest_whenUserNotExists_shouldThrowNotFoundException() {
        long userId = 1L;

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestsByUser(userId));
        verify(itemRequestRepository, never()).findAllByRequestorId(anyLong());
        verify(itemRepository, never()).findAllByRequestId(anyLong());
    }
}