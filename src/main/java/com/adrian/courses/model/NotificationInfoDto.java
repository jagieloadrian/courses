package com.adrian.courses.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
public class NotificationInfoDto {

    @NotBlank
    private List<String> emails;
    @NotBlank
    private String code;
    @NotBlank
    private String courseName;
    @NotBlank
    private String courseDescription;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;
}
