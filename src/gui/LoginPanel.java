/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JFrame {
    private final JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPassword;
    private final JButton loginButton;
    private int attempts = 0;
    private Timer lockoutTimer;

    public LoginPanel() {
        setTitle("MotorPH Payroll System - Login");
        setSize(400, 250);
        setLayout(null);
        getContentPane().setBackground(Color.DARK_GRAY);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(50, 30, 100, 25);
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 30, 180, 25);
        add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(50, 70, 100, 25);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 70, 180, 25);
        add(passwordField);

        showPassword = new JCheckBox("Show Password");
        showPassword.setBounds(150, 100, 150, 25);
        showPassword.setBackground(Color.DARK_GRAY);
        showPassword.setForeground(Color.WHITE);
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });
        add(showPassword);

        loginButton = new JButton("Login");
        loginButton.setBounds(150, 140, 100, 30);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> checkLogin());
        add(loginButton);

        setVisible(true);
    }

   private void checkLogin() {
    String user = usernameField.getText();
    String pass = new String(passwordField.getPassword());

    if (user.equals("admin") && pass.equals("1234")) {
        JOptionPane.showMessageDialog(this, "Login Successful!");
        dispose(); // Close the login window

        // Show the dashboard properly (make sure it's a JFrame or similar)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MotorPH Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new DashboardPanel());
            frame.setVisible(true);
        });
    } else {
        attempts++;
        if (attempts >= 3) {
            loginButton.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Too many attempts. Login disabled for 1 minute.");

            lockoutTimer = new Timer(60000, e -> {
                loginButton.setEnabled(true);
                attempts = 0;
                ((Timer) e.getSource()).stop(); // Stop timer after execution
            });
            lockoutTimer.setRepeats(false);
            lockoutTimer.start();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials. Attempts: " + attempts);
        }
    }
}


   
}
