package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class EmployeePanel extends JPanel {

    private final Color gradientStart = new Color(255, 204, 229);
    private final Color gradientEnd = new Color(255, 229, 180);

    private final DefaultTableModel tableModel;
    private final JTable employeeTable;
    private final Map<String, Employee> employeeMap = new LinkedHashMap<>();

    private final JTextField searchField = new JTextField(20);

    public EmployeePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        setupSampleData();

        // === TOP PANEL ===
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(20, 50, 0, 50));

        // === LEFT SIDE: Search Field + Button ===
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(buildSearchPanel());

        // === RIGHT SIDE: View + Add Buttons ===
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JButton viewButton = new JButton("View Employee");
        styleMinimalButton(viewButton, 120, 36);
        viewButton.addActionListener(e -> showSelectedEmployeeDetails());
        rightPanel.add(viewButton);

        JButton addButton = new JButton("Add Employee");
        styleMinimalButton(addButton, 120, 36);
        addButton.addActionListener(e -> showAddEmployeeDialog());
        rightPanel.add(addButton);

        // === Combine into topPanel ===
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        // === Table Section ===
        String[] columns = {"Employee No.", "Last Name", "First Name", "Birthday", "SSS No.", "PhilHealth No.", "TIN No.", "Pag-IBIG No."};
        tableModel = new DefaultTableModel(columns, 0);
        employeeTable = new JTable(tableModel);
        loadEmployeesToTable();

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(new EmptyBorder(20, 50, 10, 50));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);

        searchField.setToolTipText("Search by ID, Last Name, or First Name");
        searchField.setPreferredSize(new Dimension(200, 36));
        searchField.setBackground(Color.WHITE);
        searchField.setForeground(Color.BLACK);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton searchButton = new JButton("Search");
        styleColoredButton(searchButton, new Color(30, 144, 255), 80, 36);
        searchButton.addActionListener(this::performSearch);

        panel.add(searchField);
        panel.add(searchButton);

        return panel;
    }

    private void performSearch(ActionEvent e) {
        String query = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);

        if (query.isEmpty()) {
            loadEmployeesToTable();
            return;
        }

        for (Employee emp : employeeMap.values()) {
            if (emp.getId().toLowerCase().contains(query) ||
                emp.getLastName().toLowerCase().contains(query) ||
                emp.getFirstName().toLowerCase().contains(query)) {
                addEmployeeToTable(emp);
            }
        }
    }

    private void setupSampleData() {
        employeeMap.put("10001", new Employee("10001", "Garcia", "Manuel III", "10/11/1983", "SSS001", "PH001", "TIN001", "PAG001"));
        employeeMap.put("10002", new Employee("10002", "Lim", "Antonio", "06/19/1988", "SSS002", "PH002", "TIN002", "PAG002"));
        employeeMap.put("10003", new Employee("10003", "Aquino", "Bianca Sofia", "08/04/1989", "SSS003", "PH003", "TIN003", "PAG003"));
    }

    private void loadEmployeesToTable() {
        for (Employee emp : employeeMap.values()) {
            addEmployeeToTable(emp);
        }
    }

    private void showAddEmployeeDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Add New Employee", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new GridLayout(9, 2, 20, 15));

        String[] labels = {"Employee #", "Last Name", "First Name", "Birthday", "SSS #", "PhilHealth #", "TIN #", "Pag-IBIG #"};
        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            dialog.add(new JLabel(labels[i]));
            fields[i] = new JTextField();
            dialog.add(fields[i]);
        }

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        styleMinimalButton(saveButton, 100, 30);
        styleMinimalButton(cancelButton, 100, 30);

        dialog.add(saveButton);
        dialog.add(cancelButton);

        saveButton.addActionListener(e -> {
            for (JTextField field : fields) {
                if (field.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            String empId = fields[0].getText().trim();
            if (employeeMap.containsKey(empId)) {
                JOptionPane.showMessageDialog(dialog, "Employee ID already exists.", "Duplicate Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Employee emp = new Employee(
                fields[0].getText().trim(), fields[1].getText().trim(), fields[2].getText().trim(), fields[3].getText().trim(),
                fields[4].getText().trim(), fields[5].getText().trim(), fields[6].getText().trim(), fields[7].getText().trim()
            );

            employeeMap.put(emp.getId(), emp);
            addEmployeeToTable(emp);
            JOptionPane.showMessageDialog(dialog, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addEmployeeToTable(Employee emp) {
        tableModel.addRow(new Object[]{
            emp.getId(), emp.getLastName(), emp.getFirstName(), emp.getBirthday(),
            emp.getSss(), emp.getPhilHealth(), emp.getTin(), emp.getPagibig()
        });
    }

    private void showSelectedEmployeeDetails() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow >= 0) {
            String empId = (String) tableModel.getValueAt(selectedRow, 0);
            Employee emp = employeeMap.get(empId);

            if (emp != null) {
                JFrame detailsFrame = new JFrame("Employee Details - " + emp.getFullName());
                detailsFrame.setLayout(new GridLayout(0, 2, 10, 10));
                detailsFrame.add(new JLabel("Employee No.:")); detailsFrame.add(new JLabel(emp.getId()));
                detailsFrame.add(new JLabel("Last Name:")); detailsFrame.add(new JLabel(emp.getLastName()));
                detailsFrame.add(new JLabel("First Name:")); detailsFrame.add(new JLabel(emp.getFirstName()));
                detailsFrame.add(new JLabel("Birthday:")); detailsFrame.add(new JLabel(emp.getBirthday()));
                detailsFrame.add(new JLabel("SSS No.:")); detailsFrame.add(new JLabel(emp.getSss()));
                detailsFrame.add(new JLabel("PhilHealth No.:")); detailsFrame.add(new JLabel(emp.getPhilHealth()));
                detailsFrame.add(new JLabel("TIN No.:")); detailsFrame.add(new JLabel(emp.getTin()));
                detailsFrame.add(new JLabel("Pag-IBIG No.:")); detailsFrame.add(new JLabel(emp.getPagibig()));

                detailsFrame.pack();
                detailsFrame.setLocationRelativeTo(this);
                detailsFrame.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee first.");
        }
    }

    private void styleMinimalButton(JButton button, int width, int height) {
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(width, height));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.BLACK);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
                super.paint(g2, c);
                g2.dispose();
            }
        });

        button.setMargin(new Insets(0, 15, 0, 15));
    }

    private void styleColoredButton(JButton button, Color bgColor, int width, int height) {
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(width, height));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
                super.paint(g2, c);
                g2.dispose();
            }
        });

        button.setMargin(new Insets(0, 15, 0, 15));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(new GradientPaint(0, 0, gradientStart, 0, getHeight(), gradientEnd));
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    private static class Employee {
        private final String id, lastName, firstName, birthday, sss, philHealth, tin, pagibig;

        public Employee(String id, String lastName, String firstName, String birthday,
                        String sss, String philHealth, String tin, String pagibig) {
            this.id = id;
            this.lastName = lastName;
            this.firstName = firstName;
            this.birthday = birthday;
            this.sss = sss;
            this.philHealth = philHealth;
            this.tin = tin;
            this.pagibig = pagibig;
        }

        public String getId() { return id; }
        public String getLastName() { return lastName; }
        public String getFirstName() { return firstName; }
        public String getBirthday() { return birthday; }
        public String getSss() { return sss; }
        public String getPhilHealth() { return philHealth; }
        public String getTin() { return tin; }
        public String getPagibig() { return pagibig; }

        public String getFullName() {
            return firstName + " " + lastName;
        }
    }
}
