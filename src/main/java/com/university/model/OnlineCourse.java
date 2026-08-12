package com.university.model;

public class OnlineCourse extends Course {

    private int weeks;
    private int videoLessons;

    public OnlineCourse(Subject subject, Instructor instructor, int fee, int weeks, int videoLessons) {
        super(subject, instructor, fee);
        this.weeks = weeks;
        this.videoLessons = videoLessons;
    }

    public int getWeeks() {
        return weeks;
    }

    public void setWeeks(int weeks) {
        this.weeks = weeks;
    }

    public int getVideoLessons() {
        return videoLessons;
    }

    public void setVideoLessons(int videoLessons) {
        this.videoLessons = videoLessons;
    }

    @Override
    public int getMaxAssignmentMarks() {
        return 30;
    }

    @Override
    public int getMaxQuizMarks() {
        return 10;
    }
}
