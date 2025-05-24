package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import model.Attendance;
import model.Employee;
import model.Payroll;
import util.FileHandler;

public class DashboardPanel extends JPanel {
    private JTextField txtEmployeeId;
    private JButton btnSearch;
    private JTextArea outputArea;
    private Payroll payroll = new Payroll();

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.DARK_GRAY);

        // Top panel for search input
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.DARK_GRAY);

        txtEmployeeId = new JTextField(15);
        btnSearch = new JButton("Search");
        JLabel lblEmpId = new JLabel("Employee ID:");
        lblEmpId.setForeground(Color.WHITE);

        txtEmployeeId.setForeground(Color.WHITE);
        txtEmployeeId.setBackground(Color.BLACK);
        txtEmployeeId.setCaretColor(Color.WHITE);

        searchPanel.add(lblEmpId);
        searchPanel.add(txtEmployeeId);
        searchPanel.add(btnSearch);

        add(searchPanel, BorderLayout.NORTH);

        // Output panel (center)
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        outputArea.setForeground(Color.WHITE);
        outputArea.setBackground(Color.BLACK);
        outputArea.setMargin(new Insets(10, 10, 10, 10));
        outputArea.setBorder(BorderFactory.createTitledBorder("Employee Record"));

        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Search Button Action
        btnSearch.addActionListener(this::handleSearch);

        // Enter key in text field triggers Search button
        txtEmployeeId.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnSearch.doClick();
            }
        });
    }

    private void handleSearch(ActionEvent e) {
        String empId = txtEmployeeId.getText().trim();
        if (!empId.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Invalid Employee ID.");
            txtEmployeeId.selectAll(); // Select the invalid input
            txtEmployeeId.requestFocus(); // Refocus on the input field
            return;
        }

        FileHandler fh = new FileHandler();
        List<Employee> employees = fh.readEmployees();
        List<Attendance> attendanceList = fh.readAllAttendance();

        Employee emp = employees.stream()
                .filter(e1 -> e1.getEmployeeNumber().equals(empId))
                .findFirst()
                .orElse(null);

        if (emp == null) {
            JOptionPane.showMessageDialog(this, "Employee not found.");
            outputArea.setText("");
            txtEmployeeId.selectAll(); // Select the input again
            txtEmployeeId.requestFocus(); // Refocus on the field
            return;
        }

        // Calculate total hours and late minutes
        double totalHours = Payroll.calculateWeeklyHours(attendanceList, empId);
        int lateMinutes = Payroll.calculateLateMinutes(attendanceList, empId);

        double regularHours = Math.min(totalHours, 40);
        double overtimeHours = Math.max(totalHours - 40, 0);
        boolean hasLateness = lateMinutes > 0;

        double[] salaryDetails = payroll.calculateFullSalaryDetails(
                regularHours, overtimeHours, emp.getJob().getHourlyRate(), hasLateness, false);

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        outputArea.setText(
                "Employee #:         " + emp.getEmployeeNumber() + "\n" +
                "Name:               " + emp.getPerson().getFullName() + "\n" +
                "Position:           " + emp.getJob().getPosition() + "\n" +
                "Hourly Rate:        ₱" + nf.format(emp.getJob().getHourlyRate()) + "\n\n" +
                "Regular Hours:      " + nf.format(regularHours) + "\n" +
                "Overtime Hours:     " + nf.format(overtimeHours) + "\n" +
                "Late Minutes:       " + lateMinutes + "\n\n" +
                "Regular Pay:        ₱" + nf.format(salaryDetails[7]) + "\n" +
                "Overtime Pay:       ₱" + nf.format(salaryDetails[8]) + "\n" +
                "Gross Salary:       ₱" + nf.format(salaryDetails[0]) + "\n\n" +
                "SSS:                ₱" + nf.format(salaryDetails[1]) + "\n" +
                "PhilHealth:         ₱" + nf.format(salaryDetails[2]) + "\n" +
                "Pag-IBIG:           ₱" + nf.format(salaryDetails[3]) + "\n" +
                "Tax:                ₱" + nf.format(salaryDetails[5]) + "\n\n" +
                "Weekly Net Salary:  ₱" + nf.format(salaryDetails[6])
        );
    }
}
