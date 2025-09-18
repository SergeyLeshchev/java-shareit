package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.dto.ItemResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequest createItemRequest(Long userId, ItemRequest itemRequest) {
        itemRequest.setRequestor(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден")));
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public ItemRequestOutDto getItemRequestById(Long itemRequestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Запрос вещи с таким id не найден"));
        ItemRequestOutDto itemRequestOutDto = ItemRequestMapper.maptoItemRequestOutDto(itemRequest);
        List<Item> items = itemRepository.findAllByRequestId(itemRequestId);
        List<ItemResponse> itemsDTO = items.stream()
                // Преобразуем каждую вещь в списке в ItemDto
                .map(ItemRequestMapper::mapToItemResponse)
                .toList();
        itemRequestOutDto.setItems(itemsDTO);
        return itemRequestOutDto;
    }

    @Override
    public List<ItemRequest> getAllItemRequests() {
        return itemRequestRepository.findAll();
    }

    @Override
    public List<ItemRequestOutDto> getItemRequestsByUser(Long userId) {
        // Проверка существования пользователя
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));
        // Находим все запросы пользователя
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(userId);
        if (itemRequests.isEmpty()) {
            throw new NotFoundException("У пользователя нет запросов");
        }
        List<Long> requestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .toList();

        Map<Long, List<Item>> itemsForEachItem = itemRepository.findAllByRequestIdIn(requestIds).stream()
                .collect(Collectors.groupingBy(i -> i.getRequest().getId()));

        List<ItemRequestOutDto> itemRequestOutDtoList = new ArrayList<>();
        if (!itemsForEachItem.isEmpty()) {
            itemRequests.forEach(ir -> {
                List<ItemResponse> itemsForItemRequest = itemsForEachItem.get(ir.getId()).stream()
                        .map(ItemRequestMapper::mapToItemResponse)
                        .toList();
                ItemRequestOutDto itemRequestOutDto = ItemRequestMapper.maptoItemRequestOutDto(ir);
                itemRequestOutDto.setItems(itemsForItemRequest);
                itemRequestOutDtoList.add(itemRequestOutDto);
            });
        } else {
            itemRequests.forEach(ir -> {
                ItemRequestOutDto itemRequestOutDto = ItemRequestMapper.maptoItemRequestOutDto(ir);
                itemRequestOutDto.setItems(List.of());
                itemRequestOutDtoList.add(itemRequestOutDto);
            });
        }

        return itemRequestOutDtoList;
    }
}
