package com.example.demo.Volunteer.User;

import com.example.demo.Auth.AuthDto;
import com.example.demo.Auth.AuthenticationService;
import com.example.demo.Auth.ErrorResponse;
import com.example.demo.Auth.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "Endpoints for managing users and authentication")
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
    @Operation(summary = "Get all users", description = "Retrieves a list of all registered users.")
    @ApiResponse(responseCode = "200", description = "List of users retrieved successfully")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public User getUser(
            @Parameter(description = "ID of the user to be retrieved", example = "1") @PathVariable long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user based on their email and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid email or password",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> logIn(
            @Parameter(description = "Login credentials of the user", required = true) @RequestBody LoginRequest loginRequest) {
        if (authenticationService.authenticate(loginRequest.email(), loginRequest.password())) {
            Long userId = userRepository.findUserIdByEmailAndPassword(loginRequest.email(), loginRequest.password());
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
