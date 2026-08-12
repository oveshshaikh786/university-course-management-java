package com.university.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentTest {

    private Learner learner;
    private Course classroomCourse;
    private Course onlineCourse;

    @BeforeEach
    void setUp() {
        Instructor instructor = new Instructor("Mark", "mark@university.com");
        learner = new Learner("Alice", "alice@test.com");
        classroomCourse = new ClassroomCourse(new Subject("Java", 4), instructor, 1000, "Cambridge", "Winter");
        onlineCourse = new OnlineCourse(new Subject("Java Online", 4), instructor, 800, 6, 12);
    }

    @Test
    void gradeIsZeroWhenNoMarksSubmitted() {
        Enrollment enrollment = new Enrollment(learner, classroomCourse);
        assertEquals(0.0, enrollment.calculateGrade());
    }

    @Test
    void gradeIsTenWhenPerfectMarks_classroom() {
        Enrollment enrollment = new Enrollment(learner, classroomCourse);
        enrollment.setAssignmentsMarks(100); // max 100
        enrollment.setQuizMarks(30);         // max 30
        assertEquals(10.0, enrollment.calculateGrade(), 0.001);
    }

    @Test
    void gradeIsTenWhenPerfectMarks_online() {
        Enrollment enrollment = new Enrollment(learner, onlineCourse);
        enrollment.setAssignmentsMarks(30); // max 30
        enrollment.setQuizMarks(10);        // max 10
        assertEquals(10.0, enrollment.calculateGrade(), 0.001);
    }

    @Test
    void learnerPassesWhenGradeIsExactlyFive() {
        Enrollment enrollment = new Enrollment(learner, classroomCourse);
        enrollment.setAssignmentsMarks(50);  // 50/100 * 10 = 5.0
        enrollment.setQuizMarks(15);         // 15/30 * 10 = 5.0
        assertEquals(5.0, enrollment.calculateGrade(), 0.001);
        assertTrue(enrollment.hasPassed());
    }

    @Test
    void learnerFailsWhenGradeBelowFive() {
        Enrollment enrollment = new Enrollment(learner, classroomCourse);
        enrollment.setAssignmentsMarks(30);
        enrollment.setQuizMarks(10);
        assertTrue(enrollment.calculateGrade() < 5.0);
        assertFalse(enrollment.hasPassed());
    }

    @ParameterizedTest
    @CsvSource({
        "100, 30, 10.0",   // perfect
        "50,  15,  5.0",   // exactly passing
        "0,    0,  0.0",   // zero
        "75,  20,  7.08"   // mixed
    })
    void gradeCalculation_classroom(int assignmentMarks, int quizMarks, double expectedGrade) {
        Enrollment enrollment = new Enrollment(learner, classroomCourse);
        enrollment.setAssignmentsMarks(assignmentMarks);
        enrollment.setQuizMarks(quizMarks);
        assertEquals(expectedGrade, enrollment.calculateGrade(), 0.01);
    }

    @Test
    void enrollmentHoldsCorrectLearnerAndCourse() {
        Enrollment enrollment = new Enrollment(learner, classroomCourse);
        assertEquals(learner, enrollment.getLearner());
        assertEquals(classroomCourse, enrollment.getCourse());
        assertNotNull(enrollment.getId());
    }
}
