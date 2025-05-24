# CP2 MotorPH Payroll System

## Project Overview

The **MotorPH Payroll System** is a Java-based application designed to manage payroll processes for MotorPH company employees. It offers functionalities for employee information management, attendance tracking, payroll generation, and reporting.

---

## Features

### Employee Management
* Search employees
* List all employees with their details
  
### Payroll Management
* Create custom payroll for specific employees
* Calculate gross and net pay based on hours worked

### Reports
* Generate individual payslips

### Automated Calculations
* **SSS contributions** based on salary brackets
* **PhilHealth contributions** (3% of monthly basic salary)
* **Pag-IBIG contributions** (2% of monthly basic salary)
* **Withholding tax** using progressive tax rates

---

## Technical Details
* **Language**: Java 17+
* **Data Source**: Online CSV files (Google Sheets published as CSV)
* **Dependencies**: None (pure Java implementation)

---

## Calculations

### Hours Worked
* Derived from time-in and time-out records
* Late arrivals (after 8:10 AM) are highlighted in reports

### Salary Components
* **Gross Pay** = Hours Worked × Hourly Rate
* **Net Pay** = Gross Pay − (SSS + PhilHealth + Pag-IBIG + Tax)

### Deductions
* **SSS**: Based on predefined salary brackets
* **PhilHealth**: 3% of monthly salary (divided by 2 for semi-monthly)
* **Pag-IBIG**: 2% of monthly salary
* **Withholding Tax**: Progressive based on taxable income

---

## Developers | Group 15
* Conrado Santos
* Carl Justine Pontanilla
* Ghaby Gonzales 
* Rhynne Gracelle Pontanilla 
