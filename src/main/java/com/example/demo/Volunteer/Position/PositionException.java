package com.example.demo.Volunteer.Position;

import lombok.Getter;

@Getter
public class PositionException extends RuntimeException {
    private final Position fromPosition;
    private final Position toPosition;

    public PositionException(String message, Position fromPosition, Position toPosition) {
        super(message);
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
    }

}
