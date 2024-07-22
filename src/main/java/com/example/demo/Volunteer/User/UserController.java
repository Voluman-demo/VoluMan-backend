package com.example.demo.Volunteer.User;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @PostMapping("")
    public ResponseEntity<?> logIn(@RequestBody LogInDto logInDto) {
        try{
            return userService.logIn(logInDto);
        }catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
