package com.example.demo.Volunteer.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByEmailAndPassword(String email, String password);

    boolean existsByEmailAndPassword(String email, String password);

    @Query("SELECT u.userId FROM User u WHERE u.email = :email AND u.password = :password")
    Long findUserIdByEmailAndPassword(@Param("email") String email, @Param("password") String password);
}
