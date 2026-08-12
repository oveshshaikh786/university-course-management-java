package com.university.ui.animation;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A panel that wraps a single content component and crossfades between them
 * when the content is replaced.
 *
 * How it works:
 *   1. When setContentWithFade() is called, it captures a snapshot of the
 *      current content by rendering it into a BufferedImage.
 *   2. The new content is swapped in immediately (so the layout is correct).
 *   3. The snapshot is drawn on top via paintChildren() with a Timer that
 *      decreases its alpha from 1→0 over FADE_MS milliseconds.
 *   4. Once alpha hits 0 the snapshot is discarded.
 */
public class FadingContentPanel extends JPanel {

    private static final int FADE_MS  = 280;
    private static final int TIMER_HZ = 16;

    private BufferedImage snapshot;
    private float         overlayAlpha = 0f;
    private Timer         fadeTimer;

    public FadingContentPanel() {
        setLayout(new BorderLayout());
    }

    /**
     * Replace the current content with newContent and play a crossfade.
     * Safe to call before the panel is visible (snapshot will be null).
     */
    public void setContentWithFade(Component newContent) {
        // capture current state
        if (getWidth() > 0 && getHeight() > 0 && getComponentCount() > 0) {
            snapshot = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = snapshot.createGraphics();
            paintComponents(g2);
            g2.dispose();
        } else {
            snapshot = null;
        }

        // swap content
        removeAll();
        add(newContent, BorderLayout.CENTER);
        revalidate();

        // fade the snapshot out
        if (snapshot != null) {
            overlayAlpha = 1.0f;
            if (fadeTimer != null) fadeTimer.stop();

            fadeTimer = new Timer(TIMER_HZ, e -> {
                overlayAlpha -= (float) TIMER_HZ / FADE_MS;
                if (overlayAlpha <= 0f) {
                    overlayAlpha = 0f;
                    snapshot     = null;
                    ((Timer) e.getSource()).stop();
                }
                repaint();
            });
            fadeTimer.start();
        }
    }

    /** Draw the snapshot on top of the new content, then fade it away. */
    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);

        if (snapshot != null && overlayAlpha > 0f) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayAlpha));
            g2.drawImage(snapshot, 0, 0, getWidth(), getHeight(), null);
            g2.dispose();
        }
    }
}
