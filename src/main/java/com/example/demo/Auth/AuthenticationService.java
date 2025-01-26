package com.example.demo.Auth;

import com.example.demo.Model.ID;

public interface AuthenticationService {
    boolean authenticate(String email, String password);
    String createToken(ID idAccount);
}
