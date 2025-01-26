package com.example.demo.Action.Demand;

import com.example.demo.Action.Action;
import com.example.demo.Model.ID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DemandRepository extends JpaRepository<Demand, ID> {

    List<Demand> findAllByDate(LocalDate date);

    List<Demand> findAllByAction_ActionId(ID actionId);

    Optional<Demand> findByActionAndDate(Action action, LocalDate date);

    List<Demand> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
