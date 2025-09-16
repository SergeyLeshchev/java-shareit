package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.DataAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBookingTest_whenValidBooking_shouldCreateBooking() {
        long userId = 1L;
        long itemId = 1L;
        User user = new User(1L, "userName1", "email1@email.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Booking booking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3), null, null, null);
        Booking expectedBooking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3), item, user, BookingState.WAITING);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemId(itemId)).thenReturn(List.of());
        when(bookingRepository.save(expectedBooking)).thenReturn(expectedBooking);

        Booking actualBooking = bookingService.createBooking(userId, itemId, booking);

        assertEquals(expectedBooking, actualBooking);
        verify(bookingRepository, times(1)).save(expectedBooking);
    }

    @Test
    void createBookingTest_whenBookingTimeCross_shouldThrowBadRequestException() {
        long userId = 1L;
        long itemId = 1L;
        User user = new User(1L, "userName1", "email1@email.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Booking booking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3), null, null, null);
        Booking booking2 = new Booking(2L, time.plusMinutes(1), time.plusMinutes(3), null, null, null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemId(itemId)).thenReturn(List.of(booking2));

        assertThrows(BadRequestException.class, () -> bookingService.createBooking(userId, itemId, booking));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBookingTest_whenItemNotAvailable_shouldThrowBadRequestException() {
        long userId = 1L;
        long itemId = 1L;
        User user = new User(1L, "userName1", "email1@email.com");
        Item item = new Item(1L, "itemName", "itemDescription", false, user, null);
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Booking booking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3), null, null, null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> bookingService.createBooking(userId, itemId, booking));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    // Валидация перенесена в контроллер
    /*
    @Test
    void createBookingTest_whenStartEqualEnd_shouldThrowBadRequestException() {
        long userId = 1L;
        long itemId = 1L;
        User user = new User(1L, "userName1", "email1@email.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Booking booking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(2), null, null, null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> bookingService.createBooking(userId, itemId, booking));
        verify(bookingRepository, never()).save(any(Booking.class));
    }
*/
    @Test
    void createBookingTest_whenItemNotExists_shouldThrowNotFoundException() {
        long userId = 1L;
        long itemId = 1L;
        User user = new User(1L, "userName1", "email1@email.com");
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Booking booking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3), null, null, null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(userId, itemId, booking));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBookingTest_whenUserNotExists_shouldThrowNotFoundException() {
        long userId = 1L;
        long itemId = 1L;
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Booking booking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3), null, null, null);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(userId, itemId, booking));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBookingStateTest_whenApprovedTrue_shouldSetStateApproved() {
        long userId = 1L;
        long bookingId = 1L;
        Boolean approved = true;
        User booker = new User(1L, "userName1", "email1@email.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, booker, null);
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Booking booking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3),
                item, booker, BookingState.WAITING);
        Booking expectedBooking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3),
                item, booker, BookingState.APPROVED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.save(expectedBooking)).thenReturn(expectedBooking);

        Booking actualBooking = bookingService.updateBookingState(userId, bookingId, approved);

        assertEquals(expectedBooking, actualBooking);
        verify(bookingRepository, times(1)).save(expectedBooking);
    }

    @Test
    void updateBookingStateTest_whenApprovedFalse_shouldSetStateRejected() {
        long userId = 1L;
        long bookingId = 1L;
        Boolean approved = false;
        User booker = new User(1L, "userName1", "email1@email.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, booker, null);
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Booking booking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3),
                item, booker, BookingState.WAITING);
        Booking expectedBooking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3),
                item, booker, BookingState.REJECTED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.save(expectedBooking)).thenReturn(expectedBooking);

        Booking actualBooking = bookingService.updateBookingState(userId, bookingId, approved);

        assertEquals(expectedBooking, actualBooking);
        verify(bookingRepository, times(1)).save(expectedBooking);
    }

    @Test
    void updateBookingStateTest_whenBookingStateNotWaiting_shouldThrowBadRequestException() {
        long userId = 1L;
        long bookingId = 1L;
        Boolean approved = true;
        User booker = new User(1L, "userName1", "email1@email.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, booker, null);
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Booking booking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3),
                item, booker, BookingState.REJECTED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));

        assertThrows(BadRequestException.class, () -> bookingService.updateBookingState(userId, bookingId, approved));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBookingStateTest_whenUserNotOwner_shouldThrowDataAccessException() {
        long userId = 2L;
        long bookingId = 1L;
        Boolean approved = true;
        User booker = new User(1L, "userName1", "email1@email.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, booker, null);
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Booking booking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3),
                item, booker, BookingState.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));

        assertThrows(DataAccessException.class, () -> bookingService.updateBookingState(userId, bookingId, approved));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBookingStateTest_whenUserNotExists_shouldThrowNotFoundException() {
        long userId = 1L;
        long bookingId = 1L;
        Boolean approved = true;
        User booker = new User(1L, "userName1", "email1@email.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, null, null);
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Booking booking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3),
                item, booker, BookingState.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(DataAccessException.class, () -> bookingService.updateBookingState(userId, bookingId, approved));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBookingStateTest_whenBookingNotExists_shouldThrowNotFoundException() {
        long userId = 1L;
        long bookingId = 1L;
        Boolean approved = true;

        assertThrows(NotFoundException.class, () -> bookingService.updateBookingState(userId, bookingId, approved));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getBookingByIdTest_whenValidRequest_shouldReturnBooking() {
        long userId = 1L;
        long bookingId = 1L;
        User booker = new User(1L, "userName1", "email1@email.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, booker, null);
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Booking expectedBooking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3),
                item, booker, BookingState.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));

        Booking actualBooking = bookingService.getBookingById(userId, bookingId);

        assertEquals(expectedBooking, actualBooking);
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getBookingByIdTest_whenUserNotBookerAndNotOwnerItem_shouldThrowDataAccessException() {
        long userId = 2L;
        long bookingId = 1L;
        User booker = new User(1L, "userName1", "email1@email.com");
        Item item = new Item(1L, "itemName", "itemDescription", true, booker, null);
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Booking expectedBooking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3),
                item, booker, BookingState.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));

        assertThrows(DataAccessException.class, () -> bookingService.getBookingById(userId, bookingId));
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getBookingByIdTest_whenUserNotExists_shouldThrowNotFoundException() {
        long userId = 1L;
        long bookingId = 1L;
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Booking expectedBooking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3),
                null, null, BookingState.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(userId, bookingId));
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getBookingByIdTest_whenBookingNotExists_shouldThrowNotFoundException() {
        long userId = 1L;
        long bookingId = 1L;

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(userId, bookingId));
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getAllBookingsByUserTest_whenBookingStateNotAll_shouldReturnNotAllBookings() {
        long userId = 1L;
        BookingState state = BookingState.APPROVED;
        User booker = new User(1L, "userName1", "email1@email.com");
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Booking booking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3),
                null, null, BookingState.APPROVED);
        List<Booking> expectedBookings = List.of(booking);
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndState(userId, state)).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllBookingsByUser(userId, state);

        assertEquals(expectedBookings, actualBookings);
        verify(bookingRepository, times(1)).findAllByBookerIdAndState(userId, state);
    }

    @Test
    void getAllBookingsByUserTest_whenBookingStateAll_shouldReturnAllBookings() {
        long userId = 1L;
        BookingState state = BookingState.ALL;
        User booker = new User(1L, "userName1", "email1@email.com");
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Booking booking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3),
                null, null, BookingState.APPROVED);
        Booking booking1 = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3),
                null, null, BookingState.WAITING);
        List<Booking> expectedBookings = List.of(booking, booking1);
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerId(userId)).thenReturn(List.of(booking, booking1));

        List<Booking> actualBookings = bookingService.getAllBookingsByUser(userId, state);

        assertEquals(expectedBookings, actualBookings);
        verify(bookingRepository, times(1)).findAllByBookerId(userId);
    }

    @Test
    void getAllBookingsByUserTest_whenUserNotExists_shouldThrowNotFoundException() {
        long userId = 1L;
        BookingState state = BookingState.ALL;

        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByUser(userId, state));
        verify(bookingRepository, never()).findAllByBookerId(userId);
        verify(bookingRepository, never()).findAllByBookerIdAndState(userId, state);
    }

    @Test
    void getAllBookingsByOwnerTest_whenBookingStateNotAll_shouldReturnNotAllBookings() {
        long userId = 1L;
        BookingState state = BookingState.APPROVED;
        User booker = new User(1L, "userName1", "email1@email.com");
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Booking booking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3),
                null, null, BookingState.APPROVED);
        List<Booking> expectedBookings = List.of(booking);
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByItemOwnerIdAndState(userId, state)).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllBookingsByOwner(userId, state);

        assertEquals(expectedBookings, actualBookings);
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndState(userId, state);
    }

    @Test
    void getAllBookingsByOwnerTest_whenBookingStateAll_shouldReturnAllBookings() {
        long userId = 1L;
        BookingState state = BookingState.ALL;
        User booker = new User(1L, "userName1", "email1@email.com");
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        Booking booking = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3),
                null, null, BookingState.APPROVED);
        Booking booking1 = new Booking(1L, time.plusMinutes(2), time.plusMinutes(3),
                null, null, BookingState.WAITING);
        List<Booking> expectedBookings = List.of(booking, booking1);
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByItemOwnerId(userId)).thenReturn(List.of(booking, booking1));

        List<Booking> actualBookings = bookingService.getAllBookingsByOwner(userId, state);

        assertEquals(expectedBookings, actualBookings);
        verify(bookingRepository, times(1)).findAllByItemOwnerId(userId);
    }

    @Test
    void getAllBookingsByOwnerTest_whenUserNotExists_shouldThrowNotFoundException() {
        long userId = 1L;
        BookingState state = BookingState.ALL;

        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByOwner(userId, state));
        verify(bookingRepository, never()).findAllByItemOwnerId(userId);
        verify(bookingRepository, never()).findAllByItemOwnerIdAndState(userId, state);
    }
}