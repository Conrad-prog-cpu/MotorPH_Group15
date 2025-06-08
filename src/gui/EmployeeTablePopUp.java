/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

import model.FileHandler;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author santos.conrad
 */
public class EmployeeTablePopUp {

    private final JTable table;
    private final FileHandler fileHandler;
    private final DashboardTable dashboardTable;

    public EmployeeTablePopUp(JTable table, FileHandler fileHandler, DashboardTable dashboardTable) {
        this.table = table;
        this.fileHandler = fileHandler;
        this.dashboardTable = dashboardTable;
        initPopupMenu();
    }

    private void initPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem updateItem = new JMenuItem("Update");
        JMenuItem deleteItem = new JMenuItem("Delete");

        popupMenu.add(updateItem);
        popupMenu.add(deleteItem);

        updateItem.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) return;

            String employeeId = (String) table.getValueAt(selectedRow, 0);
            String[] employee = fileHandler.updateEmployeeField(employeeId);

            if (employee == null) {
                JOptionPane.showMessageDialog(null, "Employee not found.");
                return;
            }

            JTextField[] fields = new JTextField[employee.length];
            JPanel panel = new JPanel(new SpringLayout());
            String[] headers = fileHandler.getEmployeeHeaders();

            for (int i = 0; i < employee.length; i++) {
                panel.add(new JLabel(headers[i] + ":"));
                fields[i] = new JTextField(employee[i], 20);
                panel.add(fields[i]);
            }

            SpringUtilities.makeCompactGrid(panel, employee.length, 2, 6, 6, 6, 6);
            int result = JOptionPane.showConfirmDialog(null, panel, "Update Employee", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String[] updatedData = new String[employee.length];
                for (int i = 0; i < fields.length; i++) {
                    updatedData[i] = fields[i].getText().trim();
                }

                try {
                    fileHandler.updateEmployeeField(employeeId, updatedData);
                    dashboardTable.refreshTable(fileHandler.getEmployeeData());
                    JOptionPane.showMessageDialog(null, "Employee updated successfully.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Failed to update: " + ex.getMessage());
                }
            }
        });

        deleteItem.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) return;

            String employeeId = (String) table.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete employee ID " + employeeId + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    fileHandler.deleteEmployeeById(employeeId);
                    dashboardTable.refreshTable(fileHandler.getEmployeeData());
                    JOptionPane.showMessageDialog(null, "Employee deleted.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Failed to delete: " + ex.getMessage());
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = table.rowAtPoint(e.getPoint());
                    table.setRowSelectionInterval(row, row);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }
}
