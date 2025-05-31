/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

/**
 *
 * @author santos.conrad
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import model.Benefits;

public class SalaryCalculatorGUI extends JFrame {

    private JTextField txtHourlyRate, txtLateMinutes, txtBasicSalary;
    private JComboBox<Integer> cbHoursWorked;
    private JTextArea txtResult;

    public SalaryCalculatorGUI() {
        setTitle("Employee Weekly Salary Calculator");
        setSize(550, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtHourlyRate = new JTextField(10);
        cbHoursWorked = new JComboBox<>(new Integer[]{40, 42, 44, 45, 48});
        txtLateMinutes = new JTextField(10);
        txtBasicSalary = new JTextField(10);
        txtResult = new JTextArea(10, 30);
        txtResult.setEditable(false);

        int y = 0;

        addToPanel(inputPanel, gbc, new JLabel("Hourly Rate:"), txtHourlyRate, y++);
        addToPanel(inputPanel, gbc, new JLabel("Hours Worked:"), cbHoursWorked, y++);
        addToPanel(inputPanel, gbc, new JLabel("Late Minutes:"), txtLateMinutes, y++);
        addToPanel(inputPanel, gbc, new JLabel("Basic Salary (Monthly):"), txtBasicSalary, y++);

        JButton btnCalculate = new JButton("Calculate Salary");
        gbc.gridx = 0;
        gbc.gridy = y++;
        gbc.gridwidth = 2;
        inputPanel.add(btnCalculate, gbc);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(txtResult), BorderLayout.CENTER);

        btnCalculate.addActionListener(new CalculateListener());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addToPanel(JPanel panel, GridBagConstraints gbc, JComponent label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private class CalculateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double hourlyRate = Double.parseDouble(txtHourlyRate.getText());
                int hoursWorked = (int) cbHoursWorked.getSelectedItem();
                int lateMinutes = Integer.parseInt(txtLateMinutes.getText());
                double basicSalary = Double.parseDouble(txtBasicSalary.getText().replace(",", ""));

                // Get Benefits from class
                double riceSubsidy = Benefits.getRiceSubsidy();
                double phoneAllowance = Benefits.getPhoneAllowance();
                double clothingAllowance = Benefits.getClothingAllowance();

                // Calculate Gross Salary
                SalaryCalculator calculator = new SalaryCalculator();
                double grossWeekly = calculator.calculateGrossWeeklySalary(hourlyRate, hoursWorked, riceSubsidy, phoneAllowance, clothingAllowance);

                // Deduct Late Penalty
                double latePenalty = calculator.calculateLateDeduction(hourlyRate, lateMinutes);
                double adjustedGross = grossWeekly - latePenalty;

                // Weekly deductions from monthly contributions
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
        ▪ Rice Subsidy: \u20b1%,.2f
        ▪ Phone Allowance: \u20b1%,.2f
        ▪ Clothing Allowance: \u20b1%,.2f
        ▪ Total Benefits: \u20b1%,.2f

        ➤ WORK DETAILS:
        ▪ Hourly Rate: \u20b1%,.2f
        ▪ Hours Worked: %d

        ➤ SALARY:
        ▪ Gross Weekly Salary (with benefits): \u20b1%,.2f
        ▪ Late Deduction: \u20b1%,.2f
        ▪ Adjusted Gross Salary: \u20b1%,.2f

        ➤ DEDUCTIONS (Monthly Basis):
        ▪ SSS: \u20b1%,.2f
        ▪ PhilHealth: \u20b1%,.2f
        ▪ Pag-IBIG: \u20b1%,.2f
        ▪ Withholding Tax: \u20b1%,.2f
        ▪ Weekly Deduction Total: \u20b1%,.2f

        ✅ Net Weekly Salary: \u20b1%,.2f
        """,
        riceSubsidy,
        phoneAllowance,
        clothingAllowance,
        (riceSubsidy + phoneAllowance + clothingAllowance),
        hourlyRate,
        (int) hoursWorked,
        grossWeekly,
        latePenalty,
        adjustedGross,
        sss, ph, pi, tax, weeklyDeductions,
        netWeekly
));

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter valid numbers in all fields.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SalaryCalculatorGUI());
    }
}
