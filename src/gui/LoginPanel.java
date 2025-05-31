package gui;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginPanel extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JCheckBox showPassword;
    private final JButton loginButton;
    private final JLabel feedbackLabel;
    private int attempts = 0;
    private Timer lockoutTimer;
    private final Color originalButtonColor = new Color(0, 191, 255);
    private final ImageIcon backgroundImage = new ImageIcon(getClass().getResource("/assets/loginpanel_bg.png"));

    public LoginPanel() {
        setTitle("MotorPH Payroll System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image img = backgroundImage.getImage();
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(360, 340));
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 20, 2, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel titleLabel = new JLabel("Sign In", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.DARK_GRAY);
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 20, 10, 20);
        card.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridy++;
        gbc.insets = new Insets(5, 20, 2, 20);
        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(Color.GRAY);
        card.add(userLabel, gbc);

        gbc.gridy++;
        usernameField = new JTextField();
        usernameField.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        usernameField.setOpaque(false);
        usernameField.setPreferredSize(new Dimension(200, 28));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(usernameField, gbc);

        gbc.gridy++;
        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(Color.GRAY);
        card.add(passLabel, gbc);

        gbc.gridy++;
        passwordField = new JPasswordField();
        passwordField.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        passwordField.setOpaque(false);
        passwordField.setPreferredSize(new Dimension(200, 28));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(passwordField, gbc);

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

        gbc.gridy++;
        gbc.insets = new Insets(10, 20, 10, 20);
        loginButton = new JButton("Login") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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
            protected void paintBorder(Graphics g) {}
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

        gbc.gridy++;
        feedbackLabel = new JLabel(" ", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        feedbackLabel.setForeground(Color.RED);
        card.add(feedbackLabel, gbc);

        backgroundPanel.add(card);
        setVisible(true);
    }

    private void checkLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (user.equals("admin") && pass.equals("1234")) {
            feedbackLabel.setForeground(new Color(34, 139, 34));
            feedbackLabel.setText("Login Successful!");

            Timer successTimer = new Timer(1000, e -> {
                dispose();
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
            attempts++;
            feedbackLabel.setForeground(Color.RED);

            if (attempts >= 3) {
                feedbackLabel.setText("Too many attempts. Try again after 1 minute.");
                loginButton.setEnabled(false);

                lockoutTimer = new Timer(60000, e -> {
                    loginButton.setEnabled(true);
                    feedbackLabel.setText(" ");
                    attempts = 0;
                    ((Timer) e.getSource()).stop();
                });
                lockoutTimer.setRepeats(false);
                lockoutTimer.start();
            } else {
                feedbackLabel.setText("<html><div align='center'>Incorrect username or password.<br>Attempt " + attempts + " of 3.</div></html>");
                 usernameField.requestFocus();
                 passwordField.setText("");
                 usernameField.setText(""); // This highlights all text in the field
            }
        }
    }
}
