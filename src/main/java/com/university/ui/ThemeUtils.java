package com.university.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Static helpers for the unified dark UI theme.
 * All panels use these constants and methods so colours stay consistent.
 */
public final class ThemeUtils {

    private ThemeUtils() {}

    // ── palette ──────────────────────────────────────────────────────────────
    public static final Color BG_BASE    = new Color(18,  18,  30);
    public static final Color BG_CARD    = new Color(28,  28,  44);
    public static final Color BG_ROW_ALT = new Color(34,  34,  52);
    public static final Color BG_INPUT   = new Color(26,  26,  44);  // matches nimbusLightBackground
    public static final Color BG_HEADER  = new Color(20,  20,  34);
    public static final Color TEXT_PRI   = new Color(215, 215, 232);
    public static final Color TEXT_SEC   = new Color(128, 128, 158);
    public static final Color BORDER_COL = new Color(50,  50,  74);
    public static final Color ACCENT     = new Color(110, 100, 210);
    public static final Color SEL_BG     = new Color(65,  65,  108);

    // ── table ────────────────────────────────────────────────────────────────
    public static void styleTable(JTable table, JScrollPane scroll) {
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRI);
        table.setGridColor(BORDER_COL);
        table.setSelectionBackground(SEL_BG);
        table.setSelectionForeground(Color.WHITE);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                setBackground(sel ? SEL_BG : (row % 2 == 0 ? BG_CARD : BG_ROW_ALT));
                setForeground(sel ? Color.WHITE : TEXT_PRI);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setFont(new Font("SansSerif", Font.PLAIN, 13));
                return this;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_HEADER);
        header.setForeground(ACCENT);
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean focus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        t, value, sel, focus, row, col);
                lbl.setBackground(BG_HEADER);
                lbl.setForeground(ACCENT);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, BORDER_COL),
                        BorderFactory.createEmptyBorder(0, 10, 0, 10)));
                return lbl;
            }
        });

        if (scroll != null) {
            scroll.getViewport().setBackground(BG_CARD);
            scroll.setBorder(BorderFactory.createLineBorder(BORDER_COL));
        }
    }

    // ── text field ───────────────────────────────────────────────────────────
    public static void styleTextField(JTextField f) {
        f.setBackground(BG_INPUT);
        f.setForeground(TEXT_PRI);
        f.setCaretColor(TEXT_PRI);
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COL),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    // ── combo box ────────────────────────────────────────────────────────────
    public static void styleCombo(JComboBox<?> combo) {
        combo.setBackground(BG_INPUT);
        combo.setForeground(TEXT_PRI);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int idx, boolean sel, boolean focus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, idx, sel, focus);
                lbl.setBackground(sel ? SEL_BG : BG_INPUT);
                lbl.setForeground(sel ? Color.WHITE : TEXT_PRI);
                lbl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return lbl;
            }
        });
    }

    // ── spinner ──────────────────────────────────────────────────────────────
    public static void styleSpinner(JSpinner s) {
        s.setBackground(BG_INPUT);
        s.setForeground(TEXT_PRI);
        s.setBorder(BorderFactory.createLineBorder(BORDER_COL));
        if (s.getEditor() instanceof JSpinner.DefaultEditor de) {
            de.getTextField().setBackground(BG_INPUT);
            de.getTextField().setForeground(TEXT_PRI);
            de.getTextField().setCaretColor(TEXT_PRI);
            de.getTextField().setFont(new Font("SansSerif", Font.PLAIN, 13));
            de.getTextField().setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
        }
    }

    // ── accent button ────────────────────────────────────────────────────────
    public static JButton accentButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed()  ? ACCENT.darker()
                         : getModel().isRollover() ? new Color(130, 120, 230)
                         : ACCENT;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont().deriveFont(Font.BOLD, 13f));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 34));
        return btn;
    }

    // ── labels ───────────────────────────────────────────────────────────────
    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_PRI);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return l;
    }

    public static JLabel secondaryLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_SEC);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return l;
    }

    // ── titled border ────────────────────────────────────────────────────────
    public static TitledBorder titledBorder(String title) {
        TitledBorder b = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COL), title);
        b.setTitleColor(TEXT_SEC);
        b.setTitleFont(new Font("SansSerif", Font.PLAIN, 12));
        return b;
    }

    // ── dark panel helpers ───────────────────────────────────────────────────
    public static void darken(JPanel... panels) {
        for (JPanel p : panels) {
            p.setBackground(BG_BASE);
        }
    }

    public static void darkenCard(JPanel... panels) {
        for (JPanel p : panels) {
            p.setBackground(BG_CARD);
        }
    }
}
