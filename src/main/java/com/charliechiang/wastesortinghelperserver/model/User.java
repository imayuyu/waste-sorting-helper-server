package com.charliechiang.wastesortinghelperserver.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String realName;
    private String username;
    @JsonBackReference
    private String password;
    @ManyToOne
    @JoinColumn(name = "school_id", referencedColumnName = "id")
    private School school;
    private Short timeOfEnrollment;
    private Integer credit = 0;
    private Integer schoolRanking = Integer.MAX_VALUE;
    private Integer collegeRanking = Integer.MAX_VALUE;
    private LocalDateTime timeLastUpdatedCredit = LocalDateTime.of(1970, 1, 1, 1, 1);
    @JsonBackReference
    // the start date of last punished interval
    private LocalDateTime timeLastPunishedInterval = LocalDateTime.of(1970, 1, 1, 1, 1);
    @JsonBackReference
    private String openId;
    @JsonBackReference
    private String appId;
    @JsonBackReference
    private String sessionKey;
    @JsonBackReference
    private String unionId;
    @JsonBackReference
    private Boolean needFullCreditUpdate = false;
    @JsonBackReference
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    public User() {

    }


    public User(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return this.roles
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
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

    public Boolean getNeedFullCreditUpdate() {
        return needFullCreditUpdate;
    }

    public void setNeedFullCreditUpdate(Boolean needFullCreditUpdate) {
        this.needFullCreditUpdate = needFullCreditUpdate;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
