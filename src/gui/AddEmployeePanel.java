package gui;

// Importing the FileHandler class from the model package
import model.FileHandler;

// Importing date picker components from external library
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

// Swing components
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

// AWT classes for layout and graphics
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

// This panel handles adding a new employee via GUI
public class AddEmployeePanel extends JPanel {

    // FileHandler instance to read/write employee data
    private final FileHandler fileHandler;

    // Stores form input components mapped to field names
    private final Map<String, JComponent> fieldMap = new LinkedHashMap<>();

    // Buttons for submitting the form or going back
    private final JButton submitButton = new JButton("Add Employee");
    private final JButton backButton = new JButton("Back");

    // Callback to be run after an employee is added
    private final Runnable onEmployeeAdded;

    // Additional fields not always in file headers
    private final String[] additionalFields = {"Birthday", "Phone Number"};

    // Panels to organize the form and bottom buttons
    private JPanel formPanel;
    private JPanel bottomPanel;

    // Constructor initializes UI and logic
    public AddEmployeePanel(FileHandler fileHandler, Runnable onEmployeeAdded) {
        this.fileHandler = fileHandler;
        this.onEmployeeAdded = onEmployeeAdded;

        // Set transparent background and layout
        setOpaque(false);
        setLayout(new BorderLayout(10, 10));

        // Add padding to panel edges
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create form layout with spacing
        formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setOpaque(false);

        // Container to hold form with extra padding
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setOpaque(false);
        formContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
        formContainer.add(formPanel, BorderLayout.NORTH);

        // Add scrolling capability to the form
        JScrollPane scrollPane = new JScrollPane(formContainer);
        scrollPane.setPreferredSize(new Dimension(500, 450));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        // Read employee headers from file
        fileHandler.readEmployeeFile();
        String[] headersFromFile = fileHandler.getEmployeeHeaders();

        // Combine headers from file and additional fields
        LinkedHashMap<String, Boolean> finalHeaders = new LinkedHashMap<>();
        for (String header : headersFromFile) {
            finalHeaders.put(header, isRequired(header));
        }
        for (String extra : additionalFields) {
            finalHeaders.putIfAbsent(extra, true);
        }

        // Generate form fields based on headers
        for (Map.Entry<String, Boolean> entry : finalHeaders.entrySet()) {
            String header = entry.getKey();
            boolean required = entry.getValue();

            JPanel labelPanel = new JPanel(new BorderLayout());
            labelPanel.setOpaque(false);

            JLabel label = new JLabel(header + ":");

            // Add red asterisk and bold if required
            if (required) {
                JLabel asterisk = new JLabel("*");
                asterisk.setForeground(Color.RED);
                asterisk.setFont(asterisk.getFont().deriveFont(Font.BOLD));
                label.setFont(label.getFont().deriveFont(Font.BOLD));
                label.setToolTipText("Required field");
                labelPanel.add(label, BorderLayout.WEST);
                labelPanel.add(asterisk, BorderLayout.EAST);
            } else {
                // Grey italic style for optional fields
                label.setForeground(Color.GRAY);
                label.setFont(label.getFont().deriveFont(Font.ITALIC));
                labelPanel.add(label, BorderLayout.WEST);
            }

            // Create appropriate input field
            JComponent inputField;
            switch (header.toLowerCase()) {
                case "birthday":
                    DatePickerSettings settings = new DatePickerSettings();
                    settings.setFormatForDatesCommonEra("yyyy/MM/dd");
                    inputField = new DatePicker(settings);
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
                    inputField = new JComboBox<>(new String[]{"Regular", "Probationary"});
                    break;
                default:
                    inputField = new JTextField();
            }

            // Add label and input to form
            formPanel.add(labelPanel);
            formPanel.add(inputField);
            fieldMap.put(header, inputField);
        }

        // Customize button appearance
        submitButton.setBackground(Color.BLACK);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);

        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);

        // Attach event listeners
        submitButton.addActionListener(this::addEmployee);
        backButton.addActionListener(e -> {
            if (onEmployeeAdded != null) onEmployeeAdded.run();
        });

        // Create bottom panel for buttons
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottomPanel.add(new JLabel("* Required fields"));
        bottomPanel.add(backButton);
        bottomPanel.add(submitButton);

        // Add scroll and buttons to main layout
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Draw gradient background for panel
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();

        GradientPaint gp = new GradientPaint(
                0, 0, new Color(0xFFD1DC),
                0, h / 2, new Color(0xFFE4CC)
        );

        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
        g2d.dispose();
    }

    // Add employee logic with validation
    private void addEmployee(ActionEvent e) {
        String[] newRow = new String[fieldMap.size()];
        int index = 0;
        boolean hasError = false;
        StringBuilder errorMessages = new StringBuilder();

        // Reset field borders
        for (JComponent field : fieldMap.values()) {
            field.setBorder(UIManager.getBorder("TextField.border"));
        }

        // Loop through each field to validate and get input
        for (Map.Entry<String, JComponent> entry : fieldMap.entrySet()) {
            String header = entry.getKey();
            JComponent component = entry.getValue();
            String value = "";

            if (component instanceof JTextField) {
                value = ((JTextField) component).getText().trim();
            } else if (component instanceof DatePicker) {
                value = ((DatePicker) component).getDate() != null
                        ? ((DatePicker) component).getDate().toString()
                        : "";
            } else if (component instanceof JComboBox<?>) {
                value = ((JComboBox<?>) component).getSelectedItem().toString();
            }

            // Validate required fields
            if (isRequired(header) && value.isEmpty()) {
                component.setBorder(new LineBorder(Color.RED, 2));
                hasError = true;
                errorMessages.append("- ").append(header).append(" is required.\n");
            }

            // Validate numeric-only fields
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

            // Check if employee number already exists
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

        // Show error if validation failed
        if (hasError) {
            JOptionPane.showMessageDialog(this, "Please fix the following:\n" + errorMessages,
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Append to file and confirm success/failure
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

    // Check if employee number already exists in file
    private boolean employeeNumberExists(String empNum) {
        for (String[] row : fileHandler.getEmployeeData()) {
            if (row.length > 0 && row[0].equalsIgnoreCase(empNum)) {
                return true;
            }
        }
        return false;
    }

    // Clear all input fields in the form
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

    // Determine if a field is required
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

    // Filter to allow only numeric input
    private static class NumericDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string != null && string.matches("\\d+")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text != null && text.matches("\\d+")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    // Ufor testing purpose and run standalone version of this panel
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            FileHandler fileHandler = new FileHandler();
//            fileHandler.readEmployeeFile();
//
//            JFrame frame = new JFrame("Add Employee Panel");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setSize(600, 650);
//            frame.setLocationRelativeTo(null);
//            frame.add(new AddEmployeePanel(fileHandler, null));
//            frame.setVisible(true);
//        });
//    }
}
