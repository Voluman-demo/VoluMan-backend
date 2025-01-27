//package com.example.demo.Volunteer.Duty;
//
//import com.example.demo.Model.ID;
//import com.example.demo.Volunteer.Volunteer;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Repository
//public interface DutyRepository extends JpaRepository<Duty, ID> {
//    List<Duty> findAllByDate(LocalDate date);
//
//    List<Duty> findAllByVolunteerAndDateBetween(Volunteer volunteer, LocalDate startOfWeek, LocalDate endOfWeek);
//
//    List<Duty> findByVolunteer_VolunteerIdAndDateBetween(ID volunteerId, LocalDate startDate, LocalDate endDate);
//
//}
