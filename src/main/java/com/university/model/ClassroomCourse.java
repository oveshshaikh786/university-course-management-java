package com.university.model;

public class ClassroomCourse extends Course {

    private String school;
    private String session;

    public ClassroomCourse(Subject subject, Instructor instructor, int fee, String school, String session) {
        super(subject, instructor, fee);
        this.school = school;
        this.session = session;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    @Override
    public int getMaxAssignmentMarks() {
        return 100;
    }

    @Override
    public int getMaxQuizMarks() {
        return 30;
    }
}
