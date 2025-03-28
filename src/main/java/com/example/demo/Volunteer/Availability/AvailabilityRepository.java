package com.example.demo.Volunteer.Availability;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    Optional<Availability> findByVolunteer_VolunteerIdAndDate(Long volunteer_volunteerId, LocalDate date);
//    boolean existsByVolunteer_VolunteerId(Long volunteerId);
    List<Availability> findAllByDate(LocalDate date);
//    List<Availability> findAllByVolunteer_VolunteerId(Long volunteerId);
}
