package com.university.repository.impl;

import com.university.model.Enrollment;
import com.university.repository.EnrollmentRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryEnrollmentRepository implements EnrollmentRepository {

    private final Map<UUID, Enrollment> store = new HashMap<>();

    @Override
    public Optional<Enrollment> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Enrollment> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Enrollment save(Enrollment enrollment) {
        store.put(enrollment.getId(), enrollment);
        return enrollment;
    }

    @Override
    public void deleteById(UUID id) {
        store.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return store.containsKey(id);
    }

    @Override
    public List<Enrollment> findByLearnerId(UUID learnerId) {
        return store.values().stream()
                .filter(e -> e.getLearner().getId().equals(learnerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Enrollment> findByCourseId(UUID courseId) {
        return store.values().stream()
                .filter(e -> e.getCourse().getId().equals(courseId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Enrollment> findByLearnerIdAndCourseId(UUID learnerId, UUID courseId) {
        return store.values().stream()
                .filter(e -> e.getLearner().getId().equals(learnerId)
                        && e.getCourse().getId().equals(courseId))
                .findFirst();
    }
}
