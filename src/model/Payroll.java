/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.List;

public class Payroll {

      // rates for deductions - these are from the MotorPH website
    private static final double PHILHEALTH_RATE = 0.03;       // 3% PhilHealth (total amount)
    private static final double PHILHEALTH_EE_SHARE = 0.5;    // Employee pays half
    private static final double PAGIBIG_RATE_LOWER = 0.01;    // 1% for lower salary
    private static final double PAGIBIG_RATE_UPPER = 0.02;    // 2% for higher salary
    private static final double PAGIBIG_MAX = 100.0;          // Max contribution is 100 pesos
    public static final double LATE_PENALTY_RATE = 0.10; // penalty for tardiness 
    
    // PhilHealth stuff
    private static final double PHILHEALTH_MIN_SALARY = 10000.0;
    private static final double PHILHEALTH_MAX_SALARY = 60000.0;
    private static final double PHILHEALTH_MIN_CONTRIBUTION = 300.0;
    private static final double PHILHEALTH_MAX_CONTRIBUTION = 1800.0;
    
    // Pag-IBIG threshold 
    private static final double PAGIBIG_THRESHOLD = 1500.0;
    
    // Tax brackets monthly (From MotorPH website)
    private static final double TAX_BRACKET_1 = 20832.0;   // no tax below this
    private static final double TAX_BRACKET_2 = 33333.0;   // 20% over bracket 1
    private static final double TAX_BRACKET_3 = 66667.0;   // 2,500 + 25% over bracket 2
    private static final double TAX_BRACKET_4 = 166667.0;  // 10,833 + 30% over bracket 3
    private static final double TAX_BRACKET_5 = 666667.0;  // 40,833.33 + 32% over bracket 4
    
    // tax rates 
    private static final double TAX_RATE_1 = 0.0;
    private static final double TAX_RATE_2 = 0.20;
    private static final double TAX_RATE_3 = 0.25;
    private static final double TAX_RATE_4 = 0.30;
    private static final double TAX_RATE_5 = 0.32;
    private static final double TAX_RATE_6 = 0.35;
    
    // fixed tax parts
    private static final double TAX_FIXED_3 = 2500.0;
    private static final double TAX_FIXED_4 = 10833.0;
    private static final double TAX_FIXED_5 = 40833.33;
    private static final double TAX_FIXED_6 = 200833.33;
    
    // OT is 1.25x
    private static final double OVERTIME_MULTIPLIER = 1.25;
    
    // for late calculation
    private static final double MINUTES_PER_HOUR = 60.0;

    public static double calculateWeeklyHours(List<Attendance> attendanceList, String empId) {
        // Sum daily hours from Attendance records
        return attendanceList.stream()
        .filter(a -> a.getEmployeeNumber().equals(empId))
        .mapToDouble(Attendance::getWorkedHours) // You must have this method
        .sum();
    }

    public static int calculateLateMinutes(List<Attendance> attendanceList, String empId) {
        return attendanceList.stream()
        .filter(a -> a.getEmployeeNumber().equals(empId))
        .mapToInt(Attendance::getLateMinutes) // You must have this method
        .sum();
    }
    
    /**
     * Gets SSS contribution based on salary
     * Followed the table from SSS as per MotorPH website
     * 
     * @param grossSalary Weekly gross salary
     * @return How much to contribute to SSS
     */
    public double calculateSSS(double grossSalary) {
        // Make weekly into monthly (x4)
        double monthlySalary = grossSalary * 4; 
        
        // many conditions, maybe there's a better way to do this??
        if (monthlySalary < 3250) return 135.00;
        else if (monthlySalary < 3750) return 157.50;
        else if (monthlySalary < 4250) return 180.00;
        else if (monthlySalary < 4750) return 202.50;
        else if (monthlySalary < 5250) return 225.00;
        else if (monthlySalary < 5750) return 247.50;
        else if (monthlySalary < 6250) return 270.00;
        else if (monthlySalary < 6750) return 292.50;
        else if (monthlySalary < 7250) return 315.00;
        else if (monthlySalary < 7750) return 337.50;
        else if (monthlySalary < 8250) return 360.00;
        else if (monthlySalary < 8750) return 382.50;
        else if (monthlySalary < 9250) return 405.00;
        else if (monthlySalary < 9750) return 427.50;
        else if (monthlySalary < 10250) return 450.00;
        else if (monthlySalary < 10750) return 472.50;
        else if (monthlySalary < 11250) return 495.00;
        else if (monthlySalary < 11750) return 517.50;
        else if (monthlySalary < 12250) return 540.00;
        else if (monthlySalary < 12750) return 562.50;
        else if (monthlySalary < 13250) return 585.00;
        else if (monthlySalary < 13750) return 607.50;
        else if (monthlySalary < 14250) return 630.00;
        else if (monthlySalary < 14750) return 652.50;
        else if (monthlySalary < 15250) return 675.00;
        else if (monthlySalary < 15750) return 697.50;
        else if (monthlySalary < 16250) return 720.00;
        else if (monthlySalary < 16750) return 742.50;
        else if (monthlySalary < 17250) return 765.00;
        else if (monthlySalary < 17750) return 787.50;
        else if (monthlySalary < 18250) return 810.00;
        else if (monthlySalary < 18750) return 832.50;
        else if (monthlySalary < 19250) return 855.00;
        else if (monthlySalary < 19750) return 877.50;
        else if (monthlySalary < 20250) return 900.00;
        else if (monthlySalary < 20750) return 922.50;
        else if (monthlySalary < 21250) return 945.00;
        else if (monthlySalary < 21750) return 967.50;
        else if (monthlySalary < 22250) return 990.00;
        else if (monthlySalary < 22750) return 1012.50;
        else if (monthlySalary < 23250) return 1035.00;
        else if (monthlySalary < 23750) return 1057.50;
        else if (monthlySalary < 24250) return 1080.00;
        else if (monthlySalary < 24750) return 1102.50;
        else return 1125.00; // max contribution 
    }
    
    /**
     * PhilHealth calculation
     * 
     * @param grossSalary Weekly salary before deductions
     * @return Employee's PhilHealth contribution (not the employer part)
     */
    public double calculatePhilHealth(double grossSalary) {
        // Convert to monthly (x4) - hope this is right
        double monthlySalary = grossSalary * 4;
        
        double totalContribution;
        
        // PhilHealth rules:
        if (monthlySalary <= PHILHEALTH_MIN_SALARY) {
            // For low earners: fixed at 300
            totalContribution = PHILHEALTH_MIN_CONTRIBUTION;
        } else if (monthlySalary >= PHILHEALTH_MAX_SALARY) {
            // For high earners: fixed at 1,800
            totalContribution = PHILHEALTH_MAX_CONTRIBUTION;
        } else {
            // Middle income: 3% of monthly pay
            totalContribution = monthlySalary * PHILHEALTH_RATE;
        }
        
        // Employee only pays half
        return totalContribution * PHILHEALTH_EE_SHARE;
    }
    
    /**
     * Calculates Pag-IBIG
     * 
     * @param grossSalary Weekly gross salary
     * @return Pag-IBIG contribution amount
     */
    public double calculatePagIbig(double grossSalary) {
        // Monthly estimate
        double monthlySalary = grossSalary * 4;
        
        double contribution;
        
        if (monthlySalary <= PAGIBIG_THRESHOLD) {
            // Lower income: 1%
            contribution = monthlySalary * PAGIBIG_RATE_LOWER;
        } else {
            // Higher income: 2%
            contribution = monthlySalary * PAGIBIG_RATE_UPPER;
        }
        
        // Can't exceed 100 pesos
        return Math.min(contribution, PAGIBIG_MAX);
    }
    
    /**
     * Figures out how much tax to deduct
     * 
     * @param taxableIncome Income after other deductions
     * @return Tax amount
     */
    public double calculateTax(double taxableIncome) {
        // Convert to monthly for tax brackets
        double monthlyTaxableIncome = taxableIncome * 4;
        
        // Find which bracket applies
        double monthlyTax;
        
        if (monthlyTaxableIncome <= TAX_BRACKET_1) {
            // No tax for low income
            monthlyTax = 0.0;
        } else if (monthlyTaxableIncome <= TAX_BRACKET_2) {
            // 20% of amount over first bracket
            monthlyTax = (monthlyTaxableIncome - TAX_BRACKET_1) * TAX_RATE_2;
        } else if (monthlyTaxableIncome <= TAX_BRACKET_3) {
            // Fixed amount + 25% of excess
            monthlyTax = TAX_FIXED_3 + ((monthlyTaxableIncome - TAX_BRACKET_2) * TAX_RATE_3);
        } else if (monthlyTaxableIncome <= TAX_BRACKET_4) {
            // Fixed amount + 30% of excess
            monthlyTax = TAX_FIXED_4 + ((monthlyTaxableIncome - TAX_BRACKET_3) * TAX_RATE_4);
        } else if (monthlyTaxableIncome <= TAX_BRACKET_5) {
            // Fixed amount + 32% of excess
            monthlyTax = TAX_FIXED_5 + ((monthlyTaxableIncome - TAX_BRACKET_4) * TAX_RATE_5);
        } else {
            // Highest bracket
            monthlyTax = TAX_FIXED_6 + ((monthlyTaxableIncome - TAX_BRACKET_5) * TAX_RATE_6);
        }
        
        // Back to weekly amount
        return monthlyTax / 4;
    }
    
    /**
    * Calculates everything about a salary
    * This is the main function that our system uses
    * Returns array with all the details we need
    * 
    * @param regularHours Normal hours worked
    * @param overtimeHours Extra hours
    * @param hourlyRate Pay per hour
    * @param hasLateness Check if employee has late
    * @param prorateDeductions Prorate deductions to 1/4
    * @return All the salary details in an array
    */
    public double[] calculateFullSalaryDetails(double regularHours, double overtimeHours, double hourlyRate, boolean hasLateness, boolean prorateDeductions) {
        double[] details = new double[9]; // uses array

        // Regular pay calculation
        double regularPay = regularHours * hourlyRate;

        // OT pay - none if employee was late (company policy)
        double overtimePay = 0;
        if (!hasLateness && overtimeHours > 0) {
            overtimePay = overtimeHours * hourlyRate * OVERTIME_MULTIPLIER;
        }

        // Add them up for gross
        double grossSalary = regularPay + overtimePay;
        details[0] = grossSalary;

        // Initializing these variables to zero before potentially calculating them later when called
        double sssContribution = 0;
        double philhealthContribution = 0;
        double pagibigContribution = 0;

        if (hasLateness) {
            if (prorateDeductions) {
                // Use 1/4 of monthly for weekly pay
                sssContribution = calculateSSS(grossSalary) / 4;
                philhealthContribution = calculatePhilHealth(grossSalary) / 4;
                pagibigContribution = calculatePagIbig(grossSalary) / 4;
            } else {
                // Full deduction amount
                sssContribution = calculateSSS(grossSalary);
                philhealthContribution = calculatePhilHealth(grossSalary);
                pagibigContribution = calculatePagIbig(grossSalary);
            }
        }

        details[1] = sssContribution;
        details[2] = philhealthContribution;
        details[3] = pagibigContribution;

        // Taxable income is what's left after contributions
        double taxableIncome = grossSalary - sssContribution - philhealthContribution - pagibigContribution;
        details[4] = taxableIncome;

        // Tax calculation
        double tax = 0;
        if (hasLateness) { // only if late
            if (prorateDeductions) {
                tax = calculateTax(taxableIncome) / 4; // 1/4 of monthly tax
            } else {
                tax = calculateTax(taxableIncome); // full tax
            }
        }
        details[5] = tax;

        // Take-home pay
        double netSalary = taxableIncome - tax;
        details[6] = netSalary;

        // Save these for the GUI display
        details[7] = regularPay;
        details[8] = overtimePay;

        return details;
    }
    
    /**
    * Figures out late penalty
    * 
    * @param regularPay Normal pay amount
    * @param lateMinutes How many minutes late
    * @return Penalty amount
    */
    public double calculateLatePenalty(double regularPay, int lateMinutes) {
        if (lateMinutes <= 0) return 0.0; // not late = no penalty

        // Formula for penalty
        double latePenaltyAmount = regularPay * LATE_PENALTY_RATE * (lateMinutes / 480.0); // 8 hour day = 480 mins

        // Max 20% of pay can be deducted
        return Math.min(latePenaltyAmount, regularPay * 0.20);
    }
    
    /**
    * Older version of the method - keeping for compatibility
    * Will use default values for the new parameters
     * @param hourlyRate
    */
    public double[] calculateFullSalaryDetails(double regularHours, double overtimeHours, double hourlyRate) {
        // Just use the full method with defaults
        return calculateFullSalaryDetails(regularHours, overtimeHours, hourlyRate, false, false);
    }
    
    
}
