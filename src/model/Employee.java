public class Employee {
    private String employeeID;
    private String name;
    private double basicSalary;
    private double semiMonthlyRate;
    private double hourlyRate;

    public Employee(String employeeID, String name, double basicSalary, double semiMonthlyRate, double hourlyRate) {
        this.employeeID = employeeID;
        this.name = name;
        this.basicSalary = basicSalary;
        this.semiMonthlyRate = semiMonthlyRate;
        this.hourlyRate = hourlyRate;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getName() {
        return name;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public double getSemiMonthlyRate() {
        return semiMonthlyRate;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }
}
