package com.university.ui.animation;

import com.university.model.Enrollment;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom-painted panel that shows enrollment results as animated horizontal bars.
 *
 * Each bar slides in from the left with easeOutCubic easing.
 * Bars are staggered: bar[i] starts STAGGER_MS * i milliseconds after the panel fires.
 * The grade label and pass/fail badge fade in as the bar finishes.
 */
public class AnimatedBarsPanel extends JPanel {

    private static final int BAR_DURATION_MS = 700;
    private static final int STAGGER_MS      = 130;
    private static final int ROW_HEIGHT      = 72;
    private static final int PADDING         = 16;
    private static final int LABEL_WIDTH     = 180;
    private static final int BADGE_WIDTH     = 90;

    // ── bar data ─────────────────────────────────────────────────────────────
    private record BarEntry(
        String courseName,
        String courseType,
        int    assignMarks,
        int    quizMarks,
        double grade,
        boolean passed
    ) {}

    private final List<BarEntry> entries = new ArrayList<>();
    private double[]  progress;   // per-bar, 0→1
    private Timer     animTimer;
    private long      startTime;

    public AnimatedBarsPanel() {
        setOpaque(false);
    }

    // ── public API ────────────────────────────────────────────────────────────
    public void setData(List<Enrollment> enrollments) {
        entries.clear();
        for (Enrollment e : enrollments) {
            entries.add(new BarEntry(
                e.getCourse().getSubject().getTitle(),
                e.getCourse().getClass().getSimpleName().replace("Course", ""),
                e.getAssignmentsMarks(),
                e.getQuizMarks(),
                e.calculateGrade(),
                e.hasPassed()
            ));
        }

        int totalH = PADDING + entries.size() * ROW_HEIGHT + PADDING;
        setPreferredSize(new Dimension(700, Math.max(totalH, 120)));

        progress = new double[entries.size()];
        startAnimation();
    }

    // ── animation ─────────────────────────────────────────────────────────────
    private void startAnimation() {
        if (animTimer != null) animTimer.stop();
        startTime = System.currentTimeMillis();

        animTimer = new Timer(16, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            boolean allDone = true;

            for (int i = 0; i < entries.size(); i++) {
                long barElapsed = elapsed - (long) i * STAGGER_MS;
                if (barElapsed <= 0) { allDone = false; continue; }
                double t = Math.min(1.0, barElapsed / (double) BAR_DURATION_MS);
                progress[i] = easeOutCubic(t);
                if (t < 1.0) allDone = false;
            }

            repaint();
            if (allDone) ((Timer) e.getSource()).stop();
        });

        animTimer.start();
    }

    // ── painting ──────────────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (entries.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        enableAA(g2);

        int maxBarW = getWidth() - LABEL_WIDTH - BADGE_WIDTH - PADDING * 3;

        for (int i = 0; i < entries.size(); i++) {
            drawRow(g2, i, PADDING + i * ROW_HEIGHT, maxBarW);
        }

        g2.dispose();
    }

    private void drawRow(Graphics2D g2, int idx, int y, int maxBarW) {
        if (progress == null || idx >= progress.length) return;
        BarEntry bar  = entries.get(idx);
        double   prog = progress[idx];

        // ── course label ─────────────────────────────────────────────────────
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g2.setColor(new Color(190, 190, 210));
        g2.drawString(bar.courseName(), PADDING, y + 20);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(new Color(110, 110, 140));
        g2.drawString("(" + bar.courseType() + ") A:" + bar.assignMarks() + " Q:" + bar.quizMarks(), PADDING, y + 36);

        // ── track ────────────────────────────────────────────────────────────
        int bx = LABEL_WIDTH;
        int by = y + 24;
        int bh = 20;

        g2.setColor(new Color(38, 38, 58));
        g2.fillRoundRect(bx, by, maxBarW, bh, 10, 10);

        // ── animated bar ─────────────────────────────────────────────────────
        int bw = (int) (prog * (bar.grade() / 10.0) * maxBarW);
        if (bw > 4) {
            Color c = gradeColor(bar.grade());

            // glow behind bar
            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 55));
            g2.fillRoundRect(bx, by - 3, bw, bh + 6, 12, 12);

            // main bar
            g2.setColor(c);
            g2.fillRoundRect(bx, by, bw, bh, 10, 10);

            // highlight stripe at top
            g2.setColor(new Color(255, 255, 255, 40));
            g2.fillRoundRect(bx, by, bw, bh / 2, 10, 10);
        }

        // ── grade + badge (fade in after bar is 50% done) ────────────────────
        if (prog > 0.3) {
            float a = Math.min(1.0f, (float) ((prog - 0.3) / 0.5));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a));

            // grade text
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.setColor(Color.WHITE);
            String gradeStr = String.format("%.2f", bar.grade());
            g2.drawString(gradeStr, bx + maxBarW + 8, by + bh - 4);

            // pass/fail badge
            Color badgeColor = bar.passed() ? new Color(0, 180, 80) : new Color(200, 50, 50);
            String badge     = bar.passed() ? "✓ PASS" : "✗ FAIL";
            int badgeX       = bx + maxBarW + 58;
            int badgeY       = by + 2;
            int badgeH       = bh - 4;

            g2.setColor(new Color(badgeColor.getRed(), badgeColor.getGreen(), badgeColor.getBlue(), 40));
            g2.fillRoundRect(badgeX, badgeY, 62, badgeH, 6, 6);
            g2.setColor(badgeColor);
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(badge, badgeX + (62 - fm.stringWidth(badge)) / 2, badgeY + badgeH - 4);

            g2.setComposite(AlphaComposite.SrcOver);
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private static double easeOutCubic(double t) {
        return 1 - Math.pow(1 - t, 3);
    }

    private static Color gradeColor(double g) {
        if (g < 3.0) return new Color(220,  60,  60);
        if (g < 5.0) return new Color(230, 140,  40);
        if (g < 7.0) return new Color(160, 210,  60);
        return              new Color(  0, 220, 100);
    }

    private static void enableAA(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}
