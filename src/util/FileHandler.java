/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import model.*;
import java.io.*;
import java.util.*;
import model.Attendance;


public class FileHandler {
    FileHandler fileHandler;
    private static final String EMPLOYEE_FILE = "employee.txt"; // Ensure it's in your working directory

    /**
     *
     * @param dataemployeestxt
     */
    public static void loadEmployees(String dataemployeestxt) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static void loadAttendance(String dataattendancetxt) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public FileHandler() {
        
    }

    public List<Employee> readEmployees() {
    List<Employee> employees = new ArrayList<>();
    File file = new File("employee.txt");

    if (!file.exists()) {
        System.out.println("employee.txt NOT FOUND at: " + file.getAbsolutePath());
        return employees;
    }

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = br.readLine()) != null) {
            // Clean and split
            String[] parts = Arrays.stream(line.split("\\|"))
                .map(s -> s.replace("\"", "").trim())
                .toArray(String[]::new);

            if (parts.length < 19) {
                System.out.println("Skipping invalid line: " + Arrays.toString(parts));
                continue;
            }

            // Parse numbers safely
            double basicSalary = Double.parseDouble(parts[13].replace(",", ""));
            double rice = Double.parseDouble(parts[14].replace(",", ""));
            double phone = Double.parseDouble(parts[15].replace(",", ""));
            double clothing = Double.parseDouble(parts[16].replace(",", ""));
            double semiMonthly = Double.parseDouble(parts[17].replace(",", ""));
            double hourly = Double.parseDouble(parts[18].replace(",", ""));

            // Build object structure
            Person person = new Person(parts[1], parts[2], parts[3]);
            ContactInfo contact = new ContactInfo(parts[4], parts[5]);
            GovernmentID ids = new GovernmentID(parts[6], parts[7], parts[8], parts[9]);
            Job job = new Job(parts[11], parts[12], basicSalary, rice, phone, clothing, semiMonthly, hourly);
            Employee emp = new Employee(parts[0], person, contact, parts[10], ids, job);

            employees.add(emp);
        }
    } catch (Exception e) {
        System.out.println("Error reading employee file: " + e.getMessage());
    }

    return employees;
}

    public List<Attendance> readAllAttendance() {
    List<Attendance> records = new ArrayList<>();
    File file = new File("attendance.txt");

    if (!file.exists()) {
        System.err.println("[ERROR] attendance.txt not found at: " + file.getAbsolutePath());
        return records;
    }

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        int lineNum = 0;

        while ((line = br.readLine()) != null) {
            lineNum++;

            if (line.trim().isEmpty()) continue;

            String[] tokens = line.trim().split("\\|");

            if (tokens.length < 6) {
                System.err.printf("[WARNING] Line %d skipped: not enough fields (%d): %s%n", lineNum, tokens.length, line);
                continue;
            }

            String empId = tokens[0].replace("\"", "").trim();
            String date = tokens[3].replace("\"", "").trim();
            String logIn = tokens[4].replace("\"", "").trim();
            String logOut = tokens[5].replace("\"", "").trim();

            if (!logIn.matches("\\d{1,2}:\\d{2}") || !logOut.matches("\\d{1,2}:\\d{2}")) {
                System.err.printf("[WARNING] Line %d skipped: invalid time format (LogIn='%s', LogOut='%s')%n", lineNum, logIn, logOut);
                continue;
            }

            records.add(new Attendance(empId, date, logIn, logOut));
        }

    } catch (IOException e) {
        System.err.println("[ERROR] Failed to read attendance.txt: " + e.getMessage());
    }

    return records;
}



}



//public static void main(String[] args) {
//    FileHandler fh = new FileHandler();
//    List<Employee> employees = fh.readEmployees();
//    System.out.println("Loaded employees: " + employees.size());
//}

 


    
