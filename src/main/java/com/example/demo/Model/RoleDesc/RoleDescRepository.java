package com.example.demo.Model.RoleDesc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDescRepository extends JpaRepository<RoleDesc, Long> {
}
