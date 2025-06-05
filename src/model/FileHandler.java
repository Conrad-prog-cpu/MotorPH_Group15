package model;

import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileHandler {

    private List<String> employeeHeaders = new ArrayList<>();
    private List<String[]> employeeData = new ArrayList<>();

    private List<String> attendanceHeaders = new ArrayList<>();
    private List<String[]> attendanceData = new ArrayList<>();

    private final String folderPath = "data";

    public void readEmployeeFile() {
        File file = new File(folderPath + "/employee.txt");

        if (!file.exists()) {
            System.out.println("❌ employee.txt not found.");
            return;
        }

        employeeHeaders.clear();
        employeeData.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] fields = line.split("(?=(?:[^\"]*\"[^\"]*\")*[^\"]*)\\;");

                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fields[i].replaceAll("^\"|\"$", "").trim();
                }

                if (isFirstLine) {
                    employeeHeaders.addAll(Arrays.asList(fields));
                    isFirstLine = false;
                } else {
                    if (fields.length < employeeHeaders.size()) {
                        String[] fixed = Arrays.copyOf(fields, employeeHeaders.size());
                        Arrays.fill(fixed, fields.length, employeeHeaders.size(), "");
                        employeeData.add(fixed);
                    } else if (fields.length > employeeHeaders.size()) {
                        String[] fixed = Arrays.copyOf(fields, employeeHeaders.size());
                        employeeData.add(fixed);
                    } else {
                        employeeData.add(fields);
                    }
                }
            }

            System.out.println("✅ employee.txt loaded: " + employeeData.size() + " rows");

        } catch (IOException e) {
            System.out.println("❌ Error reading employee file: " + e.getMessage());
        }
    }
    
    public Benefits getBenefitsByEmployeeId(String employeeId) {
    for (String[] emp : employeeData) {
        if (emp[0].equals(employeeId)) {
            // Assuming rice = emp[14], phone = emp[15], clothing = emp[16]
            double rice = safeParseDouble(emp[14], 0.0);
            double phone = safeParseDouble(emp[15], 0.0);
            double clothing = safeParseDouble(emp[16], 0.0);
            return new Benefits(rice, phone, clothing);
        }
    }
    return new Benefits(0.0, 0.0, 0.0); // default fallback
}
        private double safeParseDouble(String value, double defaultValue) {
        try {
        // Remove quotes, commas, and trim spaces
        return Double.parseDouble(value.replace("\"", "").replace(",", "").trim());
        } catch (NumberFormatException e) {
        System.err.println("Failed to parse double: " + value);
        return defaultValue;
        }
        }



    public void readAttendanceFile() {
        String filePath = folderPath + "/attendance.txt";

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("attendance.txt file not found.");
                return;
            }

            try (CSVReader reader = new CSVReaderBuilder(new FileReader(file))
                    .withCSVParser(new CSVParserBuilder().withSeparator(',').build())
                    .build()) {

                String[] header = reader.readNext();
                if (header == null) {
                    System.out.println("attendance.txt is empty or missing headers.");
                    return;
                }

                attendanceHeaders.clear();
                attendanceData.clear();

                attendanceHeaders.addAll(Arrays.asList(header));

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
                System.err.println("CSV validation error: " + ex.getMessage());
            }

        } catch (IOException e) {
            System.out.println("Error reading attendance.txt: " + e.getMessage());
        }
    }

    public void writeEmployeeFile(List<String[]> data) {
        String filePath = folderPath + "/employee.txt";

        try {
            File file = new File(filePath);
            if (!file.exists()) file.createNewFile();

            try (ICSVWriter writer = new CSVWriterBuilder(new FileWriter(file))
                    .withSeparator(';')
                    .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build()) {

                writer.writeNext(employeeHeaders.toArray(String[]::new));
                for (String[] row : data) {
                    writer.writeNext(row);
                }

                System.out.println("employee.txt written successfully.");
            }

        } catch (IOException e) {
            System.out.println("Error writing to employee.txt: " + e.getMessage());
        }
    }
    
    public boolean updateBenefitsByEmployeeId(String employeeId, Benefits benefits) {
    int riceIndex = employeeHeaders.indexOf("Rice Subsidy");
    int phoneIndex = employeeHeaders.indexOf("Phone Allowance");
    int clothingIndex = employeeHeaders.indexOf("Clothing Allowance");
    int idIndex = employeeHeaders.indexOf("Employee #");

    for (String[] row : employeeData) {
        if (row[idIndex].equals(employeeId)) {
            row[riceIndex] = String.valueOf(benefits.getRiceSubsidy());
            row[phoneIndex] = String.valueOf(benefits.getPhoneAllowance());
            row[clothingIndex] = String.valueOf(benefits.getClothingAllowance());
            writeEmployeeFile(employeeData); // persist changes
            return true;
        }
    }
    return false;
}


    public void writeAttendanceFile(List<String[]> data) {
        String filePath = folderPath + "/attendance.txt";

        try {
            File file = new File(filePath);
            if (!file.exists()) file.createNewFile();

            try (ICSVWriter writer = new CSVWriterBuilder(new FileWriter(file))
                    .withSeparator(',')
                    .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build()) {

                writer.writeNext(attendanceHeaders.toArray(String[]::new));
                for (String[] row : data) {
                    writer.writeNext(row);
                }

                System.out.println("attendance.txt written successfully.");
            }

        } catch (IOException e) {
            System.out.println("Error writing to attendance.txt: " + e.getMessage());
        }
    }

    public boolean appendEmployeeToFile(String[] employeeRow) {
        String filePath = folderPath + "/employee.txt";

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("employee.txt not found.");
                return false;
            }

            try (ICSVWriter writer = new CSVWriterBuilder(new FileWriter(file, true))
                    .withSeparator(';')
                    .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build()) {
                writer.writeNext(employeeRow);
                return true;
            }

        } catch (IOException e) {
            System.out.println("Error appending to employee.txt: " + e.getMessage());
            return false;
        }
    }

    public List<String[]> getEmployeeData() {
        return employeeData;
    }

    public List<String[]> getAttendanceData() {
        return attendanceData;
    }

    public String[] getEmployeeHeaders() {
        return new String[]{
                "Employee #", "Last Name", "First Name", "Birthday", "Address", "Phone Number",
                "SSS #", "Philhealth #", "TIN #", "Pag-ibig #", "Status", "Position",
                "Immediate Supervisor", "Basic Salary", "Rice Subsidy", "Phone Allowance",
                "Clothing Allowance", "Gross Semi-monthly Rate", "Hourly Rate"
        };
    }

    public List<String> getAttendanceHeaders() {
        return attendanceHeaders;
    }
    
    
}
