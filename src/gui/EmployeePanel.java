package gui;

import gui.EmployeeTable;
import model.FileHandler;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Vector;

public class EmployeePanel extends JPanel {

    private final Color gradientStart = new Color(255, 204, 229);
    private final Color gradientEnd = new Color(255, 229, 180);

    private final JTextField searchField = new JTextField(20);
    private final FileHandler fileHandler;
    private final EmployeeTable dashboardTable;
    
    public EmployeePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        fileHandler = new FileHandler();
        dashboardTable = new EmployeeTable(fileHandler);
        // Top Panel: Search and Action Buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(20, 50, 0, 50));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(buildSearchPanel());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        // View Employee Button
        JButton viewButton = new JButton("View Employee");
        styleMinimalButton(viewButton, 120, 36);
        viewButton.addActionListener(e -> showSelectedEmployeeDetails());
        rightPanel.add(viewButton);

        // Add Employee Button
        JButton addButton = new JButton("Add Employee");
        styleMinimalButton(addButton, 120, 36);
        addButton.addActionListener(e -> showAddEmployeeDialog());
        rightPanel.add(addButton);

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        

        // Modern Table
        
        dashboardTable.setBorder(new EmptyBorder(20, 50, 10, 50));
        add(dashboardTable, BorderLayout.CENTER);
        
        // Inside your EmployeePanel constructor, after `add(dashboardTable, BorderLayout.CENTER);`
        
       // Create a bottom panel with right alignment
      
        // Bottom Button Panel aligned to right with Update and Delete side by side
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // right-aligned
        bottomButtonPanel.setOpaque(false);
        bottomButtonPanel.setBorder(new EmptyBorder(10, 50, 20, 50)); // match top padding

        // Create Update Button (black)
        JButton updateButton = new JButton("Update");
        styleColoredButton(updateButton, Color.BLACK, 120, 36); // Match top button size
        updateButton.setEnabled(false);
        bottomButtonPanel.add(updateButton);

        // Create Delete Button (red)
        JButton deleteButton = new JButton("Delete");
        styleColoredButton(deleteButton, new Color(220, 20, 60), 120, 36); // Crimson red
        deleteButton.setEnabled(false);
        bottomButtonPanel.add(deleteButton);

        // Add to main layout
        add(bottomButtonPanel, BorderLayout.SOUTH);
        // Position below the table

        // Add a ListSelectionListener to the table to toggle button states based on row selection
        dashboardTable.getTable().getSelectionModel().addListSelectionListener(e -> {
        boolean isSelected = dashboardTable.getTable().getSelectedRow() != -1;
        updateButton.setEnabled(isSelected);
        deleteButton.setEnabled(isSelected);
        });

        updateButton.addActionListener(e -> {
    int selectedRow = dashboardTable.getTable().getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select an employee to update.");
        return;
    }

         String employeeId = (String) dashboardTable.getTable().getValueAt(selectedRow, 0);
        String[] employee = fileHandler.getEmployeeById(employeeId);
        if (employee == null) {
        JOptionPane.showMessageDialog(this, "Employee not found.");
        return;
         }

    JTextField[] fields = new JTextField[employee.length];
    JPanel panel = new JPanel(new SpringLayout());
    String[] headers = fileHandler.getEmployeeHeaders();


    for (int i = 0; i < employee.length; i++) {
        panel.add(new JLabel(headers[i] + ":"));
        fields[i] = new JTextField(employee[i], 20);
        panel.add(fields[i]);
         }

        SpringUtilities.makeCompactGrid(panel, employee.length, 2, 6, 6, 6, 6);
        int result = JOptionPane.showConfirmDialog(this, panel, "Update Employee", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
        String[] updatedData = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            updatedData[i] = fields[i].getText().trim();
        }

        try {
             fileHandler.updateEmployeeField(employeeId, updatedData);
             dashboardTable.refreshTable(fileHandler.getEmployeeData());
            JOptionPane.showMessageDialog(this, "Employee updated successfully.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to update: " + ex.getMessage());
        }
        }
        });

            deleteButton.addActionListener(e -> {
            int selectedRow = dashboardTable.getTable().getSelectedRow();
        if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select an employee to delete.");
        return;
        }

        String employeeId = (String) dashboardTable.getTable().getValueAt(selectedRow, 0);
         int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete employee ID " + employeeId + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        try {
            fileHandler.deleteEmployeeById(employeeId);
            dashboardTable.refreshTable(fileHandler.getEmployeeData());
            JOptionPane.showMessageDialog(this, "Employee deleted.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete: " + ex.getMessage());
        }
    }
});

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
    Vector<Object> selected = dashboardTable.getSelectedEmployeeFullDetails();
    if (selected == null || selected.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please select an employee first.");
        return;
    }
    
    new ViewEmployeePanel(selected);
}

    private void showAddEmployeeDialog() {
        FileHandler fileHandler = new FileHandler();
        fileHandler.readEmployeeFile();

        JFrame frame = new JFrame("Add Employee Panel with Date Picker");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(550, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.add(new AddEmployeePanel(fileHandler, () -> {
            dashboardTable.refreshTable(fileHandler.getEmployeeData());
            frame.setVisible(false);
        }));
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
