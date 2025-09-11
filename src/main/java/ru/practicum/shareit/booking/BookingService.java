package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Long userId, Booking booking);

    Booking updateBookingStatus(Long userId, Long bookingId, Boolean approved);

    Booking getBookingById(Long userId, Long bookingId);

    List<Booking> getAllBookingsByUser(Long userId, Status state);

    List<Booking> getAllBookingsByOwner(Long userId, Status state);
}
