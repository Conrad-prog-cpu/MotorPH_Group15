// Employee.java
/**
 * Employee class to store basic employee information
 * 
 * This class handles the core employee data structure.
 * I kept this simple with just the essential properties while
 * ensuring good encapsulation principles (as discussed in Week 4).
 */
public class Employee {
    // Private properties to enforce encapsulation
    // Used meaningful names as recommended in the Java style guide
    private String employeeId;  // Unique identifier for each employee
    private String lastName;    // Family name
    private String firstName;   // Given name
    private String birthday;    // Format: MM/DD/YYYY
    private double hourlyRate;  // Pay rate per hour (PHP)
    
    /**
     * Constructor for Employee class - creates a new employee instance
     * Note: We initially set hourlyRate to 0 and update it later
     * 
     * @param employeeId Employee ID (format: 10XXX)
     * @param lastName Last name 
     * @param firstName First name
     * @param birthday Birthday in MM/DD/YYYY format
     */
    public Employee(String employeeId, String lastName, String firstName, String birthday) {
        // Initialize all fields from parameters
        // This pattern ensures we don't forget any fields
        this.employeeId = employeeId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthday = birthday;
        this.hourlyRate = 0.0; // Default value
        
        // TODO: Consider adding validation for employeeId format (should be 10XXX)
        // Ran out of time to implement this, but would be a good enhancement
    }
    
    /**
     * Get employee ID (getter method)
     * Note: Method name doesn't follow the exact field name - this was a design choice
     * to make the API more intuitive for calling code
     * 
     * @return Employee ID
     */
    public String getEmployeeNumber() {
        return employeeId; // Simply return the field value
    }
    
    /**
     * Get employee full name formatted for display
     * Used LastName, FirstName format as specified in the requirements
     * 
     * @return Last name followed by first name (e.g., "Garcia, Manuel")
     */
    public String getFullName() {
        // Format: LastName, FirstName
        // This format is common in business applications and formal displays
        return lastName + ", " + firstName;
        
        // Could have also used String.format() for more complex formatting:
        // return String.format("%s, %s", lastName, firstName);
    }
    
    /**
     * Get first name
     * Used standard JavaBean naming convention for getters
     * 
     * @return First name
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Get last name
     * Used standard JavaBean naming convention for getters
     * 
     * @return Last name
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Get birthday
     * String format gives more flexibility than using Date object
     * 
     * @return Birthday as string (MM/DD/YYYY)
     */
    public String getBirthday() {
        return birthday;
        
    }
    
    /**
     * Get hourly rate
     * Critical for payroll calculations
     * 
     * @return Hourly rate in PHP
     */
    public double getHourlyRate() {
        return hourlyRate;
    }
    
    /**
     * Set hourly rate
     * This is separate from constructor because rates may change
     * 
     * @param hourlyRate New hourly rate (must be positive)
     */
    public void setHourlyRate(double hourlyRate) {
        // Could add validation here to ensure rate is positive
        // but keeping it simple for now
        this.hourlyRate = hourlyRate;
        
        // In a real-world application, we might want to:
        // 1. Validate the rate is within acceptable ranges
        // 2. Log the change for audit purposes
        // 3. Maybe store historical rates
    }
}
