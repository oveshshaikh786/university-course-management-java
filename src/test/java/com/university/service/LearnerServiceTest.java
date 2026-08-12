package com.university.service;

import com.university.exception.EnrollmentNotFoundException;
import com.university.exception.InvalidMarksException;
import com.university.exception.LearnerNotFoundException;
import com.university.model.*;
import com.university.repository.impl.InMemoryCourseRepository;
import com.university.repository.impl.InMemoryEnrollmentRepository;
import com.university.repository.impl.InMemoryLearnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LearnerServiceTest {

    private LearnerService learnerService;
    private CourseService courseService;
    private Course classroomCourse;
    private Course onlineCourse;

    @BeforeEach
    void setUp() {
        InMemoryCourseRepository courseRepo = new InMemoryCourseRepository();
        courseService = new CourseService(courseRepo);
        learnerService = new LearnerService(
                new InMemoryLearnerRepository(),
                courseRepo,
                new InMemoryEnrollmentRepository()
        );

        Instructor instructor = new Instructor("Mark", "mark@university.com");
        classroomCourse = new ClassroomCourse(new Subject("Java", 4), instructor, 1000, "Cambridge", "Winter");
        onlineCourse = new OnlineCourse(new Subject("Java Online", 4), instructor, 800, 6, 12);
        courseService.addCourse(classroomCourse);
        courseService.addCourse(onlineCourse);
    }

    @Test
    void canRegisterAndRetrieveLearner() {
        Learner learner = learnerService.registerLearner("Alice", "alice@test.com");
        Learner found = learnerService.getLearnerById(learner.getId());
        assertEquals("Alice", found.getName());
        assertEquals("alice@test.com", found.getEmail());
    }

    @Test
    void throwsLearnerNotFoundForUnknownId() {
        assertThrows(LearnerNotFoundException.class, () -> learnerService.getLearnerById(UUID.randomUUID()));
    }

    @Test
    void canEnrollLearnerInCourse() {
        Learner learner = learnerService.registerLearner("Alice", "alice@test.com");
        Enrollment enrollment = learnerService.enroll(learner.getId(), classroomCourse.getId());
        assertNotNull(enrollment.getId());
        assertEquals(learner.getId(), enrollment.getLearner().getId());
        assertEquals(classroomCourse.getId(), enrollment.getCourse().getId());
    }

    @Test
    void throwsWhenEnrollingInSameCourseTwice() {
        Learner learner = learnerService.registerLearner("Alice", "alice@test.com");
        learnerService.enroll(learner.getId(), classroomCourse.getId());
        assertThrows(IllegalStateException.class,
                () -> learnerService.enroll(learner.getId(), classroomCourse.getId()));
    }

    @Test
    void learnerCanEnrollInMultipleCourses() {
        Learner learner = learnerService.registerLearner("Alice", "alice@test.com");
        learnerService.enroll(learner.getId(), classroomCourse.getId());
        learnerService.enroll(learner.getId(), onlineCourse.getId());
        List<Enrollment> enrollments = learnerService.getLearnerEnrollments(learner.getId());
        assertEquals(2, enrollments.size());
    }

    @Test
    void canSubmitValidMarksAndCalculateGrade() {
        Learner learner = learnerService.registerLearner("Alice", "alice@test.com");
        learnerService.enroll(learner.getId(), classroomCourse.getId());
        Enrollment enrollment = learnerService.submitMarks(learner.getId(), classroomCourse.getId(), 80, 25);
        assertTrue(enrollment.calculateGrade() > 5.0);
        assertTrue(enrollment.hasPassed());
    }

    @Test
    void throwsInvalidMarksWhenAssignmentExceedsMax() {
        Learner learner = learnerService.registerLearner("Alice", "alice@test.com");
        learnerService.enroll(learner.getId(), classroomCourse.getId());
        assertThrows(InvalidMarksException.class,
                () -> learnerService.submitMarks(learner.getId(), classroomCourse.getId(), 101, 20));
    }

    @Test
    void throwsInvalidMarksWhenNegative() {
        Learner learner = learnerService.registerLearner("Alice", "alice@test.com");
        learnerService.enroll(learner.getId(), classroomCourse.getId());
        assertThrows(InvalidMarksException.class,
                () -> learnerService.submitMarks(learner.getId(), classroomCourse.getId(), -1, 20));
    }

    @Test
    void throwsEnrollmentNotFoundWhenSubmittingWithoutEnrolling() {
        Learner learner = learnerService.registerLearner("Alice", "alice@test.com");
        assertThrows(EnrollmentNotFoundException.class,
                () -> learnerService.submitMarks(learner.getId(), classroomCourse.getId(), 80, 20));
    }

    @Test
    void multipleLearnersSameCourseDontConflict() {
        Learner alice = learnerService.registerLearner("Alice", "alice@test.com");
        Learner bob = learnerService.registerLearner("Bob", "bob@test.com");

        learnerService.enroll(alice.getId(), classroomCourse.getId());
        learnerService.enroll(bob.getId(), classroomCourse.getId());

        learnerService.submitMarks(alice.getId(), classroomCourse.getId(), 90, 28);
        learnerService.submitMarks(bob.getId(), classroomCourse.getId(), 40, 10);

        List<Enrollment> aliceEnrollments = learnerService.getLearnerEnrollments(alice.getId());
        List<Enrollment> bobEnrollments = learnerService.getLearnerEnrollments(bob.getId());

        assertTrue(aliceEnrollments.get(0).hasPassed());
        assertFalse(bobEnrollments.get(0).hasPassed());
    }

    @Test
    void onlineCourseUsesCorrectMaxMarks() {
        Learner learner = learnerService.registerLearner("Alice", "alice@test.com");
        learnerService.enroll(learner.getId(), onlineCourse.getId());
        // max for online: assignment=30, quiz=10 — submitting over max throws
        assertThrows(InvalidMarksException.class,
                () -> learnerService.submitMarks(learner.getId(), onlineCourse.getId(), 31, 5));
    }
}
