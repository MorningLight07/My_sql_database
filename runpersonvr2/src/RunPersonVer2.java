import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

class SingletonFile {
    private static SingletonFile single_instance = null;
    Connection connection;

    private SingletonFile() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sample_test", "root", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SingletonFile getInstance() {
        if (single_instance == null)
            single_instance = new SingletonFile();

        return single_instance;
    }
}

abstract class Person extends Frame implements ActionListener {
    Button save = null;
    Button back = null;
    Label first_name = null;
    Label last_name = null;
    Label id = null;
    TextField first_name_input = null;
    TextField last_name_input = null;
    TextField id_input = null;

    private String fname;
    private String lname;
    private String pid;

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void actionPerformed(ActionEvent e) {
    }
}

class SavePrompt extends Person {
    JFrame frame = null;
    Label info = null;
    Button save = null;
    JButton okButton;

    SavePrompt() {
        frame = new JFrame();
        info = new Label("Saved!");
        save = new Button("Okay");
      

        frame.setSize(400, 200);
        frame.setTitle("Saved!");
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        info.setFont(new Font("Times New Roman", Font.PLAIN, 40));
        info.setForeground(Color.GREEN);
        info.setBounds(120, 20, 150, 50);
        frame.add(info);

        save.setBounds(130, 80, 100, 60);
        save.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        frame.add(save);

        save.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(save)) {
            frame.dispose();
            new PersonView();
            
        }
    }
}

class EmptyPrompt extends Person {
    JFrame frame = null;
    Label info = null;
    Button save = null;

    EmptyPrompt() {
        frame = new JFrame();
        info = new Label("Empty!");
        save = new Button("Okay");

        frame.setSize(400, 200);
        frame.setTitle("Empty!");
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        info.setFont(new Font("Times New Roman", Font.PLAIN, 40));
        info.setForeground(Color.RED);
        info.setBounds(120, 20, 150, 50);
        frame.add(info);

        save.setBounds(130, 80, 100, 60);
        save.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        frame.add(save);

        save.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(save)) {
            frame.dispose();
        }
    }
}

class PersonController {
    private static String fname;
    private static String lname;
    private static String pid;

    public static String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public static String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public static String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public boolean Save(String isNewEntry) {
        PersonModel person = new PersonModel();
        boolean isSuccessful = false;

        if (isNewEntry.equals("Yes"))
            isSuccessful = person.Add(this);
        else
            isSuccessful = person.Update(this);

        return isSuccessful;
    }
}

class PersonModel {
    public boolean Add(PersonController person) {
        boolean iSuccess = false;
        try {
            SingletonFile s = SingletonFile.getInstance();
            PreparedStatement preparedStatement = s.connection.prepareStatement("insert into person_table(PerID, PerFname, PerLname) values (?,?,?)");

            preparedStatement.setString(1, person.getPid());
            preparedStatement.setString(2, person.getFname());
            preparedStatement.setString(3, person.getLname());

            preparedStatement.addBatch();
            preparedStatement.executeBatch();

            preparedStatement.clearBatch();
            preparedStatement.close();
            iSuccess = true;
        } catch (SQLIntegrityConstraintViolationException sqlErr) {
            System.err.println("Duplicate Key!");
            sqlErr.printStackTrace();
        } catch (Exception exception) {
            System.err.println("Error occurred!");
            exception.printStackTrace();
        }
        return iSuccess;
    }

    public boolean Update(PersonController person) {
        boolean iSuccess = false;
        try {
            SingletonFile s = SingletonFile.getInstance();
            PreparedStatement preparedStatement = s.connection.prepareStatement("Update person_table Set PerFname=?, PerLname=? " + " Where PerID = ? ");

            preparedStatement.setString(1, person.getFname());
            preparedStatement.setString(2, person.getLname());
            preparedStatement.setString(3, person.getPid());

            preparedStatement.addBatch();
            preparedStatement.executeBatch();

            preparedStatement.clearBatch();
            preparedStatement.close();
            iSuccess = true;
        } catch (SQLIntegrityConstraintViolationException sqlErr) {
            System.err.println("Duplicate Key!");
            sqlErr.printStackTrace();
        } catch (Exception exception) {
            System.err.println("Error occurred!");
            exception.printStackTrace();
        }
        return iSuccess;
    }
}
class ViewFrame extends JFrame {
    JButton closeButton;
    JLabel idLabel, lastNameLabel, firstNameLabel;

    ViewFrame(String id, String lastName, String firstName) {
        setTitle("View");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(0, 255, 255)); // Set background color

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.LINE_END;

        idLabel = new JLabel("ID:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(idLabel, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel idValueLabel = new JLabel(id);
        idValueLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        panel.add(idValueLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;
        lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(lastNameLabel, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel lastNameValueLabel = new JLabel(lastName);
        lastNameValueLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        panel.add(lastNameValueLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;
        firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(firstNameLabel, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel firstNameValueLabel = new JLabel(firstName);
        firstNameValueLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        panel.add(firstNameValueLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 18));
        closeButton.setBackground(new Color(222, 49, 99));
        closeButton.setForeground(new Color(2, 48, 32));
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        closeButton.setPreferredSize(new Dimension(165, 45)); // Set preferred size to 100x40 pixels
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                
            }
        });
        
        panel.add(closeButton, gbc);

        setContentPane(panel);
        setVisible(true);
    }
}
class InsertForm extends JFrame implements ActionListener {
    JTextField idField, firstNameField, lastNameField;
    JButton saveButton, closeButton;

    InsertForm() {
        setTitle("Insert");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

         // Initialize CardLayout and cardPanel
 
         // Initialize insert form
         JPanel insertPanel = new JPanel(new BorderLayout());
         insertPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(204, 204, 255));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();  
        ImageIcon imageIcon = new ImageIcon("Paul_George_Pacers.jpg"); // Provide the path to your image
        JLabel imageLabel = new JLabel(imageIcon);
        panel.add(imageLabel, BorderLayout.NORTH);
        
        int halfScreenWidth = screenSize.width / 2;                                      
        int halfScreenHeight = screenSize.height / 2;

        setSize(halfScreenWidth, halfScreenHeight);                                          // Set frame size to half of the screen size

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        Font labelFont = new Font("Arial", Font.BOLD, 30); // Larger font for labels
        Font textFieldFont = new Font("Arial", Font.PLAIN, 20); // Larger font for text fields

        JLabel idLabel = new JLabel("ID:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 30)); // Set font for label
        idField = new JTextField(30); // Adjust the width of the text field
        idField.setFont(textFieldFont); // Set font for text field
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(idLabel, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(idField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setFont(new Font("Arial", Font.BOLD, 30)); // Set font for label
        firstNameField = new JTextField(30); // Adjust the width of the text field
        firstNameField.setFont(textFieldFont); // Set font for text field
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(firstNameLabel, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(firstNameField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(new Font("Arial", Font.BOLD, 30)); // Set font for label
        lastNameField = new JTextField(30); // Adjust the width of the text field
        lastNameField.setFont(textFieldFont); // Set font for text field
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(lastNameLabel, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(lastNameField, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        saveButton = new JButton("Save");
        saveButton.setFont(new Font("Arial", Font.BOLD, 18));
        saveButton.setPreferredSize(new Dimension(150, 40)); // Adjust button size
        saveButton.addActionListener(this);
        saveButton.setBackground(new Color(229, 184, 11));             // CERISE RED
        saveButton.setForeground(new Color(2, 48, 32)); 
        saveButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));  // Border color black 
        

        closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 18));
        closeButton.setPreferredSize(new Dimension(150, 40)); // Adjust button size
        closeButton.addActionListener(this);
        closeButton.setBackground(new Color(222, 49, 99));             // CERISE RED
        closeButton.setForeground(new Color(2, 48, 32)); 
        closeButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));  // Border color black 
        


        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(panel);
        setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            // Get data from fields
            String id = idField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            PersonController personController = new PersonController();
            personController.setPid(id);
            personController.setFname(firstName);
            personController.setLname(lastName);

            boolean saved = personController.Save("Yes");

            if (saved) {
                new SavePrompt();
                dispose(); // Dispose of the InsertForm frame
                
            } else {
                JOptionPane.showMessageDialog(this, "This ID is already in use. Please choose a different one.", "Please input another ID", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == closeButton) {
            new PersonView();
            dispose();
        }
    }
}
class ModifyForm extends JFrame implements ActionListener {
    JLabel idLabel, firstNameLabel, lastNameLabel;
    JTextField firstNameField, lastNameField;
    JButton updateButton, closeButton;
    String personID;

    ModifyForm(String id, String firstName, String lastName) {
        setTitle("Modify");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        idLabel = new JLabel("ID:");
        panel.add(idLabel);
        JLabel idValueLabel = new JLabel(id);
        panel.add(idValueLabel);
        personID = id; // Save person ID for updating

        firstNameLabel = new JLabel("First Name:");
        panel.add(firstNameLabel);
        firstNameField = new JTextField(firstName);
        panel.add(firstNameField);

        lastNameLabel = new JLabel("Last Name:");
        panel.add(lastNameLabel);
        lastNameField = new JTextField(lastName);
        panel.add(lastNameField);

        updateButton = new JButton("Update");
        updateButton.addActionListener(this);
        panel.add(updateButton);

        closeButton = new JButton("Close");
        closeButton.addActionListener(this);
        panel.add(closeButton);

        setContentPane(panel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == updateButton) {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            if (!firstName.isEmpty() && !lastName.isEmpty()) {
                // Update the database
                updatePerson(personID, firstName, lastName);
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == closeButton) {
            dispose(); // Close the frame
        }
    }

    private void updatePerson(String id, String firstName, String lastName) {
        try {
            SingletonFile s = SingletonFile.getInstance();
            Connection connection = s.connection;
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE person_table SET PerFname=?, PerLname=? WHERE PerID=?");
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, id);
            int rowsUpdated = preparedStatement.executeUpdate();
            preparedStatement.close();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Update successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Update failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            dispose(); // Close the frame after successful update
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating person", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
class PersonView extends JFrame implements ActionListener {
    JButton insertButton, modifyButton, viewButton, closeButton;
    JTable personTable;
    DefaultTableModel tableModel;

    PersonView() {
        setTitle("Person Listings");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.GRAY);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();                  // Get screen size
        int halfScreenWidth = screenSize.width / 2;
        int halfScreenHeight = screenSize.height / 2;

        setSize(halfScreenWidth, halfScreenHeight);                                          // Set frame size to half of the screen size

        JPanel buttonPanel = new JPanel(new FlowLayout());
        //buttonPanel.setLayout(new GridLayout(1, 4, 5, 5));
        insertButton = new JButton("Insert");
        insertButton.setFont(new Font("Arial", Font.BOLD, 18));
        insertButton.setBackground(new Color(204, 204, 255));
        insertButton.setForeground(new Color(2, 48, 32));  
        insertButton.setFocusPainted(false);
        insertButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));  // Border color black 

        modifyButton = new JButton("Modify");
        modifyButton.setFont(new Font("Arial", Font.BOLD, 18));
        modifyButton.setBackground(new Color(95, 158, 160));       //CADET BLUE
        modifyButton.setForeground(new Color(2, 48, 32));  
        modifyButton.setFocusPainted(false);
        modifyButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));  // Border color black 


        viewButton = new JButton("View");
        viewButton.setFont(new Font("Arial", Font.BOLD, 18));
        viewButton.setBackground(new Color(0, 255, 255));             // CYAN BLUE
        viewButton.setForeground(new Color(2, 48, 32));  
        viewButton.setFocusPainted(false);
        viewButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));  // Border color black 

        closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 18));
        closeButton.setBackground(new Color(222, 49, 99));             // CERISE RED
        closeButton.setForeground(new Color(2, 48, 32));  
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));  // Border color black 


        

        insertButton.setPreferredSize(new Dimension(150, 40));                    // button sizes 
        modifyButton.setPreferredSize(new Dimension(150, 40));
        viewButton.setPreferredSize(new Dimension(150, 40));
        closeButton.setPreferredSize(new Dimension(150, 40));

        insertButton.addActionListener(this);
        modifyButton.addActionListener(this);
        viewButton.addActionListener(this);
        closeButton.addActionListener(this);

        buttonPanel.add(insertButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(closeButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Last Name");
        tableModel.addColumn("First Name");

        personTable = new JTable(tableModel);
        
        personTable.setBackground(new Color(192, 192, 192));
        personTable.setForeground(new Color(2, 48, 32));
        JScrollPane scrollPane = new JScrollPane(personTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        

       
        getContentPane().add(panel);
        setVisible(true);
        populateTable();
    }

    // Method to populate data into the table
    private void populateTable() {
        try {
            // Get database connection
            SingletonFile s = SingletonFile.getInstance();
            Connection connection = s.connection;

            // Create statement
            Statement statement = connection.createStatement();

            // Execute query to fetch data from database
            ResultSet resultSet = statement.executeQuery("SELECT PerID, PerLname, PerFname FROM person_table");

            // Populate data into the table
            DefaultTableModel model = (DefaultTableModel) personTable.getModel();
            while (resultSet.next()) {
                String id = resultSet.getString("PerID");
                String lastName = resultSet.getString("PerLname");
                String firstName = resultSet.getString("PerFname");
                model.addRow(new Object[]{id, lastName, firstName});
            }

            // Close statement and result set
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred while fetching data from the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeButton) {
            closeFrame();
        }
        if (e.getSource() == insertButton) {
            new InsertForm();
            dispose();
        }
        DefaultTableModel tableModel = (DefaultTableModel) personTable.getModel();
        int selectedRow = personTable.getSelectedRow();
        if (e.getSource() == modifyButton) {
            if (selectedRow != -1) {
                String id = tableModel.getValueAt(selectedRow, 0).toString();
                String lastName = tableModel.getValueAt(selectedRow, 1).toString();
                String firstName = tableModel.getValueAt(selectedRow, 2).toString();
                new ModifyForm(id, firstName, lastName); // Open modify form with existing data
            } else {
                JOptionPane.showMessageDialog(this, "Please select a person to modify.");
        }
        if (e.getSource() == viewButton) {
            if (personTable.getSelectedRow() != -1) { // Check if a row is selected
                String id = tableModel.getValueAt(personTable.getSelectedRow(), 0).toString();
                String lastName = tableModel.getValueAt(personTable.getSelectedRow(), 1).toString();
                String firstName = tableModel.getValueAt(personTable.getSelectedRow(), 2).toString();
                new ViewFrame(id, lastName, firstName); // Display the selected person's information
            } else {
                JOptionPane.showMessageDialog(this, "Please select a person to view.");
            }
        }
         
        
    }
    }
    

    void closeFrame() {
        dispose();
        new MainFrame();
    }
}

class MainFrame extends JFrame implements ActionListener {
    JButton personsButton, exitButton;

    MainFrame() {
        setTitle("Main Menu");
        setSize(500, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.GRAY);

        personsButton = new JButton("Persons");
        personsButton.setFont(new Font("Arial", Font.BOLD, 18));
        personsButton.setBackground(new Color(192, 192, 192));
        personsButton.setForeground(new Color(2, 48, 32));  
        personsButton.setFocusPainted(false);
        personsButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));  // Border color black 

        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 18));
        exitButton.setBackground(new Color(222, 49, 99));             // CERISE RED
        exitButton.setForeground(new Color(2, 48, 32));  
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));  // Border color black 

        panel.add(personsButton);
        panel.add(exitButton);

        personsButton.addActionListener(this);
        exitButton.addActionListener(this);

        setContentPane(panel);

        // Set frame location in the middle of the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        int frameWidth = getWidth();
        int frameHeight = getHeight();
        setLocation((screenWidth - frameWidth) / 2, (screenHeight - frameHeight) / 2);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(personsButton)) {
            // Open the Persons frame
            new PersonView();
            dispose();
        } else if (e.getSource().equals(exitButton)) {
            // Exit the application
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}


class LoginDialog extends JFrame implements ActionListener {
    JLabel usernameLabel = new JLabel("Username:");
    JLabel passwordLabel = new JLabel("Password:");
    JTextField usernameField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton loginButton = new JButton("Login");

    LoginDialog() {
        setTitle("Login");
        setSize(300, 150);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        usernameLabel.setBounds(20, 20, 80, 25);
        add(usernameLabel);

        usernameField.setBounds(100, 20, 160, 25);
        add(usernameField);

        passwordLabel.setBounds(20, 50, 80, 25);
        add(passwordLabel);

        passwordField.setBounds(100, 50, 160, 25);
        add(passwordField);

        loginButton.setBounds(100, 80, 80, 25);
        add(loginButton);
        loginButton.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());
        // Check login credentials
        if (username.equals("admin") && password.equals("admin")) {
            new MainFrame();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!");
        }
    }
}

public class RunPersonVer2 {
    public static void main(String[] args) {
        new LoginDialog();
        
       SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
