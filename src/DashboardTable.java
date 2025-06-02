import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class DashboardTable extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private FileHandler fileHandler;
    private List<String[]> employeeData;

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

        String[] columnNames = {
            "Employee ID", "Last Name", "First Name",
            "SSS Number", "PhilHealth Number", "TIN", "Pag-IBIG Number"
        };
        int[] indices = {0, 1, 2, 6, 7, 8, 9};

        model = new DefaultTableModel(columnNames, 0);
        for (String[] row : employeeData) {
            if (row.length >= 10) {
                String[] displayRow = new String[indices.length];
                for (int i = 0; i < indices.length; i++) {
                    displayRow[i] = row[indices[i]].trim();
                }
                model.addRow(displayRow);
            }
        }

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(700, 300));
        table.setFillsViewportHeight(true);
        add(scrollPane, BorderLayout.CENTER);

        // Button to show details in a dialog
        JButton showDetailsButton = new JButton("Show Full Details");
        showDetailsButton.addActionListener(this::handleShowDetails);
        add(showDetailsButton, BorderLayout.SOUTH);
    }

    private void handleShowDetails(ActionEvent e) {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String selectedEmployeeId = (String) table.getValueAt(selectedRow, 0);

            for (String[] row : employeeData) {
                if (row.length >= 10 && row[0].equals(selectedEmployeeId)) {
                    showDetailDialog(row);
                    return;
                }
            }

            JOptionPane.showMessageDialog(this, "Employee data not found.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred while retrieving details: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

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
