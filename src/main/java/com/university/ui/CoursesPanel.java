package com.university.ui;

import com.university.model.Course;
import com.university.service.CourseService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CoursesPanel extends JPanel {

    private final CourseService      courseService;
    private final DefaultTableModel  tableModel;

    private static final String[] COLUMNS = {
            "Subject", "Type", "Instructor", "Fee ($)", "Max Assignment", "Max Quiz"
    };

    public CoursesPanel(CourseService courseService) {
        this.courseService = courseService;

        setLayout(new BorderLayout(10, 10));
        setBackground(ThemeUtils.BG_BASE);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // ── title ──────────────────────────────────────────────────────────────
        JLabel title = new JLabel("Available Courses");
        title.setForeground(ThemeUtils.TEXT_PRI);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        // ── table ──────────────────────────────────────────────────────────────
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);
        ThemeUtils.styleTable(table, scroll);
        add(scroll, BorderLayout.CENTER);

        // ── south bar ──────────────────────────────────────────────────────────
        JButton refreshBtn = ThemeUtils.accentButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());
        refreshBtn.setPreferredSize(new Dimension(100, 34));

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        south.setBackground(ThemeUtils.BG_BASE);
        south.add(refreshBtn);
        add(south, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Course> courses = courseService.getAllCourses();
        for (Course c : courses) {
            tableModel.addRow(new Object[]{
                    c.getSubject().getTitle(),
                    c.getClass().getSimpleName().replace("Course", ""),
                    c.getInstructor().getName(),
                    c.getFee(),
                    c.getMaxAssignmentMarks(),
                    c.getMaxQuizMarks()
            });
        }
    }
}
