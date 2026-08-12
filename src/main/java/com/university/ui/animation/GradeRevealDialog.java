package com.university.ui.animation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Animated grade reveal dialog.
 *
 * Phases:
 *   0 – 1500ms : circular arc fills + grade counter counts up (easeOutCubic)
 *   1500–2100ms : PASSED / FAILED label fades in
 *   2100ms+     : if passed, particles burst outward
 *
 * Click anywhere to close.
 */
public class GradeRevealDialog extends JDialog {

    // ── timing ──────────────────────────────────────────────────────────────
    private static final int ARC_MS   = 1500;
    private static final int FADE_MS  = 600;
    private static final int TIMER_HZ = 16;   // ~60 fps

    // ── layout constants ────────────────────────────────────────────────────
    private static final int W         = 400;
    private static final int H         = 430;
    private static final int ARC_SIZE  = 220;
    private static final int ARC_X     = (W - ARC_SIZE) / 2;   // 90
    private static final int ARC_Y     = 30;
    private static final int ARC_CX    = ARC_X + ARC_SIZE / 2; // 200
    private static final int ARC_CY    = ARC_Y + ARC_SIZE / 2; // 140

    // ── state ───────────────────────────────────────────────────────────────
    private final double   targetGrade;
    private final boolean  passed;
    private double         currentGrade  = 0;
    private double         arcProgress   = 0;   // 0→1 tracks phase-1 progress
    private float          textAlpha     = 0;
    private boolean        particlesDone = false;
    private final List<Particle> particles = new ArrayList<>();

    private final Timer    animTimer;
    private final long     startTime;

    // ── constructor ─────────────────────────────────────────────────────────
    public GradeRevealDialog(Frame parent, double grade, boolean passed) {
        super(parent, true);
        this.targetGrade = grade;
        this.passed      = passed;

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        GradePanel panel = new GradePanel();
        panel.setPreferredSize(new Dimension(W, H));
        panel.setOpaque(false);
        panel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { close(); }
        });
        add(panel);
        pack();
        setLocationRelativeTo(parent);

        startTime = System.currentTimeMillis();
        animTimer = new Timer(TIMER_HZ, e -> tick(panel));
        animTimer.start();
    }

    // ── animation tick ──────────────────────────────────────────────────────
    private void tick(GradePanel panel) {
        long elapsed = System.currentTimeMillis() - startTime;

        if (elapsed <= ARC_MS) {
            double t = elapsed / (double) ARC_MS;
            double eased = easeOutCubic(t);
            arcProgress  = eased;
            currentGrade = targetGrade * eased;

        } else {
            arcProgress  = 1.0;
            currentGrade = targetGrade;

            long fadeElapsed = elapsed - ARC_MS;
            textAlpha = Math.min(1.0f, (float) (fadeElapsed / (double) FADE_MS));

            if (passed && !particlesDone && textAlpha >= 0.6f) {
                launchParticles();
                particlesDone = true;
            }
        }

        particles.removeIf(Particle::isDead);
        particles.forEach(Particle::update);
        panel.repaint();
    }

    private void close() {
        animTimer.stop();
        dispose();
    }

    // ── particle launch ─────────────────────────────────────────────────────
    private void launchParticles() {
        Random rand = new Random();
        Color[] palette = {
            new Color(255, 215, 0),   // gold
            new Color(0, 220, 100),   // mint green
            new Color(100, 180, 255), // sky blue
            new Color(255, 120, 180), // pink
            new Color(255, 160, 40),  // orange
        };
        for (int i = 0; i < 55; i++) {
            Particle p  = new Particle();
            p.x         = ARC_CX;
            p.y         = ARC_CY;
            double angle = rand.nextDouble() * 2 * Math.PI;
            double speed = 3 + rand.nextDouble() * 8;
            p.vx    = (float) (Math.cos(angle) * speed);
            p.vy    = (float) (Math.sin(angle) * speed) - 4f; // upward bias
            p.alpha = 1.0f;
            p.color = palette[rand.nextInt(palette.length)];
            p.radius = 4 + rand.nextInt(6);
            particles.add(p);
        }
    }

    // ── easing ──────────────────────────────────────────────────────────────
    private static double easeOutCubic(double t) {
        return 1 - Math.pow(1 - t, 3);
    }

    // ── color by grade ──────────────────────────────────────────────────────
    private static Color gradeColor(double g) {
        if (g < 3.0) return new Color(220,  60,  60);
        if (g < 5.0) return new Color(230, 140,  40);
        if (g < 7.0) return new Color(160, 210,  60);
        return              new Color(  0, 220, 100);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Custom paint panel
    // ═══════════════════════════════════════════════════════════════════════
    private class GradePanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            enableAA(g2);

            // ── dark card background ──────────────────────────────────────
            g2.setColor(new Color(14, 14, 26));
            g2.fill(new RoundRectangle2D.Float(0, 0, W, H, 24, 24));

            // thin border
            g2.setColor(new Color(50, 50, 72));
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(1, 1, W - 2, H - 2, 24, 24));

            drawArc(g2);
            drawGradeNumber(g2);
            if (textAlpha > 0) drawResult(g2);
            drawParticles(g2);
            drawHint(g2);

            g2.dispose();
        }

        // ── arc ────────────────────────────────────────────────────────────
        private void drawArc(Graphics2D g2) {
            int stroke = 18;

            // track
            g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(38, 38, 58));
            g2.drawArc(ARC_X, ARC_Y, ARC_SIZE, ARC_SIZE, 90, -360);

            int sweep = (int) (arcProgress * (targetGrade / 10.0) * 360);
            if (sweep <= 0) return;

            Color c = gradeColor(currentGrade);

            // outer glow
            g2.setStroke(new BasicStroke(stroke + 10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 35));
            g2.drawArc(ARC_X - 5, ARC_Y - 5, ARC_SIZE + 10, ARC_SIZE + 10, 90, -sweep);

            // mid glow
            g2.setStroke(new BasicStroke(stroke + 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 70));
            g2.drawArc(ARC_X, ARC_Y, ARC_SIZE, ARC_SIZE, 90, -sweep);

            // main arc
            g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(c);
            g2.drawArc(ARC_X, ARC_Y, ARC_SIZE, ARC_SIZE, 90, -sweep);
        }

        // ── grade counter ───────────────────────────────────────────────────
        private void drawGradeNumber(Graphics2D g2) {
            // big number
            g2.setFont(new Font("SansSerif", Font.BOLD, 60));
            g2.setColor(Color.WHITE);
            String num = String.format("%.1f", currentGrade);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(num, (W - fm.stringWidth(num)) / 2, ARC_CY + fm.getAscent() / 2 - 8);

            // "out of 10"
            g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g2.setColor(new Color(110, 110, 140));
            String sub = "out of 10";
            fm = g2.getFontMetrics();
            g2.drawString(sub, (W - fm.stringWidth(sub)) / 2, ARC_CY + 40);
        }

        // ── PASSED / FAILED label ───────────────────────────────────────────
        private void drawResult(Graphics2D g2) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, textAlpha));

            Color rc = passed ? new Color(0, 220, 100) : new Color(220, 60, 60);
            String label = passed ? "PASSED!" : "FAILED";

            // subtle glow behind text
            g2.setFont(new Font("SansSerif", Font.BOLD, 34));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (W - fm.stringWidth(label)) / 2;
            int ty = ARC_Y + ARC_SIZE + 58;

            g2.setColor(new Color(rc.getRed(), rc.getGreen(), rc.getBlue(), 50));
            for (int d = 3; d >= 1; d--) {
                g2.drawString(label, tx - d, ty);
                g2.drawString(label, tx + d, ty);
                g2.drawString(label, tx, ty - d);
                g2.drawString(label, tx, ty + d);
            }

            g2.setColor(rc);
            g2.drawString(label, tx, ty);

            // divider
            g2.setColor(new Color(45, 45, 65, (int)(textAlpha * 255)));
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(50, ty + 18, W - 50, ty + 18);

            g2.setComposite(AlphaComposite.SrcOver);
        }

        // ── particles ───────────────────────────────────────────────────────
        private void drawParticles(Graphics2D g2) {
            for (Particle p : particles) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0, p.alpha)));
                g2.setColor(p.color);
                g2.fillOval((int) p.x - p.radius, (int) p.y - p.radius, p.radius * 2, p.radius * 2);
            }
            g2.setComposite(AlphaComposite.SrcOver);
        }

        // ── click-to-close hint ─────────────────────────────────────────────
        private void drawHint(Graphics2D g2) {
            if (textAlpha < 0.3f) return;
            float a = Math.min(textAlpha, 0.45f);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.setColor(new Color(100, 100, 130));
            String hint = "click anywhere to close";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(hint, (W - fm.stringWidth(hint)) / 2, H - 14);
            g2.setComposite(AlphaComposite.SrcOver);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Particle
    // ═══════════════════════════════════════════════════════════════════════
    private static class Particle {
        float x, y, vx, vy, alpha;
        Color color;
        int   radius;

        void update() {
            x    += vx;
            y    += vy;
            vy   += 0.20f;  // gravity
            vx   *= 0.98f;  // air drag
            alpha -= 0.013f;
        }

        boolean isDead() { return alpha <= 0; }
    }

    // ── helpers ──────────────────────────────────────────────────────────────
    private static void enableAA(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
    }
}
