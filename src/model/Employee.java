package model;

// This class represents an Employee with relevant salary details
public class Employee {

    // Employee ID (e.g., unique identifier for each employee)
    private String employeeID;

    // Full name of the employee
    private String name;

    // Basic monthly salary of the employee
    private double basicSalary;

    // Semi-monthly salary rate (used if paid twice a month)
    private double semiMonthlyRate;

    // Hourly rate used for computing pay based on hours worked
    private double hourlyRate;

    // Constructor to initialize all attributes of the Employee object
    public Employee(String employeeID, String name, double basicSalary, double semiMonthlyRate, double hourlyRate) {
        this.employeeID = employeeID;
        this.name = name;
        this.basicSalary = basicSalary;
        this.semiMonthlyRate = semiMonthlyRate;
        this.hourlyRate = hourlyRate;
    }

    // Getter method to retrieve employee ID
    public String getEmployeeID() {
        return employeeID;
    }

    // Getter method to retrieve employee name
    public String getName() {
        return name;
    }

    // Getter method to retrieve the basic salary
    public double getBasicSalary() {
        return basicSalary;
    }

    // Getter method to retrieve the semi-monthly rate
    public double getSemiMonthlyRate() {
        return semiMonthlyRate;
    }

    // Getter method to retrieve the hourly rate
    public double getHourlyRate() {
        return hourlyRate;
    }
}
