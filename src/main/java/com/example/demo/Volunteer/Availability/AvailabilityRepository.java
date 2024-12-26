package com.example.demo.Volunteer.Availability;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {
    Optional<Availability> findByVolunteer_VolunteerIdAndDate(Long volunteerId, LocalDate requestDate);
    boolean existsByVolunteer_VolunteerId(Long volunteerId);
    List<Availability> findAllByDate(LocalDate date);
    List<Availability> findAllByVolunteer_VolunteerId(Long volunteerId);
}
