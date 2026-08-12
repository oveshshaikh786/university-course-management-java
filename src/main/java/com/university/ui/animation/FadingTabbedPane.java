package com.university.ui.animation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A custom tabbed pane that fades between tabs instead of snapping.
 *
 * Replaces JTabbedPane. Tabs are rendered as a styled horizontal bar at the
 * top; the content area is a FadingContentPanel.
 *
 * Usage:
 *   FadingTabbedPane tabs = new FadingTabbedPane();
 *   tabs.addTab("Label", panel);                    // no callback
 *   tabs.addTab("Label", panel, panel::refresh);    // refresh before fade-in
 */
public class FadingTabbedPane extends JPanel {

    // ── colours ───────────────────────────────────────────────────────────────
    private static final Color BAR_BG      = new Color(22, 22, 36);
    private static final Color TAB_IDLE    = new Color(40, 40, 58);
    private static final Color TAB_HOVER   = new Color(55, 55, 78);
    private static final Color TAB_ACTIVE  = new Color(75, 75, 115);
    private static final Color TEXT_IDLE   = new Color(140, 140, 168);
    private static final Color TEXT_ACTIVE = Color.WHITE;
    private static final Color ACCENT      = new Color(120, 110, 220);

    // ── tab entries ───────────────────────────────────────────────────────────
    private record TabEntry(String title, Component content, Runnable onSelect, JButton button) {}
    private final List<TabEntry> tabs = new ArrayList<>();

    private int selectedIdx = -1;

    private final JPanel            tabBar;
    private final FadingContentPanel contentPanel;

    public FadingTabbedPane() {
        setLayout(new BorderLayout());

        tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 6));
        tabBar.setBackground(BAR_BG);
        tabBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 50, 72)));
        add(tabBar, BorderLayout.NORTH);

        contentPanel = new FadingContentPanel();
        add(contentPanel, BorderLayout.CENTER);
    }

    // ── public API ────────────────────────────────────────────────────────────
    /** Add a tab with no pre-switch callback. */
    public void addTab(String title, Component content) {
        addTab(title, content, null);
    }

    /**
     * Add a tab.
     *
     * @param onSelect  Optional Runnable called just before the fade-in starts,
     *                  so the panel can refresh its data while still hidden.
     */
    public void addTab(String title, Component content, Runnable onSelect) {
        int idx   = tabs.size();
        JButton btn = buildTabButton(title, idx);
        tabs.add(new TabEntry(title, content, onSelect, btn));
        tabBar.add(btn);

        if (tabs.size() == 1) selectTab(0);
    }

    public int getSelectedIndex() { return selectedIdx; }

    // ── internals ─────────────────────────────────────────────────────────────
    private void selectTab(int idx) {
        if (idx == selectedIdx || idx < 0 || idx >= tabs.size()) return;
        selectedIdx = idx;

        // refresh callback — runs before the fade so content is current
        TabEntry entry = tabs.get(idx);
        if (entry.onSelect() != null) entry.onSelect().run();

        // update button visuals
        for (int i = 0; i < tabs.size(); i++) {
            styleButton(tabs.get(i).button(), i == idx);
        }

        contentPanel.setContentWithFade(entry.content());
    }

    private JButton buildTabButton(String title, int idx) {
        JButton btn = new JButton(title) {
            @Override
            protected void paintComponent(Graphics g) {
                // custom rounded paint handled by button UI via opaque=false
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean active = (idx == selectedIdx);
                Color bg = active ? TAB_ACTIVE : (getModel().isRollover() ? TAB_HOVER : TAB_IDLE);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // accent underline when active
                if (active) {
                    g2.setColor(ACCENT);
                    g2.fillRoundRect(6, getHeight() - 3, getWidth() - 12, 3, 2, 2);
                }

                g2.setColor(active ? TEXT_ACTIVE : TEXT_IDLE);
                g2.setFont(getFont().deriveFont(active ? Font.BOLD : Font.PLAIN, 13f));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };

        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(148, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> selectTab(idx));

        // hover repaint
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.repaint(); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { btn.repaint(); }
        });

        return btn;
    }

    private void styleButton(JButton btn, boolean active) {
        btn.repaint();   // paintComponent reads selectedIdx which is already updated
    }
}
