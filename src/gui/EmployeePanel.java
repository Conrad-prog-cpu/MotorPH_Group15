package gui;

import gui.DashboardTable;
import model.FileHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EmployeePanel extends JPanel {

    private final Color gradientStart = new Color(255, 204, 229);
    private final Color gradientEnd = new Color(255, 229, 180);

    private final JTextField searchField = new JTextField(20);
    private final DashboardTable dashboardTable;

    public EmployeePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Top Panel: Search and Action Buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(20, 50, 0, 50));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(buildSearchPanel());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JButton viewButton = new JButton("View Employee");
        styleMinimalButton(viewButton, 120, 36);
        viewButton.addActionListener(e -> showSelectedEmployeeDetails());
        rightPanel.add(viewButton);

        JButton addButton = new JButton("Add Employee");
        styleMinimalButton(addButton, 120, 36);
        addButton.addActionListener(e -> showAddEmployeeDialog());
        rightPanel.add(addButton);

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Modern Table
        dashboardTable = new DashboardTable(new FileHandler());
        dashboardTable.setBorder(new EmptyBorder(20, 50, 10, 50));
        add(dashboardTable, BorderLayout.CENTER);
    }

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);

        searchField.setToolTipText("Search by ID, Last Name, or First Name");
        searchField.setPreferredSize(new Dimension(200, 36));
        searchField.setBackground(Color.WHITE);
        searchField.setForeground(Color.BLACK);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton searchButton = new JButton("Search");
        styleColoredButton(searchButton, new Color(30, 144, 255), 80, 36);
        searchButton.addActionListener(this::performSearch);

        panel.add(searchField);
        panel.add(searchButton);
        return panel;
    }

    private void performSearch(ActionEvent e) {
        String query = searchField.getText().trim().toLowerCase();
        dashboardTable.filterTable(query);
    }

    private void showSelectedEmployeeDetails() {
        String[] selected = dashboardTable.getSelectedEmployee();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee first.");
            return;
        }

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Employee Details", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(500, 600);

        String[] labels = {
            "Employee No.", "Last Name", "First Name", "Birthday",
            "SSS No.", "PhilHealth No.", "TIN No.", "Pag-IBIG No."
        };

        for (int i = 0; i < labels.length; i++) {
            dialog.add(new JLabel(labels[i]));
            dialog.add(new JLabel(selected[i] != null && !selected[i].isEmpty() ? selected[i] : "[Not Provided]"));
        }

        JButton closeButton = new JButton("Close");
        styleMinimalButton(closeButton, 80, 30);
        closeButton.addActionListener(ev -> dialog.dispose());
        dialog.add(new JLabel()); // filler
        dialog.add(closeButton);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showAddEmployeeDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Add New Employee", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new GridLayout(9, 2, 20, 15));

        String[] labels = {
            "Employee No.", "Last Name", "First Name", "Birthday",
            "SSS No.", "PhilHealth No.", "TIN No.", "Pag-IBIG No."
        };

        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            dialog.add(new JLabel(labels[i]));
            fields[i] = new JTextField();
            dialog.add(fields[i]);
        }

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        styleMinimalButton(saveButton, 100, 30);
        styleMinimalButton(cancelButton, 100, 30);

        dialog.add(saveButton);
        dialog.add(cancelButton);

        saveButton.addActionListener(e -> {
            for (JTextField field : fields) {
                if (field.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            String[] newEmployee = new String[fields.length];
            for (int i = 0; i < fields.length; i++) {
                newEmployee[i] = fields[i].getText().trim();
            }

            if (dashboardTable.addEmployee(newEmployee)) {
                JOptionPane.showMessageDialog(dialog, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Employee ID already exists.", "Duplicate Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void styleMinimalButton(JButton button, int width, int height) {
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(width, height));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.BLACK);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
                super.paint(g2, c);
                g2.dispose();
            }
        });

        button.setMargin(new Insets(0, 15, 0, 15));
    }

    private void styleColoredButton(JButton button, Color bgColor, int width, int height) {
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(width, height));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
                super.paint(g2, c);
                g2.dispose();
            }
        });

        button.setMargin(new Insets(0, 15, 0, 15));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(new GradientPaint(0, 0, gradientStart, 0, getHeight(), gradientEnd));
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
