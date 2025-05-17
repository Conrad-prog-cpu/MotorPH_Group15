// FileHandler.java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class helps us read employee and attendance records from text files.
 * We use it to load data into our program for processing.
 */
public class FileHandler {

    /**
     * Reads attendance records for a specific employee from a text file.
     * This method scans each line and checks if the employee ID matches.
     * 
     * @param filePath Path to the attendance data file
     * @param employeeId Employee ID to filter records
     * @return Array of TimeKeeping objects for the specified employee
     * @throws IOException If there's an error reading the file
     */
    public static TimeKeeping[] readAttendanceRecords(String filePath, String employeeId) throws IOException {
        List<TimeKeeping> records = new ArrayList<>(); // List to store valid records
        
        // Open file and start reading line by line
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line; // Stores each line from the file
            while ((line = reader.readLine()) != null) {
                System.out.println("Reading line: " + line); // Debugging output
                String[] parts = line.split(","); // Split line by commas
                if (parts.length >= 4) { // Ensure there are enough columns
                    String id = parts[0].trim(); // Get employee ID
                    if (id.equals(employeeId)) { // Check if it matches
                        String date = parts[1].trim();
                        String timeIn = parts[2].trim();
                        String timeOut = parts[3].trim();
                        
                        // Add new attendance record
                        records.add(new TimeKeeping(employeeId, date, timeIn, timeOut));
                    }
                }
            }
        }
        
        // Convert list into an array (Needed to return multiple records)
        return records.toArray(new TimeKeeping[0]); 
    }
}
