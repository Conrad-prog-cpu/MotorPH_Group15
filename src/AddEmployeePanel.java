/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ca
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AddEmployeePanel extends JPanel {

    private final FileHandler fileHandler;
    private final Map<String, JTextField> fieldMap = new LinkedHashMap<>();
    private final JButton submitButton = new JButton("Add Employee");

    public AddEmployeePanel(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        fileHandler.readEmployeeFile();  // Ensure headers are loaded

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Add New Employee"));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setPreferredSize(new Dimension(400, 400));

        String[] headers = fileHandler.getEmployeeHeaders();

        for (String header : headers) {
            JLabel label = new JLabel(header + (isRequired(header) ? " *:" : ":"));
            JTextField field = new JTextField();
            formPanel.add(label);
            formPanel.add(field);
            fieldMap.put(header, field);
        }

        submitButton.addActionListener(this::addEmployee);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(new JLabel("* Required fields"));
        bottomPanel.add(submitButton);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addEmployee(ActionEvent e) {
        List<String[]> data = fileHandler.getEmployeeData();

        String[] newRow = new String[fieldMap.size()];
        int index = 0;

        for (Map.Entry<String, JTextField> entry : fieldMap.entrySet()) {
            String header = entry.getKey();
            String value = entry.getValue().getText().trim();

            // Validate required fields
            if (isRequired(header) && value.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in the required field: " + header, "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check uniqueness of Employee Number
            if (header.equalsIgnoreCase("Employee Number") && employeeNumberExists(value)) {
                JOptionPane.showMessageDialog(this, "Employee Number already exists!", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            newRow[index++] = value;
        }

        data.add(newRow);
        fileHandler.writeEmployeeFile(data);

        JOptionPane.showMessageDialog(this, "✅ Employee added successfully!");
        clearFields();
    }

    private boolean employeeNumberExists(String empNum) {
        for (String[] row : fileHandler.getEmployeeData()) {
            if (row.length > 0 && row[0].equalsIgnoreCase(empNum)) {
                return true;
            }
        }
        return false;
    }

    private void clearFields() {
        for (JTextField field : fieldMap.values()) {
            field.setText("");
        }
    }

    private boolean isRequired(String header) {
        return header.equalsIgnoreCase("Employee Number") ||
               header.equalsIgnoreCase("Last Name") ||
               header.equalsIgnoreCase("First Name");
    }

    // ✅ Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileHandler fileHandler = new FileHandler();
            fileHandler.readEmployeeFile();

            JFrame frame = new JFrame("Add Employee Panel (Dynamic)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 600);
            frame.setLocationRelativeTo(null);
            frame.add(new AddEmployeePanel(fileHandler));
            frame.setVisible(true);
        });
    }
}
