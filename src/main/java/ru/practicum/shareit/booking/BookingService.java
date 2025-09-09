package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Long userId, Booking booking);

    Booking updateBookingStatus(Long userId, Long bookingId, String approved);

    Booking getBookingById(Long userId, Long bookingId);

    List<Booking> getAllBookingsByUser(Long userId, String state);

    List<Booking> getAllBookingsByOwner(Long userId, String state);
}
