package com.university.ui;

import com.university.model.Course;
import com.university.model.Enrollment;
import com.university.model.Learner;
import com.university.service.CourseService;
import com.university.service.LearnerService;

import javax.swing.*;
import java.awt.*;

public class EnrollPanel extends JPanel {

    private final CourseService  courseService;
    private final LearnerService learnerService;
    private final MainWindow     mainWindow;

    private final JComboBox<LearnerItem> learnerCombo = new JComboBox<>();
    private final JComboBox<CourseItem>  courseCombo  = new JComboBox<>();

    public EnrollPanel(CourseService courseService, LearnerService learnerService, MainWindow mainWindow) {
        this.courseService  = courseService;
        this.learnerService = learnerService;
        this.mainWindow     = mainWindow;

        // ── outer: dark base, padding; form anchors to NORTH ──────────────────
        setLayout(new BorderLayout());
        setBackground(ThemeUtils.BG_BASE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildForm(), BorderLayout.NORTH);
    }

    private JPanel buildForm() {
        // Style combo boxes
        ThemeUtils.styleCombo(learnerCombo);
        ThemeUtils.styleCombo(courseCombo);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(ThemeUtils.BG_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
                ThemeUtils.titledBorder("Enroll Learner in Course"),
                BorderFactory.createEmptyBorder(4, 4, 8, 4)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; form.add(ThemeUtils.label("Learner:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; form.add(learnerCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; form.add(ThemeUtils.label("Course:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; form.add(courseCombo, gbc);

        JButton enrollBtn = ThemeUtils.accentButton("Enroll");
        enrollBtn.addActionListener(e -> enroll());
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        gbc.fill    = GridBagConstraints.NONE;
        form.add(enrollBtn, gbc);

        return form;
    }

    public void refresh() {
        learnerCombo.removeAllItems();
        courseCombo.removeAllItems();
        for (Learner l : learnerService.getAllLearners()) {
            learnerCombo.addItem(new LearnerItem(l));
        }
        for (Course c : courseService.getAllCourses()) {
            courseCombo.addItem(new CourseItem(c));
        }
    }

    private void enroll() {
        LearnerItem learnerItem = (LearnerItem) learnerCombo.getSelectedItem();
        CourseItem  courseItem  = (CourseItem)  courseCombo.getSelectedItem();

        if (learnerItem == null || courseItem == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a learner and a course.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Enrollment e = learnerService.enroll(
                    learnerItem.learner.getId(), courseItem.course.getId());
            JOptionPane.showMessageDialog(this,
                    learnerItem.learner.getName() + " enrolled in "
                    + courseItem.course.getSubject().getTitle()
                    + "!\nEnrollment ID: " + e.getId(),
                    "Enrolled", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── wrapper classes (also used by SubmitMarksPanel and ResultsPanel) ──────

    static class LearnerItem {
        final Learner learner;
        LearnerItem(Learner l) { this.learner = l; }
        @Override public String toString() {
            return learner.getName() + " (" + learner.getEmail() + ")";
        }
    }

    static class CourseItem {
        final Course course;
        CourseItem(Course c) { this.course = c; }
        @Override public String toString() {
            return course.getSubject().getTitle() + " — "
                    + course.getClass().getSimpleName().replace("Course", "");
        }
    }
}
