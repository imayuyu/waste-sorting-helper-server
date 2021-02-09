package com.charliechiang.wastesortinghelperserver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface WasteRepository extends JpaRepository<Waste, Long> {
    ArrayList<Waste> findTop5ByDustbinOrderByIdDesc(Dustbin dustbin);
    ArrayList<Waste> findByDustbinOrderByIdDesc(Dustbin dustbin);
    ArrayList<Waste> findByUserOrderByIdDesc(User user);
    ArrayList<Waste> findTop20ByUserOrderByIdDesc(User user);
    ArrayList<Waste> findByCategory(WasteCategory category);
    ArrayList<Waste> findByUserAndIsCorrectlyCategorizedIsTrue(User user);
    Waste findById(long id);
}
