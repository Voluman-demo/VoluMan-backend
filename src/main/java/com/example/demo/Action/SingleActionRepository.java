package com.example.demo.Action;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SingleActionRepository extends JpaRepository<SingleAction, Long> {
}