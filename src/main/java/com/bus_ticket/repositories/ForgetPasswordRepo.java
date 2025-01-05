package com.bus_ticket.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bus_ticket.entities.ForgetPassword;

public interface ForgetPasswordRepo extends JpaRepository<ForgetPassword, Integer> {
    Optional<ForgetPassword> findByPhnum(String phnum);
}