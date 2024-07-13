package com.example.demo.Volunteer.Duty;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DutyService {
    private final DutyRepository dutyRepository;

    public DutyService(DutyRepository dutyRepository) {
        this.dutyRepository = dutyRepository;
    }

    public List<Duty> findByDate(LocalDate date) {
        return dutyRepository.findAllByDate(date);
    }

    public void addDuty(Duty duty) {
        dutyRepository.save(duty);
    }
}
