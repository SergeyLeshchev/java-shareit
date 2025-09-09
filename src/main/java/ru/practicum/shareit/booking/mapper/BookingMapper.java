package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingResponseDto mapToBookingResponseDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart().toLocalDateTime(),
                booking.getEnd().toLocalDateTime(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public Booking mapToBooking(Long userId, BookingRequestDto bookingRequestDto) {
        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));
        ZoneId zoneId = ZoneId.of("UTC+0");
        return new Booking(
                null,
                bookingRequestDto.getStart().atZone(zoneId),
                bookingRequestDto.getEnd().atZone(zoneId),
                item,
                booker,
                null
        );
    }
}
