package com.example.demo.Action;

import com.example.demo.Model.ID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ActionRepository extends JpaRepository<Action, ID> {

}
