package gui;

import model.FileHandler;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

public class AddEmployeePanel extends JPanel {

    private final FileHandler fileHandler;
    private final Map<String, JComponent> fieldMap = new LinkedHashMap<>();
    private final JButton submitButton = new JButton("Add Employee");
    private final JButton backButton = new JButton("Back");

    private final Runnable onEmployeeAdded;
    private final String[] additionalFields = {"Birthday", "Phone Number"};

    public AddEmployeePanel(FileHandler fileHandler, Runnable onEmployeeAdded) {
        this.fileHandler = fileHandler;
        this.onEmployeeAdded = onEmployeeAdded;

        // Set up main panel layout
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Add New Employee"));

        // Panel to hold form fields
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setPreferredSize(new Dimension(450, 450));

        // Load headers from employee file
        fileHandler.readEmployeeFile();
        String[] headersFromFile = fileHandler.getEmployeeHeaders();

        // Merge default and additional required fields
        LinkedHashMap<String, Boolean> finalHeaders = new LinkedHashMap<>();
        for (String header : headersFromFile) {
            finalHeaders.put(header, isRequired(header));
        }
        for (String extra : additionalFields) {
            finalHeaders.putIfAbsent(extra, true);
        }

        // Dynamically generate label-input pairs
        for (Map.Entry<String, Boolean> entry : finalHeaders.entrySet()) {
            String header = entry.getKey();
            boolean required = entry.getValue();

            JPanel labelPanel = new JPanel(new BorderLayout());
            JLabel label = new JLabel(header + ":");

            // Visual indicator for required fields
            if (required) {
                JLabel asterisk = new JLabel("*");
                asterisk.setForeground(Color.RED);
                asterisk.setFont(asterisk.getFont().deriveFont(Font.BOLD));
                label.setFont(label.getFont().deriveFont(Font.BOLD));
                label.setToolTipText("Required field");
                labelPanel.add(label, BorderLayout.WEST);
                labelPanel.add(asterisk, BorderLayout.EAST);
            } else {
                label.setForeground(Color.GRAY);
                label.setFont(label.getFont().deriveFont(Font.ITALIC));
                labelPanel.add(label, BorderLayout.WEST);
            }

            // Define input field based on field type
            JComponent inputField;
            switch (header.toLowerCase()) {
                case "birthday":
                    DatePickerSettings settings = new DatePickerSettings();
                    settings.setFormatForDatesCommonEra("yyyy/MM/dd");
                    DatePicker datePicker = new DatePicker(settings);
                    inputField = datePicker;
                    break;

                case "phone number":
                case "employee number":
                case "sss":
                case "philhealth":
                case "tin":
                case "pagibig":
                    JTextField numericField = new JTextField();
                    ((AbstractDocument) numericField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
                    inputField = numericField;
                    break;

                case "status":
                    JComboBox<String> statusBox = new JComboBox<>(new String[]{"Regular", "Probationary"});
                    inputField = statusBox;
                    break;

                default:
                    inputField = new JTextField();
            }

            // Add label and field to the form panel
            formPanel.add(labelPanel);
            formPanel.add(inputField);
            fieldMap.put(header, inputField);
        }

        // Set up button actions
        submitButton.addActionListener(this::addEmployee);
        backButton.addActionListener(e -> {
            if (onEmployeeAdded != null) onEmployeeAdded.run();
        });

        // Bottom panel for buttons and reminder
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(new JLabel("* Required fields"));
        bottomPanel.add(backButton);
        bottomPanel.add(submitButton);

        // Add everything to main panel
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Collects and validates input data, then adds employee to file.
     */
    private void addEmployee(ActionEvent e) {
        String[] newRow = new String[fieldMap.size()];
        int index = 0;
        boolean hasError = false;
        StringBuilder errorMessages = new StringBuilder();

        // Reset all field borders before validation
        for (JComponent field : fieldMap.values()) {
            field.setBorder(UIManager.getBorder("TextField.border"));
        }

        // Loop through each field for validation
        for (Map.Entry<String, JComponent> entry : fieldMap.entrySet()) {
            String header = entry.getKey();
            JComponent component = entry.getValue();
            String value = "";

            // Extract field value
            if (component instanceof JTextField) {
                value = ((JTextField) component).getText().trim();
            } else if (component instanceof DatePicker) {
                value = ((DatePicker) component).getDate() != null ? ((DatePicker) component).getDate().toString() : "";
            } else if (component instanceof JComboBox<?>) {
                value = ((JComboBox<?>) component).getSelectedItem().toString();
            }

            // Check if required field is empty
            if (isRequired(header) && value.isEmpty()) {
                component.setBorder(new LineBorder(Color.RED, 2));
                hasError = true;
                errorMessages.append("- ").append(header).append(" is required.\n");
            }

            // Validate numeric fields
            if ((header.equalsIgnoreCase("Employee #") ||
                 header.equalsIgnoreCase("Phone Number") ||
                 header.equalsIgnoreCase("SSS #") ||
                 header.equalsIgnoreCase("Philhealth #") ||
                 header.equalsIgnoreCase("TIN #") ||
                 header.equalsIgnoreCase("Pag-ibig #")) &&
                 !value.matches("\\d+")) {

                component.setBorder(new LineBorder(Color.RED, 2));
                hasError = true;
                errorMessages.append("- ").append(header).append(" must be numeric.\n");
            }

            // Check for duplicate employee number
            if (header.equalsIgnoreCase("Employee Number") && employeeNumberExists(value)) {
                component.setBorder(new LineBorder(Color.RED, 2));
                component.requestFocus();
                JOptionPane.showMessageDialog(this,
                        "Employee Number already exists!",
                        "Duplicate Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            newRow[index++] = value;
        }

        // Show validation errors if any
        if (hasError) {
            JOptionPane.showMessageDialog(this, "Please fix the following:\n" + errorMessages,
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Append to file and notify user
        if (fileHandler.appendEmployeeToFile(newRow)) {
            fileHandler.readEmployeeFile();
            JOptionPane.showMessageDialog(this, "✅ Employee added successfully!");
            clearFields();
            if (onEmployeeAdded != null) onEmployeeAdded.run();
            this.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(this, "❌ Failed to add employee.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Checks if given employee number already exists in file.
     */
    private boolean employeeNumberExists(String empNum) {
        for (String[] row : fileHandler.getEmployeeData()) {
            if (row.length > 0 && row[0].equalsIgnoreCase(empNum)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clears all input fields after submission or reset.
     */
    private void clearFields() {
        for (Map.Entry<String, JComponent> entry : fieldMap.entrySet()) {
            JComponent field = entry.getValue();
            if (field instanceof JTextField) {
                ((JTextField) field).setText("");
            } else if (field instanceof DatePicker) {
                ((DatePicker) field).clear();
            } else if (field instanceof JComboBox<?>) {
                ((JComboBox<?>) field).setSelectedIndex(0);
            }
            field.setBorder(UIManager.getBorder("TextField.border"));
        }
    }

    /**
     * Determines if a field is required based on its name.
     */
    private boolean isRequired(String header) {
        return header.equalsIgnoreCase("Employee #")
                || header.equalsIgnoreCase("Last Name")
                || header.equalsIgnoreCase("First Name")
                || header.equalsIgnoreCase("Birthday")
                || header.equalsIgnoreCase("Phone Number")
                || header.equalsIgnoreCase("SSS #")
                || header.equalsIgnoreCase("Philhealth #")
                || header.equalsIgnoreCase("TIN #")
                || header.equalsIgnoreCase("Pag-ibig #");
    }

    /**
     * Document filter that restricts input to numeric characters only.
     */
    private static class NumericDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string != null && string.matches("\\d+")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text != null && text.matches("\\d+")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    // Uncomment this to test the panel independently
    /*
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileHandler fileHandler = new FileHandler();
            fileHandler.readEmployeeFile();

            JFrame frame = new JFrame("Add Employee Panel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(550, 600);
            frame.setLocationRelativeTo(null);
            frame.add(new AddEmployeePanel(fileHandler, null));
            frame.setVisible(true);
        });
    }
    */
}
