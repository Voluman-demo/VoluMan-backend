package com.example.demo.Action.ActionDto;


import com.example.demo.Model.ID;

public record ChangeDescriptionRequest(
    ID leaderId,
    String description
){}

