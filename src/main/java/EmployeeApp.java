import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmployeeApp extends JFrame {

    static final String DB_URL = "jdbc:mysql://localhost:3306/employee_db";
    static final String USER = "root";
    static final String PASS = "Utkarsh620";

    private Connection conn;
    
    private JTable table;
    private DefaultTableModel tableModel;
    
    private JTextField nameField;
    private JTextField positionField;
    private JTextField emailField;
    private JTextField idField;
    private JTextField searchField;

    public EmployeeApp() {
        setTitle("Employee Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        connectToDatabase();
        initUI();
        loadEmployees();
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // --- Top Panel (Search) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search by Name:"));
        searchField = new JTextField(20);
        topPanel.add(searchField);
        JButton searchBtn = new JButton("Search");
        topPanel.add(searchBtn);
        JButton viewAllBtn = new JButton("View All");
        topPanel.add(viewAllBtn);
        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel (Table) ---
        String[] columns = {"ID", "Name", "Position", "Email"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Bottom Panel (Form & Actions) ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formPanel.add(new JLabel("ID (for Update/Del):"));
        idField = new JTextField(5);
        formPanel.add(idField);
        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField(10);
        formPanel.add(nameField);
        formPanel.add(new JLabel("Position:"));
        positionField = new JTextField(10);
        formPanel.add(positionField);
        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField(15);
        formPanel.add(emailField);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addBtn = new JButton("Add Employee");
        JButton updateBtn = new JButton("Update Position");
        JButton deleteBtn = new JButton("Delete Employee");
        actionPanel.add(addBtn);
        actionPanel.add(updateBtn);
        actionPanel.add(deleteBtn);

        bottomPanel.add(formPanel);
        bottomPanel.add(actionPanel);
        
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Event Listeners ---
        
        viewAllBtn.addActionListener(e -> loadEmployees());

        searchBtn.addActionListener(e -> {
            String search = searchField.getText();
            if(!search.isEmpty()) {
                searchEmployees(search);
            } else {
                loadEmployees();
            }
        });

        addBtn.addActionListener(e -> {
            addEmployee();
            loadEmployees();
        });

        updateBtn.addActionListener(e -> {
            updateEmployee();
            loadEmployees();
        });

        deleteBtn.addActionListener(e -> {
            deleteEmployee();
            loadEmployees();
        });
        
        // Populate form when row is clicked
        table.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if(selectedRow >= 0) {
                idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                positionField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                emailField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            }
        });
    }

    private void loadEmployees() {
        tableModel.setRowCount(0);
        try {
            if (conn != null) {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM employees");
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("position"),
                            rs.getString("email")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading employees: " + e.getMessage());
        }
    }

    private void searchEmployees(String search) {
        tableModel.setRowCount(0);
        try {
            if (conn != null) {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM employees WHERE name LIKE ?");
                ps.setString(1, "%" + search + "%");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("position"),
                            rs.getString("email")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching employees: " + e.getMessage());
        }
    }

    private void addEmployee() {
        String name = nameField.getText();
        String position = positionField.getText();
        String email = emailField.getText();

        if(name.isEmpty() || position.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Position are required.");
            return;
        }

        try {
            if (conn != null) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO employees(name,position,email) VALUES(?,?,?)");
                ps.setString(1, name);
                ps.setString(2, position);
                ps.setString(3, email);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Employee Added!");
                
                nameField.setText("");
                positionField.setText("");
                emailField.setText("");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding employee: " + e.getMessage());
        }
    }

    private void updateEmployee() {
        String idStr = idField.getText();
        String position = positionField.getText();

        if(idStr.isEmpty() || position.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID and new Position are required for update.");
            return;
        }

        try {
            if (conn != null) {
                int id = Integer.parseInt(idStr);
                PreparedStatement ps = conn.prepareStatement("UPDATE employees SET position=? WHERE id=?");
                ps.setString(1, position);
                ps.setInt(2, id);
                int updated = ps.executeUpdate();
                if(updated > 0) {
                    JOptionPane.showMessageDialog(this, "Employee Updated!");
                    idField.setText("");
                    nameField.setText("");
                    positionField.setText("");
                    emailField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "No employee found with that ID.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating employee: " + e.getMessage());
        }
    }

    private void deleteEmployee() {
        String idStr = idField.getText();

        if(idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID is required for deletion.");
            return;
        }

        try {
            if (conn != null) {
                int id = Integer.parseInt(idStr);
                PreparedStatement ps = conn.prepareStatement("DELETE FROM employees WHERE id=?");
                ps.setInt(1, id);
                int deleted = ps.executeUpdate();
                if(deleted > 0) {
                    JOptionPane.showMessageDialog(this, "Employee Deleted!");
                    idField.setText("");
                    nameField.setText("");
                    positionField.setText("");
                    emailField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "No employee found with that ID.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting employee: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Run UI in Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            EmployeeApp app = new EmployeeApp();
            app.setVisible(true);
        });
    }
}
