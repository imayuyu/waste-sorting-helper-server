package com.charliechiang.wastesortinghelperserver;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DustbinRepository extends CrudRepository<Dustbin, Long> {
    Optional<Dustbin> findById(Long id);
}
