package com.charliechiang.wastesortinghelperserver;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DustbinRepository extends CrudRepository<Dustbin, Long> {
}