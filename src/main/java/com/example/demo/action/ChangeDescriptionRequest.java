package com.example.demo.action;


public record ChangeDescriptionRequest(
    Long leaderId,
    String description
){}

