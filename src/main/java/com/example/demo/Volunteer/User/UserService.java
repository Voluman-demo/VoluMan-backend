package com.example.demo.Volunteer.User;

import com.example.demo.Auth.AuthDto;
import com.example.demo.Config.AppException;
import com.example.demo.Volunteer.Candidate.Candidate;
import com.example.demo.Volunteer.VolunteerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final VolunteerService volunteerService;

    @Value("${app.security.salt}")
    String SALT;

    public UserService(UserRepository userRepository, VolunteerService volunteerService) {
        this.userRepository = userRepository;
        this.volunteerService = volunteerService;
    }

//    public ResponseEntity<?> logIn(LogInDto logInDto) {
//        if(userRepository.existsUserByEmailAndPassword(logInDto.email(), logInDto.password())) {
//            //TODO TOKEN
//
//
//            return ResponseEntity.ok().body("User logged in");
//        }
//        else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//    }

    public void register(Optional<Candidate> candidate) {
        if(candidate.isPresent()) {
            User user = new User();
            user.setEmail(candidate.get().getEmail());
            user.setPassword(generatePassword(candidate.get().getPhone()));
            user.setVolunteer(volunteerService.addVolunteerFromCandidate(candidate));

            if(user.getVolunteer() == null){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid email or password");
            }
            userRepository.save(user);
        }
    }

    private String generatePassword(String phone) {
        try {
            // Add the salt to the phone number
            String phoneWithSalt = SALT + phone;

            // Create a message digest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Compute the hash
            byte[] hash = digest.digest(phoneWithSalt.getBytes(StandardCharsets.UTF_8));

            // Encode the hash to Base64 to get a readable password
            String base64Hash = Base64.getEncoder().encodeToString(hash);

            // Return only 12 characters of the Base64 encoded hash
            return base64Hash.substring(2, 14);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating password", e);
        }
    }

    public boolean authenticateLogin(String email, String password) {
        return userRepository.existsByEmailAndPassword(email,password);
    }

    public AuthDto findByUserId(String userId) {
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        return new AuthDto(user.getUserId(), null);
    }
}
