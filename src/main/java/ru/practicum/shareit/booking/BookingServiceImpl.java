package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.DataAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public Booking createBooking(Long userId, Booking booking) {
        // Существование пользователя и вещи осуществляется в маппере.
        // Существование вещи в других методах не проверяю так как не может быть создано бронирование без вещи
        if (booking.getStart().equals(booking.getEnd())) {
            throw new BadRequestException("Начало и завершение бронирования не могут быть в одно время");
        }
        if (!booking.getItem().getAvailable()) {
            throw new BadRequestException("Нельзя забронировать вещь, которая недоступна");
        }

        List<Booking> bookings = bookingRepository.findAllByItemId(booking.getItem().getId());
        if (bookings.stream()
                // Выбираем только те бронирования, которые актуальны на настоящий момент
                .filter(booking1 -> booking1.getEnd().isAfter(ZonedDateTime.now(ZoneId.of("UTC"))))
                // Проверяем пересекается ли новое бронирование с уже существующими
                .anyMatch(booking1 -> !(booking.getStart().isAfter(booking1.getEnd()) ||
                        booking.getEnd().isBefore(booking1.getStart())))) {
            throw new BadRequestException("Новое бронирование пересекается с уже существующими бронированиями");
        }
        booking.setStatus(Status.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBookingStatus(Long userId, Long bookingId, Boolean approved) {
        if (approved == null) {
            throw new BadRequestException("Чтобы изменить статус бронирования, нужно передать параметр approved " +
                    "со значением true или false");
        }
        // Проверка на существование бронирования
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        // Здесь проверка покажет, является ли пользователь владельцем вещи.
        // Если пользователь не существует, то так же будет выброшено исключение
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new DataAccessException("Менять статус бронирования может только владелец бронируемой вещи");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BadRequestException("Отклонить или подтвердить можно бронирования только в статусе WAITING");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.CANCELED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Long userId, Long bookingId) {
        // Проверка на существование бронирования
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        // Здесь проверка покажет, является ли пользователь владельцем вещи.
        // Если пользователь не существует, то так же будет выброшено исключение
        if (!userId.equals(booking.getBooker().getId()) &&
                !userId.equals(booking.getItem().getOwner().getId())) {
            throw new DataAccessException("Получить данные о бронировании может только автор " +
                    "бронирования или владелец вещи, к которой относится бронирование");
        }
        return booking;
    }

    @Override
    public List<Booking> getAllBookingsByUser(Long userId, Status status) {
        // Проверка существования пользователя
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (status.equals(Status.ALL)) {
            return bookingRepository.findAllByBookerId(userId);
        }
        return bookingRepository.findAllByBookerIdAndStatus(userId, status);
    }

    @Override
    public List<Booking> getAllBookingsByOwner(Long userId, Status status) {
        // Проверка существования пользователя
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (status.equals(Status.ALL)) {
            return bookingRepository.findAllByItemOwnerId(userId);
        }
        return bookingRepository.findAllByItemOwnerIdAndStatus(userId, status);
    }
}
