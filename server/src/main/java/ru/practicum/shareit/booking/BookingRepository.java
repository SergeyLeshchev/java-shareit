package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Если state = ALL
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    // Если state = APPROVED, WAITING, REJECTED
    List<Booking> findAllByBookerIdAndStateOrderByStartDesc(Long bookerId, BookingState state);

    // Если state = PAST
    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, ZonedDateTime endBefore);

    // Если state = CURRENT
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, ZonedDateTime startBefore,
                                                                             ZonedDateTime endAfter);

    // Если state = FUTURE
    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, ZonedDateTime startAfter);

    // Если state = ALL
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    // Если state = APPROVED, WAITING, REJECTED
    List<Booking> findAllByItemOwnerIdAndStateOrderByStartDesc(Long ownerId, BookingState state);

    // Если state = PAST
    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long userId, ZonedDateTime endBefore);

    // Если state = CURRENT
    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, ZonedDateTime startBefore,
                                                                                ZonedDateTime endAfter);

    // Если state = FUTURE
    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long userId, ZonedDateTime startAfter);

    // Вспомогательный для других методов
    List<Booking> findAllByItemIdOrderByStartDesc(Long itemId);
}
