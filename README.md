# University Course Management System

A Java 17 desktop application demonstrating **full OOP design**, a **layered backend architecture**, and an **animated Swing GUI** — built as an interview-ready portfolio project.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Build | Maven 3.x |
| GUI | Java Swing (custom dark theme + animations) |
| Logging | SLF4J 2 + Logback |
| Testing | JUnit 5.10 |

---

## How to Build and Run

```bash
# Build and run tests
mvn package

# Launch the GUI
java -jar target/university-course-management-system-1.0.0.jar
```

Logs are written to `logs/university-cms.log` (rolling daily, 7-day retention).

---

## Project Structure

```
src/
├── main/java/com/university/
│   ├── Main.java                        # Entry point — wires DI, applies Nimbus L&F
│   ├── model/                           # Domain entities
│   │   ├── Course.java                  # Abstract base — polymorphic max marks
│   │   ├── ClassroomCourse.java         # Concrete: maxAssignment=100, maxQuiz=30
│   │   ├── OnlineCourse.java            # Concrete: maxAssignment=30,  maxQuiz=10
│   │   ├── Learner.java
│   │   ├── Instructor.java
│   │   ├── Subject.java
│   │   └── Enrollment.java              # Junction entity — owns marks + grade
│   ├── interfaces/
│   │   └── Assessments.java             # Contract: setMarks, calculateGrade, hasPassed
│   ├── exception/                       # Custom RuntimeExceptions (4 types)
│   ├── repository/                      # Generic Repository<T, ID> + specialisations
│   │   └── impl/                        # In-memory HashMap implementations
│   ├── service/
│   │   ├── CourseService.java
│   │   └── LearnerService.java          # Business logic + validation + logging
│   ├── ui/
│   │   ├── ThemeUtils.java              # Shared dark palette + component helpers
│   │   ├── MainWindow.java
│   │   ├── DashboardPanel.java          # Animated stat cards
│   │   ├── CoursesPanel.java
│   │   ├── LearnersPanel.java
│   │   ├── EnrollPanel.java
│   │   ├── SubmitMarksPanel.java
│   │   ├── ResultsPanel.java
│   │   └── animation/
│   │       ├── FadingTabbedPane.java    # Custom tab bar with crossfade
│   │       ├── FadingContentPanel.java  # BufferedImage snapshot fade
│   │       ├── GradeRevealDialog.java   # Arc + particle burst animation
│   │       └── AnimatedBarsPanel.java   # Staggered grade bars
└── test/java/com/university/
    ├── model/EnrollmentTest.java         # 7 tests (incl. @ParameterizedTest)
    ├── service/CourseServiceTest.java    # 6 tests
    └── service/LearnerServiceTest.java   # 10 tests
```

---

## Key Design Decisions

### 1. Abstract polymorphism for course types
`Course` is abstract with two abstract methods:
```java
public abstract int getMaxAssignmentMarks();
public abstract int getMaxQuizMarks();
```
`ClassroomCourse` and `OnlineCourse` override them to return different limits (100/30 vs 30/10). Any code that calls these methods — `Enrollment.calculateGrade()`, the UI spinners — works correctly for both types **without a single `instanceof` check**. Adding a third course type requires no changes to existing code.

### 2. Enrollment as a junction entity
Rather than storing marks directly on `Learner` or `Course` (a common mistake), `Enrollment` is its own entity that owns the relationship:
```
Learner ──< Enrollment >── Course
                 │
           assignmentsMarks
           quizMarks
           calculateGrade()
           hasPassed()
```
This correctly models the real-world constraint: one learner can enroll in many courses, and marks belong to *that specific enrollment*, not to the learner globally.

### 3. Generic Repository pattern
```java
public interface Repository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
    boolean existsById(ID id);
}
```
In-memory `HashMap` implementations are injected at startup via manual DI in `Main.java`. Swapping to JPA / JDBC requires only a new `impl/` class — the `Service` layer is unchanged. This is the same pattern Spring Data uses.

### 4. Service layer owns all business logic
Controllers / UI panels never touch repositories directly. The service layer enforces:
- Duplicate enrollment detection
- Marks bounds validation (`InvalidMarksException`)
- Entity existence checks (throw domain-specific exceptions, never NPE)
- Email format validation (regex, RFC-5322 pattern)
- Structured logging at INFO/WARN/ERROR levels

### 5. Custom exception hierarchy
Four `RuntimeException` subclasses — `CourseNotFoundException`, `LearnerNotFoundException`, `EnrollmentNotFoundException`, `InvalidMarksException` — let callers handle specific failure modes instead of catching generic `Exception`.

---

## OOP Principles Applied

| Principle | Where |
|---|---|
| **Encapsulation** | All model fields are `private` with getters/setters |
| **Inheritance** | `ClassroomCourse` and `OnlineCourse` extend abstract `Course` |
| **Polymorphism** | `getMaxAssignmentMarks()` / `getMaxQuizMarks()` — called on `Course`, dispatches to subtype |
| **Abstraction** | `Course` (abstract class), `Assessments` (interface), `Repository<T,ID>` (interface) |

---

## GUI Features

- **Dark theme** — consistent across all panels via `ThemeUtils` (single source of truth for colours)
- **Nimbus L&F** — palette overridden at startup so combo boxes, text fields, and spinners all respect `setBackground()`
- **Tab crossfade** — `FadingTabbedPane` captures a `BufferedImage` snapshot of the old panel and alpha-fades it out while the new panel renders beneath
- **Grade reveal dialog** — animated arc (easeOutCubic, 1500ms) + particle burst on grade submission
- **Animated bars** — staggered easeOutCubic bars in the Results tab, with fade-in badge
- **Dashboard** — four stat cards with animated number counters (easeOutCubic from 0 to real value on every tab visit)

---

## Running Tests

```bash
mvn test
```

23 unit tests covering:
- Grade calculation for all course types (parametrized)
- Pass/fail boundary conditions
- Duplicate enrollment rejection
- Marks validation (out-of-range inputs)
- Multiple learners in the same course don't share state
