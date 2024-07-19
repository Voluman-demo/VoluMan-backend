package com.example.demo.action.demand;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DemandRepository extends JpaRepository<Demand, Long> {

    List<Demand> findAllByDate(LocalDate date);

    List<Demand> findAllByAction_ActionId(Long actionId);

    List<Demand> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
