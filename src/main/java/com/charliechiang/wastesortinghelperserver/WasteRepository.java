package com.charliechiang.wastesortinghelperserver;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface WasteRepository extends CrudRepository<Waste, Long> {
    ArrayList<Waste> findTop5ByDustbinOrderByIdDesc(Dustbin dustbin);
    ArrayList<Waste> findByUserOrderByIdDesc(User user);
    ArrayList<Waste> findTop20ByUserOrderByIdDesc(User user);
    ArrayList<Waste> findByCategory(WasteCategory category);
    Waste findById(long id);
}
