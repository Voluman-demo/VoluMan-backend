package com.example.demo.Volunteer;

import com.example.demo.Model.ID;
import com.example.demo.Volunteer.Position.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, ID> {

    boolean existsByVolunteerIdAndPosition(ID volunteerId, Position position);

    boolean existsByEmail(String email);

    Volunteer getVolunteerByVolunteerId(ID volId);

    Optional<Object> findByVolunteerIdAndPosition(ID volId, Position position);
}
