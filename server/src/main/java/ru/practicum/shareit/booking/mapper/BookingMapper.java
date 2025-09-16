package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    public static BookingResponseDto mapToBookingResponseDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart().toLocalDateTime(),
                booking.getEnd().toLocalDateTime(),
                ItemMapper.mapToItemDto(booking.getItem()),
                UserMapper.mapToUserDto(booking.getBooker()),
                booking.getState()
        );
    }

    public static Booking mapToBooking(Long userId, BookingRequestDto bookingRequestDto) {
        return new Booking(
                null,
                bookingRequestDto.getStart().atZone(ZoneOffset.UTC),
                bookingRequestDto.getEnd().atZone(ZoneOffset.UTC),
                null,
                null,
                null
        );
    }
}
