package com.charliechiang.wastesortinghelperserver;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface DustbinRepository extends JpaRepository<Dustbin, Long> {
    Optional<Dustbin> findById(Long id);
    Optional<Dustbin> findByName(String name);
}
