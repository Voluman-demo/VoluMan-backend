package com.example.demo.Action.ActionDemand;

import com.example.demo.Action.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActionDemandRepository extends JpaRepository<ActionDemand, Long> {

    List<ActionDemand> findAllByDate(LocalDate date);

    List<ActionDemand> findAllByAction_ActionId(Long actionId);

    Optional<ActionDemand> findByActionAndDate(Action action, LocalDate date);

}
