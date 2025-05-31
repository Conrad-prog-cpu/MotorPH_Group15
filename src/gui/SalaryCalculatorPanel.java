package gui; // Package declaration

import javax.swing.*; // Swing GUI components
import java.awt.*; // Layouts and colors
import java.awt.event.ActionEvent; // Action event for buttons
import java.awt.event.ActionListener; // Interface for event listener
import model.Benefits; // Importing Benefits class from model package

// Panel class for calculating and displaying weekly salary with benefits and deductions
public class SalaryCalculatorPanel extends JPanel {

    // GUI components used for input and output
    private JTextField txtHourlyRate, txtLateMinutes, txtBasicSalary;
    private JComboBox<Integer> cbHoursWorked;
    private JTextArea txtResult;

    // Constructor initializes and lays out all GUI components
    public SalaryCalculatorPanel() {
        setOpaque(true); // Makes panel non-transparent
        setLayout(new BorderLayout()); // Uses BorderLayout for main panel

        JPanel inputPanel = new JPanel(new GridBagLayout()); // Input section with flexible layout
        inputPanel.setOpaque(false); // Transparent background
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding around input panel
        GridBagConstraints gbc = new GridBagConstraints(); // Constraint object for GridBagLayout
        gbc.insets = new Insets(8, 8, 8, 8); // Spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally

        // Initialize input fields
        txtHourlyRate = new JTextField(10);
        cbHoursWorked = new JComboBox<>(new Integer[]{40, 42, 44, 45, 48}); // Preset hour options
        txtLateMinutes = new JTextField(10);
        txtBasicSalary = new JTextField(10);
        txtResult = new JTextArea(10, 30); // Output area for results
        txtResult.setEditable(false); // Make output area read-only

        int y = 0; // Row counter for grid
        // Add labeled inputs to the panel row by row
        addToPanel(inputPanel, gbc, new JLabel("Hourly Rate:"), txtHourlyRate, y++);
        addToPanel(inputPanel, gbc, new JLabel("Hours Worked:"), cbHoursWorked, y++);
        addToPanel(inputPanel, gbc, new JLabel("Late Minutes:"), txtLateMinutes, y++);
        addToPanel(inputPanel, gbc, new JLabel("Basic Salary (Monthly):"), txtBasicSalary, y++);

        // Add calculate button
        JButton btnCalculate = new JButton("Calculate Salary");
        gbc.gridx = 0;
        gbc.gridy = y++;
        gbc.gridwidth = 2; // Span 2 columns
        inputPanel.add(btnCalculate, gbc); // Add button to panel

        add(inputPanel, BorderLayout.NORTH); // Add input panel to top
        add(new JScrollPane(txtResult), BorderLayout.CENTER); // Scrollable output in center

        btnCalculate.addActionListener(new CalculateListener()); // Link action to button
    }

    // Draw custom background with vertical gradient
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Default painting
        Graphics2D g2d = (Graphics2D) g; // Cast to 2D for advanced painting
        Color gradientStart = new Color(255, 204, 229); // Light pink
        Color gradientEnd = new Color(255, 229, 180); // Light peach
        g2d.setPaint(new GradientPaint(0, 0, gradientStart, 0, getHeight(), gradientEnd)); // Vertical gradient
        g2d.fillRect(0, 0, getWidth(), getHeight()); // Fill panel with gradient
    }

    // Helper method to add label and input side-by-side to a panel
    private void addToPanel(JPanel panel, GridBagConstraints gbc, JComponent label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(label, gbc); // Add label to column 0

        gbc.gridx = 1;
        panel.add(field, gbc); // Add input field to column 1
        setOpaque(true); // Ensure opaque setting
    }

    // Inner class to handle salary calculation when button is clicked
    private class CalculateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Get user inputs and convert to appropriate types
                double hourlyRate = Double.parseDouble(txtHourlyRate.getText());
                int hoursWorked = (int) cbHoursWorked.getSelectedItem();
                int lateMinutes = Integer.parseInt(txtLateMinutes.getText());
                double basicSalary = Double.parseDouble(txtBasicSalary.getText().replace(",", ""));

                // Get static benefit values from Benefits class
                double riceSubsidy = Benefits.getRiceSubsidy();
                double phoneAllowance = Benefits.getPhoneAllowance();
                double clothingAllowance = Benefits.getClothingAllowance();

                // Calculate gross weekly salary and late penalty
                SalaryCalculator calculator = new SalaryCalculator();
                double grossWeekly = calculator.calculateGrossWeeklySalary(hourlyRate, hoursWorked, riceSubsidy, phoneAllowance, clothingAllowance);
                double latePenalty = calculator.calculateLateDeduction(hourlyRate, lateMinutes);
                double adjustedGross = grossWeekly - latePenalty; // Gross minus late deduction

                // Calculate government deductions based on monthly salary
                Deductions deductions = new Deductions();
                double sss = deductions.calculateSSS(basicSalary);
                double ph = deductions.calculatePhilHealth(basicSalary);
                double pi = deductions.calculatePagIbig(basicSalary);
                double tax = deductions.getMonthlyWithholdingTax(basicSalary);

                // Divide monthly deductions by 4 to get weekly value
                double weeklyDeductions = (sss + ph + pi + tax) / 4;
                double netWeekly = adjustedGross - weeklyDeductions; // Final net salary

                // Format and display result in text area
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
                        riceSubsidy, phoneAllowance, clothingAllowance, (riceSubsidy + phoneAllowance + clothingAllowance),
                        hourlyRate, hoursWorked,
                        grossWeekly, latePenalty, adjustedGross,
                        sss, ph, pi, tax, weeklyDeductions, netWeekly
                ));

            } catch (NumberFormatException ex) {
                // If inputs are invalid, show error message
                JOptionPane.showMessageDialog(null, "Please enter valid numbers in all fields.");
            }
        }
    }
}
