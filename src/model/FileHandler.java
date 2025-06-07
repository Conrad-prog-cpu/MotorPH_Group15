package model; // Declares the package location of this class

import com.opencsv.*; // Imports OpenCSV classes for CSV operations
import com.opencsv.exceptions.CsvValidationException; // Imports exception handling for CSV validation

import java.io.*; // Imports Java IO classes for file handling
import java.util.*; // Imports utility classes like List, ArrayList

public class FileHandler { // Class declaration for handling file-related operations

    private List<String> employeeHeaders = new ArrayList<>(); // Stores the column headers for the employee file
    private List<String[]> employeeData = new ArrayList<>(); // Stores employee data rows

    private List<String> attendanceHeaders = new ArrayList<>(); // Stores column headers for attendance file
    private List<String[]> attendanceData = new ArrayList<>(); // Stores attendance data rows

    private final String folderPath = "data"; // Directory path where files are stored

    public void readEmployeeFile() { // Reads employee.txt and loads data
        File file = new File(folderPath + "/employee.txt"); // Defines the employee file path

        if (!file.exists()) { // Checks if file exists
            System.out.println("❌ employee.txt not found."); // Logs error if file doesn't exist
            return; // Exit method
        }

        employeeHeaders.clear(); // Clears any previous headers
        employeeData.clear(); // Clears any previous employee data

        try (BufferedReader br = new BufferedReader(new FileReader(file))) { // BufferedReader for file reading
            String line; // Holds each line
            boolean isFirstLine = true; // Flag to check header row

            while ((line = br.readLine()) != null) { // Reads line by line
                if (line.trim().isEmpty()) continue; // Skips empty lines

                String[] fields = line.split("(?=(?:[^\"]*\"[^\"]*\")*[^\"]*)\\;"); // Splits by semicolon, preserving quoted text

                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fields[i].replaceAll("^\"|\"$", "").trim(); // Removes surrounding quotes and trims whitespace
                }

                if (isFirstLine) { // If first line, treat as headers
                    employeeHeaders.addAll(Arrays.asList(fields)); // Adds headers
                    isFirstLine = false; // Set flag false after headers
                } else { // If data row
                    if (fields.length < employeeHeaders.size()) { // If row is too short
                        String[] fixed = Arrays.copyOf(fields, employeeHeaders.size()); // Pads row with empty strings
                        Arrays.fill(fixed, fields.length, employeeHeaders.size(), "");
                        employeeData.add(fixed); // Add padded row
                    } else if (fields.length > employeeHeaders.size()) { // If row is too long
                        String[] fixed = Arrays.copyOf(fields, employeeHeaders.size()); // Trims excess fields
                        employeeData.add(fixed); // Add trimmed row
                    } else {
                        employeeData.add(fields); // Row matches header length
                    }
                }
            }

            System.out.println("✅ employee.txt loaded: " + employeeData.size() + " rows"); // Success message

        } catch (IOException e) { // Handle file read exception
            System.out.println("❌ Error reading employee file: " + e.getMessage()); // Print error
        }
    }

    public void readAttendanceFile() { // Reads attendance.txt
        String filePath = folderPath + "/attendance.txt"; // Defines file path

        try {
            File file = new File(filePath); // File object
            if (!file.exists()) { // If file doesn't exist
                System.out.println("attendance.txt file not found."); // Log error
                return; // Exit method
            }

            try (CSVReader reader = new CSVReaderBuilder(new FileReader(file)) // Builds CSVReader
                    .withCSVParser(new CSVParserBuilder().withSeparator(',').build()) // Uses comma as separator
                    .build()) {

                attendanceHeaders.clear(); // Clears previous headers
                attendanceData.clear(); // Clears previous data

                String[] line;
                while ((line = reader.readNext()) != null) { // Reads each line
                    if (line.length == 0 || line[0].trim().isEmpty()) continue; // Skip empty
                    attendanceData.add(line); // Add row
                }

                System.out.println("✅ Attendance data loaded: " + attendanceData.size() + " rows"); // Success

            } catch (CsvValidationException ex) { // Catch parsing errors
                System.err.println("CSV validation error: " + ex.getMessage()); // Print error
            }

        } catch (IOException e) { // Catch file read errors
            System.out.println("Error reading attendance.txt: " + e.getMessage()); // Print error
        }
    }

    public void writeEmployeeFile(List<String[]> data) { // Writes entire employee file
        String filePath = folderPath + "/employee.txt"; // File path

        try {
            File file = new File(filePath); // File object
            if (!file.exists()) file.createNewFile(); // Create file if not exists

            try (ICSVWriter writer = new CSVWriterBuilder(new FileWriter(file)) // CSV writer
                    .withSeparator(';') // Semicolon separator
                    .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER) // No quote chars
                    .build()) {

                writer.writeNext(employeeHeaders.toArray(String[]::new)); // Write headers
                for (String[] row : data) {
                    writer.writeNext(row); // Write each row
                }

                System.out.println("✅ employee.txt written successfully."); // Success
            }

        } catch (IOException e) { // Catch write errors
            System.out.println("❌ Error writing to employee.txt: " + e.getMessage()); // Print error
        }
    }

    public void writeAttendanceFile(List<String[]> data) { // Writes attendance file
        String filePath = folderPath + "/attendance.txt"; // File path

        try {
            File file = new File(filePath); // File object
            if (!file.exists()) file.createNewFile(); // Create if not exist

            try (ICSVWriter writer = new CSVWriterBuilder(new FileWriter(file)) // CSV writer
                    .withSeparator(',') // Comma separator
                    .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER) // No quotes
                    .build()) {

                writer.writeNext(attendanceHeaders.toArray(String[]::new)); // Write headers
                for (String[] row : data) {
                    writer.writeNext(row); // Write each row
                }

                System.out.println("✅ attendance.txt written successfully."); // Success
            }

        } catch (IOException e) { // Catch errors
            System.out.println("❌ Error writing to attendance.txt: " + e.getMessage()); // Print error
        }
    }

    public boolean appendEmployeeToFile(String[] employeeRow) { // Appends a new row to employee file
        String filePath = folderPath + "/employee.txt"; // File path

        try {
            File file = new File(filePath); // File object
            if (!file.exists()) { // If not found
                System.out.println("employee.txt not found."); // Log error
                return false;
            }

            try (ICSVWriter writer = new CSVWriterBuilder(new FileWriter(file, true)) // Writer in append mode
                    .withSeparator(';')
                    .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build()) {
                writer.writeNext(employeeRow); // Write new row
                return true; // Success
            }

        } catch (IOException e) { // Catch error
            System.out.println("❌ Error appending to employee.txt: " + e.getMessage()); // Log
            return false;
        }
    }

    public boolean updateBenefitsByEmployeeId(String employeeId, Benefits benefits) { // Update benefits by ID
        int riceIndex = employeeHeaders.indexOf("Rice Subsidy"); // Get column index
        int phoneIndex = employeeHeaders.indexOf("Phone Allowance");
        int clothingIndex = employeeHeaders.indexOf("Clothing Allowance");
        int idIndex = employeeHeaders.indexOf("Employee #");

        for (String[] row : employeeData) {
            if (row[idIndex].equals(employeeId)) { // Match ID
                row[riceIndex] = String.valueOf(benefits.getRiceSubsidy()); // Update benefit values
                row[phoneIndex] = String.valueOf(benefits.getPhoneAllowance());
                row[clothingIndex] = String.valueOf(benefits.getClothingAllowance());
                writeEmployeeFile(employeeData); // Save changes
                return true;
            }
        }
        return false; // Not found
    }

    public boolean updateEmployeeField(String employeeId, String columnName, String newValue) { // Update any field
        int idIndex = employeeHeaders.indexOf("Employee #"); // ID index
        int columnIndex = employeeHeaders.indexOf(columnName); // Target column

        if (idIndex == -1 || columnIndex == -1) return false; // Column not found

        for (String[] row : employeeData) {
            if (row[idIndex].equals(employeeId)) { // Match ID
                row[columnIndex] = newValue; // Update field
                writeEmployeeFile(employeeData); // Save
                return true;
            }
        }
        return false;
    }

    public boolean deleteEmployeeById(String employeeId) { // Delete employee by ID
        int idIndex = employeeHeaders.indexOf("Employee #"); // Find ID index
        boolean removed = employeeData.removeIf(row -> row[idIndex].equals(employeeId)); // Remove row
        if (removed) {
            writeEmployeeFile(employeeData); // Save updated data
        }
        return removed;
    }

    public boolean deleteAttendance(String employeeId, String date) { // Delete attendance row
        int empIndex = 0;  // Assuming Employee ID is column 0
        int dateIndex = 1; // Assuming Date is column 1

        boolean removed = attendanceData.removeIf(row ->
                row[empIndex].equals(employeeId) && row[dateIndex].equals(date)); // Match ID & date

        if (removed) {
            writeAttendanceFile(attendanceData); // Save
        }
        return removed;
    }

    public boolean updateAttendance(String employeeId, String date, String[] newRow) { // Update row
        int empIndex = 0;
        int dateIndex = 1;

        for (int i = 0; i < attendanceData.size(); i++) {
            String[] row = attendanceData.get(i);
            if (row[empIndex].equals(employeeId) && row[dateIndex].equals(date)) { // Match
                attendanceData.set(i, newRow); // Replace row
                writeAttendanceFile(attendanceData); // Save
                return true;
            }
        }
        return false;
    }

    public Benefits getBenefitsByEmployeeId(String employeeId) { // Fetch benefit data
        for (String[] emp : employeeData) {
            if (emp[0].equals(employeeId)) { // Match ID
                double rice = safeParseDouble(emp[14], 0.0); // Parse benefits
                double phone = safeParseDouble(emp[15], 0.0);
                double clothing = safeParseDouble(emp[16], 0.0);
                return new Benefits(rice, phone, clothing); // Return object
            }
        }
        return new Benefits(0.0, 0.0, 0.0); // Default if not found
    }

    private double safeParseDouble(String value, double defaultValue) { // Parses string to double safely
        try {
            return Double.parseDouble(value.replace("\"", "").replace(",", "").trim()); // Clean and parse
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse double: " + value); // Log error
            return defaultValue; // Return fallback
        }
    }

    public List<String[]> getEmployeeData() { // Getter for employee data
        return employeeData;
    }

    public List<String[]> getAttendanceData() { // Getter for attendance data
        return attendanceData;
    }

    public String[] getEmployeeHeaders() { // Getter for static headers
        return new String[]{
                "Employee #", "Last Name", "First Name", "Birthday", "Address", "Phone Number",
                "SSS #", "Philhealth #", "TIN #", "Pag-ibig #", "Status", "Position",
                "Immediate Supervisor", "Basic Salary", "Rice Subsidy", "Phone Allowance",
                "Clothing Allowance", "Gross Semi-monthly Rate", "Hourly Rate"
        };
    }

    public List<String> getAttendanceHeaders() { // Getter for attendance headers
        return attendanceHeaders;
    }
}
