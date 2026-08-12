package com.university.exception;

import java.util.UUID;

public class LearnerNotFoundException extends RuntimeException {

    public LearnerNotFoundException(UUID id) {
        super("Learner not found with id: " + id);
    }
}
