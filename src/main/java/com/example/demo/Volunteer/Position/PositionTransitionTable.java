package com.example.demo.Volunteer.Position;

import lombok.Getter;

import java.util.EnumMap;
import java.util.Map;

@Getter
public class PositionTransitionTable {

    private final Map<Position, Map<Position, Boolean>> transitions;

    public PositionTransitionTable() {
        transitions = new EnumMap<>(Position.class);

        for (Position fromPosition : Position.values()) {
            transitions.put(fromPosition, new EnumMap<>(Position.class));
            for (Position toPosition : Position.values()) {
                transitions.get(fromPosition).put(toPosition, null);
            }
        }
    }

    public void setTransition(Position fromPosition, Position toPosition, boolean canTransition) {
        if (transitions.containsKey(fromPosition) && transitions.get(fromPosition).containsKey(toPosition)) {
            transitions.get(fromPosition).put(toPosition, canTransition);
        } else {
            throw new IllegalArgumentException("Invalid roles specified.");
        }
    }

    public Boolean canTransition(Position fromPosition, Position toPosition) {
        if (transitions.containsKey(fromPosition) && transitions.get(fromPosition).containsKey(toPosition)) {
            return transitions.get(fromPosition).get(toPosition);
        } else {
            throw new IllegalArgumentException("Invalid roles specified.");
        }
    }

    public void validateTable() {
        for (Map.Entry<Position, Map<Position, Boolean>> fromEntry : transitions.entrySet()) {
            for (Map.Entry<Position, Boolean> toEntry : fromEntry.getValue().entrySet()) {
                Position fromPosition = fromEntry.getKey();
                Position toPosition = toEntry.getKey();

                if (toEntry.getValue() == null) {
                    throw new IllegalStateException(
                            "Undefined transition: " + fromPosition + " → " + toPosition
                    );
                }
            }
        }
    }


}
