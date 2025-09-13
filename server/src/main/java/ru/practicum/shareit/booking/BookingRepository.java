package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long bookerId);

    List<Booking> findAllByBookerIdAndState(Long bookerId, BookingState state);

    List<Booking> findAllByItemOwnerId(Long ownerId);

    List<Booking> findAllByItemOwnerIdAndState(Long ownerId, BookingState state);

    List<Booking> findAllByItemId(Long itemId);
}
