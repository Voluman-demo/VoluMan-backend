package com.example.demo.Volunteer.Position;

import com.example.demo.Volunteer.Volunteer;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class PositionService {

    private final PositionTransitionTable transitionTable;

    public PositionService() {
        this.transitionTable = new PositionTransitionTable();

        transitionTable.setTransition(Position.CANDIDATE, Position.CANDIDATE, false);
        transitionTable.setTransition(Position.CANDIDATE, Position.VOLUNTEER, true);
        transitionTable.setTransition(Position.CANDIDATE, Position.LEADER, false);
        transitionTable.setTransition(Position.CANDIDATE, Position.RECRUITER, false);
        transitionTable.setTransition(Position.CANDIDATE, Position.ADMIN, false);

        transitionTable.setTransition(Position.VOLUNTEER, Position.CANDIDATE, false);
        transitionTable.setTransition(Position.VOLUNTEER, Position.VOLUNTEER, false);
        transitionTable.setTransition(Position.VOLUNTEER, Position.LEADER, true);
        transitionTable.setTransition(Position.VOLUNTEER, Position.RECRUITER, true);
        transitionTable.setTransition(Position.VOLUNTEER, Position.ADMIN, true);

        transitionTable.setTransition(Position.LEADER, Position.CANDIDATE, false);
        transitionTable.setTransition(Position.LEADER, Position.VOLUNTEER, true);
        transitionTable.setTransition(Position.LEADER, Position.LEADER, false);
        transitionTable.setTransition(Position.LEADER, Position.RECRUITER, true);
        transitionTable.setTransition(Position.LEADER, Position.ADMIN, true);

        transitionTable.setTransition(Position.RECRUITER, Position.CANDIDATE, false);
        transitionTable.setTransition(Position.RECRUITER, Position.VOLUNTEER, true);
        transitionTable.setTransition(Position.RECRUITER, Position.LEADER, true);
        transitionTable.setTransition(Position.RECRUITER, Position.RECRUITER, false);
        transitionTable.setTransition(Position.RECRUITER, Position.ADMIN, true);

        transitionTable.setTransition(Position.ADMIN, Position.CANDIDATE, false);
        transitionTable.setTransition(Position.ADMIN, Position.VOLUNTEER, true);
        transitionTable.setTransition(Position.ADMIN, Position.LEADER, true);
        transitionTable.setTransition(Position.ADMIN, Position.RECRUITER, true);
        transitionTable.setTransition(Position.ADMIN, Position.ADMIN, false);

        validateTransitions();
    }

    public void assignRole(Volunteer volunteer, Position newPosition) {
        Position currentPosition = volunteer.getPosition();

        if (currentPosition == newPosition) {
            throw new PositionException("Volunteer already has the role: " + newPosition.toString(), currentPosition, newPosition);
        }

        Boolean canTransition = transitionTable.canTransition(currentPosition, newPosition);

        if (canTransition == null) {
            throw new PositionException("Transition undefined from " + currentPosition + " to " + newPosition, currentPosition, newPosition);
        }

        if (!canTransition) {
            throw new PositionException("Cannot transition from " + currentPosition + " to " + newPosition, currentPosition, newPosition);
        }

        volunteer.setPosition(newPosition);
    }

    public Boolean canTransition(Position fromPosition, Position toPosition) {
        return transitionTable.canTransition(fromPosition, toPosition);
    }

    private void validateTransitions() {
        try {
            transitionTable.validateTable();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Transition table validation failed: " + e.getMessage());
        }
    }

}
