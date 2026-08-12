package com.university;

import com.university.model.*;
import com.university.repository.CourseRepository;
import com.university.repository.EnrollmentRepository;
import com.university.repository.LearnerRepository;
import com.university.repository.impl.InMemoryCourseRepository;
import com.university.repository.impl.InMemoryEnrollmentRepository;
import com.university.repository.impl.InMemoryLearnerRepository;
import com.university.service.CourseService;
import com.university.service.LearnerService;
import com.university.ui.MainWindow;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Wire up repositories and services (same as before — UI is just a new layer)
        CourseRepository courseRepository       = new InMemoryCourseRepository();
        LearnerRepository learnerRepository     = new InMemoryLearnerRepository();
        EnrollmentRepository enrollmentRepository = new InMemoryEnrollmentRepository();

        CourseService courseService   = new CourseService(courseRepository);
        LearnerService learnerService = new LearnerService(learnerRepository, courseRepository, enrollmentRepository);

        seedCourses(courseService);

        // Apply Nimbus L&F with a dark colour scheme before any component is created.
        // Nimbus respects setBackground() on combo boxes — the system L&F does not.
        applyDarkNimbus();

        // Launch Swing UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow(courseService, learnerService);
            window.setVisible(true);
        });
    }

    /**
     * Switches to Nimbus and overrides its palette to a dark blue-grey scheme.
     * Must be called BEFORE any Swing component is instantiated.
     *
     * Key Nimbus UIManager properties:
     *   nimbusBase            — primary hue (affects highlights, focus rings)
     *   nimbusBlueGrey        — secondary hue (scroll bars, dividers, borders)
     *   control               — default panel / button background
     *   nimbusLightBackground — text fields, combo display area, spinner field
     *   text                  — default foreground
     */
    private static void applyDarkNimbus() {
        // Set palette BEFORE installing the L&F so Nimbus picks them up on init
        UIManager.put("nimbusBase",            new java.awt.Color(18,  18,  30));
        UIManager.put("nimbusBlueGrey",        new java.awt.Color(30,  30,  50));
        UIManager.put("control",               new java.awt.Color(28,  28,  44));
        UIManager.put("nimbusLightBackground", new java.awt.Color(26,  26,  44));
        UIManager.put("text",                  new java.awt.Color(215, 215, 232));
        UIManager.put("nimbusSelectedText",    java.awt.Color.WHITE);
        UIManager.put("nimbusSelectionBackground", new java.awt.Color(65, 65, 108));
        UIManager.put("nimbusFocus",           new java.awt.Color(110, 100, 210));
        UIManager.put("nimbusDisabledText",    new java.awt.Color(90,  90,  118));
        UIManager.put("info",                  new java.awt.Color(28,  28,  44));
        UIManager.put("infoText",              new java.awt.Color(215, 215, 232));

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    private static void seedCourses(CourseService courseService) {
        Instructor mark  = new Instructor("Mark",  "mark@university.com");
        Instructor sarah = new Instructor("Sarah", "sarah@university.com");

        courseService.addCourse(new ClassroomCourse(new Subject("Java", 4),              mark,  1000, "Cambridge", "Winter"));
        courseService.addCourse(new OnlineCourse(new Subject("Java Online", 4),           mark,   800, 6, 12));
        courseService.addCourse(new ClassroomCourse(new Subject("JavaScript", 6),         sarah, 1200, "Oxford", "Spring"));
        courseService.addCourse(new OnlineCourse(new Subject("JavaScript Online", 6),     sarah,  950, 8, 16));
    }
}
