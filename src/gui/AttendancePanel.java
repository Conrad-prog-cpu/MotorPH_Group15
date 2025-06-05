package gui;

import model.FileHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AttendancePanel extends JPanel {
    private FileHandler fileHandler;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<String[]> allData;
    private final Color gradientStart = new Color(255, 204, 229);
    private final Color gradientEnd = new Color(255, 229, 180);

    public AttendancePanel(FileHandler fileHandler) {
        this.fileHandler = fileHandler;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        fileHandler.readAttendanceFile();
        List<String> headers = List.of("Employee #", "Date", "Log In", "Log Out");
        allData = fileHandler.getAttendanceData();

        String[] columnNames = headers.toArray(String[]::new);
        
        tableModel = new DefaultTableModel(columnNames, 0);
        
        attendanceTable = new JTable(tableModel);
        styleTable(attendanceTable);

        populateTable(allData);

        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");

        searchPanel.add(new JLabel("Employee ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        tableWrapper.setBackground(Color.WHITE);
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        add(tableWrapper, BorderLayout.CENTER);

        Runnable searchAction = () -> {
            String input = searchField.getText().trim().toLowerCase();
            if (input.isEmpty()) {
                resetTableWithFocus("Please enter an Employee ID.");
                return;
            }

            List<String[]> filtered = new ArrayList<>();
            for (String[] row : allData) {
                if (row[0].toLowerCase().contains(input)) {
                    filtered.add(row);
                }
            }

            if (filtered.isEmpty()) {
                resetTableWithFocus("No records found for: " + searchField.getText());
            } else {
                populateTable(filtered);
            }
        };
        
        searchButton.addActionListener(e -> searchAction.run());
        searchField.addActionListener(e -> searchAction.run());
    }

    private void populateTable(List<String[]> data) {
        tableModel.setRowCount(0);
        for (String[] row : data) {
            tableModel.addRow(row);
            
        }
    }

    private void resetTableWithFocus(String message) {
        populateTable(allData);
        JOptionPane.showMessageDialog(this, message, "Search Error", JOptionPane.WARNING_MESSAGE);
        searchField.requestFocusInWindow();
        searchField.selectAll();
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(22);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.setIntercellSpacing(new Dimension(8, 2));
        table.setSelectionBackground(new Color(220, 235, 255));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        header.setBackground(new Color(245, 245, 245));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

        // ✅ Center-align header text
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
    
        // Optional: center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(new GradientPaint(0, 0, gradientStart, 0, getHeight(), gradientEnd));
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
