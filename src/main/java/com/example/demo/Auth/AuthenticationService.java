package com.example.demo.Auth;



public interface AuthenticationService {
    boolean authenticate(String email, String password);
    String createToken(Long idAccount);
}
