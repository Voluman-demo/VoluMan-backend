package com.example.demo.Volunteer.Duty;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("duties")
public class DutyController {

    private final DutyRepository dutyRepository;

    public DutyController(DutyRepository dutyRepository) {
        this.dutyRepository = dutyRepository;
    }

    @GetMapping("")
    public List<Duty> getAllDuty() {
        return dutyRepository.findAll();
    }
}
