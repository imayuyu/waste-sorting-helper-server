package com.charliechiang.wastesortinghelperserver;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface WasteRepository extends CrudRepository<Waste, Long> {
    ArrayList<Waste> findByUser(User user);
    ArrayList<Waste> findByCategory(WasteCategory category);
    Waste findById(long id);
}
