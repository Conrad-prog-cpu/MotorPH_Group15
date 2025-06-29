package gui;

// Core Swing classes for GUI components
import javax.swing.*;

// Border class for drawing lines around components (used here for styling input fields)
import javax.swing.border.MatteBorder;

// AWT package includes layout managers, fonts, graphics, cursor, etc.
import java.awt.*;

// Listens to key input events (e.g., Enter key press)
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// Logger classes for logging errors or debugging information
import java.util.logging.Level;
import java.util.logging.Logger;

// Main class for login screen; extends JFrame to create a standalone window
public class LoginPanel extends JFrame {
    // Text field for username input
    private final JTextField usernameField;

    // Password field (masked input)
    private final JPasswordField passwordField;

    // Checkbox to toggle password visibility
    private final JCheckBox showPassword;

    // Login button
    private final JButton loginButton;

    // Label to display success or error messages
    private final JLabel feedbackLabel;

    // Tracks number of failed login attempts
    private int attempts = 0;

    // Timer to temporarily disable login after too many failed attempts
    private Timer lockoutTimer;

    // Default color used for the login button
    private final Color originalButtonColor = new Color(0, 191, 255);

    // Background image loaded from resources
    private final ImageIcon backgroundImage = new ImageIcon(getClass().getResource("/assets/loginpanel_bg.png"));

    // Constructor initializes the login UI
    public LoginPanel() {
        setTitle("MotorPH Payroll System - Login"); // Window title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit application on close
        setSize(500, 400); // Initial size
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
        setLocationRelativeTo(null); // Center the window

        // Custom background panel with image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image img = backgroundImage.getImage();
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this); // Draw background image
            }
        };
        backgroundPanel.setLayout(new GridBagLayout()); // Layout manager to center contents
        setContentPane(backgroundPanel); // Set as main content pane

        // Login form card panel with rounded corners
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Rounded edges
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBackground(Color.WHITE); // Card background
        card.setPreferredSize(new Dimension(360, 340)); // Fixed size
        card.setLayout(new GridBagLayout()); // Flexible layout

        // Layout constraints for positioning components in card
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 20, 2, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Title: "Sign In"
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

        // Username input field
        gbc.gridy++;
        usernameField = new JTextField();
        usernameField.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)); // Bottom border
        usernameField.setOpaque(false);
        usernameField.setPreferredSize(new Dimension(200, 28));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(usernameField, gbc);

        // Password label
        gbc.gridy++;
        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(Color.GRAY);
        card.add(passLabel, gbc);

        // Password input field
        gbc.gridy++;
        passwordField = new JPasswordField();
        passwordField.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        passwordField.setOpaque(false);
        passwordField.setPreferredSize(new Dimension(200, 28));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(passwordField, gbc);

        // Trigger login when Enter is pressed in any input field
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    checkLogin();
                }
            }
        };
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);

        // Checkbox: Show/hide password
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
            passwordField.setEchoChar(showPassword.isSelected() ? (char) 0 : '•');
        });
        card.add(showPassword, gbc);

        // Login button with custom styling
        gbc.gridy++;
        gbc.insets = new Insets(10, 20, 10, 20);
        loginButton = new JButton("Login") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Adjust color based on hover/disabled state
                if (!isEnabled()) {
                    g2.setColor(Color.GRAY);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(30, 144, 255)); // Hover effect
                } else {
                    g2.setColor(originalButtonColor); // Default
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Rounded button
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {} // No visible border
        };
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginButton.setContentAreaFilled(false);
        loginButton.setOpaque(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> checkLogin());
        card.add(loginButton, gbc);

        // Label for error or success messages
        gbc.gridy++;
        feedbackLabel = new JLabel(" ", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        feedbackLabel.setForeground(Color.RED);
        card.add(feedbackLabel, gbc);

        // Add the login card to the center of background panel
        backgroundPanel.add(card);

        // Display the login frame
        setVisible(true);
    }

    // Handles login logic: validation, feedback, and lockout
    private void checkLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        // If credentials match hardcoded values
        if (user.equals("admin") && pass.equals("1234")) {
            feedbackLabel.setForeground(new Color(34, 139, 34));
            feedbackLabel.setText("Login Successful!");

            // Delay then proceed to Dashboard
            Timer successTimer = new Timer(1000, e -> {
                dispose(); // Close login window
                SwingUtilities.invokeLater(() -> {
                    try {
                        new DashboardPanel("Admin");
                    } catch (Exception ex) {
                        Logger.getLogger(LoginPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            });
            successTimer.setRepeats(false);
            successTimer.start();

        } else {
            // Failed login attempt
            attempts++;
            feedbackLabel.setForeground(Color.RED);

            if (attempts >= 3) {
                // Lockout logic after 3 attempts
                feedbackLabel.setText("Too many attempts. Try again after 1 minute.");
                loginButton.setEnabled(false);

                lockoutTimer = new Timer(60000, e -> {
                    // Re-enable login after timeout
                    loginButton.setEnabled(true);
                    feedbackLabel.setText(" ");
                    attempts = 0;
                    ((Timer) e.getSource()).stop(); // Stop the timer
                });
                lockoutTimer.setRepeats(false);
                lockoutTimer.start();

            } else {
                // Show remaining attempts
                feedbackLabel.setText("<html><div align='center'>Incorrect username or password.<br>Attempt " + attempts + " of 3.</div></html>");
                usernameField.requestFocus();
                passwordField.setText("");
                usernameField.setText("");
            }
        }
    }
}
