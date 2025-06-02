
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;




public class FileHandler {
    

    private List<String> employeeHeaders = new ArrayList<>();
    private List<String[]> employeeData = new ArrayList<>();

    private List<String> attendanceHeaders = new ArrayList<>();
    private List<String[]> attendanceData = new ArrayList<>();

    private final String folderPath = "data"; // Folder for text files

    // Read and validate employee.txt
    public void readEmployeeFile() {
        String filePath = folderPath + "/employee.txt";

        try {
            File dir = new File(folderPath);
            if (!dir.exists()) {
                dir.mkdirs(); // Create folder if it does not exist
                System.out.println("Created folder: " + folderPath);
            }

            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("employee.txt file not found.");
                return;
            }

            try (CSVReader reader = new CSVReader(new FileReader(file))) {
                String[] header = reader.readNext();
                if (header == null) {
                    System.out.println("employee.txt is empty or missing headers.");
                    return;
                }

                for (String column : header) {
                    employeeHeaders.add(column.trim()); // Save headers in ArrayList
                }

                String[] line;
                while ((line = reader.readNext()) != null) {
                    if (line.length != header.length) {
                        System.out.println("Skipping malformed row in employee.txt");
                        continue;
                    }
                    employeeData.add(line);
                }

                System.out.println("Employee data loaded successfully.");

            } catch (CsvValidationException ex) {
                System.getLogger(FileHandler.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }

        } catch (IOException e) {
            System.out.println("Error reading employee.txt: " + e.getMessage());
        }
    }

    // Read and validate attendance.txt
    public void readAttendanceFile() {
        String filePath = folderPath + "/attendance.txt";

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("attendance.txt file not found.");
                return;
            }

            try (CSVReader reader = new CSVReader(new FileReader(file))) {
                String[] header = reader.readNext();
                if (header == null) {
                    System.out.println("attendance.txt is empty or missing headers.");
                    return;
                }

                for (String column : header) {
                    attendanceHeaders.add(column.trim()); // Save headers in ArrayList
                }

                String[] line;
                while ((line = reader.readNext()) != null) {
                    if (line.length != header.length) {
                        System.out.println("Skipping malformed row in attendance.txt");
                        continue;
                    }
                    attendanceData.add(line);
                }

                System.out.println("Attendance data loaded successfully.");

            } catch (CsvValidationException ex) {
                System.getLogger(FileHandler.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }

        } catch (IOException e) {
            System.out.println("Error reading attendance.txt: " + e.getMessage());
        }
    }

    // Write new data to employee.txt from List<String[]>
    public void writeEmployeeFile(List<String[]> data) {
        String filePath = folderPath + "/employee.txt";

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("employee.txt not found. Creating new file.");
                file.createNewFile();
            }

            try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
                writer.writeNext(employeeHeaders.toArray(String[]::new)); // write header
                for (String[] row : data) {
                    writer.writeNext(row);
                }

                System.out.println("employee.txt written successfully.");
            }

        } catch (IOException e) {
            System.out.println("Error writing to employee.txt: " + e.getMessage());
        }
    }

    // Write new data to attendance.txt from List<String[]>
    public void writeAttendanceFile(List<String[]> data) {
        String filePath = folderPath + "/attendance.txt";

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("attendance.txt not found. Creating new file.");
                file.createNewFile();
            }

            try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
                writer.writeNext(attendanceHeaders.toArray(String[]::new)); // write header
                for (String[] row : data) {
                    writer.writeNext(row);
                }

                System.out.println("attendance.txt written successfully.");
            }

        } catch (IOException e) {
            System.out.println("Error writing to attendance.txt: " + e.getMessage());
        }
    }

    // Getter for employee data
    public List<String[]> getEmployeeData() {
        return employeeData;
    }

    // Getter for attendance data
    public List<String[]> getAttendanceData() {
        return attendanceData;
    }

    // Getter for headers
    public List<String> getEmployeeHeaders() {
        return employeeHeaders;
    }

    public List<String> getAttendanceHeaders() {
        return attendanceHeaders;
    }
    
    // This code is for testing purposed
//    public void displayFirstThreeRows(List<String[]> data, String title) {
//    System.out.println("\n=== " + title + " - First 3 Rows ===");
//    int count = 0;
//    for (String[] row : data) {
//        for (String value : row) {
//            System.out.print(value + "\t");
//        }
//        System.out.println();
//        count++;
//        if (count >= 3) break; // Show only the first 3 rows
//    }
//
//    if (count == 0) {
//        System.out.println("No data found.");
//    }
//}
//    // Main method to test the functionality
//    public static void main(String[] args) {
//        FileHandler fh = new FileHandler();
//
//        // Read files
//        fh.readEmployeeFile();
//        fh.readAttendanceFile();
//
//        // Display employee data
//        System.out.println("\n=== Employee Data ===");
//        for (String[] row : fh.getEmployeeData()) {
//            for (String val : row) {
//                System.out.print(val + "\t");
//            }
//            System.out.println();
//        }
//
//        // Display attendance data
//        System.out.println("\n=== Attendance Data ===");
//        for (String[] row : fh.getAttendanceData()) {
//            for (String val : row) {
//                System.out.print(val + "\t");
//            }
//            System.out.println();
//        }
//
//        // Example writing back same data (can replace with modified/new data)
//        fh.displayFirstThreeRows(fh.getEmployeeData(), "Employee Data");
//    fh.displayFirstThreeRows(fh.getAttendanceData(), "Attendance Data");
//    }
}
