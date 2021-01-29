package com.charliechiang.wastesortinghelperserver;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "waste")
public class Waste {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private WasteCategory category;
    private LocalDateTime time;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference
    private User user;
    private Double weight;
    @ManyToOne
    @JoinColumn(name = "dustbin_id", referencedColumnName = "id")
    private Dustbin dustbin;

    public Boolean getCorrectlyCategorized() {
        return isCorrectlyCategorized;
    }

    public void setCorrectlyCategorized(Boolean correctlyCategorized) {
        isCorrectlyCategorized = correctlyCategorized;
    }

    private Boolean isCorrectlyCategorized;

    public Waste() {

    }

    public Waste(User user, WasteCategory category, Double weight, Dustbin dustbin ,LocalDateTime time) {
        this.user = user;
        this.category = category;
        this.time = time;
        this.weight=weight;
        this.dustbin=dustbin;
        this.isCorrectlyCategorized=true;
    }

    public Dustbin getDustbin() {
        return dustbin;
    }

    public void setDustbin(Dustbin dustbin) {
        this.dustbin = dustbin;
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
