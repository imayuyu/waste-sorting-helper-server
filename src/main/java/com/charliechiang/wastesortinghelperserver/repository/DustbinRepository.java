package com.charliechiang.wastesortinghelperserver.repository;

import com.charliechiang.wastesortinghelperserver.model.Dustbin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DustbinRepository extends JpaRepository<Dustbin, Long> {

    Optional<Dustbin> findById(Long id);

    Optional<Dustbin> findByName(String name);
}
