package aiss.videominer;

import aiss.videominer.controller.UserController;
import aiss.videominer.exception.ConflictException;
import aiss.videominer.exception.ResourceNotFoundException;
import aiss.videominer.model.User;
import aiss.videominer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    private User fakeUser;

    @BeforeEach
    void setUp() {
        fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setName("Test User");
        fakeUser.setUser_link("https://example.com/user1");
        fakeUser.setPicture_link("https://example.com/pic1.jpg");
    }

    @Test
    void whenGetAllUsers_thenReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(fakeUser));

        List<User> result = userController.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getId());
    }

    @Test
    void whenGetAllUsers_thenReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<User> result = userController.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenGetUserByIdExists_thenReturnUser() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(fakeUser));

        User result = userController.getUserById("user1");

        assertNotNull(result);
        assertEquals("user1", result.getId());
        assertEquals("Test User", result.getName());
    }

    @Test
    void whenGetUserByIdNotExists_thenThrowNotFoundException() {
        when(userRepository.findById("user999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userController.getUserById("user999"));
    }

    @Test
    void whenCreateUserAlreadyExists_thenThrowConflictException() {
        when(userRepository.existsById("user1")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> userController.createUser(fakeUser));
    }

    @Test
    void whenCreateUserNew_thenReturnSavedUser() {
        when(userRepository.existsById("user1")).thenReturn(false);
        when(userRepository.save(fakeUser)).thenReturn(fakeUser);

        User result = userController.createUser(fakeUser);

        assertNotNull(result);
        assertEquals("user1", result.getId());
        assertEquals("Test User", result.getName());
        verify(userRepository, times(1)).save(fakeUser);
    }

    @Test
    void whenUpdateUserNotExists_thenThrowNotFoundException() {
        when(userRepository.findById("user999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userController.updateUser("user999", fakeUser));
    }

    @Test
    void whenUpdateUserExists_thenReturnUpdatedUser() {
        User updatedDetails = new User();
        updatedDetails.setName("Updated Name");
        updatedDetails.setUser_link("https://example.com/updated");
        updatedDetails.setPicture_link("https://example.com/updated_pic.jpg");

        when(userRepository.findById("user1")).thenReturn(Optional.of(fakeUser));
        when(userRepository.save(any(User.class))).thenReturn(fakeUser);

        User result = userController.updateUser("user1", updatedDetails);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void whenDeleteUserExists_thenNoException() {
        when(userRepository.existsById("user1")).thenReturn(true);
        doNothing().when(userRepository).deleteById("user1");

        assertDoesNotThrow(() -> userController.deleteUser("user1"));
        verify(userRepository, times(1)).deleteById("user1");
    }

    @Test
    void whenDeleteUserNotExists_thenThrowNotFoundException() {
        when(userRepository.existsById("user999")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> userController.deleteUser("user999"));
    }
}
