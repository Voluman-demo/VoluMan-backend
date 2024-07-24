package com.example.demo.Auth;


public record LoginRequest(
        Long userId,
        String password
){}
