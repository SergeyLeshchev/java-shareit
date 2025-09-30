package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.DataAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Booking createBooking(Long userId, Long itemId, Booking booking) {
        // Проверка на существование пользователя
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));
        // Проверка на существование вещи
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        booking.setBooker(booker);
        booking.setItem(item);
        if (!item.getAvailable()) {
            throw new BadRequestException("Нельзя забронировать вещь, которая недоступна");
        }

        List<Booking> bookings = bookingRepository.findAllByItemIdOrderByStartDesc(item.getId());
        if (bookings.stream()
                // Выбираем только те бронирования, которые актуальны на настоящий момент
                .filter(b -> b.getEnd().isAfter(ZonedDateTime.now(ZoneOffset.UTC)))
                // Проверяем пересекается ли новое бронирование с уже существующими
                .anyMatch(b -> !(booking.getStart().isAfter(b.getEnd()) ||
                        booking.getEnd().isBefore(b.getStart())))) {
            throw new BadRequestException("Новое бронирование пересекается с уже существующими бронированиями");
        }
        booking.setState(BookingState.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBookingState(Long userId, Long bookingId, Boolean approved) {
        // Проверка на существование бронирования
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        // Проверка на существование пользователя
        // Тесты в Postman хотят чтобы код ответа в этом случае был 403
        userRepository.findById(userId)
                .orElseThrow(() -> new DataAccessException("Пользователь с таким id не найден"));
        // Здесь проверка покажет, является ли пользователь владельцем вещи.
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new DataAccessException("Менять статус бронирования может только владелец бронируемой вещи");
        }
        if (!booking.getState().equals(BookingState.WAITING)) {
            throw new BadRequestException("Отклонить или подтвердить можно бронирования только в статусе WAITING");
        }
        if (approved) {
            booking.setState(BookingState.APPROVED);
        } else {
            booking.setState(BookingState.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Long userId, Long bookingId) {
        // Проверка на существование бронирования
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        // Проверка на существование пользователя
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));
        // Здесь проверка покажет, является ли пользователь владельцем вещи.
        if (!userId.equals(booking.getBooker().getId()) &&
                !userId.equals(booking.getItem().getOwner().getId())) {
            throw new DataAccessException("Получить данные о бронировании может только автор " +
                    "бронирования или владелец вещи, к которой относится бронирование");
        }
        return booking;
    }

    @Override
    public List<Booking> getAllBookingsByUser(Long userId, BookingState state) {
        // Проверка существования пользователя
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        List<Booking> bookings = switch (state) {
            case APPROVED, WAITING, REJECTED ->
                    bookingRepository.findAllByBookerIdAndStateOrderByStartDesc(userId, state);
            case PAST -> bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case CURRENT ->
                    bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case FUTURE -> bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        };
        return bookings;
    }

    @Override
    public List<Booking> getAllBookingsByOwner(Long userId, BookingState state) {
        // Проверка существования пользователя
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        List<Booking> bookings = switch (state) {
            case APPROVED, WAITING, REJECTED ->
                    bookingRepository.findAllByItemOwnerIdAndStateOrderByStartDesc(userId, state);
            case PAST -> bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
            case CURRENT ->
                    bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case FUTURE -> bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
            case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
        };
        return bookings;
    }
}
