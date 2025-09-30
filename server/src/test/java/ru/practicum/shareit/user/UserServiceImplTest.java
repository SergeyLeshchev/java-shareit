package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUserTest_whenUserValid_shouldReturnUser() {
        User userToSave = new User();
        when(userRepository.save(userToSave)).thenReturn(userToSave);

        User actualUser = userService.createUser(userToSave);

        assertEquals(userToSave, actualUser);
        verify(userRepository).save(userToSave);
    }

    @Test
    void createUserTest_whenUserNotValid_shouldThrowDuplicatedDataException() {
        User user = new User();
        user.setEmail("email@email.com");
        User userDuplicated = new User();
        userDuplicated.setEmail("email@email.com");
        when(userRepository.findAll()).thenReturn(List.of(user));

        assertThrows(DuplicatedDataException.class, () -> userService.createUser(userDuplicated));
        verify(userRepository, never()).save(userDuplicated);
    }

    // Два теста для проверки обновления полей, чтобы убедиться, что второе поле не обновляется
    // Если бы было 10 полей, то было бы так же 2 метода, в каждом методе проверялось,
    // что 5 полей обновляются, а 5 других не обновляются
    @Test
    void updateUserTest_whenUpdateName_shouldUpdateName() {
        User oldUser = new User(
                1L,
                "oldName",
                "oldEmail@email.com"
        );
        UpdateUserRequest newUser = new UpdateUserRequest("newName", null);
        User updatedUser = new User(
                1L,
                "newName",
                "oldEmail@email.com"
        );
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        when(userRepository.findById(oldUser.getId())).thenReturn(Optional.of(oldUser));

        User actualUser = userService.updateUser(oldUser.getId(), newUser);

        assertEquals(updatedUser, actualUser);
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    void updateUserTest_whenUpdateEmail_shouldUpdateEmail() {
        User oldUser = new User(
                1L,
                "oldName",
                "oldEmail@email.com"
        );
        UpdateUserRequest newUser = new UpdateUserRequest(null, "newEmail@email.com");
        User updatedUser = new User(
                1L,
                "oldName",
                "newEmail@email.com"
        );
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        when(userRepository.findById(oldUser.getId())).thenReturn(Optional.of(oldUser));

        User actualUser = userService.updateUser(oldUser.getId(), newUser);

        assertEquals(updatedUser, actualUser);
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    void updateUserTest_whenUserNotExist_shouldThrowNotFoundException() {
        long userId = 1L;
        UpdateUserRequest newUser = new UpdateUserRequest();
        User updatedUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, newUser));
        verify(userRepository, never()).save(updatedUser);
    }

    @Test
    void updateUserTest_whenUpdateDuplicateEmail_shouldThrowDuplicateDataException() {
        User oldUser = new User(
                1L,
                "oldName",
                "oldEmail@email.com"
        );
        User duplicateUser = new User(
                2L,
                "oldName",
                "oldEmail@email.com"
        );
        UpdateUserRequest newUser = new UpdateUserRequest("newName", "oldEmail@email.com");
        User updatedUser = new User();
        when(userRepository.findById(oldUser.getId())).thenReturn(Optional.of(oldUser));
        when(userRepository.findAll()).thenReturn(List.of(oldUser, duplicateUser));

        assertThrows(DuplicatedDataException.class, () -> userService.updateUser(oldUser.getId(), newUser));
        verify(userRepository, never()).save(updatedUser);
    }

    @Test
    void getAllUsersTest_whenUsersExist_shouldReturnNotEmptyList() {
        User user = new User();
        List<User> expectedList = List.of(user);
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> actualList = userService.getAllUsers();

        assertEquals(expectedList, actualList);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserByIdTest_whenUserFound_shouldReturnUser() {
        long userId = 1L;
        User expectedUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User userActual = userService.getUserById(userId);

        assertEquals(expectedUser, userActual);
    }

    @Test
    void getUserByIdTest_whenUserNotFound_shouldTrowNewNotFoundException() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void deleteUserTest_whenDeleteUser_shouldDeleteUser() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.deleteUser(user.getId());

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUserTest_whenUserNotExist_shouldTrowNewNotFoundException() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(user.getId()));
        verify(userRepository, never()).delete(user);
    }
}