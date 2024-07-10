package com.example.demo.Volunteer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {


    List<Volunteer> findAllByRole(VolunteerRole role);
    Optional<Volunteer> findByVolunteerIdAndRole(Long volunteerId, VolunteerRole role);
    boolean existsByVolunteerIdAndRole(Long volunteerId, VolunteerRole role);



}
