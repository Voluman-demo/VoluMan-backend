package com.example.demo.Volunteer.User;

import com.example.demo.Auth.AuthDto;
import com.example.demo.Config.AppException;
import com.example.demo.Volunteer.Candidate.Candidate;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VolunteerService volunteerService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Set the salt manually for testing purposes
        userService.SALT = "testSalt";
    }

    @Test
    void testRegister_Success() {
        // Given
        Candidate candidate = new Candidate();
        candidate.setEmail("test@example.com");
        candidate.setPhone("123456789");
        when(volunteerService.addVolunteerFromCandidate(Optional.of(candidate))).thenReturn(new Volunteer());

        // When
        userService.register(Optional.of(candidate));

        // Then
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_Failure() {
        // Given
        Candidate candidate = new Candidate();
        candidate.setEmail("test@example.com");
        candidate.setPhone("123456789");
        when(volunteerService.addVolunteerFromCandidate(Optional.of(candidate))).thenReturn(null);

        // When & Then
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> userService.register(Optional.of(candidate)));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatusCode());
        assertEquals("Invalid email or password", thrown.getReason());
    }

    @Test
    void testGeneratePassword() throws NoSuchAlgorithmException {
        // Given
        String phone = "123456789";
        String salt = "testSalt";
        String expectedHash = generateExpectedHash(phone, salt);

        // When
        String generatedPassword = userService.generatePassword(phone);

        // Then
        assertEquals(expectedHash, generatedPassword);
    }

    private String generateExpectedHash(String phone, String salt) throws NoSuchAlgorithmException {
        String phoneWithSalt = salt + phone;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(phoneWithSalt.getBytes(StandardCharsets.UTF_8));
        String base64Hash = Base64.getEncoder().encodeToString(hash);
        return base64Hash.substring(2, 14);
    }

    @Test
    void testAuthenticateLogin_Success() {
        // Given
        String email = "test@example.com";
        String password = "password";
        when(userRepository.existsByEmailAndPassword(email, password)).thenReturn(true);

        // When
        boolean result = userService.authenticateLogin(email, password);

        // Then
        assertTrue(result);
    }

    @Test
    void testAuthenticateLogin_Failure() {
        // Given
        String email = "test@example.com";
        String password = "password";
        when(userRepository.existsByEmailAndPassword(email, password)).thenReturn(false);

        // When
        boolean result = userService.authenticateLogin(email, password);

        // Then
        assertFalse(result);
    }

    @Test
    void testFindByUserId_Success() {
        // Given
        User user = new User();
        user.setUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        AuthDto result = userService.findByUserId("1");

        // Then
        assertNotNull(result);
        assertEquals(1L, result.idUser());
        assertNull(result.token());
    }

    @Test
    void testFindByUserId_Failure() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        AppException thrown = assertThrows(AppException.class, () -> userService.findByUserId("1"));
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
        assertEquals("Unknown user", thrown.getMessage());
    }
}
