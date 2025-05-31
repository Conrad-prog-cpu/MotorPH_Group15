package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public final class AttendancePanel extends JPanel {

    static class Attendance {
        String date, day, timeIn, breakOut, breakIn, timeOut, remarks;
        double hours;

        Attendance(String date, String day, String timeIn, String breakOut, String breakIn, String timeOut, double hours, String remarks) {
            this.date = date;
            this.day = day;
            this.timeIn = timeIn;
            this.breakOut = breakOut;
            this.breakIn = breakIn;
            this.timeOut = timeOut;
            this.hours = hours;
            this.remarks = remarks;
        }

        Object[] toRow() {
            return new Object[]{date, day, timeIn, breakOut, breakIn, timeOut, hours + " hrs", remarks};
        }
    }

    static class Employee {
        String id, name;
        List<Attendance> records = new ArrayList<>();

        Employee(String id, String name, String birthday) {
            this.id = id;
            this.name = name;
        }

        void add(Attendance a) {
            records.add(a);
        }
    }

    Map<String, Employee> data = new HashMap<>();
    JLabel idLabel = new JLabel("Employee ID: ");

    JTextField input = new JTextField(10);
    JLabel nameLabel = new JLabel("Name: ");
    JLabel totalLabel = new JLabel("Total Hours Worked: ");
    DefaultTableModel model = new DefaultTableModel(
            new String[]{"Date", "Day", "Time In", "Break Out", "Break In", "Time Out", "Hours", "Remarks"}, 0);
    JTable table = new JTable(model);
    

   public AttendancePanel() {
    setLayout(new BorderLayout());
    loadData();

    // Create transparent button
    JButton search = new JButton("Search");
    search.setFocusPainted(false);
    search.setContentAreaFilled(false);
    search.setBorderPainted(true);
    search.setOpaque(false);
    search.setForeground(Color.BLACK);
    search.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    search.addActionListener(e -> showAttendance());
    input.addActionListener(e -> showAttendance());

    // Top input panel
    JPanel top = new JPanel();
    top.setOpaque(false);
    top.add(new JLabel("Employee No:"));
    top.add(input);
    top.add(search);

    // Header info above table
    nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
    totalLabel.setHorizontalAlignment(SwingConstants.LEFT);
    idLabel.setHorizontalAlignment(SwingConstants.LEFT);

    JPanel header = new JPanel(new GridLayout(1, 3));
    header.setOpaque(false);
    header.add(nameLabel);
    header.add(idLabel);
    header.add(totalLabel);

    // Table with transparent scroll
    table.setOpaque(false);
    ((DefaultTableCellRenderer) table.getDefaultRenderer(Object.class)).setOpaque(false);

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);

    // Combine header and table in a center panel
    JPanel center = new JPanel(new BorderLayout());
    center.setOpaque(false);
    center.add(header, BorderLayout.NORTH);
    center.add(scrollPane, BorderLayout.CENTER);

    // Wrap everything in a margin panel
    JPanel marginPanel = new JPanel(new BorderLayout());
    marginPanel.setOpaque(false);
    marginPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30)); // top, left, bottom, right
    marginPanel.add(top, BorderLayout.NORTH);
    marginPanel.add(center, BorderLayout.CENTER);

    // Add to main panel
    this.add(marginPanel, BorderLayout.CENTER);
}


    void loadData() {
        Employee e1 = new Employee("10001", "Garcia, Manuel III", "10/11/1983");
        e1.add(new Attendance("Dec 01", "Fri", "08:00", "12:00", "13:00", "17:00", 8, "Present"));
        e1.add(new Attendance("Dec 02", "Sat", "-", "-", "-", "-", 0, "Weekend"));
        e1.add(new Attendance("Dec 03", "Sun", "-", "-", "-", "-", 0, "Weekend"));
        e1.add(new Attendance("Dec 04", "Mon", "08:00", "12:00", "13:00", "17:00", 8, "Present"));
        e1.add(new Attendance("Dec 05", "Tue", "08:00", "12:00", "13:00", "17:00", 8, "Present"));

        Employee e2 = new Employee("10002", "Lim, Antonio", "06/19/1988");
        e2.add(new Attendance("Dec 25", "Mon", "-", "-", "-", "-", 0, "Holiday"));
        e2.add(new Attendance("Dec 26", "Tue", "08:00", "12:00", "13:00", "17:00", 8, "Present"));
        e2.add(new Attendance("Dec 27", "Wed", "08:00", "12:00", "13:00", "17:00", 8, "Present"));
        e2.add(new Attendance("Dec 28", "Thu", "08:00", "12:00", "13:00", "17:00", 8, "Present"));
        e2.add(new Attendance("Dec 29", "Fri", "08:00", "12:00", "13:00", "17:00", 8, "Present"));

        Employee e3 = new Employee("10003", "Aquino, Bianca Sofia", "08/04/1989");
        e3.add(new Attendance("Dec 11", "Mon", "08:00", "12:00", "13:00", "17:00", 8, "Present"));
        e3.add(new Attendance("Dec 12", "Tue", "08:00", "12:00", "13:00", "17:00", 8, "Present"));
        e3.add(new Attendance("Dec 13", "Wed", "08:00", "12:00", "13:00", "17:00", 8, "Present"));
        e3.add(new Attendance("Dec 14", "Thu", "08:00", "12:00", "13:00", "17:00", 8, "Present"));
        e3.add(new Attendance("Dec 15", "Fri", "08:00", "12:00", "13:00", "17:00", 8, "Present"));

        data.put(e1.id, e1);
        data.put(e2.id, e2);
        data.put(e3.id, e3);
    }

    void showAttendance() {
    String id = input.getText().trim();
    Employee emp = data.get(id);
    model.setRowCount(0);

    if (emp != null) {
        nameLabel.setText("Name: " + emp.name);
        idLabel.setText("Employee ID: " + emp.id);

        double total = 0;
        for (Attendance a : emp.records) {
            model.addRow(a.toRow());
            total += a.hours;
        }
        totalLabel.setText("Total Hours Worked: " + total + " hrs");
    } else {
        nameLabel.setText("Name: ");
        idLabel.setText("Employee ID: ");
        totalLabel.setText("Total Hours Worked: ");
        JOptionPane.showMessageDialog(this, "Employee not found.");
    }
}

    @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    

    Graphics2D g2d = (Graphics2D) g.create();

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int width = getWidth();
    int height = getHeight();

    // Use same gradient as Dashboard
    Color gradientStart = new Color(255, 204, 229);
    Color gradientEnd = new Color(255, 229, 180);

    GradientPaint gradient = new GradientPaint(0, 0, gradientStart, 0, height, gradientEnd);
    g2d.setPaint(gradient);
    g2d.fillRect(0, 0, width, height);

    g2d.dispose();
}
}
