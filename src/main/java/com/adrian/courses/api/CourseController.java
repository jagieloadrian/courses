package com.adrian.courses.api;

import com.adrian.courses.model.Course;
import com.adrian.courses.model.CourseMember;
import com.adrian.courses.model.Status;
import com.adrian.courses.model.dto.StudentDto;
import com.adrian.courses.services.CoursesService;
import com.adrian.courses.services.StudentServiceClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CoursesService coursesService;
    private final StudentServiceClient studentServiceClient;

    CourseController(CoursesService coursesService, StudentServiceClient studentServiceClient) {
        this.coursesService = coursesService;
        this.studentServiceClient = studentServiceClient;
    }

    @GetMapping
    public List<Course> getCourses(@RequestParam(required = false) Status status) {
        return coursesService.getCourses(status);
    }

    @GetMapping("/{code}")
    public Course getCourse(@PathVariable String code) {
        return coursesService.getCourse(code);
    }

    @PostMapping
    public Course addCourse(@RequestBody @Valid Course course) {
        return coursesService.addCourse(course);
    }

    @PutMapping("/{code}")
    public Course putCourse(@PathVariable String code,
                            @RequestBody @Valid Course course){
        return coursesService.putCourse(code, course);
    }

    @PatchMapping("/{code}")
    public Course patchCourse(@PathVariable String code, @RequestBody Course course){
        return coursesService.patchCourse(code, course);
    }

    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable String code){
        coursesService.deleteCourse(code);
    }

    @GetMapping("/test")
    public String testFeignClient(){
        studentServiceClient.getStudents().forEach(System.out::println);
        return "ok";
    }

    @GetMapping("/student/{id}")
    public StudentDto getStudent(@PathVariable Long id){
        return studentServiceClient.getStudentById(id);
    }

    @PostMapping("/{code}/student/{studentId}")
    public void addMemberToCourse(@PathVariable String code, @PathVariable Long studentId){
        coursesService.addMemberToCourse(code, studentId);
    }

    @GetMapping("/{code}/list")
    @ResponseStatus(HttpStatus.OK)
    public List<CourseMember> getMembersFromCourse(@PathVariable String code){
        return coursesService.getMembersFromCourse(code);
    }

    @GetMapping("/{code}/members")
    public List<StudentDto> getStudentsFromCourse(@PathVariable String code){
        return coursesService.getStudentsFromCourse(code);
    }

    @PostMapping("/{code}/finish-enroll")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> courseFinishEnroll(@PathVariable String code){
        coursesService.courseFinishEnroll(code);
        return ResponseEntity.ok().body("Wysłano poprawnie wiadomość");
    }
}
