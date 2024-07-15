package com.example.demo.Volunteer.Duty;

import com.example.demo.Interval.DutyInterval;
import com.example.demo.Interval.DutyIntervalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DutyService {
    private final DutyRepository dutyRepository;
    private final DutyIntervalRepository dutyIntervalRepository;

    public DutyService(DutyRepository dutyRepository, DutyIntervalRepository dutyIntervalRepository) {
        this.dutyRepository = dutyRepository;
        this.dutyIntervalRepository = dutyIntervalRepository;
    }

    public List<Duty> findByDate(LocalDate date) {
        return dutyRepository.findAllByDate(date);
    }

    public void addDuty(Duty duty) {
        dutyRepository.save(duty);
    }

    // Metoda do dodawania nowego DutyInterval
    public DutyInterval addDutyInterval(DutyInterval dutyInterval, Duty duty) {
        dutyInterval.setDuty(duty); // Ustawienie referencji do Duty
        duty.getDutyIntervals().add(dutyInterval); // Dodanie do listy interwałów w Duty
        return dutyIntervalRepository.save(dutyInterval); // Zapis do bazy danych
    }

    // Metoda do aktualizacji istniejącego DutyInterval
    public DutyInterval updateDutyInterval(DutyInterval dutyInterval) {
        Optional<DutyInterval> dutyIntervalExisted = dutyIntervalRepository.findById(dutyInterval.getIntervalId());

        if(dutyIntervalExisted.isPresent()){
            DutyInterval dutyIntervalPresent =  dutyIntervalExisted.get();
            dutyIntervalPresent.setAssign(dutyInterval.getAssign());
            return dutyIntervalRepository.save(dutyIntervalPresent);
        }
        return dutyIntervalRepository.save(dutyInterval); // Aktualizacja w bazie danych
    }
}
