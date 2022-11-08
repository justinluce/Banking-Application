import java.awt.*;
import javax.swing.*;
import java.sql.*;
import java.util.Random;

public class BankingApplication extends JFrame 
{
    private JPanel main;
    private JPanel searchPanel;
    private JPanel currentPanel;
    private JTextField firstName;
    private JTextField lastName;
    private JTextField address;
    private JTextField phoneNumber;
    private JTextField phoneNumberUpdate;
    private JTextField accountNumber;
    private JTextField balance;
    private JTextField withdrawDeposit;
    private JTextField currentMonth;
    private JTextField calculatedInterest;
    private JButton searchCustomerFinal;
    private JButton previousCustomer;
    private JButton nextCustomer;
    private JButton addCustomerFinal;
    private JButton openAccountFinal;
    private JButton removeCustomer;
    private JButton updateFinal;
    private JButton depositFinal;
    private JButton withdrawFinal;
    private JButton calculateFinal;
    private JButton back;
    
    private Connection getConnection() throws SQLException {
       try {
            Class.forName("org.sqlite.JDBC");
        } catch(ClassNotFoundException e) {
            System.err.println(e);
        }
        String dbUrl = "jdbc:sqlite:C:/Java Projects/BankingApplication/src/customers.sqlite";
        Connection connection = DriverManager.getConnection(dbUrl);
        return connection;
    }
    
    public BankingApplication() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                 IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println(e);
        }
        initComponents();
    }
    
    private void initComponents() {
        JButton searchCustomer;
        JButton addCustomer;
        JButton removeCustomerFinal;
        JButton updateCustomer;
        JButton openAccount;
        JButton deposit;
        JButton withdraw;
        JButton calculate;

        JButton exit;


        setTitle("Banking Application");
        setLocationByPlatform(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        firstName = new JTextField(10);
        lastName = new JTextField(10);
        address = new JTextField(10);
        phoneNumber = new JTextField(10);
        phoneNumberUpdate = new JTextField(10);
        accountNumber = new JTextField(10);
        balance = new JTextField(10);
        withdrawDeposit = new JTextField(10);
        calculatedInterest = new JTextField(10);
        currentMonth = new JTextField(10);
        balance.setEditable(false);
        calculatedInterest.setEditable(false);
        
        searchCustomer = new JButton("Search Customer");
        searchCustomer.addActionListener(event -> searchCustomerClicked());
        searchCustomerFinal = new JButton("Search Customer");
        searchCustomerFinal.addActionListener(event -> searchFinalClicked());
        previousCustomer = new JButton("Previous Customer");
        previousCustomer.addActionListener(event -> previousCustomerClicked());
        nextCustomer = new JButton("Next Customer");
        nextCustomer.addActionListener(event -> nextCustomerClicked());
        addCustomer = new JButton("Add Customer");
        addCustomer.addActionListener(event -> addCustomerClicked());
        addCustomerFinal = new JButton("Add Customer");
        addCustomerFinal.addActionListener(event -> addCustomerFinalClicked());
        removeCustomer = new JButton("Remove Customer");
        removeCustomer.addActionListener(event -> removeCustomerClicked());
        removeCustomerFinal = new JButton("Remove Customer");
        removeCustomerFinal.addActionListener(event -> removeCustomerFinalClicked());
        updateCustomer = new JButton("Update Customer");
        updateCustomer.addActionListener(event -> updateCustomerClicked());
        updateFinal = new JButton("Update");
        updateFinal.addActionListener(event -> updateFinalClicked());
        openAccount = new JButton("Open Account");
        openAccount.addActionListener(event -> openAccountClicked());
        openAccountFinal = new JButton("Open Account");
        openAccountFinal.addActionListener(event -> openAccountFinalClicked());
        deposit = new JButton("Deposit");
        deposit.addActionListener(event -> depositClicked());
        depositFinal = new JButton("Deposit");
        depositFinal.addActionListener(event -> depositFinalClicked());
        withdraw = new JButton("Withdraw");
        withdraw.addActionListener(event -> withdrawClicked());
        withdrawFinal = new JButton("Withdraw");
        withdrawFinal.addActionListener(event -> withdrawFinalClicked());
        calculate = new JButton("Calculate Interest");
        calculate.addActionListener(event -> calculateButtonClicked());
        calculateFinal = new JButton("Calculate");
        calculateFinal.addActionListener(event -> calculateFinalClicked());
        back = new JButton("Back");
        back.addActionListener(event -> backButtonClicked(currentPanel));
        exit = new JButton("Exit");
        exit.addActionListener(event -> exitButtonClicked());
        
        main = new JPanel();
        main.setLayout(new GridBagLayout());
        main.add(searchCustomer, getConstraints(0, 0));
        main.add(addCustomer, getConstraints(0, 1));
        main.add(removeCustomer, getConstraints(0, 2));
        main.add(updateCustomer, getConstraints(0, 3));
        main.add(openAccount, getConstraints(0, 4));
        main.add(deposit, getConstraints(0, 5));
        main.add(withdraw, getConstraints(0, 6));
        main.add(calculate, getConstraints(0, 7));
        main.add(exit, getConstraints(0, 8));
        
        add(main, BorderLayout.CENTER);
        setVisible(true);
    }
    
    private GridBagConstraints getConstraints(int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 5, 0, 5);
        c.gridx = x;
        c.gridy = y;
        return c;
    }
  
    private void searchCustomerClicked() {
        searchPanel = new JPanel();
        searchPanel.setLayout(new GridBagLayout());
        searchPanel.add(new JLabel("Enter the customer's first name"), getConstraints(0, 0));
        searchPanel.add(firstName, getConstraints(1, 0));
        searchPanel.add(new JLabel("Enter the customer's last name"), getConstraints(0, 1));
        searchPanel.add(lastName, getConstraints(1, 1));
        searchPanel.add(searchCustomerFinal, getConstraints(1, 2));
        searchPanel.add(back, getConstraints(0, 2));
        
        currentPanel = searchPanel;
        add(currentPanel);
        setVisible(true);
        main.setVisible(false);
    }
    
    private void searchFinalClicked() {
        if (firstName.getText().isEmpty() || lastName.getText().isEmpty()) {
            String message = "You must enter their first and last names.";
            String title = "Invalid entry";
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
            return;
        }
        String sqlAccount = 
            "SELECT Accounts.AccountNumber, Accounts.Balance, Accounts.InterestRate FROM Accounts INNER JOIN Customers "
                + "ON Accounts.AccountNumber=Customers.AccountNumber WHERE FirstName = ? AND LastName = ?";
        String sqlCustomer = "SELECT * FROM Customers WHERE FirstName = ? AND LastName = ?";
        PreparedStatement psAccount;
        PreparedStatement psCustomer;
        ResultSet rsAccount;
        ResultSet rsCustomer;
        String first = "";
        String last = "";
        String ad = "";
        int phone = 0;
        int number = 0;
        double bal = 0;
        double interest = 0;
        try (Connection connection = getConnection()) {
            psCustomer = connection.prepareStatement(sqlCustomer);
            psCustomer.setString(1, firstName.getText());
            psCustomer.setString(2, lastName.getText());
            psAccount = connection.prepareStatement(sqlAccount);
            psAccount.setString(1, firstName.getText());
            psAccount.setString(2, lastName.getText());
            rsAccount = psAccount.executeQuery();
            while (rsAccount.next()) {
                bal = rsAccount.getDouble("Balance");
                interest = rsAccount.getDouble("InterestRate");
                number = rsAccount.getInt("AccountNumber");
            }
            rsCustomer = psCustomer.executeQuery();
            while (rsCustomer.next()) {
                first = rsCustomer.getString("FirstName");
                last = rsCustomer.getString("LastName");
                ad = rsCustomer.getString("Address");
                phone = rsCustomer.getInt("PhoneNumber");
            }
            try {
                JPanel searchFinalPanel;
                rsAccount.getClob("AccountNumber");
                searchFinalPanel = new JPanel();
                searchFinalPanel.setLayout(new GridBagLayout());
                searchFinalPanel.add(new JLabel("Which customer did you want to view?"), getConstraints(0 ,0));
                searchFinalPanel.add(new JLabel("First Name: " + first), getConstraints(0, 1));
                searchFinalPanel.add(new JLabel("Last Nane: " + last), getConstraints(0, 2));
                searchFinalPanel.add(new JLabel("Address: " + ad), getConstraints(0, 3));
                searchFinalPanel.add(new JLabel("Phone Number: " + phone), getConstraints(0, 4));
                searchFinalPanel.add(previousCustomer, getConstraints(0, 5));
                searchFinalPanel.add(nextCustomer, getConstraints(1, 5));
                System.out.println("Success.");
                
                add(searchFinalPanel);
                setVisible(true);
                searchPanel.setVisible(false);
            } catch (Exception x) {
                  JOptionPane.showMessageDialog(null, "Account Number: " + number + "\nBalance: " + bal + "\nInterest Rate: " + interest,
                    "Successful Search", JOptionPane.DEFAULT_OPTION);
                connection.close();
            }                 
        } catch (SQLException e) {
        	String message = "That customer does not exist.";
        	String title = "Invalid entry";
        	JOptionPane.showMessageDialog(this,  message, title, JOptionPane.ERROR_MESSAGE);
            System.err.println(e);
        }
    }
    
    private void nextCustomerClicked() {
        
    }
    
    private void previousCustomerClicked() {
        
    }
    
    private void addCustomerClicked() {
        JPanel addCustomerPanel;
        addCustomerPanel = new JPanel();
        addCustomerPanel.setLayout(new GridBagLayout());
        addCustomerPanel.add(new JLabel("First Name: "), getConstraints(0, 0));
        addCustomerPanel.add(firstName, getConstraints(1, 0));
        addCustomerPanel.add(new JLabel("Last Name: "), getConstraints(0, 1));
        addCustomerPanel.add(lastName, getConstraints(1, 1));
        addCustomerPanel.add(new JLabel("Address:"), getConstraints(0, 2));
        addCustomerPanel.add(address, getConstraints(1, 2));
        addCustomerPanel.add(new JLabel("Phone Number: "), getConstraints(0, 3));
        addCustomerPanel.add(phoneNumber, getConstraints(1, 3));
        addCustomerPanel.add(addCustomerFinal, getConstraints(1, 4));
        addCustomerPanel.add(back, getConstraints(0, 4));
        
        currentPanel = addCustomerPanel;
        add(currentPanel);
        setVisible(true);
        main.setVisible(false);
    }
    
    private void addCustomerFinalClicked() {
        if (firstName.getText().isEmpty() || lastName.getText().isEmpty() || address.getText().isEmpty() || phoneNumber.getText().isEmpty()) {
            String message = "You must input a first name, last name, address, and phone number.";
            String title = "Invalid entry";
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (phoneNumber.getText().length() != 10) {
            String message = "You must enter a valid phone number.";
            String title = "Invalid entry";
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        } else {
            String sql = "INSERT INTO Customers VALUES (?, ?, ?, ?, NULL)";
            String sql2 = "SELECT * FROM Customers WHERE PhoneNumber = ?";
            try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                PreparedStatement ps2 = connection.prepareStatement(sql2)) {
                ps.setString(1, firstName.getText());
                ps.setString(2, lastName.getText());
                ps.setString(3, address.getText());
                ps.setString(4, phoneNumber.getText());
                ps2.setString(1, phoneNumber.getText());
                try {
                    ps2.executeQuery();
                    ps.executeUpdate();
                    String message = "Successfully added the customer to the database.";
                    String title = "Success!";
                    JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
                    connection.close();

                } catch (SQLException e) {
                    System.err.println(e);
                    String message = "Account already exists.";
                    String title = "Error";
                    JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException t) {
                System.err.println(t);
            }

        }
    }
    
    private void removeCustomerClicked() {
        JPanel removeCustomerPanel;
    	removeCustomerPanel = new JPanel();
    	removeCustomerPanel.setLayout(new GridBagLayout());
    	removeCustomerPanel.add(new JLabel("Enter the Phone number for the customer to be deleted."), getConstraints(0, 0));
    	removeCustomerPanel.add(phoneNumber, getConstraints(1, 0));
    	removeCustomerPanel.add(back, getConstraints(0, 1));
    	removeCustomerPanel.add(removeCustomer, getConstraints(1, 1));
    	
    	currentPanel = removeCustomerPanel;
    	add(currentPanel);
    	setVisible(true);
    	main.setVisible(false);
    }
    
    private void removeCustomerFinalClicked() {
    	if (phoneNumber.getText().isBlank() || phoneNumber.getText().length() != 10) {
    		String message = "You must input a valid phone number.";
    		String title = "Invalid Entry";
    		JOptionPane.showMessageDialog(this,  message, title, JOptionPane.ERROR_MESSAGE);
    	} else {
    		String sql = "DELETE FROM Customers WHERE PhoneNumber = ?";
    		try (Connection connection = getConnection();
    			PreparedStatement ps = connection.prepareStatement(sql)) {
    			ps.setString(1,  phoneNumber.getText());
    			try {
    				ps.executeUpdate();
    				String message = "Successfully deleted customer from the database.";
    				String title = "Success!";
    				JOptionPane.showMessageDialog(this,  message, title, JOptionPane.INFORMATION_MESSAGE);
    				connection.close();
    			} catch (SQLException e) {
    				System.err.println(e);
    				String message = "Account does not exist.";
					String title = "Error";
					JOptionPane.showMessageDialog(this,  message, title, JOptionPane.ERROR_MESSAGE);
    			}
    		} catch (SQLException t) {
    			System.err.println(t);
    			
    		}
    	}
    }
    
    private void openAccountClicked() {
        JPanel openAccountPanel;
        openAccountPanel = new JPanel();
        openAccountPanel.setLayout(new GridBagLayout());
        openAccountPanel.add(new JLabel("Enter the phone number of the customer:"), getConstraints(0 ,0));
        openAccountPanel.add(phoneNumber, getConstraints(1, 0));
        openAccountPanel.add(openAccountFinal, getConstraints(1, 1));
        openAccountPanel.add(back, getConstraints(0, 1));
        
        currentPanel = openAccountPanel;
        add(currentPanel);
        setVisible(true);
        main.setVisible(false);
    }
    
    private void openAccountFinalClicked() {
        Random rand = new Random();
        int upperBound = 100000;
        int number = rand.nextInt(upperBound);
        if (phoneNumber.getText().length() != 10) {
            String message = "You must enter a valid phone number.";
            String title = "Invalid entry";
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        } else {
            String getCustomer = "SELECT PhoneNumber FROM Customers WHERE PhoneNumber = ?";
            String getAccount = "INSERT INTO Accounts VALUES (" + number + ", 0, .3)";
            String addToCustomer = "UPDATE Customers SET AccountNumber = " + number + " WHERE Customers.PhoneNumber = " + phoneNumber.getText();
            try (Connection connection = getConnection();
                PreparedStatement psCustomer = connection.prepareStatement(getCustomer);
                PreparedStatement psAccount = connection.prepareStatement(getAccount);
                PreparedStatement psAddToCustomer = connection.prepareStatement(addToCustomer)) {
                psCustomer.setString(1, phoneNumber.getText());
                psCustomer.executeQuery();
                psAccount.executeUpdate();
                psAddToCustomer.executeUpdate();
                String message = "Succesfully opened the account";
                String title = "Succes!";
                JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                System.out.println(addToCustomer);
                System.err.println(e);
                String message = "This customer does not exist.";
                String title = "Invalid entry";
                JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateCustomerClicked() {
        JPanel updatePanel;
        updatePanel = new JPanel();
        updatePanel.setLayout(new GridBagLayout());
        updatePanel.add(new JLabel("What information would you like to update?"), getConstraints(0, 0));
        updatePanel.add(new JLabel("First Name"), getConstraints(0, 1));
        updatePanel.add(firstName, getConstraints(1, 1));
        updatePanel.add(new JLabel("Last Name"), getConstraints(0, 2));
        updatePanel.add(lastName, getConstraints(1, 2));
        updatePanel.add(new JLabel("Address"), getConstraints(0, 3));
        updatePanel.add(address, getConstraints(1, 3));
        updatePanel.add(new JLabel("Phone Number"), getConstraints(0, 4));
        updatePanel.add(phoneNumber, getConstraints(1, 4));
        updatePanel.add(new JLabel("Please enter the current Phone Number for whom you would like to update."), getConstraints(0, 5));
        updatePanel.add(new JLabel("Phone Number"), getConstraints(0, 6));
        updatePanel.add(phoneNumberUpdate, getConstraints(1, 6));
        updatePanel.add(updateFinal, getConstraints(1, 7));
        updatePanel.add(back, getConstraints(0, 7));
        
        currentPanel = updatePanel;
        add(currentPanel);
        setVisible(true);
        main.setVisible(false);
    }

    private void updateFinalClicked() {
        if (firstName.getText().isEmpty() || lastName.getText().isEmpty() || address.getText().isEmpty() || phoneNumber.getText().isEmpty()) {
            String message = "You must input values into the first four fields.";
            String title = "Invalid entry";
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (phoneNumber.getText().length() != 10 || phoneNumberUpdate.getText().length() != 10) {
            String message = "Phone number must be valid.";
            String title = "Invalid entry";
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        } else {
            String sql = "UPDATE Customers SET FirstName = ?, LastName = ?, Address = ?, PhoneNumber = ? WHERE PhoneNumber = ? ";
            try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, firstName.getText());
                ps.setString(2, lastName.getText());
                ps.setString(3, address.getText());
                ps.setString(4, phoneNumber.getText());
                ps.setString(5, phoneNumberUpdate.getText());
                ps.executeUpdate();
                String message = "Successfully updated the customer's information.";
                String title = "Success!";
                JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }
    
    private void depositClicked() {
        JPanel depositPanel;
        depositPanel = new JPanel();
        depositPanel.setLayout(new GridBagLayout());
        depositPanel.add(new JLabel("Enter the amount to deposit."), getConstraints(0, 0));
        depositPanel.add(withdrawDeposit, getConstraints(1, 0));
        depositPanel.add(new JLabel("Enter the account number for the deposit."), getConstraints(0, 1));
        depositPanel.add(accountNumber, getConstraints(1, 1));
        depositPanel.add(depositFinal, getConstraints(1, 2));
        depositPanel.add(back, getConstraints(0, 2));
        
        currentPanel = depositPanel;
        add(currentPanel);
        setVisible(true);
        main.setVisible(false);
    }
    
    private void depositFinalClicked() {
        if (withdrawDeposit.getText().isEmpty()) {
            String message = "You must enter an amount to deposit.";
            String title = "Invalid entry";
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        } else {
            String sql = "UPDATE Accounts SET Balance = Balance + ? WHERE Accounts.AccountNumber = ?";
            try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, withdrawDeposit.getText());
                ps.setString(2, accountNumber.getText());
                ps.executeUpdate();
                String message = "Successfully deposited.";
                String title = "Success!";
                JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }
    
    private void withdrawClicked() {
        JPanel withdrawPanel;
        withdrawPanel = new JPanel();
        withdrawPanel.setLayout(new GridBagLayout());
        withdrawPanel.add(new JLabel("Enter the amount to withdraw."), getConstraints(0, 0));
        withdrawPanel.add(withdrawDeposit, getConstraints(1, 0));
        withdrawPanel.add(new JLabel("Enter the account number for the withdraw."), getConstraints(0, 1));
        withdrawPanel.add(accountNumber, getConstraints(1, 1));
        withdrawPanel.add(withdrawFinal, getConstraints(1, 2));
        withdrawPanel.add(back, getConstraints(0, 2));
        
        currentPanel = withdrawPanel;
        add(currentPanel);
        setVisible(true);
        main.setVisible(false);
    }
    
    private void withdrawFinalClicked() {
        if (withdrawDeposit.getText().isEmpty()) {
            String message = "You must enter an amount to withdraw.";
            String title = "Invalid entry";
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        } else {
            String sql = "UPDATE Accounts SET Balance = Balance - ? WHERE Accounts.AccountNumber = ?";
            try (Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, withdrawDeposit.getText());
                ps.setString(2, accountNumber.getText());
                ps.executeUpdate();
                String message = "Successfully withdrew.";
                String title = "Success!";
                JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }
    
    private void calculateButtonClicked() {
        JPanel calculatePanel;
        calculatePanel = new JPanel();
        calculatePanel.setLayout(new GridBagLayout());
        calculatePanel.add(new JLabel("Enter the account number of the account you would like to calculate:"), getConstraints(0, 0));
        calculatePanel.add(accountNumber, getConstraints(0, 1));
        calculatePanel.add(new JLabel("Enter the current month:"), getConstraints(0, 2));
        calculatePanel.add(currentMonth, getConstraints(0, 3));
        calculatePanel.add(calculateFinal, getConstraints(1, 4));
        calculatePanel.add(back, getConstraints(0, 4));
        
        currentPanel = calculatePanel;
        add(currentPanel);
        setVisible(true);
        main.setVisible(false);
    }
    
    private void calculateFinalClicked() {
        if (accountNumber.getText().isEmpty() || accountNumber.getText().length() != 5) {
            String message = "You must enter a valid phone number.";
            String title = "Invalid entry";
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        } else if (currentMonth.getText().isEmpty()
                || Integer.valueOf(currentMonth.getText()) < 0
                || Integer.valueOf(currentMonth.getText()) > 12) {
                String message = "You must enter a valid month.";
                String title = "invalid entry";
                JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
            }
        else {
            double bal = 0;
            double interest = 0;
            double totalInterest = 0;
            ResultSet rs;
            PreparedStatement ps;
            String sql = "SELECT * FROM Accounts WHERE AccountNumber = ?";
            try (Connection connection = getConnection()) {
                ps = connection.prepareStatement(sql);
                ps.setString(1, accountNumber.getText());
                rs = ps.executeQuery();
                while (rs.next()) {
                    bal = rs.getDouble("Balance");
                    interest = rs.getDouble("InterestRate");
                }
                totalInterest = bal * interest * Integer.valueOf(currentMonth.getText());
                JOptionPane.showMessageDialog(null, "Your total interest is: " + totalInterest,
                    "Successful Search", JOptionPane.DEFAULT_OPTION);
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }
    
    private void backButtonClicked(JPanel currentPanel) {
        currentPanel.setVisible(false);
        main.setVisible(true);
    }
    
    private void exitButtonClicked() {
        System.exit(0);
    }
    
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            JFrame frame = new BankingApplication();
            frame.setSize(500, 500);
        });        
    }
}
