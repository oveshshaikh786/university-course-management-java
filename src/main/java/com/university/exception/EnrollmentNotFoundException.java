package com.university.exception;

import java.util.UUID;

public class EnrollmentNotFoundException extends RuntimeException {

    public EnrollmentNotFoundException(UUID learnerId, UUID courseId) {
        super("Enrollment not found for learner: " + learnerId + " and course: " + courseId);
    }
}
