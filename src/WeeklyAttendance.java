// WeeklyAttendance.java
import java.util.ArrayList;
import java.util.List;

/**
 * WeeklyAttendance class to handle weekly attendance records and calculations
 * 
 * This class aggregates daily TimeKeeping records to provide weekly summaries.
 * I improved on the original design by adding methods for lateness tracking
 */
public class WeeklyAttendance {
    private String employeeId;        // Employee identifier
    private String weekStartDate;     // Date the week starts (e.g., "06/03/2024")
    private List<TimeKeeping> dailyAttendance;  // List of daily attendance records
    
    // Standard weekly hours constant - 40 hours (8 hours × 5 days)
    // Used static final for optimization as mentioned in lecture/resources
    private static final double WEEKLY_REGULAR_HOURS = 40.0;
    
    /**
     * Constructor for weekly attendance
     * Simple initialization - created for modularity and reusability
     */
    public WeeklyAttendance(String employeeId, String weekStartDate) {
        this.employeeId = employeeId;
        this.weekStartDate = weekStartDate;
        // Used ArrayList implementation for its dynamic sizing capability
        this.dailyAttendance = new ArrayList<>();  // Initially empty
    }
    
    /**
     * Add a daily attendance record to the weekly collection
     * I added error checking to prevent data inconsistency issues
     * 
     * @param attendance The daily TimeKeeping record to add
     */
    public void addDailyAttendance(TimeKeeping attendance) {
        // Verify the record belongs to the correct employee
        if (attendance.getEmployeeId().equals(employeeId)) {
            dailyAttendance.add(attendance);
        } else {
            // This error message was helpful during my testing phase
            System.out.println("Error: Employee ID mismatch.");
        }
    }
    
    /**
     * Get count of daily attendance records
     * Added this method to help with validation and testing
     * 
     * @return Number of daily records in the collection
     */
    public int getDailyAttendanceCount() {
        return dailyAttendance.size();
    }
    
    /**
     * Calculate total weekly hours worked
     * This is a key method that uses a summation pattern we learned in class
     * 
     * @return Total hours worked in the week (rounded to 2 decimal places)
     */
    public double calculateWeeklyHours() {
        // Accumulator pattern for summing values
        double totalHours = 0.0;
        
        // Loop through each day and add up the hours
        // I chose the enhanced for loop for cleaner code compared to traditional for loop
        for (TimeKeeping day : dailyAttendance) {
            totalHours += day.calculateDailyHours();
        }
        
        // Round to 2 decimal places using the efficient math formula
        // I learned this trick is faster than using DecimalFormat
        return Math.round(totalHours * 100.0) / 100.0;
    }
    
    /**
     * Calculate regular hours within 8am-5pm window only
     * This fixes the critical issue with regular hours calculation
     * 
     * @return Regular hours (max 40 per week)
     */
    public double calculateRegularHours() {
        double totalRegularHours = 0.0;

        // Sum the properly calculated daily regular hours
        for (TimeKeeping day : dailyAttendance) {
            totalRegularHours += day.calculateRegularHours();
        }

        // Round to 2 decimal places
        return Math.round(totalRegularHours * 100.0) / 100.0;
    }
   /**
    * Calculate overtime hours considering company policies
    * Fixed to properly handle the 8am-5pm principle
    * 
    * @return Total overtime hours for the week
    */
   public double calculateOvertimeHours() {
       // No overtime for employees with lateness
       if (hasDeductibleLateness()) {
           return 0.0;
       }

       double totalOvertimeHours = 0.0;

       // Sum up overtime from each individual day
       // This ensures overtime is only counted for days they stayed past 5pm
       for (TimeKeeping day : dailyAttendance) {
           totalOvertimeHours += day.calculateOvertimeHours();
       }

       // Round to 2 decimal places
       return Math.round(totalOvertimeHours * 100.0) / 100.0;
   }
    /**
     * Get total late minutes for the week
     * I wrote this after struggling with the salary calculation logic
     * 
     * @return Total minutes late across all days
     */
    public int getTotalLateMinutes() {
        int totalLateMinutes = 0;
    
        // Sum up late minutes from each day
        for (TimeKeeping day : dailyAttendance) {
            totalLateMinutes += day.calculateLateMinutes();
        }
    
        return totalLateMinutes;
        // Note: Considered adding rounding but minutes should be whole numbers
    }

    /**
     * Check if there are any days with deductible lateness
     * This is my own enhancement to make the payroll logic cleaner
     * 
     * @return true if any day has lateness beyond the grace period
     */
    public boolean hasDeductibleLateness() {
        // Used efficient short-circuit return instead of accumulator pattern
        for (TimeKeeping day : dailyAttendance) {
            if (day.isLateForDeduction()) {
                return true;  // Return early once we find any late day
            }
        }
        return false;  // No lateness found
    }

    /**
     * Get total late minutes that qualify for deduction
     * Differentiates between grace period and deductible lateness
     * 
     * @return Total deductible late minutes
     */
    public int getDeductibleLateMinutes() {
        int deductibleLateMinutes = 0;
    
        // Only count late minutes for days that exceed the grace period
        for (TimeKeeping day : dailyAttendance) {
            if (day.isLateForDeduction()) {
               deductibleLateMinutes += day.calculateLateMinutes();
            }
        }
    
        return deductibleLateMinutes;
    }

    /**
     * Get total undertime minutes for the week
     * "Undertime" means leaving early, learned this term in Week 7
     * 
     * @return Total minutes of undertime across all days
     */
    public int getTotalUndertimeMinutes() {
        int totalUndertimeMinutes = 0;
    
        // Sum up undertime from each day - similar pattern to late minutes
        for (TimeKeeping day : dailyAttendance) {
            totalUndertimeMinutes += day.calculateUndertimeMinutes();
        }
    
        return totalUndertimeMinutes;
    }
    
    /**
     * Display weekly attendance details in a formatted table
     * This was challenging to make it look nice in the console!
     * Used printf formatting which I learned about in a coding demo resources
     */
    public void displayWeeklyDetails() {
        System.out.println("+-------------------------+");
        System.out.println("|       WEEKLY HOURS      |");
        System.out.println("+-------------------------+");
        System.out.println("Employee: " + employeeId);
        System.out.println("Week of: " + weekStartDate);
        System.out.println();

        // Header with all columns left-aligned
        // Used printf for consistent column widths - much cleaner than concatenation
        System.out.printf("%-8s  %-10s  %-10s  %-8s  %-8s  %-8s\n", 
            "Date", "Time In", "Time Out", "Hours", "Reg", "OT");
        System.out.println("--------  ----------  ----------  --------  --------  --------");

        // Data rows with all values left-aligned
        // Looping through each day to display details
        for (TimeKeeping day : dailyAttendance) {
            System.out.printf("%-8s  %-10s  %-10s  %-8.2f  %-8.2f  %-8.2f\n", 
                day.getDate(), 
                day.getTimeIn(), 
                day.getTimeOut(), 
                day.calculateDailyHours(),
                day.calculateRegularHours(),
                day.calculateOvertimeHours());
        }
        
        // Summary information
        System.out.println();
        System.out.println("Total Hours Worked: " + calculateWeeklyHours());
        System.out.println("Regular Hours: " + calculateRegularHours());
        System.out.println("Overtime Hours: " + calculateOvertimeHours());
        
        // Only show lateness if applicable
        // I added this conditional to make the output cleaner when no lateness
        if (getTotalLateMinutes() > 0) {
            int lateMinutes = getTotalLateMinutes();
            int lateHours = lateMinutes / 60;  // Whole hours
            int lateRemainingMinutes = lateMinutes % 60;  // Remaining minutes

            // Format late time in a more readable way (hrs:mins)
            if (lateHours > 0) {
                System.out.println("Total Late: " + lateHours + " hrs " + lateRemainingMinutes + " mins (" + lateMinutes + " mins)");
            } else {
                System.out.println("Total Late: " + lateMinutes + " mins");
            }
        }
        
        // Only show undertime if applicable
        if (getTotalUndertimeMinutes() > 0) {
            System.out.println("Total Undertime Minutes: " + getTotalUndertimeMinutes());
        }
        
        System.out.println();
        // TODO: Maybe add more statistics in the future, like average daily hours?
    }
    
    /**
     * Get the employee ID
     * @return Employee ID
     */
    public String getEmployeeId() {
        return employeeId;
    }
    
    /**
     * Get the week start date
     * @return Week start date
     */
    public String getWeekStartDate() {
        return weekStartDate;
    }
    
    /**
     * Get the daily attendance records
     * @return List of daily TimeKeeping records
     */
    public List<TimeKeeping> getDailyAttendance() {
        // Returns the internal list - could return defensive copy if needed
        return dailyAttendance;
    }
}
