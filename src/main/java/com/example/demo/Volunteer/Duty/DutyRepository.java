package com.example.demo.Volunteer.Duty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DutyRepository extends JpaRepository<Duty, Long> {
    List<Duty> findAllByDate(LocalDate date);
}
