package com.example.demo.Auth;

import com.example.demo.Config.UserAuthenticationProvider;

import com.example.demo.Volunteer.User.UserService;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final UserAuthenticationProvider userAuthenticationProvider;

    public AuthenticationServiceImpl(UserService userService, UserAuthenticationProvider userAuthenticationProvider) {
        this.userService = userService;
        this.userAuthenticationProvider = userAuthenticationProvider;
    }

    @Override
    public boolean authenticate(String email, String password) {
        return userService.authenticateLogin(email, password);
    }

    @Override
    public String createToken(Long idAccount) {
        return userAuthenticationProvider.createToken(idAccount);
    }
}
