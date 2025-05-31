/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class EmployeePanel extends JPanel {

    // Gradient background colors (same as Dashboard)
    private final Color gradientStart = new Color(255, 204, 229);
    private final Color gradientEnd = new Color(255, 229, 180);

    // Labels for display
    private final JLabel idLabel = new JLabel("Employee ID: ");
    private final JLabel nameLabel = new JLabel("Full Name: ");
    private final JLabel birthdayLabel = new JLabel("Birthday: ");
    private final JLabel statusLabel = new JLabel("Input an employee ID to view details.");

    // UI components for search
    private final JTextField searchField = new JTextField(10);
    private final JButton searchButton = new JButton("Search");
    

    // Sample employee data (ID -> Employee object)
    private final Map<String, Employee> employeeMap = new LinkedHashMap<>();

    public EmployeePanel() {
        setLayout(new BorderLayout());
        setOpaque(false); // Needed for gradient background

        // Sample employee entries
        employeeMap.put("10001", new Employee("10001", "Garcia, Manuel III", "10/11/1983"));
        employeeMap.put("10002", new Employee("10002", "Lim, Antonio", "06/19/1988"));
        employeeMap.put("10003", new Employee("10003", "Aquino, Bianca Sofia", "08/04/1989"));

        // Fonts
        Font labelFont = new Font("Segoe UI", Font.BOLD, 20);
        Font statusFont = new Font("Segoe UI", Font.PLAIN, 13);

        idLabel.setFont(labelFont);
        nameLabel.setFont(labelFont);
        birthdayLabel.setFont(labelFont);
        statusLabel.setFont(statusFont);
        statusLabel.setForeground(Color.DARK_GRAY);

        idLabel.setForeground(Color.BLACK);
        nameLabel.setForeground(Color.BLACK);
        birthdayLabel.setForeground(Color.BLACK);

        // 🔍 Search panel setup
        JPanel searchPanel = new JPanel();
        searchPanel.setOpaque(false);
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 0, 0));

        JLabel searchLabel = new JLabel("Enter Employee ID:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Make button transparent
        searchButton.setFocusPainted(false);
        searchButton.setContentAreaFilled(false);
        searchButton.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0)));
        searchButton.setForeground(Color.BLACK);
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        searchButton.addMouseListener(new java.awt.event.MouseAdapter() {
         @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
        searchButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        searchButton.setForeground(Color.BLUE); // Optional hover color
    }

    @Override
    public void mouseExited(java.awt.event.MouseEvent evt) {
        searchButton.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0)));
        searchButton.setForeground(Color.BLACK);
    }
});

        // 🔘 Button action
        searchButton.addActionListener((ActionEvent e) -> {
            String empId = searchField.getText().trim();
            displayEmployeeById(empId);
        });

        // ↵ Enter key also triggers search
        searchField.addActionListener(e -> searchButton.doClick());

        // 📄 Info display panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 0, 0));

        infoPanel.add(idLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(birthdayLabel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(statusLabel);

        // Add panels to main layout
        add(searchPanel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
    }

    // Method to update employee info based on ID
    public void displayEmployeeById(String empId) {
        Employee emp = employeeMap.get(empId.trim());
        if (emp != null) {
            idLabel.setText("Employee ID: " + emp.getId());
            nameLabel.setText("Full Name: " + emp.getName());
            birthdayLabel.setText("Birthday: " + emp.getBirthday());
            statusLabel.setText("Employee found.");
            searchField.requestFocus();
            searchField.selectAll();
       } else {
            idLabel.setText("Employee ID: ");
            nameLabel.setText("Full Name: ");
            birthdayLabel.setText("Birthday: ");
            statusLabel.setText("Employee not found.");
            searchField.requestFocus();
            searchField.selectAll();
        }
    }

    // Paint gradient background
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(new GradientPaint(0, 0, gradientStart, 0, getHeight(), gradientEnd));
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    public JTextField getSearchField() {
    return searchField;
}

    // Simple Employee model
    private static class Employee {
        private final String id;
        private final String name;
        private final String birthday;

        public Employee(String id, String name, String birthday) {
            this.id = id;
            this.name = name;
            this.birthday = birthday;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getBirthday() {
            return birthday;
        }
    }

//    for testing
//    public static void main(String[] args) {
//    SwingUtilities.invokeLater(() -> {
//        JFrame frame = new JFrame("Employee Panel Test");
//        EmployeePanel employeePanel = new EmployeePanel();
//        
//        // Call test here: display employee by ID
//        employeePanel.displayEmployeeById(""); // You can change this to "10001", "10003", or invalid ID
//
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(500, 300);
//        frame.setLocationRelativeTo(null); // Center on screen
//        frame.setContentPane(employeePanel);
//        frame.setVisible(true);
//    });

}
