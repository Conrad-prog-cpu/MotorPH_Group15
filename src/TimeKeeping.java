// TimeKeeping.java
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/* TimeKeeping class to handle all the employee time tracking functionality
 * This was challenging but I think I figured out the most efficient way
 * References: Java Duration API documentation and Week 6 lectures
 */
public class TimeKeeping {
    // Needed properties based on requirements doc
    private String employeeId;  // unique ID for each employee
    private String date;        // the work date (format varies)
    private String timeIn;      // when they clocked in
    private String timeOut;     // when they clocked out
    
    // Constants for work schedule - based on company policy from project specs
    // Using static final for efficiency as discussed in lecture
    private static final LocalTime REGULAR_START_TIME = LocalTime.of(8, 0); // 8am start time
    private static final LocalTime REGULAR_END_TIME = LocalTime.of(17, 0);  // 5pm end time
    private static final LocalTime GRACE_PERIOD_END = LocalTime.of(8, 10);  // 10min grace period
    private static final LocalTime DEDUCTION_START_TIME = LocalTime.of(8, 11); // when penalties start
    private static final double REGULAR_HOURS_PER_DAY = 8.0; // standard work day hours
    
    // Constructor - pretty straightforward
    public TimeKeeping(String employeeId, String date, String timeIn, String timeOut) {
        this.employeeId = employeeId;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        // I considered validating the time formats here but decided to handle in methods
    }
    
    // Basic getters - these were easy to implement
    public String getEmployeeId() {
        return employeeId;
    }
    
    public String getDate() {
        return date;
    }
    
    public String getTimeIn() {
        return timeIn;
    }
    
    public String getTimeOut() {
        return timeOut;
    }
    
    /* This was the trickiest method to write
     * Had to rewrite it three times to get it right :(
     * The challenge was handling the lunch break deduction
     */
    public double calculateDailyHours() {
        try {
            // Convert string times to LocalTime objects using DateTimeFormatter
            // The "H:mm" pattern means hour (1-24) and minutes - from Week 6 examples
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
            LocalTime inTime = LocalTime.parse(timeIn, formatter);
            LocalTime outTime = LocalTime.parse(timeOut, formatter);
            
            // Get the time difference - Duration class is perfect for this
            Duration duration = Duration.between(inTime, outTime);
            
            // Convert to decimal hours - divide minutes by 60.0 to get fractional hours
            double hours = duration.toMinutes() / 60.0;
            
            // Apply lunch break rule - subtract 1 hour if worked more than 5 hours
            // Got this rule from the project requirements doc
            if (hours > 5.0) {
                hours -= 1.0;  // subtract lunch hour
            }
            
            // Round to 2 decimal places - this formula is more efficient than using DecimalFormat
            return Math.round(hours * 100.0) / 100.0;
        } catch (DateTimeParseException e) {
            // There might be a better error handling approach but this works for now
            System.out.println("Error parsing time: " + e.getMessage());
            return 0.0;  // Default to zero hours on error
        }
    }

    /**
     * Method to calculate regular hours (capped at standard work hours)
     * Fixed to properly account for late arrival - reducing regular hours
     * 
     * @return Regular hours (maximum 8 hours, reduced if late arrival)
     */
    public double calculateRegularHours() {
        try {
            // Parse time strings to LocalTime objects
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
            LocalTime inTime = LocalTime.parse(timeIn, formatter);
            LocalTime outTime = LocalTime.parse(timeOut, formatter);

            // Define workday boundaries for determining regular hours
            LocalTime workdayStart = REGULAR_START_TIME; // 8:00 AM
            LocalTime workdayEnd = REGULAR_END_TIME;     // 5:00 PM

            // If employee came in late, their regular hours should be less than 8
            // Adjust the effective start time - can't be earlier than when they arrived
            LocalTime effectiveStartTime = inTime.isAfter(workdayStart) ? inTime : workdayStart;

            // Adjust the effective end time - can't be later than when they left or 5pm
            LocalTime effectiveEndTime = outTime.isBefore(workdayEnd) ? outTime : workdayEnd;

            // Calculate effective regular hours within 8am-5pm window
            // This finally fixes the bug in the original implementation
            double effectiveRegularMinutes = 0;

            // Only calculate if there's a valid time period
            if (effectiveEndTime.isAfter(effectiveStartTime)) {
                Duration regularDuration = Duration.between(effectiveStartTime, effectiveEndTime);
                effectiveRegularMinutes = regularDuration.toMinutes();

                // Subtract lunch hour (60 minutes) if regular period includes lunch
                // Assuming lunch is from 12-1pm
                LocalTime lunchStart = LocalTime.of(12, 0);
                LocalTime lunchEnd = LocalTime.of(13, 0);

                // Check if work period covers any part of lunch
                boolean workIncludesLunchStart = !effectiveStartTime.isAfter(lunchStart) && 
                                                 effectiveEndTime.isAfter(lunchStart);
                boolean workIncludesLunchEnd = effectiveStartTime.isBefore(lunchEnd) && 
                                               !effectiveEndTime.isBefore(lunchEnd);
                boolean workEncompassesLunch = workIncludesLunchStart && workIncludesLunchEnd;
                boolean workWithinLunch = !workIncludesLunchStart && !workIncludesLunchEnd && 
                                          !effectiveStartTime.isBefore(lunchStart) && 
                                          !effectiveEndTime.isAfter(lunchEnd);

                // Calculate lunch deduction based on overlap
                int lunchDeduction = 0;
                if (workEncompassesLunch) {
                    // Entire lunch period is within work hours
                    lunchDeduction = 60;
                } else if (workIncludesLunchStart) {
                    // Work includes start of lunch until end of work
                    lunchDeduction = (int) Duration.between(lunchStart, effectiveEndTime).toMinutes();
                    lunchDeduction = Math.min(lunchDeduction, 60); // Cap at 60 minutes
                } else if (workIncludesLunchEnd) {
                    // Work includes from start of work until end of lunch
                    lunchDeduction = (int) Duration.between(effectiveStartTime, lunchEnd).toMinutes();
                    lunchDeduction = Math.min(lunchDeduction, 60); // Cap at 60 minutes
                } else if (workWithinLunch) {
                    // Entire work period is within lunch
                    lunchDeduction = (int) effectiveRegularMinutes;
                }

                // Subtract lunch deduction
                effectiveRegularMinutes -= lunchDeduction;
            }

            // Convert minutes to hours and round to 2 decimal places
            double regularHours = effectiveRegularMinutes / 60.0;
            return Math.round(regularHours * 100.0) / 100.0;

        } catch (DateTimeParseException e) {
            System.out.println("Error calculating regular hours: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Method to calculate overtime hours
     * Only counts hours worked after 5pm, and only if employee wasn't late
     * 
     * @return Overtime hours worked beyond 5pm
     */
    public double calculateOvertimeHours() {
        // No overtime for late employees per company policy
        if (isLateForDeduction()) {
            return 0.0;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
            LocalTime outTime = LocalTime.parse(timeOut, formatter);

            // Only calculate overtime if they stayed past 5pm
            if (outTime.isAfter(REGULAR_END_TIME)) {
                // Calculate minutes worked after 5pm
                Duration overtimeDuration = Duration.between(REGULAR_END_TIME, outTime);
                double overtimeMinutes = overtimeDuration.toMinutes();

                // Convert to hours and round
                double overtimeHours = overtimeMinutes / 60.0;
                return Math.round(overtimeHours * 100.0) / 100.0;
            }

            return 0.0;
        } catch (DateTimeParseException e) {
            System.out.println("Error calculating overtime: " + e.getMessage());
            return 0.0;
        }
    }
    /* Checks if employee is late beyond grace period
     * This is used for salary deductions according to company policy
     * Initially had a bug where I was checking against GRACE_PERIOD_END instead of DEDUCTION_START_TIME
     */
    public boolean isLateForDeduction() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
            LocalTime inTime = LocalTime.parse(timeIn, formatter);
        
            // If login time is after 8:11am, they're late enough for deduction
            return inTime.isAfter(DEDUCTION_START_TIME);
        } catch (DateTimeParseException e) {
            // This shouldn't happen if data is valid, but just in case
            System.out.println("Error parsing time: " + e.getMessage());
            return false;  // Give employee benefit of the doubt on errors
        }
    }

    // Calculate late minutes (after 8:00 AM)
    // I optimized this by checking if they're late first before calculating
    public int calculateLateMinutes() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
            LocalTime inTime = LocalTime.parse(timeIn, formatter);
            
            // Quick return if not late at all - no need to calculate
            if (!inTime.isAfter(REGULAR_START_TIME)) {
                return 0;  // On time or early
            }
            
            // Calculate minutes late - Duration.between is perfect for this
            Duration lateDuration = Duration.between(REGULAR_START_TIME, inTime);
            return (int) lateDuration.toMinutes();  // Convert to minutes and cast to int
        } catch (DateTimeParseException e) {
            // Using println for debugging - maybe switch to logger in future version
            System.out.println("Error parsing time: " + e.getMessage());
            return 0;  // Default to on-time if there's an error
        }
    }
    
    /* Calculates how many minutes employee left before end of day
     * Was tempted to call these "early minutes" but "undertime" is the correct term
     */
    public int calculateUndertimeMinutes() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
            LocalTime outTime = LocalTime.parse(timeOut, formatter);
            
            // If they left after or at end time, no undertime
            if (!outTime.isBefore(REGULAR_END_TIME)) {
                return 0;  // Worked full day or overtime
            }
            
            // Calculate undertime minutes - time between when they left and when day ends
            Duration undertimeDuration = Duration.between(outTime, REGULAR_END_TIME);
            return (int) undertimeDuration.toMinutes();  // Cast to int since we want whole minutes
        } catch (DateTimeParseException e) {
            // Might improve error handling in v2.0 but this works for now
            System.out.println("Error parsing time: " + e.getMessage());
            return 0;  // Default to no undertime if there's an error
        }
    }
}
