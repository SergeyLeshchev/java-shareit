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

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        Booking booking = bookingService.createBooking(userId, bookingRequestDto.getItemId(),
                BookingMapper.mapToBooking(userId, bookingRequestDto));
        return BookingMapper.mapToBookingResponseDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable Long bookingId,
                                                  @RequestParam Boolean approved) {
        Booking booking = bookingService.updateBookingStatus(userId, bookingId, approved);
        return BookingMapper.mapToBookingResponseDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        Booking booking = bookingService.getBookingById(userId, bookingId);
        return BookingMapper.mapToBookingResponseDto(booking);
    }

    @GetMapping()
    public List<BookingResponseDto> getAllBookingsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(name = "state",
                                                                 defaultValue = "ALL") Status status) {
        return bookingService.getAllBookingsByUser(userId, status)
                .stream()
                .map(BookingMapper::mapToBookingResponseDto)
                .toList();
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @RequestParam(name = "state",
                                                                  defaultValue = "ALL") Status status) {
        return bookingService.getAllBookingsByOwner(userId, status)
                .stream()
                .map(BookingMapper::mapToBookingResponseDto)
                .toList();
    }
}
