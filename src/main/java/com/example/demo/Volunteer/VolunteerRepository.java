package com.example.demo.Volunteer;


import com.example.demo.Volunteer.Position.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {

    boolean existsByVolunteerIdAndPosition(Long volunteerId, Position position);

    boolean existsByEmail(String email);

    Volunteer getVolunteerByVolunteerId(Long volId);

    Optional<Object> findByVolunteerIdAndPosition(Long volId, Position position);
}
