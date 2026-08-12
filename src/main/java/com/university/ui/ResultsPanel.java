package com.university.ui;

import com.university.model.Enrollment;
import com.university.model.Learner;
import com.university.service.LearnerService;
import com.university.ui.EnrollPanel.LearnerItem;
import com.university.ui.animation.AnimatedBarsPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ResultsPanel extends JPanel {

    private final LearnerService         learnerService;
    private final JComboBox<LearnerItem> learnerCombo = new JComboBox<>();
    private final AnimatedBarsPanel      barsPanel    = new AnimatedBarsPanel();

    public ResultsPanel(LearnerService learnerService, MainWindow mainWindow) {
        this.learnerService = learnerService;

        setLayout(new BorderLayout(10, 10));
        setBackground(ThemeUtils.BG_BASE);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // ── top bar ────────────────────────────────────────────────────────────
        ThemeUtils.styleCombo(learnerCombo);

        JButton viewBtn = ThemeUtils.accentButton("View Results");
        viewBtn.addActionListener(e -> loadResults());
        viewBtn.setPreferredSize(new Dimension(130, 34));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        top.setBackground(ThemeUtils.BG_BASE);
        top.add(ThemeUtils.label("Select Learner:"));
        top.add(learnerCombo);
        top.add(viewBtn);
        add(top, BorderLayout.NORTH);

        // ── animated bars ──────────────────────────────────────────────────────
        JScrollPane scroll = new JScrollPane(barsPanel);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeUtils.BORDER_COL));
        scroll.getViewport().setBackground(new Color(20, 20, 32));
        add(scroll, BorderLayout.CENTER);
    }

    public void refresh() {
        learnerCombo.removeAllItems();
        for (Learner l : learnerService.getAllLearners()) {
            learnerCombo.addItem(new LearnerItem(l));
        }
    }

    private void loadResults() {
        LearnerItem item = (LearnerItem) learnerCombo.getSelectedItem();
        if (item == null) return;

        List<Enrollment> enrollments = learnerService.getLearnerEnrollments(item.learner.getId());
        if (enrollments.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No enrollments found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        barsPanel.setData(enrollments);
    }
}
