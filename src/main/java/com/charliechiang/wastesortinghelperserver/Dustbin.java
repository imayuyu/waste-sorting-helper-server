package com.charliechiang.wastesortinghelperserver;

import javax.persistence.*;

@Entity
@Table(name = "dustbin")
public class Dustbin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Boolean isFull;

    public Dustbin() {}

    public Dustbin(Long id, String name, Double latitude, Double longitude) {
        this.id=id;
        this.name=name;
        this.latitude=latitude;
        this.longitude=longitude;
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

    public Boolean getFull() {
        return isFull;
    }

    public void setFull(Boolean full) {
        isFull = full;
    }

}