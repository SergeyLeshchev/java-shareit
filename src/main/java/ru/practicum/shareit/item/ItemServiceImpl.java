package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final CommentMapper commentMapper;

    @Override
    public Item createItem(Item item, Long userId) {
        if (!userRepository.findAll().stream()
                .map(User::getId)
                .toList().contains(userId)) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        if (item.getOwner() == null) {
            item.setOwner(userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден")));
        }
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, UpdateItemRequest updateItemRequest) {
        System.out.println(userId);
        System.out.println(itemId);
        System.out.println(updateItemRequest.toString());
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь"));
        System.out.println(item.toString());
        if (!item.getOwner().getId().equals(userId)) {
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
        return itemRepository.save(item);
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        // Преобразуем в itemDto так как только у них есть поля lastBooking и nextBooking
        List<ItemDto> itemDtos = items.stream()
                .map(ItemMapper::mapToItemDto)
                .toList();

        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(userId);
        // Получаем список бронирований для каждой вещи
        Map<Long, List<Booking>> bookingsForEachItem = bookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        // Получаем список комментариев для каждой вещи
        Map<Long, List<Comment>> commentsForEachItem = commentRepository.findAll().stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        // Находим и присваиваем каждой вещи даты последнего и следующего бронирований
        if (!bookingsForEachItem.isEmpty()) {
            itemDtos.forEach(itemDto -> {
                List<Booking> bookingsForItem = bookingsForEachItem.get(itemDto.getId());

                Booking lastBooking = bookingsForItem.stream()
                        // выбираем все бронирования, которые закончились до now
                        .filter(booking -> booking.getEnd().isBefore(ZonedDateTime.now()))
                        // выбираем одно бронирование с самой поздней датой
                        .max(Comparator.comparing(Booking::getStart))
                        .orElse(null);

                Booking nextBooking = bookingsForItem.stream()
                        // выбираем все бронирования, которые начинаются после now
                        .filter(booking -> booking.getStart().isAfter(ZonedDateTime.now()))
                        // выбираем одно бронирование с самой ранней датой
                        .min(Comparator.comparing(Booking::getStart))
                        .orElse(null);

                if (lastBooking != null) {
                    itemDto.setLastBooking(lastBooking.getEnd().toLocalDateTime());
                }
                if (nextBooking != null) {
                    itemDto.setLastBooking(nextBooking.getStart().toLocalDateTime());
                }
            });
        }
        // Присваиваем каждой вещи комментарии
        if (!commentsForEachItem.isEmpty()) {
            itemDtos.forEach(itemDto -> {
                List<Comment> comments = commentsForEachItem.get(itemDto.getId());
                if (comments != null && !comments.isEmpty()) {
                    itemDto.setComments(
                            comments.stream()
                                    .map(commentMapper::mapToCommentDto)
                                    .toList()
                    );
                }
            });
        }
        return itemDtos;
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        ItemDto itemDto = ItemMapper.mapToItemDto(
                itemRepository.findById(itemId)
                        .orElseThrow(() -> new NotFoundException("Не найдена вещь"))
        );
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        if (comments != null && !comments.isEmpty()) {
            itemDto.setComments(
                    comments.stream()
                            .map(commentMapper::mapToCommentDto)
                            .toList()
            );
        }

        return itemDto;
    }

    @Override
    public void deleteItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не удалось найти вещь"));
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
        List<Booking> bookings = bookingRepository.findAllByBookerId(userId);
        if (bookings.isEmpty()) {
            throw new BadRequestException("Оставлять комментарий к вещи может только пользователь, " +
                    "который брал эту вещь в аренду");
        }
        if (bookings.stream()
                // Выбираем все бронирования, которые создавал данный пользователь на данную вещь
                .filter(booking -> booking.getItem().getId().equals(itemId))
                // Если все бронирования данной вещи заканчиваются позже чем сейчас, то пользователь не может
                // оставить комментарий
                .allMatch(booking ->
                        booking.getEnd().isAfter(ZonedDateTime.now(ZoneId.of("UTC")).plusHours(2)))) {
            throw new BadRequestException("Оставлять комментарий к вещи может только пользователь, " +
                    "который брал эту вещь в аренду, и аренда завершена");
        }
        return commentRepository.save(comment);
    }
}
