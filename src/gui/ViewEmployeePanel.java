package gui;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class ViewEmployeePanel extends JFrame {

    public ViewEmployeePanel(Vector<Object> employeeData) {
        setTitle("Employee Details");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(0, 2, 10, 10));

        String[] labels = {
            "Employee #", "Last Name", "First Name", "Birthday", "Address", "Phone Number",
            "SSS #", "Philhealth #", "TIN #", "Pag-ibig #", "Status", "Position",
            "Immediate Supervisor", "Basic Salary", "Rice Subsidy", "Phone Allowance",
            "Clothing Allowance", "Gross Semi-monthly Rate", "Hourly Rate"
        };

        for (int i = 0; i < labels.length; i++) {
            add(new JLabel(labels[i] + ":"));
            add(new JLabel(employeeData.get(i).toString()));
        }

        setVisible(true);
    }

    // Uncomment for standalone testing
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            Vector<Object> mockData = new Vector<>();
//            mockData.add("EMP001");                  // Employee #
//            mockData.add("Doe");                     // Last Name
//            mockData.add("John");                    // First Name
//            mockData.add("1990-01-01");              // Birthday
//            mockData.add("123 Main St");             // Address
//            mockData.add("09171234567");             // Phone Number
//            mockData.add("12-3456789-0");            // SSS #
//            mockData.add("123456789012");            // Philhealth #
//            mockData.add("123-456-789");             // TIN #
//            mockData.add("9876-5432-1098");          // Pag-ibig #
//            mockData.add("Single");                  // Status
//            mockData.add("Software Engineer");       // Position
//            mockData.add("Jane Smith");              // Immediate Supervisor
//            mockData.add("50000");                   // Basic Salary
//            mockData.add("1500");                    // Rice Subsidy
//            mockData.add("1000");                    // Phone Allowance
//            mockData.add("1200");                    // Clothing Allowance
//            mockData.add("27000");                   // Gross Semi-monthly Rate
//            mockData.add("312.50");                  // Hourly Rate
//
//            new ViewEmployeePanel(mockData);
//        });
//    }
}
