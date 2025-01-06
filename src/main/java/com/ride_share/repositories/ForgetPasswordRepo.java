package com.ride_share.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ride_share.entities.ForgetPassword;

public interface ForgetPasswordRepo extends JpaRepository<ForgetPassword, Integer> {
    Optional<ForgetPassword> findByPhnum(String phnum);
}