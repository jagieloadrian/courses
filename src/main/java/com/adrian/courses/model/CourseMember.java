package com.adrian.courses.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CourseMember {

    private String email;

    private LocalDateTime saveDate;

    public CourseMember(String email) {
        this.email = email;
        this.saveDate = LocalDateTime.now();
    }
}
