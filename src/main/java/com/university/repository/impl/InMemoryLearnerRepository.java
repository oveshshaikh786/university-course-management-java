package com.university.repository.impl;

import com.university.model.Learner;
import com.university.repository.LearnerRepository;

import java.util.*;

public class InMemoryLearnerRepository implements LearnerRepository {

    private final Map<UUID, Learner> store = new HashMap<>();

    @Override
    public Optional<Learner> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Learner> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Learner save(Learner learner) {
        store.put(learner.getId(), learner);
        return learner;
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
