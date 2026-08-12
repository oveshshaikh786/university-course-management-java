package com.university.service;

import com.university.exception.EnrollmentNotFoundException;
import com.university.exception.InvalidMarksException;
import com.university.exception.LearnerNotFoundException;
import com.university.exception.CourseNotFoundException;
import com.university.model.Course;
import com.university.model.Enrollment;
import com.university.model.Learner;
import com.university.repository.CourseRepository;
import com.university.repository.EnrollmentRepository;
import com.university.repository.LearnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class LearnerService {

    private static final Logger log = LoggerFactory.getLogger(LearnerService.class);

    /** Simple RFC-5322-style email pattern — catches obviously malformed input. */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+\\-]+@[\\w\\-]+(\\.[\\w\\-]+)*\\.[a-zA-Z]{2,}$");

    private final LearnerRepository    learnerRepository;
    private final CourseRepository     courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public LearnerService(LearnerRepository learnerRepository,
                          CourseRepository courseRepository,
                          EnrollmentRepository enrollmentRepository) {
        this.learnerRepository    = learnerRepository;
        this.courseRepository     = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public Learner registerLearner(String name, String email) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Learner name must not be blank.");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            log.warn("registerLearner rejected invalid email: '{}'", email);
            throw new IllegalArgumentException("Invalid email address: " + email);
        }
        Learner learner = new Learner(name.trim(), email.trim());
        Learner saved   = learnerRepository.save(learner);
        log.info("Learner registered: [{}] {} <{}>", saved.getId(), saved.getName(), saved.getEmail());
        return saved;
    }

    public Learner getLearnerById(UUID id) {
        return learnerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Learner not found: {}", id);
                    return new LearnerNotFoundException(id);
                });
    }

    public List<Learner> getAllLearners() {
        List<Learner> learners = learnerRepository.findAll();
        log.debug("getAllLearners() returned {} learner(s)", learners.size());
        return learners;
    }

    public Enrollment enroll(UUID learnerId, UUID courseId) {
        Learner learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new LearnerNotFoundException(learnerId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        boolean alreadyEnrolled = enrollmentRepository
                .findByLearnerIdAndCourseId(learnerId, courseId)
                .isPresent();

        if (alreadyEnrolled) {
            log.warn("Duplicate enrollment attempt: learner='{}' course='{}'",
                    learner.getName(), course.getSubject().getTitle());
            throw new IllegalStateException("Learner " + learner.getName()
                    + " is already enrolled in " + course.getSubject().getTitle());
        }

        Enrollment enrollment = new Enrollment(learner, course);
        Enrollment saved      = enrollmentRepository.save(enrollment);
        log.info("Enrolled: learner='{}' -> course='{}' [enrollmentId={}]",
                learner.getName(), course.getSubject().getTitle(), saved.getId());
        return saved;
    }

    public Enrollment submitMarks(UUID learnerId, UUID courseId,
                                  int assignmentsMarks, int quizMarks) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        validateMarks(assignmentsMarks, course.getMaxAssignmentMarks(), "Assignment");
        validateMarks(quizMarks,        course.getMaxQuizMarks(),       "Quiz");

        Enrollment enrollment = enrollmentRepository
                .findByLearnerIdAndCourseId(learnerId, courseId)
                .orElseThrow(() -> new EnrollmentNotFoundException(learnerId, courseId));

        enrollment.setAssignmentsMarks(assignmentsMarks);
        enrollment.setQuizMarks(quizMarks);
        Enrollment saved = enrollmentRepository.save(enrollment);

        log.info("Marks submitted: learner='{}' course='{}' assignment={} quiz={} -> grade={} ({})",
                enrollment.getLearner().getName(),
                course.getSubject().getTitle(),
                assignmentsMarks, quizMarks,
                String.format("%.2f", saved.calculateGrade()),
                saved.hasPassed() ? "PASS" : "FAIL");
        return saved;
    }

    public List<Enrollment> getLearnerEnrollments(UUID learnerId) {
        if (!learnerRepository.existsById(learnerId)) {
            throw new LearnerNotFoundException(learnerId);
        }
        return enrollmentRepository.findByLearnerId(learnerId);
    }

    public List<Enrollment> getCourseEnrollments(UUID courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new CourseNotFoundException(courseId);
        }
        return enrollmentRepository.findByCourseId(courseId);
    }

    private void validateMarks(int marks, int max, String type) {
        if (marks < 0 || marks > max) {
            log.error("Invalid {} marks: {} (max allowed: {})", type, marks, max);
            throw new InvalidMarksException(
                    type + " marks must be between 0 and " + max + ", but got: " + marks);
        }
    }
}
