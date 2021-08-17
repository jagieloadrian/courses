package com.adrian.courses.services;

import com.adrian.courses.exception.CourseError;
import com.adrian.courses.exception.CourseException;
import com.adrian.courses.model.Course;
import com.adrian.courses.model.CourseMember;
import com.adrian.courses.model.NotificationInfoDto;
import com.adrian.courses.model.Status;
import com.adrian.courses.model.dto.StudentDto;
import com.adrian.courses.repository.CourseRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoursesServiceImpl implements CoursesService {

    public static final String EXCHANGE_ENROLL_FINISH = "enroll_finish";
    private final CourseRepository courseRepository;
    private final StudentServiceClient studentServiceClient;
    private final RabbitTemplate rabbitTemplate;

    public CoursesServiceImpl(CourseRepository courseRepository,
                              StudentServiceClient studentServiceClient, RabbitTemplate rabbitTemplate) {
        this.courseRepository = courseRepository;
        this.studentServiceClient = studentServiceClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public List<Course> getCourses(Status status) {
        if (status != null) {
            return courseRepository.findAllByStatus(status);
        } else {
            return courseRepository.findAll();
        }
    }

    @Override
    public Course getCourse(String code) {
        return courseRepository.findById(code)
                .orElseThrow(() -> new CourseException(CourseError.COURSE_NOT_FOUND));
    }

    @Override
    public Course addCourse(Course course) {
        isExistCourse(course);
        course.isValidDate();
        course.isValidateStatus();
        course.isValidNumberParticipant();
        return courseRepository.save(course);
    }

    private void isExistCourse(Course course) {
        if (courseRepository.existsById(course.getCode())) {
            throw new CourseException(CourseError.COURSE_CODE_EXISTS);
        }
    }

    @Override
    public void deleteCourse(String code) {
        Course tempCourse = courseRepository.findById(code)
                .orElseThrow(() -> new CourseException(CourseError.COURSE_NOT_FOUND));
        tempCourse.setStatus(Status.I);
        courseRepository.save(tempCourse);
    }

    @Override
    public Course putCourse(String code, Course course) {
        return courseRepository.findById(code).map(
                courseFromDB -> {
                    courseFromDB.setName(course.getName());
                    courseFromDB.setDescription(course.getDescription());
                    courseFromDB.setStartDate(course.getStartDate());
                    courseFromDB.setEndDate(course.getEndDate());
                    courseFromDB.setParticipantLimit(course.getParticipantLimit());
                    courseFromDB.setParticipantsNumber(course.getParticipantsNumber());
                    courseFromDB.setStatus(course.getStatus());
                    courseFromDB.setParticipantsList(course.getParticipantsList());
                    courseFromDB.isValidDate();
                    courseFromDB.isValidateStatus();
                    courseFromDB.isValidNumberParticipant();
                    courseRepository.save(courseFromDB);
                    return courseFromDB;
                }).orElseThrow(() -> new CourseException(CourseError.COURSE_NOT_FOUND));
    }

    @Override
    public Course patchCourse(String code, Course course) {
        return courseRepository.findById(code).map(
                courseFromDB -> {
                    if (StringUtils.hasText(course.getName())) {
                        courseFromDB.setName(course.getName());
                    }
                    if (StringUtils.hasText(course.getDescription())) {
                        courseFromDB.setDescription(course.getDescription());
                    }
                    if (StringUtils.hasText(course.getStartDate().toString())) {
                        courseFromDB.setStartDate(course.getStartDate());
                    }
                    if (StringUtils.hasText(course.getEndDate().toString())) {
                        courseFromDB.setEndDate(course.getEndDate());
                    }
                    if (StringUtils.hasText(course.getParticipantLimit().toString())) {
                        courseFromDB.setParticipantLimit(course.getParticipantLimit());
                    }
                    if (StringUtils.hasText(course.getParticipantLimit().toString())) {
                        courseFromDB.setParticipantsNumber(course.getParticipantsNumber());
                    }
                    if (StringUtils.hasText(course.getStatus().toString())) {
                        courseFromDB.setStatus(course.getStatus());
                    }
                    if (StringUtils.hasText(course.getParticipantsList().toString())) {
                        courseFromDB.setParticipantsList(course.getParticipantsList());
                    }
                    courseFromDB.isValidDate();
                    courseFromDB.isValidateStatus();
                    courseFromDB.isValidNumberParticipant();
                    courseRepository.save(courseFromDB);
                    return courseFromDB;
                }).orElseThrow(() -> new CourseException(CourseError.COURSE_NOT_FOUND));
    }

    @Override
    public void addMemberToCourse(String code, Long studentId) {
        Course course = getCourse(code);
        course.isValidStatus();
        course.isValidNumberParticipant();
        StudentDto studentDto = studentServiceClient.getStudentById(studentId);
        validateStudentBeforeCourseEnrollment(course, studentDto);
        course.incrementParticipantsNumber();
        course.isValidateStatus();
        course.getParticipantsList().add(new CourseMember(studentDto.getEmail()));
        courseRepository.save(course);
    }

    private void validateStudentBeforeCourseEnrollment(Course course, StudentDto studentDto) {
        if (!StudentDto.Status.ACTIVE.equals(studentDto.getStatus())) {
            throw new CourseException(CourseError.STUDENT_IS_NOT_ACTIVE);
        }
        if (course.getParticipantsList().stream()
                .anyMatch(courseMember -> studentDto.getEmail().equals(courseMember.getEmail()))) {
            throw new CourseException(CourseError.STUDENT_EXIST);
        }
    }

    @Override
    public List<CourseMember> getMembersFromCourse(String code) {
        Course course = getCourse(code);
        return course.getParticipantsList();
    }

    @Override
    public List<StudentDto> getStudentsFromCourse(String code) {
        List<@NotNull String> emails = getCourseMembersEmails(getCourse(code));
        return studentServiceClient.getStudentsByEmail(emails);
    }

    @Override
    public void courseFinishEnroll(String code) {
        Course course = getCourse(code);
        if (course.getStatus().equals(Status.I)) {
            throw new CourseException(CourseError.COURSE_IS_INACTIVE);
        }
        course.setStatus(Status.I);
        courseRepository.save(course);
        sendMessageToRabbit(course);
    }

    private List<@NotNull String> getCourseMembersEmails(Course course) {
        return course.getParticipantsList().stream()
                .map(CourseMember::getEmail).collect(Collectors.toList());
    }

    private void sendMessageToRabbit(Course course) {
        NotificationInfoDto notificationInfoDto = createNotificationInfo(course);
        rabbitTemplate.convertAndSend(EXCHANGE_ENROLL_FINISH, notificationInfoDto);
    }

    private NotificationInfoDto createNotificationInfo(Course course) {
        List<String> emails = getCourseMembersEmails(course);
        return NotificationInfoDto.builder()
                .code(course.getCode())
                .courseName(course.getName())
                .courseDescription(course.getDescription())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .emails(emails)
                .build();
    }
}