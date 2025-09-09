package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        Booking booking = bookingService.createBooking(userId,
                bookingMapper.mapToBooking(userId, bookingRequestDto));
        return bookingMapper.mapToBookingResponseDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable Long bookingId,
                                                  @RequestParam String approved) {
        Booking booking = bookingService.updateBookingStatus(userId, bookingId, approved);
        return bookingMapper.mapToBookingResponseDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        Booking booking = bookingService.getBookingById(userId, bookingId);
        return bookingMapper.mapToBookingResponseDto(booking);
    }

    @GetMapping()
    public List<BookingResponseDto> getAllBookingsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsByUser(userId, state)
                .stream()
                .map(bookingMapper::mapToBookingResponseDto)
                .toList();
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsByOwner(userId, state)
                .stream()
                .map(bookingMapper::mapToBookingResponseDto)
                .toList();
    }
}
