package com.pavelitelprojects.tutorbot.repository;

import com.pavelitelprojects.tutorbot.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
}
