package com.charliechiang.wastesortinghelperserver.repository;

import com.charliechiang.wastesortinghelperserver.config.Schools;
import com.charliechiang.wastesortinghelperserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findById(long id);
    // TODO: delayed updating
    ArrayList<User> findAllBySchoolOrderByCreditDesc(Schools school);
    ArrayList<User> findAllByOrderByCreditDesc();
    ArrayList<User> findAll();
}