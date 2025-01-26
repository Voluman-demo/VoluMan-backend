package com.example.demo.Volunteer.User;

import com.example.demo.Auth.AuthDto;
import com.example.demo.Auth.AuthenticationService;
import com.example.demo.Auth.ErrorResponse;
import com.example.demo.Auth.LoginRequest;
import com.example.demo.Model.ID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    public UserController(UserRepository userRepository, UserService userService, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable ID userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @PostMapping("/login")
    public ResponseEntity<?> logIn(@RequestBody LoginRequest loginRequest) {
        if(authenticationService.authenticate(loginRequest.email(), loginRequest.password())){
            ID userId = userRepository.findUserIdByEmailAndPassword(loginRequest.email(), loginRequest.password());
            AuthDto authDto = new AuthDto(
                    userId,
                    authenticationService.createToken(userId)
            );
            return ResponseEntity.status(HttpStatus.OK).body(authDto);
        } else {
            ErrorResponse errorResponse = new ErrorResponse("Błąd logowania. Sprawdź email i hasło.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}
