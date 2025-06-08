package gui;

import model.FileHandler;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class EmployeeTable extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private FileHandler fileHandler;
    private List<String[]> employeeData;

    private final String[] columnNames = {
        "Employee ID", "Last Name", "First Name",
        "SSS No.", "PhilHealth No.", "TIN", "Pag-IBIG No."
    };
    private final int[] indices = {0, 1, 2, 6, 7, 8, 9};

    private final Color gradientStart = new Color(255, 204, 229);
    private final Color gradientEnd = new Color(255, 229, 180);

    public EmployeeTable(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        setLayout(new BorderLayout());

        try {
            fileHandler.readEmployeeFile();
            employeeData = fileHandler.getEmployeeData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load employee data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setOpaque(false);
        table.setShowGrid(true);
        table.setRowHeight(25);
        table.setBorder(BorderFactory.createEmptyBorder());

        table.setDefaultRenderer(Object.class, new ResponsiveCellRenderer());

        JTableHeaderRenderer headerRenderer = new JTableHeaderRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

        add(scrollPane, BorderLayout.CENTER);

        refreshTable(employeeData);
        new EmployeeTablePopUp(table, fileHandler, this);

        // View Employee button GUI is removed but logic remains
    }

    private static class JTableHeaderRenderer extends DefaultTableCellRenderer {
        public JTableHeaderRenderer() {
            setOpaque(true);
            setBackground(new Color(33, 150, 243));
            setForeground(Color.WHITE);
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return this;
        }
    }

    private class ResponsiveCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(230, 240, 255));
            }
            String colName = table.getColumnName(column).toLowerCase();
            boolean isNumberField = colName.contains("sss") || colName.contains("phil")
                    || colName.contains("tin") || colName.contains("pag");
            int width = EmployeeTable.this.getWidth();
            int fontSize = width < 600 ? 10 : (width < 800 ? 12 : 14);
            c.setFont(new Font("SansSerif", Font.PLAIN, isNumberField ? fontSize : 14));
            return c;
        }
    }

    private static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = Color.WHITE;
            trackColor = new Color(0, 0, 0, 0);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createInvisibleButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createInvisibleButton();
        }

        private JButton createInvisibleButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0, gradientStart, 0, getHeight(), gradientEnd);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    public boolean addEmployee(String[] newEmployee) {
        if (newEmployee == null || newEmployee.length == 0) return false;

        String newId = newEmployee[0].trim();
        for (String[] existing : employeeData) {
            if (existing.length > 0 && existing[0].trim().equals(newId)) {
                return false;
            }
        }

        employeeData.add(newEmployee);
        try {
            fileHandler.appendEmployeeToFile(newEmployee);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save employee to file: " + e.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        refreshTable(employeeData);
        return true;
    }

    public void filterTable(String query) {
        if (query == null || query.isEmpty()) {
            refreshTable(employeeData);
            return;
        }

        List<String[]> filtered = new ArrayList<>();
        for (String[] row : employeeData) {
            for (int i : indices) {
                if (i < row.length && row[i].toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(row);
                    break;
                }
            }
        }

        refreshTable(filtered);
    }

    public final void refreshTable(List<String[]> rows) {
        model.setRowCount(0);
        for (String[] row : rows) {
            if (row.length >= 10) {
                String[] displayRow = new String[indices.length];
                for (int i = 0; i < indices.length; i++) {
                    displayRow[i] = row[indices[i]].trim();
                }
                model.addRow(displayRow);
            }
        }
    }

    // === Logic for "View Employee" kept without GUI ===

    private void handleShowDetails(ActionEvent e) {
        Vector<Object> selected = getSelectedEmployeeFullDetails();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        showDetailDialog(selected);
    }

    public Vector<Object> getSelectedEmployeeFullDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return null;

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String employeeId = (String) table.getModel().getValueAt(modelRow, 0);

        for (String[] employee : employeeData) {
            if (employee.length > 0 && employee[0].equals(employeeId)) {
                Vector<Object> fullData = new Vector<>();
                for (String item : employee) {
                    fullData.add(item);
                }
                return fullData;
            }
        }

        return null;
    }



    private void showDetailDialog(Vector<Object> row) {
        String[] headers = fileHandler.getEmployeeHeaders();
        JPanel detailPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        for (int i = 0; i < headers.length && i < row.size(); i++) {
            detailPanel.add(new JLabel(headers[i] + ": " + row.get(i)));
        }

        JScrollPane scrollPane = new JScrollPane(detailPanel);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Employee Full Details", JOptionPane.INFORMATION_MESSAGE);
    }
}
