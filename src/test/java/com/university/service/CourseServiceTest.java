package com.university.service;

import com.university.exception.CourseNotFoundException;
import com.university.model.ClassroomCourse;
import com.university.model.Course;
import com.university.model.Instructor;
import com.university.model.Subject;
import com.university.repository.impl.InMemoryCourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CourseServiceTest {

    private CourseService courseService;
    private Course javaCourse;

    @BeforeEach
    void setUp() {
        courseService = new CourseService(new InMemoryCourseRepository());
        Instructor instructor = new Instructor("Mark", "mark@university.com");
        javaCourse = new ClassroomCourse(new Subject("Java", 4), instructor, 1000, "Cambridge", "Winter");
        courseService.addCourse(javaCourse);
    }

    @Test
    void canAddAndRetrieveCourse() {
        Course found = courseService.getCourseById(javaCourse.getId());
        assertEquals(javaCourse.getId(), found.getId());
        assertEquals("Java", found.getSubject().getTitle());
    }

    @Test
    void getAllCoursesReturnsAllAdded() {
        Instructor instructor = new Instructor("Sarah", "sarah@university.com");
        courseService.addCourse(new ClassroomCourse(new Subject("JavaScript", 6), instructor, 1200, "Oxford", "Spring"));
        List<Course> courses = courseService.getAllCourses();
        assertEquals(2, courses.size());
    }

    @Test
    void throwsCourseNotFoundForUnknownId() {
        UUID unknownId = UUID.randomUUID();
        assertThrows(CourseNotFoundException.class, () -> courseService.getCourseById(unknownId));
    }

    @Test
    void canDeleteCourse() {
        courseService.deleteCourse(javaCourse.getId());
        assertThrows(CourseNotFoundException.class, () -> courseService.getCourseById(javaCourse.getId()));
    }

    @Test
    void deleteThrowsForUnknownId() {
        assertThrows(CourseNotFoundException.class, () -> courseService.deleteCourse(UUID.randomUUID()));
    }

    @Test
    void classroomCourseHasCorrectMaxMarks() {
        assertEquals(100, javaCourse.getMaxAssignmentMarks());
        assertEquals(30, javaCourse.getMaxQuizMarks());
    }
}
