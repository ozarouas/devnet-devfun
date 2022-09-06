package com.imizsoft.backendsecurity.payload;

import java.time.LocalDate;

public class ProfileRequest {

    private String id;
    private String firstname;
    private String lastname;
    private LocalDate birthdate;
    private String job;

    public ProfileRequest(){}

    public ProfileRequest(String id, String firstname, String lastname, LocalDate birthdate, String job) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthdate = birthdate;
        this.job = job;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

}
