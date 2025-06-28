package gui;

import model.FileHandler;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import javax.swing.border.*;
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

    private JPanel formPanel;
    private JPanel bottomPanel;

    public AddEmployeePanel(FileHandler fileHandler, Runnable onEmployeeAdded) {
        this.fileHandler = fileHandler;
        this.onEmployeeAdded = onEmployeeAdded;

        setOpaque(false);
        setLayout(new BorderLayout(10, 10));

        // Add padding around the panel to prevent overlapping edges
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel to contain form fields
        formPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // Added gap between components
        formPanel.setOpaque(false);

        // Wrap formPanel in a container for extra padding
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setOpaque(false);
        formContainer.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding inside scroll
        formContainer.add(formPanel, BorderLayout.NORTH);

        // Scroll pane for form (if content overflows)
        JScrollPane scrollPane = new JScrollPane(formContainer);
        scrollPane.setPreferredSize(new Dimension(500, 450));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null); // Remove default border

        // Read headers
        fileHandler.readEmployeeFile();
        String[] headersFromFile = fileHandler.getEmployeeHeaders();

        // Merge headers
        LinkedHashMap<String, Boolean> finalHeaders = new LinkedHashMap<>();
        for (String header : headersFromFile) {
            finalHeaders.put(header, isRequired(header));
        }
        for (String extra : additionalFields) {
            finalHeaders.putIfAbsent(extra, true);
        }

        // Generate form fields
        for (Map.Entry<String, Boolean> entry : finalHeaders.entrySet()) {
            String header = entry.getKey();
            boolean required = entry.getValue();

            JPanel labelPanel = new JPanel(new BorderLayout());
            labelPanel.setOpaque(false);

            JLabel label = new JLabel(header + ":");

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

            formPanel.add(labelPanel);
            formPanel.add(inputField);
            fieldMap.put(header, inputField);
        }

        // Button styling
        submitButton.setBackground(Color.BLACK);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);

        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);

        submitButton.addActionListener(this::addEmployee);
        backButton.addActionListener(e -> {
            if (onEmployeeAdded != null) onEmployeeAdded.run();
        });

        // Bottom panel layout and padding
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottomPanel.add(new JLabel("* Required fields"));
        bottomPanel.add(backButton);
        bottomPanel.add(submitButton);

        // Add everything to the main panel
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Paint background gradient (top to half)
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

    // Add new employee after validation
    private void addEmployee(ActionEvent e) {
        String[] newRow = new String[fieldMap.size()];
        int index = 0;
        boolean hasError = false;
        StringBuilder errorMessages = new StringBuilder();

        for (JComponent field : fieldMap.values()) {
            field.setBorder(UIManager.getBorder("TextField.border"));
        }

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

            if (isRequired(header) && value.isEmpty()) {
                component.setBorder(new LineBorder(Color.RED, 2));
                hasError = true;
                errorMessages.append("- ").append(header).append(" is required.\n");
            }

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

        if (hasError) {
            JOptionPane.showMessageDialog(this, "Please fix the following:\n" + errorMessages,
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

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

    private boolean employeeNumberExists(String empNum) {
        for (String[] row : fileHandler.getEmployeeData()) {
            if (row.length > 0 && row[0].equalsIgnoreCase(empNum)) {
                return true;
            }
        }
        return false;
    }

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
