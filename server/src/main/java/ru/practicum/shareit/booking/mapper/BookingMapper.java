package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.ZoneId;

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
        ZoneId zoneId = ZoneId.of("UTC+0");
        return new Booking(
                null,
                bookingRequestDto.getStart().atZone(zoneId),
                bookingRequestDto.getEnd().atZone(zoneId),
                null,
                null,
                null
        );
    }
}
