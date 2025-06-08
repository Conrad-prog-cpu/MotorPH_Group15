/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

/**
 *
 * @author santos.conrad
 */
public class PayrollLogic {

    private Deductions deductions;

    public PayrollLogic() {
        this.deductions = new Deductions();
    }

    public double calculateGrossWeeklySalary(double hourlyRate, double hoursWorked, double rice, double phone, double clothing) {
    double baseSalary = hourlyRate * hoursWorked;
    double totalBenefits = rice + phone + clothing;
    return baseSalary + totalBenefits;
    }

    public double calculateNetWeeklySalary(double basicSalary, double grossWeeklySalary) {
        // Get total monthly deductions
        double totalMonthlyDeductions = deductions.getTotalDeductions(basicSalary, grossWeeklySalary);

        // Convert to weekly (approx. 4 weeks in a month)
        double weeklyDeductions = totalMonthlyDeductions / 4;

        // Net weekly = gross weekly - 1/4 monthly deductions
        return grossWeeklySalary - weeklyDeductions;
    }

    public void displaySalaryBreakdown(double hourlyRate, double hoursWorked, double riceSubsidy,
                                       double phoneAllowance, double clothingAllowance, double basicSalary) {

        double grossWeekly = calculateGrossWeeklySalary(hourlyRate, hoursWorked, riceSubsidy, phoneAllowance, clothingAllowance);
        double netWeekly = calculateNetWeeklySalary(basicSalary, grossWeekly);

        double monthlySSS = deductions.calculateSSS(basicSalary);
        double monthlyPH = deductions.calculatePhilHealth(basicSalary);
        double monthlyPI = deductions.calculatePagIbig(basicSalary);
        double monthlyTax = deductions.getMonthlyWithholdingTax(basicSalary);

        System.out.println("GROSS WEEKLY SALARY: ₱" + String.format("%.2f", grossWeekly));
        System.out.println("SSS DEDUCTION (monthly): ₱" + monthlySSS);
        System.out.println("PhilHealth DEDUCTION (monthly): ₱" + monthlyPH);
        System.out.println("Pag-IBIG DEDUCTION (monthly): ₱" + monthlyPI);
        System.out.println("Withholding TAX (monthly): ₱" + String.format("%.2f", monthlyTax));
        System.out.println("TOTAL DEDUCTIONS (weekly): ₱" + String.format("%.2f", (monthlySSS + monthlyPH + monthlyPI + monthlyTax) / 4));
        System.out.println("NET WEEKLY SALARY: ₱" + String.format("%.2f", netWeekly));
    }

    double calculateLateDeduction(double hourlyRate, int lateMinutes) {
        double perMinuteRate = hourlyRate / 60;
        return perMinuteRate * lateMinutes;}
}
