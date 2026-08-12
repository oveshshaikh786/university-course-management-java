package com.university.ui;

import com.university.model.Course;
import com.university.model.Enrollment;
import com.university.model.Learner;
import com.university.service.CourseService;
import com.university.service.LearnerService;
import com.university.ui.EnrollPanel.CourseItem;
import com.university.ui.EnrollPanel.LearnerItem;
import com.university.ui.animation.GradeRevealDialog;

import javax.swing.*;
import java.awt.*;

public class SubmitMarksPanel extends JPanel {

    private final CourseService  courseService;
    private final LearnerService learnerService;

    private final JComboBox<LearnerItem> learnerCombo      = new JComboBox<>();
    private final JComboBox<CourseItem>  courseCombo       = new JComboBox<>();
    private final JSpinner               assignmentSpinner =
            new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    private final JSpinner               quizSpinner       =
            new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    private final JLabel                 assignmentHint    = ThemeUtils.secondaryLabel("(max: ?)");
    private final JLabel                 quizHint          = ThemeUtils.secondaryLabel("(max: ?)");

    public SubmitMarksPanel(CourseService courseService, LearnerService learnerService,
                            MainWindow mainWindow) {
        this.courseService  = courseService;
        this.learnerService = learnerService;

        // ── outer: dark base, padding; form anchors to NORTH ──────────────────
        setLayout(new BorderLayout());
        setBackground(ThemeUtils.BG_BASE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildForm(), BorderLayout.NORTH);
    }

    private JPanel buildForm() {
        ThemeUtils.styleCombo(learnerCombo);
        ThemeUtils.styleCombo(courseCombo);
        ThemeUtils.styleSpinner(assignmentSpinner);
        ThemeUtils.styleSpinner(quizSpinner);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(ThemeUtils.BG_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
                ThemeUtils.titledBorder("Submit Marks"),
                BorderFactory.createEmptyBorder(4, 4, 8, 4)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; form.add(ThemeUtils.label("Learner:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; form.add(learnerCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; form.add(ThemeUtils.label("Course:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; form.add(courseCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        form.add(ThemeUtils.label("Assignment Marks:"), gbc);
        JPanel aRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        aRow.setBackground(ThemeUtils.BG_CARD);
        aRow.add(assignmentSpinner);
        aRow.add(assignmentHint);
        gbc.gridx = 1; gbc.weightx = 1; form.add(aRow, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        form.add(ThemeUtils.label("Quiz Marks:"), gbc);
        JPanel qRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        qRow.setBackground(ThemeUtils.BG_CARD);
        qRow.add(quizSpinner);
        qRow.add(quizHint);
        gbc.gridx = 1; gbc.weightx = 1; form.add(qRow, gbc);

        JButton submitBtn = ThemeUtils.accentButton("Submit Marks");
        submitBtn.addActionListener(e -> submitMarks());
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        gbc.fill    = GridBagConstraints.NONE;
        form.add(submitBtn, gbc);

        // Update spinner limits whenever the selected course changes
        courseCombo.addActionListener(e -> updateSpinnerLimits());

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
        updateSpinnerLimits();
    }

    /** Adjust spinner upper bounds and clamp current values when course changes. */
    private void updateSpinnerLimits() {
        CourseItem item = (CourseItem) courseCombo.getSelectedItem();
        if (item == null) return;

        int maxA = item.course.getMaxAssignmentMarks();
        int maxQ = item.course.getMaxQuizMarks();

        SpinnerNumberModel modelA = (SpinnerNumberModel) assignmentSpinner.getModel();
        SpinnerNumberModel modelQ = (SpinnerNumberModel) quizSpinner.getModel();

        // Update the upper bound first
        modelA.setMaximum(maxA);
        modelQ.setMaximum(maxQ);

        // Then clamp current value so it never exceeds the new max
        if ((int) modelA.getValue() > maxA) assignmentSpinner.setValue(maxA);
        if ((int) modelQ.getValue() > maxQ) quizSpinner.setValue(maxQ);

        assignmentHint.setText("(max: " + maxA + ")");
        quizHint.setText("(max: " + maxQ + ")");
    }

    private void submitMarks() {
        LearnerItem learnerItem = (LearnerItem) learnerCombo.getSelectedItem();
        CourseItem  courseItem  = (CourseItem)  courseCombo.getSelectedItem();

        if (learnerItem == null || courseItem == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a learner and a course.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int assignmentMarks = (int) assignmentSpinner.getValue();
        int quizMarks       = (int) quizSpinner.getValue();

        try {
            Enrollment enrollment = learnerService.submitMarks(
                    learnerItem.learner.getId(),
                    courseItem.course.getId(),
                    assignmentMarks, quizMarks);

            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            new GradeRevealDialog(parentFrame,
                    enrollment.calculateGrade(), enrollment.hasPassed())
                    .setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
