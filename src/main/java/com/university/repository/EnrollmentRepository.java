package com.university.repository;

import com.university.model.Enrollment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository extends Repository<Enrollment, UUID> {

    List<Enrollment> findByLearnerId(UUID learnerId);

    List<Enrollment> findByCourseId(UUID courseId);

    Optional<Enrollment> findByLearnerIdAndCourseId(UUID learnerId, UUID courseId);
}
