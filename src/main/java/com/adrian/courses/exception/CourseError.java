package com.adrian.courses.exception;

import org.springframework.http.HttpStatus;

public enum CourseError {

    COURSE_NOT_FOUND("This course doesn't exist", HttpStatus.NOT_FOUND),
    COURSE_CODE_EXISTS("This course code exist in database", HttpStatus.CONFLICT),
    COURSE_AFTER_ENDDATE("The Start date cannot be after end date", HttpStatus.CONFLICT),
    COURSE_INACTIVE("Course is inactive", HttpStatus.BAD_REQUEST),
    COURSE_FULL("Course is full", HttpStatus.BAD_REQUEST),
    COURSE_NUMBER_OFF_LIMITS("The number of participant is over the limit", HttpStatus.CONFLICT),
    CANNOT_SET_ACTIVE_STATUS("Course can not set active status and participant limit is equals participant number", HttpStatus.BAD_REQUEST),
    STUDENT_EXIST("Students is exist in this course", HttpStatus.CONFLICT),
    STUDENT_IS_NOT_ACTIVE("Student status is not active", HttpStatus.NOT_ACCEPTABLE),
    COURSE_IS_INACTIVE("Course is inactive, you can't change the status", HttpStatus.CONFLICT);



    private String message;
    private HttpStatus status;

    CourseError(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    HttpStatus getStatus() {
        return status;
    }
}
