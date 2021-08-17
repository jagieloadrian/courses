package com.adrian.courses.repository;

import com.adrian.courses.model.Course;
import com.adrian.courses.model.Status;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CourseRepository extends MongoRepository<Course, String> {

    List<Course> findAllByStatus(Status status);
}
