package ru.practicum.shareit.booking;

public enum BookingState {
    // Все
    ALL,
    // Текущие
    CURRENT,
    // Будущие
    FUTURE,
    // Завершенные
    PAST,
    // Подтвержденные
    APPROVED,
    // Отклоненные
    REJECTED,
    // Ожидающие подтверждения
    WAITING;
}
