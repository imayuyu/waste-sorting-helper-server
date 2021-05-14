package com.charliechiang.wastesortinghelperserver.repository;

import com.charliechiang.wastesortinghelperserver.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {

}
