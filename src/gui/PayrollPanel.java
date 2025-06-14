package gui;

import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;
import java.time.LocalTime;
import java.time.Duration;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class PayrollPanel extends JPanel {

    // Table to display employees
    private JTable employeeTable;

    // Text area to show calculation results (if used later)
    private JTextArea txtResult;

    // File handler to read/write employee and attendance data
    private FileHandler fileHandler;

    // Button to compute payroll
    private JButton btnCalculate;

    // To keep track of selected employee ID from the table
    private String selectedEmployeeID;

    public PayrollPanel() {
        // Initialize file handler and load employee & attendance records
        fileHandler = new FileHandler();
        fileHandler.readEmployeeFile();
        fileHandler.readAttendanceFile();

        // Set layout for main panel
        setLayout(new BorderLayout());

        // Create a content panel to hold components with padding
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(contentPanel, BorderLayout.CENTER);

        // Make PayrollPanel opaque
        setOpaque(true);

        // ----------- Top-right button panel section ---------------

        // Create and style the "Compute" button
        btnCalculate = new JButton("Compute");
        styleMinimalButton(btnCalculate, 160, 40);

        // Add button to top-right-aligned panel
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRightPanel.setOpaque(false);
        topRightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        topRightPanel.add(btnCalculate);
        contentPanel.add(topRightPanel, BorderLayout.NORTH);

        // ----------- Employee table setup section ----------------

        // Define table column headers
        String[] columnNames = {"ID Number", "Last Name", "First Name", "Status", "Position"};

        // Create table model and populate it with employee data
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        for (String[] emp : fileHandler.getEmployeeData()) {
            tableModel.addRow(new Object[]{emp[0], emp[1], emp[2], emp[10], emp[11]});
        }

        // Create JTable using the model
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow one row selection at a time
        employeeTable.setShowGrid(true); // Show grid lines
        employeeTable.setRowHeight(25); // Set row height
        employeeTable.setOpaque(false); // Transparent background
        employeeTable.setFillsViewportHeight(true); // Fill scrollpane height
        employeeTable.setBorder(BorderFactory.createEmptyBorder());

        // Set custom cell renderer for table rows
        employeeTable.setDefaultRenderer(Object.class, (TableCellRenderer) new PayrollTableCellRenderer());

        // Set custom header renderer for each column
        for (int i = 0; i < employeeTable.getColumnCount(); i++) {
            employeeTable.getColumnModel().getColumn(i).setHeaderRenderer((TableCellRenderer) new PayrollTableHeaderRenderer());
        }

        // Wrap the table in a scroll pane with custom scrollbar UI
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

        // Panel to hold the table and add spacing
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Add table panel to content panel center
        contentPanel.add(tablePanel, BorderLayout.CENTER);
   

       // Result output area
txtResult = new JTextArea(20, 50); // Multi-line text area for displaying salary calculation results
txtResult.setEditable(false); // Makes the text area read-only
txtResult.setOpaque(false); // Makes background transparent
txtResult.setBorder(null); // Removes border

JScrollPane resultScroll = new JScrollPane(txtResult); // Wraps text area in a scroll pane
resultScroll.setOpaque(false); // Transparent scroll pane background
resultScroll.getViewport().setOpaque(false); // Transparent viewport
resultScroll.getVerticalScrollBar().setUI(new ModernScrollBarUI()); // Custom vertical scrollbar
resultScroll.getHorizontalScrollBar().setUI(new ModernScrollBarUI()); // Custom horizontal scrollbar

JPanel resultPanel = new JPanel(new BorderLayout()); // Panel to hold the scroll pane
resultPanel.setOpaque(false); // Transparent panel
resultPanel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10)); // Adds padding inside panel
resultPanel.add(resultScroll, BorderLayout.CENTER); // Adds scroll pane to panel
contentPanel.add(resultPanel, BorderLayout.SOUTH); // Adds result panel to bottom of main layout

// Listener for table selection
employeeTable.getSelectionModel().addListSelectionListener(e -> {
    int selectedRow = employeeTable.getSelectedRow(); // Get selected row index
    if (selectedRow != -1) { // If a row is selected
        selectedEmployeeID = employeeTable.getValueAt(selectedRow, 0).toString(); // Get employee ID from first column
        btnCalculate.setEnabled(true); // Enable the "Calculate Salary" button
    }
});

// Action for Calculate Salary button
btnCalculate.addActionListener(e -> {
    if (selectedEmployeeID == null) return; // Exit if no employee is selected

    Set<String> months = new HashSet<>(); // To collect distinct months from attendance data
    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy"); // Date format used in attendance file

    // Gather distinct months from selected employee's attendance
    for (String[] record : fileHandler.getAttendanceData()) {
        if (record[0].equals(selectedEmployeeID)) {
            try {
                LocalDate date = LocalDate.parse(record[1], inputFormatter); // Parse date
                months.add(date.getMonth().toString()); // Add month to set
            } catch (DateTimeParseException ex) {
                System.err.println("Skipping invalid date: " + record[1]); // Handle parsing errors
            }
        }
    }

    // Show warning if no attendance found
    if (months.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No attendance records found for this employee.");
        return;
    }

    // Prompt user to select a month
    String month = (String) JOptionPane.showInputDialog(this, "Choose month:",
            "Select Month", JOptionPane.PLAIN_MESSAGE, null,
            months.toArray(String[]::new), null);

    if (month == null) return; // Exit if no month selected

    int totalWorkedMinutes = 0; // Accumulator for worked minutes
    int totalLateMinutes = 0;   // Accumulator for late minutes

    // Calculate total worked and late minutes for the selected month
    for (String[] record : fileHandler.getAttendanceData()) {
        if (record[0].equals(selectedEmployeeID)) {
            try {
                LocalDate date = LocalDate.parse(record[1], inputFormatter);
                if (date.getMonth().toString().equals(month)) {
                    int[] result = calculateWorkAndLateOffset(record[2], record[3]); // [0] = worked, [1] = late
                    totalWorkedMinutes += result[0];
                    totalLateMinutes += result[1];
                }
            } catch (DateTimeParseException ex) {
                System.err.println("Skipping invalid date: " + record[1]);
            }
        }
    }

    double totalHoursWorked = totalWorkedMinutes / 60.0; // Convert minutes to hours

    // Fetch employee data by ID
    String[] employee = fileHandler.getEmployeeData().stream()
            .filter(emp -> emp[0].equals(selectedEmployeeID))
            .findFirst().orElse(null);

    if (employee == null) return; // Exit if employee not found

    try {
        // Parse salary-related values from employee data
        double hourlyRate = safeParseDouble(employee[18], 0.0);
        double basicSalary = safeParseDouble(employee[13], 0.0);

        // Get employee-specific benefits
        Benefits benefits = fileHandler.getBenefitsByEmployeeId(selectedEmployeeID);
        double riceSubsidy = benefits.getRiceSubsidy();
        double phoneAllowance = benefits.getPhoneAllowance();
        double clothingAllowance = benefits.getClothingAllowance();

        // Use PayrollLogic to calculate salaries and deductions
        PayrollLogic calculator = new PayrollLogic();
        double grossWeekly = calculator.calculateGrossWeeklySalary(hourlyRate, totalHoursWorked, riceSubsidy, phoneAllowance, clothingAllowance);
        double latePenalty = calculator.calculateLateDeduction(hourlyRate, totalLateMinutes);
        double adjustedGross = grossWeekly - latePenalty;

        // Monthly deductions calculated and divided by 4 for weekly
        Deductions deductions = new Deductions();
        double sss = deductions.calculateSSS(basicSalary);
        double ph = deductions.calculatePhilHealth(basicSalary);
        double pi = deductions.calculatePagIbig(basicSalary);
        double tax = deductions.getMonthlyWithholdingTax(basicSalary);
        double weeklyDeductions = (sss + ph + pi + tax) / 4;

        double netWeekly = adjustedGross - weeklyDeductions; // Final net salary for the week

        // Format and display the full report in the result text area
        txtResult.setText(String.format("""
                ===== WEEKLY SALARY REPORT =====

                ➤ BENEFITS:
                • Rice Subsidy: ₱%,.2f
                • Phone Allowance: ₱%,.2f
                • Clothing Allowance: ₱%,.2f
                • Total Benefits: ₱%,.2f

                ➤ WORK DETAILS:
                • Hourly Rate: ₱%,.2f
                • Total Hours Worked (Monthly): %.2f
                • Total Late Minutes (Monthly): %d

                ➤ SALARY:
                • Gross Weekly Salary (with benefits): ₱%,.2f
                • Late Deduction: ₱%,.2f
                • Adjusted Gross Salary: ₱%,.2f

                ➤ DEDUCTIONS (Monthly Basis):
                • SSS: ₱%,.2f
                • PhilHealth: ₱%,.2f
                • Pag-IBIG: ₱%,.2f
                • Withholding Tax: ₱%,.2f
                • Weekly Deduction Total: ₱%,.2f

                ✅ Net Weekly Salary: ₱%,.2f
            """,
            riceSubsidy, phoneAllowance, clothingAllowance, (riceSubsidy + phoneAllowance + clothingAllowance),
            hourlyRate, totalHoursWorked, totalLateMinutes,
            grossWeekly, latePenalty, adjustedGross,
            sss, ph, pi, tax, weeklyDeductions, netWeekly
        ));

    } catch (Exception ex) {
        // Show error if salary calculation fails
        JOptionPane.showMessageDialog(this, "Error calculating salary: " + ex.getMessage());
    }
    });

    // Auto-refresh on show
        this.addComponentListener(new ComponentAdapter() {
      @Override
         public void componentShown(ComponentEvent e) {
        fileHandler.readEmployeeFile(); // Reload employee data
        fileHandler.readAttendanceFile(); // Reload attendance data
        refreshTable(); // Refresh JTable display
    }
    });
    }

   // Safely parses a string into a double, returning a default value if parsing fails
private double safeParseDouble(String value, double defaultValue) {
    try {
        return Double.parseDouble(value.replace("\"", "").trim().replace(",", ""));
    } catch (NumberFormatException e) {
        System.err.println("Failed to parse double: " + value);
        return defaultValue;
    }
}

// Removes non-printable and special characters for debugging purposes
private String debugSanitize(String input) {
    return input.replaceAll("[^\\x20-\\x7E]", "").replace("\"", "").trim();
}

// Calculates worked time, late offset, and overtime based on time-in and time-out strings
private int[] calculateWorkAndLateOffset(String timeInStr, String timeOutStr) {
    timeInStr = debugSanitize(timeInStr);  // Sanitize time-in string
    timeOutStr = debugSanitize(timeOutStr);  // Sanitize time-out string

    // List of acceptable time formats
    List<DateTimeFormatter> formatters = List.of(
            DateTimeFormatter.ofPattern("H:mm"),
            DateTimeFormatter.ofPattern("HH:mm"),
            DateTimeFormatter.ofPattern("H:mm:ss"),
            DateTimeFormatter.ofPattern("HH:mm:ss")
    );

    try {
        // Try to parse both time strings into LocalTime objects
        LocalTime timeIn = tryParseTime(timeInStr, formatters);
        LocalTime timeOut = tryParseTime(timeOutStr, formatters);

        // Define work start, grace period, and end times
        LocalTime workStart = LocalTime.of(8, 0);
        LocalTime graceTime = LocalTime.of(8, 15);
        LocalTime workEnd = LocalTime.of(17, 0);

        // Calculate total worked minutes and deduct 60 mins for break
        int totalWorked = (int) Duration.between(timeIn, timeOut).toMinutes();
        int netWorked = totalWorked - 60;

        // Calculate late minutes if time-in is beyond grace period
        int late = timeIn.isAfter(graceTime)
                ? (int) Duration.between(graceTime, timeIn).toMinutes()
                : 0;

        // Calculate overtime if time-out is beyond work end time
        int overtime = timeOut.isAfter(workEnd)
                ? (int) Duration.between(workEnd, timeOut).toMinutes()
                : 0;

        // Offset late minutes against overtime if applicable
        int offsetLate = Math.max(late - overtime, 0);
        int actualOvertimeEarned = Math.max(overtime - late, 0);

        return new int[]{netWorked, offsetLate, actualOvertimeEarned};

    } catch (Exception e) {
        System.err.println("Invalid time format (Work/Late/OT): [" + timeInStr + "] - [" + timeOutStr + "]");
        return new int[]{0, 0, 0};
    }
}

    // Attempts to parse a time string using a list of formatters until one succeeds
    private LocalTime tryParseTime(String timeStr, List<DateTimeFormatter> formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalTime.parse(timeStr, formatter);
            } catch (Exception ignored) {
            }
        }
        throw new IllegalArgumentException("Time format not supported: " + timeStr);
    }

    // Refreshes the employee table by clearing and reloading data from file
    public void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) employeeTable.getModel();
        model.setRowCount(0); // Clear existing rows
        for (String[] emp : fileHandler.getEmployeeData()) {
            // Add selected columns from each employee record
            model.addRow(new Object[]{emp[0], emp[1], emp[2], emp[10], emp[11]});
        }
    }

    // Paints a vertical gradient background on the panel
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Color gradientStart = new Color(255, 204, 229);
        Color gradientEnd = new Color(255, 229, 180);
        g2d.setPaint(new GradientPaint(0, 0, gradientStart, 0, getHeight(), gradientEnd));
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    // Applies a minimal style to a JButton with custom rounded background and no borders
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
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20); // Rounded button background
                super.paint(g2, c);
                g2.dispose();
            }
        });

        button.setMargin(new Insets(0, 15, 0, 15));
    }

    // Custom renderer for table headers: sets font, color, and alignment
    private static class PayrollTableHeaderRenderer extends DefaultTableCellRenderer {
        public PayrollTableHeaderRenderer() {
            setOpaque(true);
            setBackground(new Color(33, 150, 243));
            setForeground(Color.WHITE);
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return this;
        }
    }

    // Custom renderer for table cells: zebra striping and font styling
    private static class PayrollTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(230, 240, 255)); // Alternating row colors
            } else {
                c.setBackground(new Color(200, 230, 255)); // Selected row color
            }
            c.setFont(new Font("SansSerif", Font.PLAIN, 14));
            return c;
        }
    }

    // Custom scrollbar UI with transparent track and hidden buttons
    private static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = Color.WHITE; // Scroll thumb color
            trackColor = new Color(0, 0, 0, 0); // Transparent scroll track
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createInvisibleButton(); // Hide decrease button
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createInvisibleButton(); // Hide increase button
        }

        // Creates an invisible button by setting size to zero
        private JButton createInvisibleButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
    }
    }