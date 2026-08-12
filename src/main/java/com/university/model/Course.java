package com.university.model;

import java.util.UUID;

public abstract class Course {

    private final UUID id;
    private Subject subject;
    private Instructor instructor;
    private int fee;

    protected Course(Subject subject, Instructor instructor, int fee) {
        this.id = UUID.randomUUID();
        this.subject = subject;
        this.instructor = instructor;
        this.fee = fee;
    }

    public UUID getId() {
        return id;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public abstract int getMaxAssignmentMarks();

    public abstract int getMaxQuizMarks();

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "{subject=" + subject.getTitle() +
                ", instructor=" + instructor.getName() +
                ", fee=" + fee + '}';
    }
}
