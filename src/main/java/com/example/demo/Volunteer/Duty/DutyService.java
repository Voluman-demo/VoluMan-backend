//package com.example.demo.Volunteer.Duty;
//
//import com.example.demo.Volunteer.Duty.DutyInterval.DutyInterval;
//import com.example.demo.Volunteer.Duty.DutyInterval.DutyIntervalRepository;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class DutyService {
//    private final DutyRepository dutyRepository;
//    private final DutyIntervalRepository dutyIntervalRepository;
//
//    public DutyService(DutyRepository dutyRepository, DutyIntervalRepository dutyIntervalRepository) {
//        this.dutyRepository = dutyRepository;
//        this.dutyIntervalRepository = dutyIntervalRepository;
//    }
//
//    public List<Duty> findByDate(LocalDate date) {
//        return dutyRepository.findAllByDate(date);
//    }
//
//
//    // Metoda do dodawania nowego DutyInterval
//    public void addDutyInterval(DutyInterval dutyInterval, Duty duty) {
//        dutyInterval.setDuty(duty); // Ustawienie referencji do Duty
//        duty.getDutyIntervals().add(dutyInterval); // Dodanie do listy interwałów w Duty
//        dutyIntervalRepository.save(dutyInterval);
//    }
//
//    // Metoda do aktualizacji istniejącego DutyInterval
//    public void updateDutyInterval(DutyInterval dutyInterval) {
//        Optional<DutyInterval> dutyIntervalExisted = dutyIntervalRepository.findById(dutyInterval.getIntervalId());
//
//        if(dutyIntervalExisted.isPresent()){
//            DutyInterval dutyIntervalPresent =  dutyIntervalExisted.get();
//            dutyIntervalRepository.save(dutyIntervalPresent);
//            return;
//        }
//        dutyIntervalRepository.save(dutyInterval);
//    }
//}
