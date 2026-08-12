package com.university.ui;

import com.university.service.CourseService;
import com.university.service.LearnerService;
import com.university.ui.animation.FadingTabbedPane;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    private final CourseService  courseService;
    private final LearnerService learnerService;

    private EnrollPanel      enrollPanel;
    private SubmitMarksPanel submitMarksPanel;
    private ResultsPanel     resultsPanel;

    public MainWindow(CourseService courseService, LearnerService learnerService) {
        this.courseService  = courseService;
        this.learnerService = learnerService;
        initUI();
    }

    private void initUI() {
        setTitle("University Course Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);

        // Panels
        DashboardPanel   dashboardPanel = new DashboardPanel(courseService, learnerService);
        CoursesPanel     coursesPanel   = new CoursesPanel(courseService);
        LearnersPanel    learnersPanel  = new LearnersPanel(learnerService, this);
        enrollPanel      = new EnrollPanel(courseService, learnerService, this);
        submitMarksPanel = new SubmitMarksPanel(courseService, learnerService, this);
        resultsPanel     = new ResultsPanel(learnerService, this);

        // FadingTabbedPane — each tab gets an optional onSelect callback
        // that fires *before* the fade-in, so data is always fresh
        FadingTabbedPane tabs = new FadingTabbedPane();
        tabs.addTab("🏠  Dashboard",     dashboardPanel,   dashboardPanel::refresh);
        tabs.addTab("📚  Courses",       coursesPanel);
        tabs.addTab("🎓  Learners",      learnersPanel);
        tabs.addTab("📋  Enroll",        enrollPanel,      enrollPanel::refresh);
        tabs.addTab("✏️   Submit Marks",  submitMarksPanel, submitMarksPanel::refresh);
        tabs.addTab("📊  Results",       resultsPanel,     resultsPanel::refresh);

        add(tabs, BorderLayout.CENTER);
        pack();
    }

    /** Called by LearnersPanel after a new learner is registered. */
    public void onLearnersChanged() {
        enrollPanel.refresh();
        submitMarksPanel.refresh();
        resultsPanel.refresh();
    }
}
