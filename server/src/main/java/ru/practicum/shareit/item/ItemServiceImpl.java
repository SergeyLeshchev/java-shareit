package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.DataAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public Item createItem(Item item, Long userId, Long itemRequestId) {
        // Проверка существования пользователя
        // Вещь не может быть null так как создается в маппере
        item.setOwner(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден")));
        if (itemRequestId != null) {
            item.setRequest(itemRequestRepository.findById(itemRequestId)
                    .orElseThrow(() -> new NotFoundException("Запрос вещи с таким id не найден")));
        }
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, UpdateItemRequest updateItemRequest) {
        // Проверка существования вещи
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь"));
        // Проверка существования пользователя
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Изменять вещь может только владелец вещи");
        }
        // Проверка существования новых данных для обновления чтобы далее не было выброшено NullPointerException
        if (updateItemRequest == null) {
            throw new BadRequestException("Для обновления вещи нужно передать новые данные");
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
        return itemRepository.save(item);
    }

    @Override
    public List<ItemOutDto> getItemsByUserId(Long userId) {
        // Проверка существования пользователя
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        // Проверка, что у пользователя есть вещи
        if (items.isEmpty()) {
            throw new NotFoundException("У данного пользователя нет вещей");
        }
        // Преобразуем в itemDto так как только у них есть поля lastBooking и nextBooking
        List<ItemOutDto> itemOutDtos = items.stream()
                .map(ItemMapper::mapToItemOutDto)
                .toList();

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
        // Получаем список бронирований для каждой вещи
        Map<Long, List<Booking>> bookingsForEachItem;
        if (!bookings.isEmpty()) {
            bookingsForEachItem = bookings.stream()
                    .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        } else {
            bookingsForEachItem = new HashMap<>();
        }
        // Получаем список комментариев для каждой вещи
        Map<Long, List<Comment>> commentsForEachItem = commentRepository.findAll().stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        // Находим и присваиваем каждой вещи даты последнего и следующего бронирований
        if (!bookingsForEachItem.isEmpty()) {
            itemOutDtos.forEach(itemDto -> {
                List<Booking> bookingsForItem = bookingsForEachItem.get(itemDto.getId());

                Booking lastBooking = bookingsForItem.stream()
                        // выбираем все бронирования, которые закончились до now
                        .filter(booking -> booking.getEnd().isBefore(ZonedDateTime.now(ZoneOffset.UTC)))
                        // выбираем одно бронирование с самой поздней датой
                        .max(Comparator.comparing(Booking::getEnd))
                        .orElse(null);

                Booking nextBooking = bookingsForItem.stream()
                        // выбираем все бронирования, которые начинаются после now
                        .filter(booking -> booking.getStart().isAfter(ZonedDateTime.now(ZoneOffset.UTC)))
                        // выбираем одно бронирование с самой ранней датой
                        .min(Comparator.comparing(Booking::getStart))
                        .orElse(null);

                if (lastBooking != null) {
                    itemDto.setLastBooking(lastBooking.getEnd().toLocalDateTime());
                }
                if (nextBooking != null) {
                    itemDto.setNextBooking(nextBooking.getStart().toLocalDateTime());
                }
            });
        }
        // Присваиваем каждой вещи комментарии
        if (!commentsForEachItem.isEmpty()) {
            itemOutDtos.forEach(itemDto -> {
                List<Comment> comments = commentsForEachItem.get(itemDto.getId());
                if (comments != null && !comments.isEmpty()) {
                    itemDto.setComments(
                            comments.stream()
                                    .map(CommentMapper::mapToCommentResponseDto)
                                    .toList()
                    );
                }
            });
        }
        return itemOutDtos;
    }

    @Override
    public ItemOutDto getItemById(Long itemId) {
        // Проверка существования вещи
        ItemOutDto itemOutDto = ItemMapper.mapToItemOutDto(
                itemRepository.findById(itemId)
                        .orElseThrow(() -> new NotFoundException("Не найдена вещь"))
        );
        // Проверка существования комментариев
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        if (comments != null && !comments.isEmpty()) {
            itemOutDto.setComments(
                    comments.stream()
                            .map(CommentMapper::mapToCommentResponseDto)
                            .toList()
            );
        }

        return itemOutDto;
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        // Проверка существования вещи
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не удалось найти вещь"));
        // Проверка существования пользователя, и что он является владельцем
        if (!item.getOwner().getId().equals(userId)) {
            throw new DataAccessException("Только владелец вещи может удалить вещь");
        }
        itemRepository.delete(item);
    }

    @Override
    public List<Item> getItemsByText(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        // С помощью фильтра возвращаем только доступные вещи
        return itemRepository.findItemsByText(text).stream()
                .filter(item -> item.getAvailable() == true)
                .toList();
    }

    @Override
    public Comment createComment(Long userId, Long itemId, Comment comment) {
        // Проверка существования пользователя
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        // Проверка существования вещи
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        comment.setAuthor(author);
        comment.setItem(item);

        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        // Проверка, что у пользователя есть бронирования на эту вещь
        if (bookings.isEmpty()) {
            throw new BadRequestException("Оставлять комментарий к вещи может только пользователь, " +
                    "который брал эту вещь в аренду");
        }

        boolean hasEndBeforeNow = bookings.stream()
                .filter(b -> b.getItem().getId().equals(itemId))
                .anyMatch(b -> b.getEnd().isBefore(ZonedDateTime.now(ZoneOffset.UTC)));
        if (!hasEndBeforeNow) {
            throw new BadRequestException("Оставлять комментарий к вещи может только пользователь, " +
                    "который брал эту вещь в аренду, и аренда завершена");
        }
        return commentRepository.save(comment);
    }
}
