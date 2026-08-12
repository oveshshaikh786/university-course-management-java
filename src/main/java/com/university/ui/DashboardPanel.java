package com.university.ui;

import com.university.model.Enrollment;
import com.university.service.CourseService;
import com.university.service.LearnerService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dashboard tab — shows four animated stat cards that count up from 0
 * to the real value every time the tab is selected.
 */
public class DashboardPanel extends JPanel {

    private final CourseService  courseService;
    private final LearnerService learnerService;

    // One StatCard per metric
    private final StatCard cardLearners    = new StatCard("Total Learners",    "🎓", new Color(110, 100, 210));
    private final StatCard cardCourses     = new StatCard("Total Courses",     "📚", new Color(70,  160, 220));
    private final StatCard cardEnrollments = new StatCard("Enrollments",       "📋", new Color(60,  180, 140));
    private final StatCard cardPassRate    = new StatCard("Pass Rate",         "✅", new Color(220, 160,  60));

    public DashboardPanel(CourseService courseService, LearnerService learnerService) {
        this.courseService  = courseService;
        this.learnerService = learnerService;

        setBackground(ThemeUtils.BG_BASE);
        setLayout(new BorderLayout(0, 0));

        // ── header ─────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 18));
        header.setBackground(ThemeUtils.BG_BASE);
        JLabel title = new JLabel("Dashboard");
        title.setForeground(ThemeUtils.TEXT_PRI);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        JLabel sub = new JLabel("Live summary — refreshes on every tab visit");
        sub.setForeground(ThemeUtils.TEXT_SEC);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        header.add(title);
        header.add(sub);
        add(header, BorderLayout.NORTH);

        // ── card grid ──────────────────────────────────────────────────────────
        JPanel grid = new JPanel(new GridLayout(1, 4, 18, 0));
        grid.setBackground(ThemeUtils.BG_BASE);
        grid.setBorder(BorderFactory.createEmptyBorder(10, 18, 18, 18));
        grid.add(cardLearners);
        grid.add(cardCourses);
        grid.add(cardEnrollments);
        grid.add(cardPassRate);
        add(grid, BorderLayout.CENTER);
    }

    /** Called by FadingTabbedPane just before the tab fades in. */
    public void refresh() {
        int totalLearners    = learnerService.getAllLearners().size();
        int totalCourses     = courseService.getAllCourses().size();

        List<Enrollment> allEnrollments = courseService.getAllCourses().stream()
                .flatMap(c -> learnerService.getCourseEnrollments(c.getId()).stream())
                .toList();

        int totalEnrollments = allEnrollments.size();
        long passed = allEnrollments.stream()
                .filter(e -> e.getAssignmentsMarks() > 0 || e.getQuizMarks() > 0)
                .filter(Enrollment::hasPassed)
                .count();
        long graded = allEnrollments.stream()
                .filter(e -> e.getAssignmentsMarks() > 0 || e.getQuizMarks() > 0)
                .count();
        int passRate = graded == 0 ? 0 : (int) Math.round(100.0 * passed / graded);

        // Animate each card from 0 to its target value
        cardLearners   .animateTo(totalLearners,    "",  false);
        cardCourses    .animateTo(totalCourses,     "",  false);
        cardEnrollments.animateTo(totalEnrollments, "",  false);
        cardPassRate   .animateTo(passRate,         "%", true);
    }

    // ── inner: StatCard ───────────────────────────────────────────────────────

    /**
     * A dark rounded card with an icon, a big animated number, and a subtitle.
     */
    private static class StatCard extends JPanel {

        private static final int ANIM_MS  = 900;
        private static final int TIMER_HZ = 16;

        private final String accent;   // emoji icon
        private final Color  accentCol;
        private final String label;

        private int    target   = 0;
        private int    current  = 0;
        private String suffix   = "";
        private boolean percent = false;
        private Timer  timer;

        StatCard(String label, String accent, Color accentCol) {
            this.label     = label;
            this.accent    = accent;
            this.accentCol = accentCol;

            setOpaque(false);
            setPreferredSize(new Dimension(180, 160));
        }

        void animateTo(int newTarget, String newSuffix, boolean isPercent) {
            if (timer != null) timer.stop();
            this.target  = newTarget;
            this.suffix  = newSuffix;
            this.percent = isPercent;
            this.current = 0;

            long startMs = System.currentTimeMillis();

            timer = new Timer(TIMER_HZ, e -> {
                double elapsed = System.currentTimeMillis() - startMs;
                double t       = Math.min(elapsed / ANIM_MS, 1.0);
                double ease    = 1 - Math.pow(1 - t, 3);   // easeOutCubic
                current = (int) Math.round(ease * target);
                repaint();
                if (t >= 1.0) ((Timer) e.getSource()).stop();
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            // ── card background ────────────────────────────────────────────────
            g2.setColor(ThemeUtils.BG_CARD);
            g2.fillRoundRect(0, 0, w, h, 18, 18);

            // ── accent top-bar ─────────────────────────────────────────────────
            g2.setColor(accentCol);
            g2.fillRoundRect(0, 0, w, 5, 4, 4);
            g2.fillRect(0, 2, w, 5);   // square off the bottom of the rounded top

            // ── icon ──────────────────────────────────────────────────────────
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            g2.setColor(accentCol);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(accent, (w - fm.stringWidth(accent)) / 2, 52);

            // ── animated number ───────────────────────────────────────────────
            String numStr = current + suffix;
            g2.setFont(new Font("SansSerif", Font.BOLD, 36));
            g2.setColor(ThemeUtils.TEXT_PRI);
            fm = g2.getFontMetrics();
            g2.drawString(numStr, (w - fm.stringWidth(numStr)) / 2, 100);

            // ── label ─────────────────────────────────────────────────────────
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(ThemeUtils.TEXT_SEC);
            fm = g2.getFontMetrics();
            g2.drawString(label, (w - fm.stringWidth(label)) / 2, 124);

            g2.dispose();
        }
    }
}
