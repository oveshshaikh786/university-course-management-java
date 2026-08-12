package com.university.model;

public class Subject {

    private String title;
    private double credits;

    public Subject(String title, double credits) {
        this.title = title;
        this.credits = credits;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getCredits() {
        return credits;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }

    @Override
    public String toString() {
        return "Subject{title='" + title + "', credits=" + credits + '}';
    }
}
