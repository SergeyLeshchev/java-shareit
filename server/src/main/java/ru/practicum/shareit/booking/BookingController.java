package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody BookingRequestDto bookingRequestDto) {
        Booking booking = bookingService.createBooking(userId, bookingRequestDto.getItemId(),
                BookingMapper.mapToBooking(userId, bookingRequestDto));
        return BookingMapper.mapToBookingResponseDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBookingState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam Boolean approved) {
        Booking booking = bookingService.updateBookingState(userId, bookingId, approved);
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
                                                         @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getAllBookingsByUser(userId, state)
                .stream()
                .map(BookingMapper::mapToBookingResponseDto)
                .toList();
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getAllBookingsByOwner(userId, state)
                .stream()
                .map(BookingMapper::mapToBookingResponseDto)
                .toList();
    }
}
