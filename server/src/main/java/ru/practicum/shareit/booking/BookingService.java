package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Long userId, Long itemId, Booking booking);

    Booking updateBookingState(Long userId, Long bookingId, Boolean approved);

    Booking getBookingById(Long userId, Long bookingId);

    List<Booking> getAllBookingsByUser(Long userId, BookingState state);

    List<Booking> getAllBookingsByOwner(Long userId, BookingState state);
}
