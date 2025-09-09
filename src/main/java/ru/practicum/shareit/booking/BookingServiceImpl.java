package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.DataAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public Booking createBooking(Long userId, Booking booking) {
        if (booking.getStart().equals(booking.getEnd())) {
            throw new BadRequestException("Начало и завершение бронирования не могут быть в одно время");
        }
        if (!booking.getItem().getAvailable()) {
            throw new BadRequestException("Нельзя забронировать вещь, которая недоступна");
        }
        booking.setStatus(Status.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBookingStatus(Long userId, Long bookingId, String approved) {
        if (approved == null || approved.isBlank()) {
            throw new BadRequestException("Статус бронирования можнт быть только " +
                    "WAITING, APPROVED, REJECTED, CANCELED, CURRENT, PAST, FUTURE или ALL");
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new DataAccessException("Менять статус бронирования может только владелец бронируемой вещи");
        }

        if (approved.equals("true")) {
            booking.setStatus(Status.APPROVED);
        } else if (approved.equals("false")) {
            booking.setStatus(Status.CANCELED);
        } else {
            throw new BadRequestException("Статус бронирования можнт быть только " +
                    "WAITING, APPROVED, REJECTED, CANCELED, CURRENT, PAST, FUTURE или ALL");
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с таким id не найдено"));

        if (!userId.equals(booking.getBooker().getId()) &&
                !userId.equals(booking.getItem().getOwner().getId())) {
            throw new DataAccessException("Получить данные о бронировании может только автор " +
                    "бронирования или владелец вещи, к которой относится бронирование");
        }
        return booking;
    }

    @Override
    public List<Booking> getAllBookingsByUser(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Status status;
        try {
            status = Status.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Статус бронирования можнт быть только " +
                    "WAITING, APPROVED, REJECTED, CANCELED, CURRENT, PAST, FUTURE или ALL");
        }
        List<Booking> bookings = bookingRepository.findAllByBookerId(userId);
        if (status.equals(Status.ALL)) {
            return bookings;
        }
        return bookings.stream()
                .filter(booking -> booking.getStatus().equals(status))
                .toList();
    }

    @Override
    public List<Booking> getAllBookingsByOwner(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Status status;
        try {
            status = Status.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Статус бронирования можнт быть только " +
                    "WAITING, APPROVED, REJECTED, CANCELED, CURRENT, PAST, FUTURE или ALL");
        }
        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(userId);
        if (status.equals(Status.ALL)) {
            return bookings;
        }
        return bookings.stream()
                .filter(booking -> booking.getStatus().equals(status))
                .toList();
    }
}
