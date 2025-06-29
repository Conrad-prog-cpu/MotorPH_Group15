// Declares the package location of this class
package gui;

// Import statements for GUI components and utilities
import javax.swing.*;
// Imports the EmptyBorder class to create an invisible border around Swing components
// Often used for spacing/padding in layouts
import javax.swing.border.EmptyBorder;

// Imports all classes from the java.awt package
// Includes core classes for GUI components, colors, layout managers, etc.
import java.awt.*;

// Imports MouseAdapter class, which provides empty implementations of MouseListener methods
// Convenient for handling only specific mouse events (e.g., click or hover)
import java.awt.event.MouseAdapter;

// Imports MouseEvent class to detect mouse actions like click, press, release, or hover
import java.awt.event.MouseEvent;

// Imports the URL class, used for locating resources such as images or files on the web or locally
import java.net.URL;

// Imports the FileHandler class from the 'model' package
// Typically used for file operations (e.g., reading/writing employee or attendance data)
import model.FileHandler;


/**
 * DashboardPanel serves as the main window for the MotorPH HR system.
 * It features navigation options and displays different panels: Attendance, Employee, and Payroll.
 */
public class DashboardPanel extends JFrame {

    // Manages panel switching within the main content area
    private final CardLayout cardLayout = new CardLayout();

    // Panel that contains different views (e.g., Employee, Attendance, Payroll)
    JPanel contentPanel = new JPanel(); 

    // Panels for displaying employee and payroll data
    private final EmployeePanel employeePanel = new EmployeePanel();

    // Ensures the payroll instruction popup only appears once

    /**
     * Constructor that initializes the dashboard and its components.
     * @param user The current user's name (currently unused)
     */
    public DashboardPanel(String user) {
        setTitle("MotorPH Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(800, 500));

        // Define UI styling
        Color sidebarColor = Color.WHITE;
        Color gradientStart = new Color(255, 204, 229);
        Color gradientEnd = new Color(255, 229, 180);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 16);
        Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Sidebar setup
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(sidebarColor);
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));

        // User profile panel at the top of sidebar
        JPanel profilePanel = new JPanel(new BorderLayout(10, 0));
        profilePanel.setBackground(sidebarColor);
        JLabel profileIcon = new JLabel(loadImageIcon("/assets/userprofile.png", 40, 40));
        profilePanel.add(profileIcon, BorderLayout.WEST);

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setBackground(sidebarColor);
        JLabel userName = new JLabel("Admin");
        JLabel userRole = new JLabel("HR Manager");
        userName.setFont(boldFont);
        userRole.setFont(regularFont);
        userRole.setForeground(Color.GRAY);
        namePanel.add(userName);
        namePanel.add(userRole);
        profilePanel.add(namePanel, BorderLayout.CENTER);

        // Navigation buttons panel
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(sidebarColor);
        navPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        navPanel.add(Box.createVerticalStrut(30));

        JLabel generalLabel = new JLabel("General");
        generalLabel.setFont(boldFont);
        generalLabel.setBorder(new EmptyBorder(0, 10, 10, 0));
        generalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        navPanel.add(generalLabel);

        // Create navigation buttons with icons
//        JButton attendanceBtn = createNavButton("Attendance", "attendance.png");
        JButton employeeBtn = createNavButton("Employee", "employee.png");
//        JButton payrollBtn = createNavButton("Payroll", "payroll.png");

        // Add buttons to sidebar
        navPanel.add(employeeBtn);
        navPanel.add(Box.createVerticalStrut(5));
//        navPanel.add(attendanceBtn);
        navPanel.add(Box.createVerticalStrut(5));
//        navPanel.add(payrollBtn);

        // Log-out button setup
        JButton logoutButton = createNavButton("Log-out", "logout.png");
        logoutButton.setForeground(Color.GRAY);
        logoutButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginPanel().setVisible(true));
        });

        // Add all components to the sidebar
        sidebar.add(profilePanel, BorderLayout.NORTH);
        sidebar.add(navPanel, BorderLayout.CENTER);
        sidebar.add(logoutButton, BorderLayout.SOUTH);

        // Main content area with a gradient background
        contentPanel = new JPanel(cardLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, gradientStart, 0, getHeight(), gradientEnd));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Load attendance data and initialize panels
        FileHandler fileHandler = new FileHandler();
        AttendancePanel attendancePanel = new AttendancePanel(fileHandler);

        // Add all panels to the card layout
        contentPanel.add(employeePanel, "Employee");
//        contentPanel.add(attendancePanel, "Attendance");
//        contentPanel.add(salaryPanel, "Payroll");

        // Button click listeners to switch views
//        attendanceBtn.addActionListener(e -> cardLayout.show(contentPanel, "Attendance"));
        employeeBtn.addActionListener(e -> cardLayout.show(contentPanel, "Employee"));
        
//        payrollBtn.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                // Show instruction on first access
//                if (!hasShownPayrollInstruction) {
//                    JOptionPane.showMessageDialog(
//                        null,
//                        "To proceed, please choose an employee by clicking on a row in the table.",
//                        "Instruction",
//                        JOptionPane.INFORMATION_MESSAGE
//                    );
//                    hasShownPayrollInstruction = true;
//                }
//                cardLayout.show(contentPanel, "Payroll");
//            }
//        });

        // Add sidebar and main content to the frame
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        setVisible(true);
        
    }

    /**
     * Creates a styled navigation button with an icon.
     * @param text The button label
     * @param iconFileName The icon image file located in /assets/
     * @return A customized JButton
     */
    private JButton createNavButton(String text, String iconFileName) {
        JButton button = new JButton(text);
        button.setIcon(loadImageIcon("/assets/" + iconFileName, 20, 20));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(15);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        // Hover effect for better UX
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(240, 240, 240));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });

        return button;
    }

    /**
     * Loads and resizes an image icon from the resources folder.
     * @param path Relative path to the image (e.g., "/assets/icon.png")
     * @param width Desired icon width
     * @param height Desired icon height
     * @return Scaled ImageIcon, or null if not found
     */
    private ImageIcon loadImageIcon(String path, int width, int height) {
        URL imageUrl = getClass().getResource(path);
        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } else {
            System.err.println("Image not found: " + path);
            return null;
        }
    }
}
