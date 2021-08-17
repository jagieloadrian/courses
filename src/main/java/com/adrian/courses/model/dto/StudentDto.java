package com.adrian.courses.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@ToString
@EqualsAndHashCode
public class StudentDto {

    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String email;
    @NotNull
    private Status status;

    public enum Status {
        ACTIVE, INACTIVE
    }
}
