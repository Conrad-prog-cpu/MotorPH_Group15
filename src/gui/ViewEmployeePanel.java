package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Vector;

public class ViewEmployeePanel extends JFrame {

    public ViewEmployeePanel(Vector<Object> employeeData) {
        setTitle("Employee Details");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Outer panel for margins and left alignment
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Inner panel with GridBagLayout for tight spacing
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;

        String[] labels = {
            "Employee #", "Last Name", "First Name", "Birthday", "Address", "Phone Number",
            "SSS #", "Philhealth #", "TIN #", "Pag-ibig #", "Status", "Position",
            "Immediate Supervisor", "Basic Salary", "Rice Subsidy", "Phone Allowance",
            "Clothing Allowance", "Gross Semi-monthly Rate", "Hourly Rate"
        };

        for (int i = 0; i < labels.length; i++) {
            // Label
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.3;
            contentPanel.add(new JLabel(labels[i] + ":"), gbc);

            // Data as wrapped text area
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            JTextArea dataField = new JTextArea(employeeData.get(i).toString());
            dataField.setWrapStyleWord(true);
            dataField.setLineWrap(true);
            dataField.setEditable(false);
            dataField.setOpaque(false);         // make it look like a label
            dataField.setFocusable(false);
            dataField.setBorder(null);          // remove border
            contentPanel.add(dataField, gbc);
        }

        mainPanel.add(new JScrollPane(contentPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }
}
