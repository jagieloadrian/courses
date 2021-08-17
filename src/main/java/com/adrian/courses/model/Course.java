package com.adrian.courses.model;

import com.adrian.courses.exception.CourseError;
import com.adrian.courses.exception.CourseException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document
@Setter
@Getter
@Builder
public class Course {

    @Id
    private String code;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @Future
    private LocalDateTime startDate;
    @NotNull
    @Future
    private LocalDateTime endDate;
    @Min(0)
    private Long participantLimit;
    @NotNull
    @Min(0)
    private Long participantsNumber;
    @NotNull
    private Status status;

    private List<CourseMember> participantsList = new ArrayList<>();

    public void incrementParticipantsNumber(){
        participantsNumber++;
        if (participantsNumber.equals(participantLimit)) {
            setStatus(Status.F);
        }
    }

    public void isValidDate() {
        if (startDate.isAfter(endDate)) {
            throw new CourseException((CourseError.COURSE_AFTER_ENDDATE));
        }
    }

    public void isValidNumberParticipant() {
        if (participantLimit < participantsNumber) {
            throw new CourseException(CourseError.COURSE_NUMBER_OFF_LIMITS);
        }
    }

    public void isValidStatus() {
        if (Status.I.equals(status)) {
            throw new CourseException(CourseError.COURSE_INACTIVE);
        } else if (Status.F.equals(status)) {
            throw new CourseException(CourseError.COURSE_FULL);
        }
    }

    public void isValidateStatus() {
        if (Status.F.equals(status) && !participantsNumber.equals(participantLimit)) {
            throw new CourseException(CourseError.COURSE_FULL);
        }
        if (Status.A.equals(status) && participantsNumber.equals(participantLimit)) {
            throw new CourseException(CourseError.CANNOT_SET_ACTIVE_STATUS);
        }
    }
}