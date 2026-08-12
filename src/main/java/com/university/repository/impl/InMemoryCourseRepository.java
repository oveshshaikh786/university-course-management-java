package com.university.repository.impl;

import com.university.model.Course;
import com.university.repository.CourseRepository;

import java.util.*;

public class InMemoryCourseRepository implements CourseRepository {

    private final Map<UUID, Course> store = new HashMap<>();

    @Override
    public Optional<Course> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Course> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Course save(Course course) {
        store.put(course.getId(), course);
        return course;
    }

    @Override
    public void deleteById(UUID id) {
        store.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return store.containsKey(id);
    }
}
