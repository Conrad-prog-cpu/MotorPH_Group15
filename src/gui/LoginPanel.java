package gui;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

// Main login window class extending JFrame
public class LoginPanel extends JFrame {
    // Declare GUI components
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JCheckBox showPassword;
    private final JButton loginButton;
    private final JLabel feedbackLabel;

    // Counter for login attempts
    private int attempts = 0;

    // Timer used for lockout after failed attempts
    private Timer lockoutTimer;

    // Color used for login button
    private final Color originalButtonColor = new Color(0, 191, 255);

    // Background image for the login panel
    private final ImageIcon backgroundImage = new ImageIcon(getClass().getResource("/assets/loginpanel_bg.png"));

    // Constructor for the login window
    public LoginPanel() {
        setTitle("MotorPH Payroll System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Open window in maximized state
        setLocationRelativeTo(null); // Center the window

        // Panel with background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image img = backgroundImage.getImage();
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this); // Draw the image scaled to panel size
            }
        };
        backgroundPanel.setLayout(new GridBagLayout()); // Center components in panel
        setContentPane(backgroundPanel);

        // Card-style panel for login form
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Rounded rectangle
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(360, 340));
        card.setLayout(new GridBagLayout());

        // Layout constraints for positioning components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 20, 2, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Title label "Sign In"
        JLabel titleLabel = new JLabel("Sign In", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.DARK_GRAY);
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 20, 10, 20);
        card.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Username label
        gbc.gridy++;
        gbc.insets = new Insets(5, 20, 2, 20);
        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(Color.GRAY);
        card.add(userLabel, gbc);

        // Username text field
        gbc.gridy++;
        usernameField = new JTextField();
        usernameField.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        usernameField.setOpaque(false);
        usernameField.setPreferredSize(new Dimension(200, 28));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(usernameField, gbc);

        // Password label
        gbc.gridy++;
        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(Color.GRAY);
        card.add(passLabel, gbc);

        // Password field input
        gbc.gridy++;
        passwordField = new JPasswordField();
        passwordField.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        passwordField.setOpaque(false);
        passwordField.setPreferredSize(new Dimension(200, 28));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(passwordField, gbc);

        // Add Enter key listener to both fields to trigger login
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    checkLogin(); // Trigger login on Enter
                }
            }
        };
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);

        // "Show Password" checkbox
        gbc.gridy++;
        showPassword = new JCheckBox("Show Password") {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setForeground(Color.DARK_GRAY);
                setOpaque(false);
                setFocusPainted(false);
            }
        };
        showPassword.setFocusPainted(false);
        showPassword.setOpaque(false);
        showPassword.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        showPassword.addActionListener(e -> {
            // Toggle password visibility
            passwordField.setEchoChar(showPassword.isSelected() ? (char) 0 : '•');
        });
        card.add(showPassword, gbc);

        // Login button with custom paint
        gbc.gridy++;
        gbc.insets = new Insets(10, 20, 10, 20);
        loginButton = new JButton("Login") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Change color based on button state
                if (!isEnabled()) {
                    g2.setColor(Color.GRAY);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(30, 144, 255));
                } else {
                    g2.setColor(originalButtonColor);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {} // No border
        };
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginButton.setContentAreaFilled(false);
        loginButton.setOpaque(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> checkLogin()); // On click, perform login
        card.add(loginButton, gbc);

        // Feedback message label
        gbc.gridy++;
        feedbackLabel = new JLabel(" ", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        feedbackLabel.setForeground(Color.RED);
        card.add(feedbackLabel, gbc);

        // Add login form panel to the background panel
        backgroundPanel.add(card);
        setVisible(true); // Show the frame
    }

    // Method to check login credentials and handle logic
    private void checkLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (user.equals("admin") && pass.equals("1234")) {
            // Successful login
            feedbackLabel.setForeground(new Color(34, 139, 34));
            feedbackLabel.setText("Login Successful!");

            // Wait 1 second then open dashboard
            Timer successTimer = new Timer(1000, e -> {
                dispose(); // Close login window
                SwingUtilities.invokeLater(() -> {
                    try {
                        new DashboardPanel("Admin"); // Open dashboard
                    } catch (Exception ex) {
                        Logger.getLogger(LoginPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            });
            successTimer.setRepeats(false);
            successTimer.start();

        } else {
            // Failed login
            attempts++;
            feedbackLabel.setForeground(Color.RED);

            if (attempts >= 3) {
                // Lock out after 3 failed attempts
                feedbackLabel.setText("Too many attempts. Try again after 1 minute.");
                loginButton.setEnabled(false);

                lockoutTimer = new Timer(60000, e -> {
                    // Re-enable after 1 minute
                    loginButton.setEnabled(true);
                    feedbackLabel.setText(" ");
                    attempts = 0;
                    ((Timer) e.getSource()).stop(); // Stop the timer
                });
                lockoutTimer.setRepeats(false);
                lockoutTimer.start();
            } else {
                // Show error message and clear fields
                feedbackLabel.setText("<html><div align='center'>Incorrect username or password.<br>Attempt " + attempts + " of 3.</div></html>");
                usernameField.requestFocus();
                passwordField.setText("");
                usernameField.setText("");
            }
        }
    }
}
