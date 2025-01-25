package com.example.demo.Volunteer;

import com.example.demo.Model.ID;
import com.example.demo.Volunteer.Position.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, ID> {

    boolean existsByIdAndPosition(ID volunteerId, Position position);

    boolean existsByEmail(String email);

    Volunteer getVolunteerById(ID volId);

}
