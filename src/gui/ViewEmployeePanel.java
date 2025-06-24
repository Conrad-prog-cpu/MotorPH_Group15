package gui;

import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;
import java.util.Locale;

public class ViewEmployeePanel extends JFrame {

    public ViewEmployeePanel(Vector<Object> employeeData) {
        setTitle("Employee Details");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ========== Left Content Panel =============
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {
            "Employee #", "Last Name", "First Name", "Birthday", "Address", "Phone Number",
            "SSS #", "Philhealth #", "TIN #", "Pag-ibig #", "Status", "Position",
            "Immediate Supervisor", "Basic Salary", "Rice Subsidy", "Phone Allowance",
            "Clothing Allowance", "Gross Semi-monthly Rate", "Hourly Rate"
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.3;
            contentPanel.add(new JLabel(labels[i] + ":"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            JTextArea dataField = new JTextArea(employeeData.get(i).toString());
            dataField.setWrapStyleWord(true);
            dataField.setLineWrap(true);
            dataField.setEditable(false);
            dataField.setOpaque(false);
            dataField.setFocusable(false);
            dataField.setBorder(null);
            contentPanel.add(dataField, gbc);
        }

        JScrollPane contentScrollPane = new JScrollPane(contentPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // ========== Right Payroll Panel =============
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.addItem("Select Month"); // Placeholder

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null && value.toString().equals("Select Month")) {
                    c.setForeground(Color.GRAY);
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        JButton submitButton = new JButton("Compute");
        JTextArea rightTextArea = new JTextArea();
        rightTextArea.setLineWrap(true);
        rightTextArea.setWrapStyleWord(true);
        rightTextArea.setEditable(false);
        JScrollPane textAreaScrollPane = new JScrollPane(rightTextArea);

        // Combo + Button Top
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(comboBox);
        topPanel.add(submitButton);
        rightPanel.add(topPanel, BorderLayout.NORTH);
        rightPanel.add(textAreaScrollPane, BorderLayout.CENTER);

        // ========== File Handler Setup ===========
        FileHandler fileHandler = new FileHandler();
        fileHandler.readEmployeeFile();
        fileHandler.readAttendanceFile();
        String employeeId = employeeData.get(0).toString();

        // Populate ComboBox with months in calendar order
        Set<Month> availableMonthEnums = new TreeSet<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        for (String[] record : fileHandler.getAttendanceData()) {
            if (record[0].equals(employeeId)) {
                try {
                    LocalDate date = LocalDate.parse(record[1], formatter);
                    availableMonthEnums.add(date.getMonth());
                } catch (DateTimeParseException e) {
                    System.err.println("Invalid date format: " + record[1]);
                }
            }
        }
        for (Month month : Month.values()) {
            if (availableMonthEnums.contains(month)) {
                String monthName = month.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                comboBox.addItem(monthName);
            }
        }
        comboBox.setSelectedIndex(0);

        // Submit Button Action
        submitButton.addActionListener(e -> {
            String selectedMonth = (String) comboBox.getSelectedItem();
            if (selectedMonth == null || selectedMonth.equals("Select Month")) {
                JOptionPane.showMessageDialog(this, "Please select a valid month.");
                return;
            }

            int totalWorkedMinutes = 0;
            int totalLateMinutes = 0;

            for (String[] record : fileHandler.getAttendanceData()) {
                if (record[0].equals(employeeId)) {
                    try {
                        LocalDate date = LocalDate.parse(record[1], formatter);
                        String recordMonth = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                        if (recordMonth.equals(selectedMonth)) {
                            int[] result = calculateWorkAndLateOffset(record[2], record[3]);
                            totalWorkedMinutes += result[0];
                            totalLateMinutes += result[1];
                        }
                    } catch (DateTimeParseException ex) {
                        System.err.println("Invalid date: " + record[1]);
                    }
                }
            }

            double totalHoursWorked = totalWorkedMinutes / 60.0;
            String[] emp = fileHandler.getEmployeeById(employeeId);
            if (emp == null) {
                JOptionPane.showMessageDialog(this, "Employee not found.");
                return;
            }

            try {
                double hourlyRate = safeParseDouble(emp[18], 0.0);
                double basicSalary = safeParseDouble(emp[13], 0.0);
                Benefits benefits = fileHandler.getBenefitsByEmployeeId(employeeId);
                double rice = benefits.getRiceSubsidy();
                double phone = benefits.getPhoneAllowance();
                double clothing = benefits.getClothingAllowance();

                PayrollLogic logic = new PayrollLogic();
                double grossWeekly = logic.calculateGrossWeeklySalary(hourlyRate, totalHoursWorked, rice, phone, clothing);
                double latePenalty = logic.calculateLateDeduction(hourlyRate, totalLateMinutes);
                double adjustedGross = grossWeekly - latePenalty;

                Deductions deductions = new Deductions();
                double sss = deductions.calculateSSS(basicSalary);
                double ph = deductions.calculatePhilHealth(basicSalary);
                double pi = deductions.calculatePagIbig(basicSalary);
                double tax = deductions.getMonthlyWithholdingTax(basicSalary);
                double weeklyDeductions = (sss + ph + pi + tax) / 4;
                double netMonthly = adjustedGross - weeklyDeductions;
                double netWeekly = netMonthly / 4;

                rightTextArea.setText(String.format("""
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
                        • Gross Monthly Salary (with benefits): ₱%,.2f
                        • Late Deduction: ₱%,.2f
                        • Adjusted Gross Salary: ₱%,.2f

                        ➤ DEDUCTIONS (Monthly Basis):
                        • SSS: ₱%,.2f
                        • PhilHealth: ₱%,.2f
                        • Pag-IBIG: ₱%,.2f
                        • Withholding Tax: ₱%,.2f
                        • Weekly Deduction Total: ₱%,.2f

                        📅 Net Monthly Salary: ₱%,.2f                             
                        ✅ Net Weekly Salary: ₱%,.2f
                        """,
                        rice, phone, clothing, (rice + phone + clothing),
                        hourlyRate, totalHoursWorked, totalLateMinutes,
                        grossWeekly, latePenalty, adjustedGross,
                        sss, ph, pi, tax, weeklyDeductions, netMonthly, netWeekly));
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Calculation error: " + ex.getMessage());
            }
        });

        // ========== JSplitPane to hold both panels =============
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, contentScrollPane, rightPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(0.5);
        splitPane.setOneTouchExpandable(true);
        add(splitPane);

        setVisible(true);
    }

    // ========== Helper Methods =============

    private double safeParseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value.replace("\"", "").trim().replace(",", ""));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String debugSanitize(String input) {
        return input.replaceAll("[^\\x20-\\x7E]", "").replace("\"", "").trim();
    }

    private int[] calculateWorkAndLateOffset(String timeInStr, String timeOutStr) {
        timeInStr = debugSanitize(timeInStr);
        timeOutStr = debugSanitize(timeOutStr);

        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ofPattern("H:mm"),
                DateTimeFormatter.ofPattern("HH:mm"),
                DateTimeFormatter.ofPattern("H:mm:ss"),
                DateTimeFormatter.ofPattern("HH:mm:ss")
        );

        try {
            LocalTime timeIn = tryParseTime(timeInStr, formatters);
            LocalTime timeOut = tryParseTime(timeOutStr, formatters);

            LocalTime graceTime = LocalTime.of(8, 15);
            LocalTime workEnd = LocalTime.of(17, 0);

            int totalWorked = (int) Duration.between(timeIn, timeOut).toMinutes();
            int netWorked = totalWorked - 60;
            int late = timeIn.isAfter(graceTime) ? (int) Duration.between(graceTime, timeIn).toMinutes() : 0;
            int overtime = timeOut.isAfter(workEnd) ? (int) Duration.between(workEnd, timeOut).toMinutes() : 0;
            int offsetLate = Math.max(late - overtime, 0);

            return new int[]{netWorked, offsetLate};
        } catch (Exception e) {
            return new int[]{0, 0};
        }
    }

    private LocalTime tryParseTime(String timeStr, List<DateTimeFormatter> formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalTime.parse(timeStr, formatter);
            } catch (Exception ignored) {}
        }
        throw new IllegalArgumentException("Unsupported time format: " + timeStr);
    }
}