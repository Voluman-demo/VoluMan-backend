package com.example.demo.Volunteer.Duty.DutyInterval;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DutyIntervalRepository extends JpaRepository<DutyInterval, Long> {

}
