package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Vector;

// This class creates a GUI window to display employee details in a formatted layout
public class ViewEmployeePanel extends JFrame {

    // Constructor that accepts a Vector containing employee data to display
    public ViewEmployeePanel(Vector<Object> employeeData) {
        // Set the window title
        setTitle("Employee Details");

        // Set window size
        setSize(500, 600);

        // Center the window on the screen
        setLocationRelativeTo(null);

        // Close only this window when the user clicks the close button
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel with border/margin
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Content panel that uses GridBagLayout to align labels and values properly
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(5, 5, 5, 5); // spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;

        // Labels for each field of employee data
        String[] labels = {
            "Employee #", "Last Name", "First Name", "Birthday", "Address", "Phone Number",
            "SSS #", "Philhealth #", "TIN #", "Pag-ibig #", "Status", "Position",
            "Immediate Supervisor", "Basic Salary", "Rice Subsidy", "Phone Allowance",
            "Clothing Allowance", "Gross Semi-monthly Rate", "Hourly Rate"
        };

        // Loop through each label and corresponding data to add them to the layout
        for (int i = 0; i < labels.length; i++) {
            // Set position and weight for the label
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.3;

            // Add label to the left
            contentPanel.add(new JLabel(labels[i] + ":"), gbc);

            // Set position and weight for the data display
            gbc.gridx = 1;
            gbc.weightx = 0.7;

            // Display data using JTextArea to allow text wrapping
            JTextArea dataField = new JTextArea(employeeData.get(i).toString());
            dataField.setWrapStyleWord(true);      // wrap at word boundaries
            dataField.setLineWrap(true);           // enable line wrapping
            dataField.setEditable(false);          // make it read-only
            dataField.setOpaque(false);            // make it look like a label
            dataField.setFocusable(false);         // prevent focus
            dataField.setBorder(null);             // remove border

            // Add the data field to the content panel
            contentPanel.add(dataField, gbc);
        }

        // Add scroll pane in case content is too long to fit the screen
        mainPanel.add(new JScrollPane(contentPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);

        // Add the main panel to the frame
        add(mainPanel);

        // Make the window visible
        setVisible(true);
    }
}
