package gui;
import model.FileHandler;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AddEmployeePanel extends JPanel {

    private final FileHandler fileHandler;
    private final Map<String, JComponent> fieldMap = new LinkedHashMap<>();
    private final JButton submitButton = new JButton("Add Employee");

    private final Runnable onEmployeeAdded; // Callback to refresh table and return
    private final String[] additionalFields = {"Birthday", "Phone Number"};

    public AddEmployeePanel(FileHandler fileHandler, Runnable onEmployeeAdded) {
        this.fileHandler = fileHandler;
        fileHandler.readEmployeeFile();
        this.onEmployeeAdded = onEmployeeAdded;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Add New Employee"));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setPreferredSize(new Dimension(450, 450));

        String[] headersFromFile = fileHandler.getEmployeeHeaders();
        LinkedHashMap<String, Boolean> finalHeaders = new LinkedHashMap<>();
        for (String header : headersFromFile) {
            finalHeaders.put(header, isRequired(header));
        }
        for (String extra : additionalFields) {
            finalHeaders.putIfAbsent(extra, true);
        }

        for (Map.Entry<String, Boolean> entry : finalHeaders.entrySet()) {
            String header = entry.getKey();
            boolean required = entry.getValue();

            JPanel labelPanel = new JPanel(new BorderLayout());
            JLabel label = new JLabel(header + ":");

            if (required) {
                JLabel asterisk = new JLabel("*");
                asterisk.setForeground(Color.RED);
                asterisk.setFont(asterisk.getFont().deriveFont(Font.BOLD));
                asterisk.setToolTipText("Required");
                label.setToolTipText("Required field");
                labelPanel.add(label, BorderLayout.WEST);
                labelPanel.add(asterisk, BorderLayout.EAST);
            } else {
                label.setForeground(Color.GRAY);
                label.setFont(label.getFont().deriveFont(Font.ITALIC));
                labelPanel.add(label, BorderLayout.WEST);
            }

            JComponent inputField;

            if (header.equalsIgnoreCase("Birthday")) {
                // Create a formatted field for date input
                try {
                    MaskFormatter dateMask = new MaskFormatter("####/##/##");
                    dateMask.setPlaceholderCharacter('_');
                    JFormattedTextField dateField = new JFormattedTextField(dateMask);
                    dateField.setToolTipText("Format: YYYY/MM/DD");
                    inputField = dateField;
                } catch (Exception e) {
                    inputField = new JTextField();
                }
            } else if (header.equalsIgnoreCase("Phone Number")) {
                JTextField phoneField = new JTextField();
                ((AbstractDocument) phoneField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
                inputField = phoneField;
            } else {
                inputField = new JTextField();
            }

            formPanel.add(labelPanel);
            formPanel.add(inputField);
            fieldMap.put(header, inputField);
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

        for (JComponent field : fieldMap.values()) {
            field.setBorder(UIManager.getBorder("TextField.border"));
        }

        for (Map.Entry<String, JComponent> entry : fieldMap.entrySet()) {
            String header = entry.getKey();
            JComponent component = entry.getValue();
            String value = (component instanceof JTextField) ? ((JTextField) component).getText().trim() : "";

            if (isRequired(header) && value.isEmpty()) {
                component.setBorder(new LineBorder(Color.RED, 2));
                component.requestFocus();
                JOptionPane.showMessageDialog(this,
                        "Please fill in the required field: " + header,
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
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

        if (fileHandler.appendEmployeeToFile(newRow)) {
            fileHandler.readEmployeeFile();
            JOptionPane.showMessageDialog(this, "✅ Employee added successfully!");
            clearFields();
             // Trigger refresh + return
            if (onEmployeeAdded != null) {
                onEmployeeAdded.run();
                this.setVisible(false);
            }
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
        for (JComponent field : fieldMap.values()) {
            if (field instanceof JTextField) {
                ((JTextField) field).setText("");
                field.setBorder(UIManager.getBorder("TextField.border"));
            }
        }
    }

    private boolean isRequired(String header) {
        return header.equalsIgnoreCase("Employee Number")
                || header.equalsIgnoreCase("Last Name")
                || header.equalsIgnoreCase("First Name")
                || header.equalsIgnoreCase("Birthday")
                || header.equalsIgnoreCase("Phone Number");
    }

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

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            FileHandler fileHandler = new FileHandler();
//            fileHandler.readEmployeeFile();
//
//            JFrame frame = new JFrame("Add Employee Panel with Date Picker");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setSize(550, 600);
//            frame.setLocationRelativeTo(null);
//            frame.add(new AddEmployeePanel(fileHandler, n));
//            frame.setVisible(true);      
//        });
//    }
}
