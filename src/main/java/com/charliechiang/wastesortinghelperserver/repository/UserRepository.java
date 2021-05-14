package com.charliechiang.wastesortinghelperserver.repository;

import com.charliechiang.wastesortinghelperserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // TODO: delayed updating
    ArrayList<User> findAllByOrderByCreditDesc();

    ArrayList<User> findAll();

    Optional<User> findByUsername(String username);
}