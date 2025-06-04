package gui;

import model.FileHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class DashboardTable extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private FileHandler fileHandler;
    private List<String[]> employeeData; // Full data for filtering
    private final String[] columnNames = {
        "Employee ID", "Last Name", "First Name",
        "SSS Number", "PhilHealth Number", "TIN", "Pag-IBIG Number"
    };
    private final int[] indices = {0, 1, 2, 6, 7, 8, 9};

    public DashboardTable(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        setLayout(new BorderLayout());

        // Load employee data
        try {
            fileHandler.readEmployeeFile();
            employeeData = fileHandler.getEmployeeData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load employee data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Set up table model and table
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        refreshTable(employeeData); // Initially load all data

        JScrollPane scrollPane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(700, 300));
        table.setFillsViewportHeight(true);
        add(scrollPane, BorderLayout.CENTER);

        // Optional button to test detail display
        JButton showDetailsButton = new JButton("Show Full Details");
        showDetailsButton.addActionListener(this::handleShowDetails);
        add(showDetailsButton, BorderLayout.SOUTH);
    }

    /**
     * Adds a new employee if the ID does not already exist.
     * Returns true if successful, false if duplicate.
     */
    public boolean addEmployee(String[] newEmployee) {
        if (newEmployee == null || newEmployee.length == 0) return false;

        String newId = newEmployee[0].trim();

        for (String[] existing : employeeData) {
            if (existing.length > 0 && existing[0].trim().equals(newId)) {
                return false; // Duplicate Employee ID
            }
        }

        // Add to in-memory list
        employeeData.add(newEmployee);

        // Persist to file
        try {
            fileHandler.appendEmployeeToFile(newEmployee);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save employee to file: " + e.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Refresh table view
        refreshTable(employeeData);
        return true;
    }

    /**
     * Filters the table based on the search query.
     */
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

    /**
     * Refreshes the JTable with given rows.
     */
    private void refreshTable(List<String[]> rows) {
        model.setRowCount(0); // Clear table
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

    /**
     * Shows full details of selected employee in a dialog.
     */
    private void handleShowDetails(ActionEvent e) {
        String[] selected = getSelectedEmployee();

        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        showDetailDialog(selected);
    }

    /**
     * Returns the full employee data (as String[]) of the selected row.
     * Returns null if nothing is selected or no match is found.
     */
    public String[] getSelectedEmployee() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return null;

        try {
            String selectedEmployeeId = (String) table.getValueAt(selectedRow, 0);

            for (String[] row : employeeData) {
                if (row.length >= 10 && row[0].equals(selectedEmployeeId)) {
                    return row;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Shows the full detail dialog for a selected employee.
     */
    private void showDetailDialog(String[] row) {
        String[] headers = fileHandler.getEmployeeHeaders();

        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new GridLayout(0, 1, 5, 5));

        for (int i = 0; i < headers.length && i < row.length; i++) {
            detailPanel.add(new JLabel(headers[i] + ": " + row[i]));
        }

        JScrollPane scrollPane = new JScrollPane(detailPanel);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Employee Full Details", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Standalone main method for testing.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileHandler handler = new FileHandler(); // Assumes it loads the file
            JFrame frame = new JFrame("Dashboard Table");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 400);
            frame.add(new DashboardTable(handler));
            frame.setVisible(true);
        });
    }
}
