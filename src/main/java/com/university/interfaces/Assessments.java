package com.university.interfaces;

public interface Assessments {

    void setAssignmentsMarks(int marks);

    void setQuizMarks(int marks);

    double calculateGrade();

    boolean hasPassed();
}
