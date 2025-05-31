package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DashboardPanel extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    JPanel contentPanel = new JPanel(); 
    private final EmployeePanel employeePanel = new EmployeePanel();

    public DashboardPanel(String user) {
        setTitle("MotorPH Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(800, 500));

        Color sidebarColor = Color.WHITE;
        Color gradientStart = new Color(255, 204, 229);
        Color gradientEnd = new Color(255, 229, 180);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 16);
        Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Sidebar
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(sidebarColor);
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Profile section
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

        // Navigation panel
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

        JButton attendanceBtn = createNavButton("Attendance", "attendance.png");
        JButton employeeBtn = createNavButton("Employee", "employee.png");
        JButton payrollBtn = createNavButton("Payroll", "payroll.png");
        
        navPanel.add(attendanceBtn);
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(employeeBtn);
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(payrollBtn);

        // Search Section
        JLabel searchLabel = new JLabel("Search Employee:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchLabel.setBorder(new EmptyBorder(15, 10, 5, 0));
        navPanel.add(searchLabel);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(sidebarColor);
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        Color originalButtonColor = new Color(100, 149, 237);

        JTextField searchField = new JTextField(12);
        JButton searchButton = new JButton("Search") {
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

        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setContentAreaFilled(false);
        searchButton.setOpaque(false);
        searchButton.setPreferredSize(new Dimension(90, 30));

        // Enter key triggers the search
        searchField.addActionListener(e -> searchButton.doClick());

        searchButton.addActionListener((ActionEvent e) -> {
            String query = searchField.getText().trim();
            cardLayout.show(contentPanel, "Employee");
            employeePanel.searchEmployee(query);
        });

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        navPanel.add(searchPanel);

        navPanel.add(Box.createVerticalGlue());

        JButton logoutButton = createNavButton("Log-out", "logout.png");
        logoutButton.setForeground(Color.GRAY);
        logoutButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginPanel().setVisible(true));
        });

        sidebar.add(profilePanel, BorderLayout.NORTH);
        sidebar.add(navPanel, BorderLayout.CENTER);
        sidebar.add(logoutButton, BorderLayout.SOUTH);

        contentPanel = new JPanel(cardLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, gradientStart, 0, getHeight(), gradientEnd));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        AttendancePanel attendancePanel = new AttendancePanel();
        contentPanel.add(attendancePanel, "Attendance");
        contentPanel.add(employeePanel, "Employee");
        contentPanel.add(new SalaryCalculatorPanel(), "Payroll");

        attendanceBtn.addActionListener(e -> cardLayout.show(contentPanel, "Attendance"));
        employeeBtn.addActionListener(e -> cardLayout.show(contentPanel, "Employee"));
        payrollBtn.addActionListener(e -> cardLayout.show(contentPanel, "Payroll"));

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    class EmployeePanel extends JPanel {

        static class Employee {
            String empNo, name, birthday;

            public Employee(String empNo, String name, String birthday) {
                this.empNo = empNo;
                this.name = name;
                this.birthday = birthday;
            }
        }

        private final Map<String, Employee> employees = new HashMap<>();
        private final JLabel nameLabel = new JLabel("Name: ");
        private final JLabel birthdayLabel = new JLabel("Birthday: ");
        private final JLabel statusLabel = new JLabel("Enter Employee No and press 'Search'");

        public EmployeePanel() {
            setLayout(new BorderLayout());
            setOpaque(false);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            birthdayLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            statusLabel.setForeground(Color.BLACK);

            JPanel infoPanel = new JPanel();
            infoPanel.setOpaque(false);
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            nameLabel.setForeground(Color.BLACK);
            birthdayLabel.setForeground(Color.BLACK);
            infoPanel.add(nameLabel);
            infoPanel.add(birthdayLabel);
            infoPanel.add(Box.createVerticalStrut(10));
            infoPanel.add(statusLabel);
            infoPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 0, 0));

            add(infoPanel, BorderLayout.NORTH);

            // Sample data
            employees.put("10001", new Employee("10001", "Garcia, Manuel III", "10/11/1983"));
            employees.put("10002", new Employee("10002", "Lim, Antonio", "06/19/1988"));
            employees.put("10003", new Employee("10003", "Aquino, Bianca Sofia", "08/04/1989"));
        }

        public void searchEmployee(String empNo) {
            Employee emp = employees.get(empNo.trim());
            if (emp != null) {
                nameLabel.setText("Name: " + emp.name);
                birthdayLabel.setText("Birthday: " + emp.birthday);
                statusLabel.setText("Employee found.");
            } else {
                nameLabel.setText("Name: ");
                birthdayLabel.setText("Birthday: ");
                statusLabel.setText("Employee not found.");
            }
        }
    }

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
