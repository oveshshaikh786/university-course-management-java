package com.university.service;

import com.university.exception.CourseNotFoundException;
import com.university.model.Course;
import com.university.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseService.class);

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course addCourse(Course course) {
        Course saved = courseRepository.save(course);
        log.info("Course added: [{}] {} ({})", saved.getId(),
                saved.getSubject().getTitle(),
                saved.getClass().getSimpleName());
        return saved;
    }

    public Course getCourseById(UUID id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Course not found: {}", id);
                    return new CourseNotFoundException(id);
                });
    }

    public List<Course> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        log.debug("getAllCourses() returned {} course(s)", courses.size());
        return courses;
    }

    public void deleteCourse(UUID id) {
        if (!courseRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent course: {}", id);
            throw new CourseNotFoundException(id);
        }
        courseRepository.deleteById(id);
        log.info("Course deleted: {}", id);
    }
}
