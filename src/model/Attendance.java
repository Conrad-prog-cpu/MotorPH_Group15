/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Attendance {
    private String employeeNumber;
    private String date;
    private String logIn;
    private String logOut;

    public Attendance(String employeeNumber, String date, String logIn, String logOut) {
        this.employeeNumber = employeeNumber;
        this.date = date;
        this.logIn = logIn;
        this.logOut = logOut;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public double getWorkedHours() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
            LocalTime in = LocalTime.parse(logIn, formatter);
            LocalTime out = LocalTime.parse(logOut, formatter);
            long minutes = ChronoUnit.MINUTES.between(in, out);
            return minutes / 60.0;
        } catch (Exception e) {
            System.err.println("Error parsing time: " + logIn + " to " + logOut);
            return 0.0;
        }
    }

    public int getLateMinutes() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
            LocalTime in = LocalTime.parse(logIn, formatter);
            LocalTime grace = LocalTime.of(8, 10); // 8:10 AM
            if (in.isAfter(grace)) {
                return (int) ChronoUnit.MINUTES.between(grace, in);
            } else {
                return 0;
            }
        } catch (Exception e) {
            System.err.println("Error calculating lateness from: " + logIn);
            return 0;
        }
    }
}
