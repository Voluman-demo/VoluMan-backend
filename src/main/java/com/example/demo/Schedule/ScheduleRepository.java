package com.example.demo.Schedule;

import com.example.demo.Model.ID;
import com.example.demo.Model.ID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, ID> {

}
