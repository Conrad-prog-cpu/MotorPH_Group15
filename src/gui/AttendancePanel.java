package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class AttendancePanel extends JPanel {

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
        String id, name, birthday;
        List<Attendance> records = new ArrayList<>();

        Employee(String id, String name, String birthday) {
            this.id = id;
            this.name = name;
            this.birthday = birthday;
        }

        void add(Attendance a) {
            records.add(a);
        }
    }

    Map<String, Employee> data = new HashMap<>();

    JTextField input = new JTextField(10);
    JLabel nameLabel = new JLabel("Name: ");
    JLabel birthdayLabel = new JLabel("Birthday: ");
    JLabel totalLabel = new JLabel("Total Hours Worked: ");
    DefaultTableModel model = new DefaultTableModel(
            new String[]{"Date", "Day", "Time In", "Break Out", "Break In", "Time Out", "Hours", "Remarks"}, 0);
    JTable table = new JTable(model);

    public AttendancePanel() {
        setLayout(new BorderLayout());
        loadData();

        JButton search = new JButton("Search");
        search.addActionListener(e -> showAttendance());
        input.addActionListener(e -> showAttendance());

        JPanel top = new JPanel();
        top.add(new JLabel("Employee No:"));
        top.add(input);
        top.add(search);

        JPanel info = new JPanel(new GridLayout(3, 1));
        info.add(nameLabel);
        info.add(birthdayLabel);
        info.add(totalLabel);

        this.add(top, BorderLayout.NORTH);
        this.add(info, BorderLayout.WEST);
        this.add(new JScrollPane(table), BorderLayout.CENTER);
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
            birthdayLabel.setText("Birthday: " + emp.birthday);

            double total = 0;
            for (Attendance a : emp.records) {
                model.addRow(a.toRow());
                total += a.hours;
            }
            totalLabel.setText("Total Hours Worked: " + total + " hrs");
        } else {
            nameLabel.setText("Name: ");
            birthdayLabel.setText("Birthday: ");
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

        GradientPaint gradient = new GradientPaint(0, 0, new Color(135, 206, 250), 0, height, Color.WHITE);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        g2d.dispose();
    }
}
