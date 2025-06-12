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

    private JTable employeeTable;
    private JTextArea txtResult;
    private FileHandler fileHandler;
    private JButton btnCalculate;
    private String selectedEmployeeID;

    public PayrollPanel() {
        fileHandler = new FileHandler();
        fileHandler.readEmployeeFile();
        fileHandler.readAttendanceFile();

        setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(contentPanel, BorderLayout.CENTER);

        setOpaque(true);

        // Top-right button panel
        btnCalculate = new JButton("Compute");
        styleMinimalButton(btnCalculate, 160, 40);

        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRightPanel.setOpaque(false);
        topRightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        topRightPanel.add(btnCalculate);
        contentPanel.add(topRightPanel, BorderLayout.NORTH);

        // Employee table setup
        String[] columnNames = {"ID Number", "Last Name", "First Name", "Status", "Position"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        for (String[] emp : fileHandler.getEmployeeData()) {
            tableModel.addRow(new Object[]{emp[0], emp[1], emp[2], emp[10], emp[11]});
        }

        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setShowGrid(true);
        employeeTable.setRowHeight(25);
        employeeTable.setOpaque(false);
        employeeTable.setFillsViewportHeight(true);
        employeeTable.setBorder(BorderFactory.createEmptyBorder());

        employeeTable.setDefaultRenderer(Object.class, (TableCellRenderer) new PayrollTableCellRenderer());

        for (int i = 0; i < employeeTable.getColumnCount(); i++) {
            employeeTable.getColumnModel().getColumn(i).setHeaderRenderer((TableCellRenderer) new PayrollTableHeaderRenderer());
        }

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        // Result output area
        txtResult = new JTextArea(20, 50);
        txtResult.setEditable(false);
        txtResult.setOpaque(false);
        txtResult.setBorder(null);

        JScrollPane resultScroll = new JScrollPane(txtResult);
        resultScroll.setOpaque(false);
        resultScroll.getViewport().setOpaque(false);
        resultScroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        resultScroll.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setOpaque(false);
        resultPanel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10)); // Adjusted padding
        resultPanel.add(resultScroll, BorderLayout.CENTER);
        contentPanel.add(resultPanel, BorderLayout.SOUTH);

        // Listener for table selection
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = employeeTable.getSelectedRow();
            if (selectedRow != -1) {
                selectedEmployeeID = employeeTable.getValueAt(selectedRow, 0).toString();
                btnCalculate.setEnabled(true);
            }
        });

        // Action for Calculate Salary button
        btnCalculate.addActionListener(e -> {
            if (selectedEmployeeID == null) return;

            Set<String> months = new HashSet<>();
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            for (String[] record : fileHandler.getAttendanceData()) {
                if (record[0].equals(selectedEmployeeID)) {
                    try {
                        LocalDate date = LocalDate.parse(record[1], inputFormatter);
                        months.add(date.getMonth().toString());
                    } catch (DateTimeParseException ex) {
                        System.err.println("Skipping invalid date: " + record[1]);
                    }
                }
            }

            if (months.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No attendance records found for this employee.");
                return;
            }

            String month = (String) JOptionPane.showInputDialog(this, "Choose month:",
                    "Select Month", JOptionPane.PLAIN_MESSAGE, null,
                    months.toArray(String[]::new), null);

            if (month == null) return;

            int totalWorkedMinutes = 0;
            int totalLateMinutes = 0;
            for (String[] record : fileHandler.getAttendanceData()) {
                if (record[0].equals(selectedEmployeeID)) {
                    try {
                        LocalDate date = LocalDate.parse(record[1], inputFormatter);
                        if (date.getMonth().toString().equals(month)) {
                            int[] result = calculateWorkAndLateOffset(record[2], record[3]);
                            totalWorkedMinutes += result[0];
                            totalLateMinutes += result[1];
                        }
                    } catch (DateTimeParseException ex) {
                        System.err.println("Skipping invalid date: " + record[1]);
                    }
                }
            }

            double totalHoursWorked = totalWorkedMinutes / 60.0;

            String[] employee = fileHandler.getEmployeeData().stream()
                    .filter(emp -> emp[0].equals(selectedEmployeeID))
                    .findFirst().orElse(null);

            if (employee == null) return;

            try {
                double hourlyRate = safeParseDouble(employee[18], 0.0);
                double basicSalary = safeParseDouble(employee[13], 0.0);

                Benefits benefits = fileHandler.getBenefitsByEmployeeId(selectedEmployeeID);

                double riceSubsidy = benefits.getRiceSubsidy();
                double phoneAllowance = benefits.getPhoneAllowance();
                double clothingAllowance = benefits.getClothingAllowance();

                PayrollLogic calculator = new PayrollLogic();
                double grossWeekly = calculator.calculateGrossWeeklySalary(hourlyRate, totalHoursWorked, riceSubsidy, phoneAllowance, clothingAllowance);
                double latePenalty = calculator.calculateLateDeduction(hourlyRate, totalLateMinutes);
                double adjustedGross = grossWeekly - latePenalty;

                Deductions deductions = new Deductions();
                double sss = deductions.calculateSSS(basicSalary);
                double ph = deductions.calculatePhilHealth(basicSalary);
                double pi = deductions.calculatePagIbig(basicSalary);
                double tax = deductions.getMonthlyWithholdingTax(basicSalary);

                double weeklyDeductions = (sss + ph + pi + tax) / 4;
                double netWeekly = adjustedGross - weeklyDeductions;

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
                JOptionPane.showMessageDialog(this, "Error calculating salary: " + ex.getMessage());
            }
        });

        // Auto-refresh on show
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                fileHandler.readEmployeeFile();
                fileHandler.readAttendanceFile();
                refreshTable();
            }
        });
    }

    private double safeParseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value.replace("\"", "").trim().replace(",", ""));
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse double: " + value);
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

            LocalTime workStart = LocalTime.of(8, 0);
            LocalTime graceTime = LocalTime.of(8, 15);
            LocalTime workEnd = LocalTime.of(17, 0);

            int totalWorked = (int) Duration.between(timeIn, timeOut).toMinutes();
            int netWorked = totalWorked - 60;

            int late = timeIn.isAfter(graceTime)
                    ? (int) Duration.between(graceTime, timeIn).toMinutes()
                    : 0;

            int overtime = timeOut.isAfter(workEnd)
                    ? (int) Duration.between(workEnd, timeOut).toMinutes()
                    : 0;

            int offsetLate = Math.max(late - overtime, 0);
            int actualOvertimeEarned = Math.max(overtime - late, 0);

            return new int[]{netWorked, offsetLate, actualOvertimeEarned};

        } catch (Exception e) {
            System.err.println("Invalid time format (Work/Late/OT): [" + timeInStr + "] - [" + timeOutStr + "]");
            return new int[]{0, 0, 0};
        }
    }

    private LocalTime tryParseTime(String timeStr, List<DateTimeFormatter> formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalTime.parse(timeStr, formatter);
            } catch (Exception ignored) {
            }
        }
        throw new IllegalArgumentException("Time format not supported: " + timeStr);
    }

    public void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) employeeTable.getModel();
        model.setRowCount(0);
        for (String[] emp : fileHandler.getEmployeeData()) {
            model.addRow(new Object[]{emp[0], emp[1], emp[2], emp[10], emp[11]});
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Color gradientStart = new Color(255, 204, 229);
        Color gradientEnd = new Color(255, 229, 180);
        g2d.setPaint(new GradientPaint(0, 0, gradientStart, 0, getHeight(), gradientEnd));
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

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
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
                super.paint(g2, c);
                g2.dispose();
            }
        });

        button.setMargin(new Insets(0, 15, 0, 15));
    }

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

    private static class PayrollTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(230, 240, 255));
            } else {
                c.setBackground(new Color(200, 230, 255));
            }
            c.setFont(new Font("SansSerif", Font.PLAIN, 14));
            return c;
        }
    }

    private static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = Color.WHITE;
            trackColor = new Color(0, 0, 0, 0);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createInvisibleButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createInvisibleButton();
        }

        private JButton createInvisibleButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
    }
}
