package model;

import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.util.*;

public class FileHandler {

    // ======== Fields and Constants ========
    private final String folderPath = "data";

    private final List<String> employeeHeaders = new ArrayList<>();
    private final List<String[]> employeeData = new ArrayList<>();

    private final List<String> attendanceHeaders = new ArrayList<>();
    private final List<String[]> attendanceData = new ArrayList<>();

    private final String EMPLOYEE_FILE = folderPath + "/employee.txt";
    private final String ATTENDANCE_FILE = folderPath + "/attendance.txt";

    // ======== Read Methods ========

    public void readEmployeeFile() {
        File file = new File(EMPLOYEE_FILE);
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
                    fields = adjustRowLength(fields, employeeHeaders.size());
                    employeeData.add(fields);
                }
            }

            System.out.println("✅ employee.txt loaded: " + employeeData.size() + " rows");

        } catch (IOException e) {
            System.out.println("❌ Error reading employee file: " + e.getMessage());
        }
    }

    public void readAttendanceFile() {
        File file = new File(ATTENDANCE_FILE);
        if (!file.exists()) {
            System.out.println("❌ attendance.txt not found.");
            return;
        }

        attendanceHeaders.clear();
        attendanceData.clear();

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(file))
                .withCSVParser(new CSVParserBuilder().withSeparator(',').build())
                .build()) {

            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length == 0 || line[0].trim().isEmpty()) continue;
                attendanceData.add(line);
            }

            System.out.println("✅ Attendance data loaded: " + attendanceData.size() + " rows");

        } catch (IOException | CsvValidationException e) {
            System.out.println("❌ Error reading attendance.txt: " + e.getMessage());
        }
    }

    // ======== Write Methods ========

    public void writeEmployeeFile(List<String[]> data) {
        try (ICSVWriter writer = new CSVWriterBuilder(new FileWriter(EMPLOYEE_FILE))
                .withSeparator(';')
                .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                .build()) {

            writer.writeNext(employeeHeaders.toArray(String[]::new));
            for (String[] row : data) {
                writer.writeNext(row);
            }

            System.out.println("✅ employee.txt written successfully.");

        } catch (IOException e) {
            System.out.println("❌ Error writing employee file: " + e.getMessage());
        }
    }

    public void writeAttendanceFile(List<String[]> data) {
        try (ICSVWriter writer = new CSVWriterBuilder(new FileWriter(ATTENDANCE_FILE))
                .withSeparator(',')
                .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                .build()) {

            writer.writeNext(attendanceHeaders.toArray(String[]::new));
            for (String[] row : data) {
                writer.writeNext(row);
            }

            System.out.println("✅ attendance.txt written successfully.");

        } catch (IOException e) {
            System.out.println("❌ Error writing attendance file: " + e.getMessage());
        }
    }

    // ======== Update & Append Methods ========

    public boolean appendEmployeeToFile(String[] employeeRow) {
        try (ICSVWriter writer = new CSVWriterBuilder(new FileWriter(EMPLOYEE_FILE, true))
                .withSeparator(';')
                .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                .build()) {
            writer.writeNext(employeeRow);
            return true;
        } catch (IOException e) {
            System.out.println("❌ Error appending to employee.txt: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateEmployeeField(String employeeId, String columnName, String newValue) {
        int idIndex = employeeHeaders.indexOf("Employee #");
        int columnIndex = employeeHeaders.indexOf(columnName);

        if (idIndex == -1 || columnIndex == -1) return false;

        for (String[] row : employeeData) {
            if (row[idIndex].equals(employeeId)) {
                row[columnIndex] = newValue;
                writeEmployeeFile(employeeData);
                return true;
            }
        }
        return false;
    }

    public boolean updateBenefitsByEmployeeId(String employeeId, Benefits benefits) {
        return updateEmployeeField(employeeId, "Rice Subsidy", String.valueOf(benefits.getRiceSubsidy())) &&
               updateEmployeeField(employeeId, "Phone Allowance", String.valueOf(benefits.getPhoneAllowance())) &&
               updateEmployeeField(employeeId, "Clothing Allowance", String.valueOf(benefits.getClothingAllowance()));
    }

    public boolean updateAttendance(String employeeId, String date, String[] newRow) {
        for (int i = 0; i < attendanceData.size(); i++) {
            String[] row = attendanceData.get(i);
            if (row[0].equals(employeeId) && row[1].equals(date)) {
                attendanceData.set(i, newRow);
                writeAttendanceFile(attendanceData);
                return true;
            }
        }
        return false;
    }

    // ======== Delete Methods ========

    public boolean deleteEmployeeById(String employeeId) {
        int idIndex = employeeHeaders.indexOf("Employee #");
        boolean removed = employeeData.removeIf(row -> row[idIndex].equals(employeeId));
        if (removed) writeEmployeeFile(employeeData);
        return removed;
    }

    public boolean deleteAttendance(String employeeId, String date) {
        boolean removed = attendanceData.removeIf(row -> row[0].equals(employeeId) && row[1].equals(date));
        if (removed) writeAttendanceFile(attendanceData);
        return removed;
    }

    // ======== Utility Methods ========

    public Benefits getBenefitsByEmployeeId(String employeeId) {
        for (String[] emp : employeeData) {
            if (emp[0].equals(employeeId)) {
                double rice = safeParseDouble(emp[14], 0.0);
                double phone = safeParseDouble(emp[15], 0.0);
                double clothing = safeParseDouble(emp[16], 0.0);
                return new Benefits(rice, phone, clothing);
            }
        }
        return new Benefits(0.0, 0.0, 0.0);
    }

    private double safeParseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value.replace("\"", "").replace(",", "").trim());
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse double: " + value);
            return defaultValue;
        }
    }

    private String[] adjustRowLength(String[] fields, int expectedSize) {
        if (fields.length < expectedSize) {
            String[] padded = Arrays.copyOf(fields, expectedSize);
            Arrays.fill(padded, fields.length, expectedSize, "");
            return padded;
        } else if (fields.length > expectedSize) {
            return Arrays.copyOf(fields, expectedSize);
        } else {
            return fields;
        }
    }

    // ======== Getters ========

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

    public String[] getEmployeeById(String employeeId) {
        int idIndex = employeeHeaders.indexOf("Employee #");
        for (String[] row : employeeData) {
            if (row[idIndex].equals(employeeId)) return row;
        }
        return null;
    }
}
