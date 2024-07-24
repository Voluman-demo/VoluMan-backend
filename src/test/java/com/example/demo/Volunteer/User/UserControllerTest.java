package com.example.demo.Volunteer.User;

import com.example.demo.Auth.AuthDto;
import com.example.demo.Auth.AuthenticationService;
import com.example.demo.Auth.ErrorResponse;
import com.example.demo.Auth.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUsers() {
        // Given
        List<User> users = List.of(new User());
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userController.getUsers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUser() {
        // Given
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        User result = userController.getUser(1L);

        // Then
        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testLogIn_ReturnsOk_WhenSuccess() {
        // Given
        String email = "test@example.com";
        String password = "password";
        LoginRequest loginRequest = new LoginRequest(email, password);
        when(authenticationService.authenticate(email, password)).thenReturn(true);
        when(userRepository.findUserIdByEmailAndPassword(email, password)).thenReturn(1L);
        when(authenticationService.createToken(1L)).thenReturn("testToken");

        // When
        ResponseEntity<?> responseEntity = userController.logIn(loginRequest);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertInstanceOf(AuthDto.class, responseEntity.getBody());
        AuthDto authDto = (AuthDto) responseEntity.getBody();
        assertEquals(1L, authDto.idUser());
        assertEquals("testToken", authDto.token());
    }

    @Test
    void testLogIn_ReturnsUnauthorized_WhenAuthFailure() {
        // Given
        String email = "test@example.com";
        String password = "wrongpassword";
        LoginRequest loginRequest = new LoginRequest(email, password);
        when(authenticationService.authenticate(email, password)).thenReturn(false);

        // When
        ResponseEntity<?> responseEntity = userController.logIn(loginRequest);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertInstanceOf(ErrorResponse.class, responseEntity.getBody());
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertEquals("Błąd logowania. Sprawdź email i hasło.", errorResponse.error());
    }
}