package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Item createItem(Item item) {
        if (!userRepository.findAllUsers().stream()
                .map(User::getId)
                .toList().contains(item.getOwner())) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        return itemRepository.saveItem(item);
    }

    @Override
    public Item updateItem(long userId, long itemId, UpdateItemRequest updateItemRequest) {
        Item item = itemRepository.findItemById(itemId);
        if (item.getOwner() != userId) {
            throw new NotFoundException("Изменять вещь может только владелец вещи");
        }
        if (updateItemRequest.hasName()) {
            item.setName(updateItemRequest.getName());
        }
        if (updateItemRequest.hasDescription()) {
            item.setDescription(updateItemRequest.getDescription());
        }
        if (updateItemRequest.hasAvailable()) {
            item.setAvailable(updateItemRequest.getAvailable());
        }

        return itemRepository.updateItem(item);
    }

    @Override
    public List<Item> getItemsByUserId(long userId) {
        return itemRepository.findItemsByUserId(userId);
    }

    @Override
    public Item getItemById(long itemId) {
        return itemRepository.findItemById(itemId);
    }

    @Override
    public void deleteItem(long itemId) {
        itemRepository.deleteItem(itemId);
    }

    @Override
    public List<Item> getItemByText(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.findItemByText(text);
    }
}
