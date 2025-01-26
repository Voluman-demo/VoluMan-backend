package com.example.demo.Auth;


import com.example.demo.Model.ID;

public record AuthDto(
        ID idUser,
        String token
){}