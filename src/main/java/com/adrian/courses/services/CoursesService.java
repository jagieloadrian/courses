package com.adrian.courses.services;

import com.adrian.courses.model.Course;
import com.adrian.courses.model.CourseMember;
import com.adrian.courses.model.Status;
import com.adrian.courses.model.dto.StudentDto;

import java.util.List;

public interface CoursesService {

    List<Course> getCourses(Status status);

    Course getCourse(String code);

    Course addCourse(Course course);

    void deleteCourse(String code);

    Course putCourse(String code, Course course);

    Course patchCourse(String code, Course course);

    void addMemberToCourse(String code, Long studentId);

    List<CourseMember> getMembersFromCourse(String code);

    List<StudentDto> getStudentsFromCourse(String code);

    void courseFinishEnroll(String code);

}
