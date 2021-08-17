package com.adrian.courses.exception;

import feign.FeignException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CourseExceptionHandler {

    @ExceptionHandler(CourseException.class)
    public ResponseEntity<ErrorInfo> handleException(CourseException e){
        return ResponseEntity.status(e.getCourseError().getStatus())
                .body(new ErrorInfo(e.getCourseError().getMessage()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<?> handleFeignException(FeignException e){
        return ResponseEntity.status(e.status()).body(new JSONObject(e.contentUTF8()).toMap());

    }
}
