package com.charliechiang.wastesortinghelperserver.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
public class User {
    @Id
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "school_id", referencedColumnName = "id")
    private School school;
    private Short timeOfEnrollment;
    private Integer credit = 0;
    private Integer schoolRanking = Integer.MAX_VALUE;
    private Integer collegeRanking = Integer.MAX_VALUE;
    private LocalDateTime timeLastUpdatedCredit = LocalDateTime.of(1970,1,1,1,1);
    @JsonBackReference
    // the start date of last punished interval
    private LocalDateTime timeLastPunishedInterval = LocalDateTime.of(1970,1,1,1,1);
    @JsonBackReference
    private String openId;
    @JsonBackReference
    private String appId;
    @JsonBackReference
    private String sessionKey;
    @JsonBackReference
    private String unionId;

    public User() {

    }


    public User(Long id, String name) {
        this.id = id;
        this.name = name;
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

    public int getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public Short getTimeOfEnrollment() {
        return timeOfEnrollment;
    }

    public void setTimeOfEnrollment(Short timeOfEnrollment) {
        this.timeOfEnrollment = timeOfEnrollment;
    }

    public Integer getSchoolRanking() {
        return schoolRanking;
    }

    public void setSchoolRanking(Integer schoolRanking) {
        this.schoolRanking = schoolRanking;
    }

    public Integer getCollegeRanking() {
        return collegeRanking;
    }

    public void setCollegeRanking(Integer collegeRanking) {
        this.collegeRanking = collegeRanking;
    }

    public LocalDateTime getTimeLastUpdatedCredit() {
        return timeLastUpdatedCredit;
    }

    public void setTimeLastUpdatedCredit(LocalDateTime timeLastUpdatedCredit) {
        this.timeLastUpdatedCredit = timeLastUpdatedCredit;
    }

    public LocalDateTime getTimeLastPunishedInterval() {
        return timeLastPunishedInterval;
    }

    public void setTimeLastPunishedInterval(LocalDateTime timeLastPunishedInterval) {
        this.timeLastPunishedInterval = timeLastPunishedInterval;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }
}
