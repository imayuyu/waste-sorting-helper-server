package com.charliechiang.wastesortinghelperserver;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "waste")
public class Waste {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private WasteCategory category;
    private LocalDateTime time;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference
    private User user;
    private Double weight;
    private Long dustbinId;

    public Waste() {

    }

    public Waste(User user, WasteCategory category, Double weight, Long dustbinId ,LocalDateTime time) {
        this.user = user;
        this.category = category;
        this.time = time;
        this.weight=weight;
        this.dustbinId=dustbinId;
    }

    public Long getDustbinId() {
        return dustbinId;
    }

    public void setDustbinId(Long dustbinId) {
        this.dustbinId = dustbinId;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WasteCategory getCategory() {
        return category;
    }

    public void setCategory(WasteCategory category) {
        this.category = category;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
