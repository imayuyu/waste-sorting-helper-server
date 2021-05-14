package com.charliechiang.wastesortinghelperserver.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Dustbin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Boolean isHazardousWasteFull;
    private Boolean isRecyclableWasteFull;
    private Boolean isFoodWasteFull;
    private Boolean isResidualWasteFull;


    public Dustbin() {
    }

    public Dustbin(String name, Double latitude, Double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getHazardousWasteFull() {
        return isHazardousWasteFull;
    }

    public void setHazardousWasteFull(Boolean hazardousWasteFull) {
        isHazardousWasteFull = hazardousWasteFull;
    }

    public Boolean getRecyclableWasteFull() {
        return isRecyclableWasteFull;
    }

    public void setRecyclableWasteFull(Boolean recyclableWasteFull) {
        isRecyclableWasteFull = recyclableWasteFull;
    }

    public Boolean getFoodWasteFull() {
        return isFoodWasteFull;
    }

    public void setFoodWasteFull(Boolean foodWasteFull) {
        isFoodWasteFull = foodWasteFull;
    }

    public Boolean getResidualWasteFull() {
        return isResidualWasteFull;
    }

    public void setResidualWasteFull(Boolean residualWasteFull) {
        isResidualWasteFull = residualWasteFull;
    }
}
