package com.university.model;

import com.university.interfaces.Assessments;

import java.util.UUID;

public class Enrollment implements Assessments {

    private static final double PASSING_GRADE = 5.0;

    private final UUID id;
    private final Learner learner;
    private final Course course;
    private int assignmentsMarks;
    private int quizMarks;

    public Enrollment(Learner learner, Course course) {
        this.id = UUID.randomUUID();
        this.learner = learner;
        this.course = course;
    }

    public UUID getId() {
        return id;
    }

    public Learner getLearner() {
        return learner;
    }

    public Course getCourse() {
        return course;
    }

    public int getAssignmentsMarks() {
        return assignmentsMarks;
    }

    public int getQuizMarks() {
        return quizMarks;
    }

    @Override
    public void setAssignmentsMarks(int marks) {
        this.assignmentsMarks = marks;
    }

    @Override
    public void setQuizMarks(int marks) {
        this.quizMarks = marks;
    }

    @Override
    public double calculateGrade() {
        double assignmentGrade = ((double) assignmentsMarks * 10) / course.getMaxAssignmentMarks();
        double quizGrade = ((double) quizMarks * 10) / course.getMaxQuizMarks();
        return (assignmentGrade + quizGrade) / 2;
    }

    @Override
    public boolean hasPassed() {
        return calculateGrade() >= PASSING_GRADE;
    }

    @Override
    public String toString() {
        return "Enrollment{learner=" + learner.getName() +
                ", course=" + course.getSubject().getTitle() +
                ", grade=" + String.format("%.2f", calculateGrade()) +
                ", passed=" + hasPassed() + '}';
    }
}
