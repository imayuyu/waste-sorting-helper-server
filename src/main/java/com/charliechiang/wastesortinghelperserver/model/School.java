package com.charliechiang.wastesortinghelperserver.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "school")
public class School {
    @Id
    private Long id;
    private String name;
    private String ename;
    private Integer studentCount;

    public School() {

    }

    public School(Long id,
                  String name,
                  String ename,
                  Integer studentCount){
        this.id=id;
        this.name=name;
        this.ename=ename;
        this.studentCount=studentCount;
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

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }
}
