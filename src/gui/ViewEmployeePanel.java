package gui;

import model.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;
import java.util.Locale;

public class ViewEmployeePanel extends JFrame {

    private static final Font UI_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Color GRADIENT_START = new Color(255, 204, 229);
    private static final Color GRADIENT_END = new Color(255, 229, 180);

    public ViewEmployeePanel(Vector<Object> employeeData) {
        setTitle("Employee Details");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ===== LEFT PANEL =====
        JPanel leftPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, GRADIENT_START, 0, getHeight(), GRADIENT_END));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setOpaque(false);
        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        String[][] sections = {
            {"Personal Information", "Employee No.", "Last Name", "First Name", "Birthday", "Address", "Phone Number"},
            {"Government Identifications", "SSS No.", "PhilHealth No.", "TIN No.", "PAG-IBIG No."},
            {"Job Information", "Status", "Position", "Immediate Supervisor"},
            {"Compensation & Benefits", "Basic Salary", "Rice Subsidy", "Phone Allowance", "Clothing Allowance", "Gross Semi-monthly Rate", "Hourly Rate"}
        };

        int dataIndex = 0;
        gbc.gridy = 0;

        for (String[] section : sections) {
            JLabel sectionTitle = new JLabel(section[0]);
            sectionTitle.setFont(HEADER_FONT);
            sectionTitle.setForeground(new Color(70, 70, 70));
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            leftPanel.add(sectionTitle, gbc);
            gbc.gridy++;
            gbc.gridwidth = 1;

            for (int i = 1; i < section.length; i++) {
                gbc.gridx = 0;
                JLabel label = new JLabel(section[i] + ":");
                label.setFont(UI_FONT);
                leftPanel.add(label, gbc);

                gbc.gridx = 1;
                JTextArea dataField = new JTextArea(employeeData.get(dataIndex++).toString());
                dataField.setWrapStyleWord(true);
                dataField.setLineWrap(true);
                dataField.setEditable(false);
                dataField.setOpaque(false);
                dataField.setFont(UI_FONT);
                dataField.setBorder(null);
                leftPanel.add(dataField, gbc);

                gbc.gridy++;
            }

            gbc.gridy++;
        }

        JScrollPane contentScrollPane = new JScrollPane(leftPanel);
        contentScrollPane.setBorder(null);
        contentScrollPane.getVerticalScrollBar().setUI(createScrollBarUI());

        // ===== RIGHT PANEL =====
        JPanel rightPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, GRADIENT_START, 0, getHeight(), GRADIENT_END));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        rightPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setFont(UI_FONT);
        comboBox.addItem("Select Month");
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setFont(UI_FONT);
                c.setForeground("Select Month".equals(value) ? Color.GRAY : Color.BLACK);
                return c;
            }
        });

        JButton submitButton = new JButton("Compute");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Bold font
        submitButton.setBackground(Color.BLACK);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorder(BorderFactory.createEmptyBorder()); // No outline
        submitButton.setPreferredSize(new Dimension(130, 40));

        JTextArea rightTextArea = new JTextArea();
        rightTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rightTextArea.setLineWrap(true);
        rightTextArea.setWrapStyleWord(true);
        rightTextArea.setEditable(false);

        JScrollPane textAreaScrollPane = new JScrollPane(rightTextArea);
        textAreaScrollPane.setBorder(null);
        textAreaScrollPane.getVerticalScrollBar().setUI(createScrollBarUI());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(comboBox);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(submitButton);

        rightPanel.add(topPanel, BorderLayout.NORTH);
        rightPanel.add(textAreaScrollPane, BorderLayout.CENTER);

        // ===== FILE HANDLING & LOGIC =====
        FileHandler fileHandler = new FileHandler();
        fileHandler.readEmployeeFile();
        fileHandler.readAttendanceFile();
        String employeeId = employeeData.get(0).toString();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        Set<Month> availableMonths = new TreeSet<>();
        for (String[] record : fileHandler.getAttendanceData()) {
            if (record[0].equals(employeeId)) {
                try {
                    LocalDate date = LocalDate.parse(record[1], formatter);
                    availableMonths.add(date.getMonth());
                } catch (DateTimeParseException ignored) {}
            }
        }
        for (Month month : availableMonths) {
            comboBox.addItem(month.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        }

        submitButton.addActionListener(e -> {
            String selectedMonth = (String) comboBox.getSelectedItem();
            if (selectedMonth == null || selectedMonth.equals("Select Month")) {
                JOptionPane.showMessageDialog(this, "Please select a valid month.");
                return;
            }

            int totalWorkedMinutes = 0, totalLateMinutes = 0;

            for (String[] record : fileHandler.getAttendanceData()) {
                if (record[0].equals(employeeId)) {
                    try {
                        LocalDate date = LocalDate.parse(record[1], formatter);
                        if (date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).equals(selectedMonth)) {
                            int[] result = calculateWorkAndLateOffset(record[2], record[3]);
                            totalWorkedMinutes += result[0];
                            totalLateMinutes += result[1];
                        }
                    } catch (DateTimeParseException ignored) {}
                }
            }

            double totalHoursWorked = totalWorkedMinutes / 60.0;
            String[] emp = fileHandler.getEmployeeById(employeeId);
            if (emp == null) {
                JOptionPane.showMessageDialog(this, "Employee not found.");
                return;
            }

            try {
                double hourlyRate = safeParseDouble(emp[18]);
                double basicSalary = safeParseDouble(emp[13]);
                Benefits benefits = fileHandler.getBenefitsByEmployeeId(employeeId);
                double rice = benefits.getRiceSubsidy(), phone = benefits.getPhoneAllowance(), clothing = benefits.getClothingAllowance();

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

                        🗕 Net Monthly Salary: ₱%,.2f
                        ✅ Net Weekly Salary: ₱%,.2f
                        """,
                        rice, phone, clothing, rice + phone + clothing,
                        hourlyRate, totalHoursWorked, totalLateMinutes,
                        grossWeekly, latePenalty, adjustedGross,
                        sss, ph, pi, tax, weeklyDeductions,
                        netMonthly, netWeekly));
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Calculation error: " + ex.getMessage());
            }
        });

        // ===== SPLIT PANE =====
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, contentScrollPane, rightPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(0.5);
        splitPane.setOneTouchExpandable(true);
        add(splitPane);

        setVisible(true);
    }

    private double safeParseDouble(String value) {
        try {
            return Double.parseDouble(value.replace("\"", "").replace(",", "").trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private int[] calculateWorkAndLateOffset(String inStr, String outStr) {
        inStr = sanitizeTime(inStr);
        outStr = sanitizeTime(outStr);

        List<DateTimeFormatter> formats = List.of(
            DateTimeFormatter.ofPattern("H:mm"),
            DateTimeFormatter.ofPattern("HH:mm"),
            DateTimeFormatter.ofPattern("H:mm:ss"),
            DateTimeFormatter.ofPattern("HH:mm:ss")
        );

        try {
            LocalTime in = tryParseTime(inStr, formats);
            LocalTime out = tryParseTime(outStr, formats);
            LocalTime grace = LocalTime.of(8, 15), workEnd = LocalTime.of(17, 0);

            int total = (int) Duration.between(in, out).toMinutes();
            int late = in.isAfter(grace) ? (int) Duration.between(grace, in).toMinutes() : 0;
            int overtime = out.isAfter(workEnd) ? (int) Duration.between(workEnd, out).toMinutes() : 0;

            return new int[]{total - 60, Math.max(late - overtime, 0)};
        } catch (Exception e) {
            return new int[]{0, 0};
        }
    }

    private LocalTime tryParseTime(String time, List<DateTimeFormatter> formats) {
        for (DateTimeFormatter f : formats) {
            try {
                return LocalTime.parse(time, f);
            } catch (Exception ignored) {}
        }
        throw new IllegalArgumentException("Invalid time: " + time);
    }

    private String sanitizeTime(String input) {
        return input.replaceAll("[^\\x20-\\x7E]", "").replace("\"", "").trim();
    }

    private BasicScrollBarUI createScrollBarUI() {
        return new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                this.thumbColor = Color.WHITE;
                this.trackColor = GRADIENT_END;
            }
            @Override protected Dimension getMinimumThumbSize() {
                return new Dimension(8, 30);
            }
        };
    }
}
