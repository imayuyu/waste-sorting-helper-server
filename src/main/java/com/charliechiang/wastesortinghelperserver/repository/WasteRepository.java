package com.charliechiang.wastesortinghelperserver.repository;

import com.charliechiang.wastesortinghelperserver.model.WasteCategory;
import com.charliechiang.wastesortinghelperserver.model.Dustbin;
import com.charliechiang.wastesortinghelperserver.model.User;
import com.charliechiang.wastesortinghelperserver.model.Waste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Repository
public interface WasteRepository extends JpaRepository<Waste, Long> {
    ArrayList<Waste> findTop5ByDustbinOrderByIdDesc(Dustbin dustbin);
    ArrayList<Waste> findByDustbinOrderByIdDesc(Dustbin dustbin);
    ArrayList<Waste> findByUserOrderByIdDesc(User user);
    ArrayList<Waste> findAllByUser(User user);
    ArrayList<Waste> findAllByUserAndTimeIsAfter(User user,LocalDateTime time);
    ArrayList<Waste> findTop20ByUserOrderByIdDesc(User user);
    ArrayList<Waste> findByCategory(WasteCategory category);
    ArrayList<Waste> findByUserAndIsCorrectlyCategorizedIsTrue(User user);
    Waste findById(long id);
}
