package com.university.ui;

import com.university.model.Learner;
import com.university.service.LearnerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LearnersPanel extends JPanel {

    private final LearnerService    learnerService;
    private final MainWindow        mainWindow;
    private final DefaultTableModel tableModel;

    private final JTextField nameField  = new JTextField(20);
    private final JTextField emailField = new JTextField(20);

    private static final String[] COLUMNS = { "Name", "Email", "ID" };

    public LearnersPanel(LearnerService learnerService, MainWindow mainWindow) {
        this.learnerService = learnerService;
        this.mainWindow     = mainWindow;

        setLayout(new BorderLayout(10, 10));
        setBackground(ThemeUtils.BG_BASE);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // ── registration form ──────────────────────────────────────────────────
        ThemeUtils.styleTextField(nameField);
        ThemeUtils.styleTextField(emailField);
        add(buildFormPanel(), BorderLayout.NORTH);

        // ── learner table ──────────────────────────────────────────────────────
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.getColumnModel().getColumn(2).setPreferredWidth(280);

        JScrollPane scroll = new JScrollPane(table);
        ThemeUtils.styleTable(table, scroll);
        add(scroll, BorderLayout.CENTER);

        loadData();
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(ThemeUtils.BG_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
                ThemeUtils.titledBorder("Register New Learner"),
                BorderFactory.createEmptyBorder(4, 4, 8, 4)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; form.add(ThemeUtils.label("Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; form.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; form.add(ThemeUtils.label("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; form.add(emailField, gbc);

        JButton registerBtn = ThemeUtils.accentButton("Register");
        registerBtn.addActionListener(e -> registerLearner());
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor  = GridBagConstraints.EAST;
        gbc.weightx = 0;
        gbc.fill    = GridBagConstraints.NONE;
        form.add(registerBtn, gbc);

        return form;
    }

    private void registerLearner() {
        String name  = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name and email are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Learner learner = learnerService.registerLearner(name, email);
        JOptionPane.showMessageDialog(this,
                "Learner registered!\nID: " + learner.getId(),
                "Success", JOptionPane.INFORMATION_MESSAGE);

        nameField.setText("");
        emailField.setText("");
        loadData();
        mainWindow.onLearnersChanged();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Learner> learners = learnerService.getAllLearners();
        for (Learner l : learners) {
            tableModel.addRow(new Object[]{ l.getName(), l.getEmail(), l.getId() });
        }
    }
}
