import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class View {
  final private static String DB_URL = "jdbc:mysql://127.0.0.1:3306/project";
  final private static String DB_USER = "root";
  final private static String DB_PASSWORD = "EricDavis44!";

  final private static int LOG_IN_OFF_TAB = 0;
  final private static int PERSONAL_INFO_TAB = 1;
  final private static int VEHICLES_TAB = 2;
  final private static int REGISTRATIONS_TAB = 3;
  final private static int CREDIT_CARDS_TAB = 4;
  final private static int BOOK_ONE_WAY_TAB = 5;
  final private static int TRIP_HISTORY_TAB = 6;
  final private static int VEHICLE_HISTORY_TAB = 7;

  private Connection connection;
  private CallableStatement getPersonID;
  private CallableStatement createAccount;
  private ResultSet resultSet;
  private Integer personID = null;

  private JFrame frame;
  private JScrollPane scrollPane;
  private JTabbedPane tabbedPane;
  private JPanel loginPanel;
  private JPanel northPanel;
  private ProjectRenderer projectRenderer = new ProjectRenderer();
  private PersonalInfoPanel personalInfoPanel;
  private VehiclesPanel vehiclesPanel;
  private RegistrationsPanel registrationsPanel;
  private CreditCardsPanel creditCardsPanel;
  private BookOneWayPanel bookOneWayPanel;
  private TripHistoryPanel tripHistoryPanel;
  private VehicleHistoryPanel vehicleHistoryPanel;

  // login panel components
  private JLabel emailLabel;
  private JTextField emailTextField;
  private JLabel passwordLabel;
  private JPasswordField passwordTextField;
  private JButton loginButton;
  private JLabel privilegesLabel;
  private JButton logoffButton;

  // account creation components
  private JLabel firstNameLabel;
  private JTextField firstNameTextField;
  private JLabel lastNameLabel;
  private JTextField lastNameTextField;
  private JLabel blankLabel1;
  private JLabel phoneNumberLabel;
  private JTextField phoneNumberTextField;
  private JLabel blankLabel2;
  private JLabel blankLabel3;
  private JButton createAccountButton;

  public static void main(String[] args) {
    try {
      SwingUtilities.invokeAndWait(new Runnable() {
        public void run() {
          new View();
        }
      });
    } catch (Exception exc) {
      System.out.println("Exception encountered: " + exc.getCause());
    }
  }

  View() {
    try {
      this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
      this.getPersonID = this.connection.prepareCall("{call get_person_id(?, ?)}");
      this.createAccount = this.connection.prepareCall("{call create_account(?, ?, ?, ?, ?)}");
    } catch (SQLException e) {
      System.out.println("failed to make a connection to the DB");
      e.printStackTrace();
    }

    // create tabbed UI
    this.frame = new JFrame("AutonomousRideSharing");
    this.frame.setSize(1500, 900);
    this.tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

    // create login panel
    createLogin();

    // add change listener
    this.tabbedPane.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent ce) {
        int selectedTabIndex = ((JTabbedPane) ce.getSource()).getSelectedIndex();

        if (personID != null) {
          switch (selectedTabIndex) {
            case PERSONAL_INFO_TAB:
              resetPersonalInfoPanel();
              break;
            case VEHICLES_TAB:
              resetVehiclesPanel();
              break;
            case REGISTRATIONS_TAB:
              resetRegistrationsPanel();
              break;
            case CREDIT_CARDS_TAB:
              resetCreditCardsPanel();
              break;
            case BOOK_ONE_WAY_TAB:
              resetBookOneWayPanel();
              break;
            case TRIP_HISTORY_TAB:
              resetTripHistoryPanel();
              break;
            case VEHICLE_HISTORY_TAB:
              resetVehicleHistoryPanel();
              break;
          }
        }
      }
    });

    this.scrollPane = new JScrollPane(this.tabbedPane);
    this.frame.add(this.scrollPane);
    this.frame.setVisible(true);
  }

  private void createLogin() {
    this.emailLabel = new JLabel("email address: ", SwingConstants.RIGHT);
    this.emailTextField = new JTextField(20);
    this.passwordLabel = new JLabel("password: ", SwingConstants.RIGHT);
    this.passwordTextField = new JPasswordField(20);
    this.loginButton = new JButton("login");

    this.loginButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        // get user input
        String newEmail = emailTextField.getText().trim();
        if (newEmail.length() > 45) {
          newEmail = newEmail.substring(0, 45);
        }

        String newPassword = new String(passwordTextField.getPassword()).trim();
        if (newPassword.length() > 45) {
          newPassword = newPassword.substring(0, 45);
        }

        // called stored procedure with user input
        try {
          getPersonID.setString(1, newEmail);
          getPersonID.setString(2, newPassword);

          if (getPersonID.execute()) {
            resultSet = getPersonID.getResultSet();
            resultSet.last();

            if (resultSet.getRow() == 0) {
              System.out.println("empty result set");
            } else {
              resultSet.first();
              personID = resultSet.getInt(1);
            }
          }
        } catch (SQLException e) {
          e.printStackTrace();
          return;
        }

        // create tabs if log-in successful
        if (personID != null) { // successful log-in
          createLogoff();
          tabbedPane.addTab("Personal Info", new JPanel().add(new JLabel("sorry, but you don't have permission to access this tab")));
          tabbedPane.addTab("Vehicles", new JPanel().add(new JLabel("sorry, but you don't have permission to access this tab")));
          tabbedPane.addTab("Registrations", new JPanel().add(new JLabel("sorry, but you don't have permission to access this tab")));
          tabbedPane.addTab("Credit Cards", new JPanel().add(new JLabel("sorry, but you don't have permission to access this tab")));
          tabbedPane.addTab("Book One-Way Trip", new JPanel().add(new JLabel("sorry, but you don't have permission to access this tab")));
          tabbedPane.addTab("Trip History", new JPanel().add(new JLabel("sorry, but you don't have permission to access this tab")));
          tabbedPane.addTab("Vehicle History", new JPanel().add(new JLabel("sorry, but you don't have permission to access this tab")));
        } else {  // unsuccessful log-in
          emailTextField.setText("");
          passwordTextField.setText("");
          revalidateANDrepaint();
          JOptionPane.showMessageDialog(loginPanel, "The credentials you entered do not match any in the system; please try again.", "Log-in Unsuccessful", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    this.firstNameLabel = new JLabel("first name: ", SwingConstants.RIGHT);
    this.firstNameTextField = new JTextField(20);
    this.lastNameLabel = new JLabel("last name: ", SwingConstants.RIGHT);
    this.lastNameTextField = new JTextField(20);
    this.blankLabel1 = new JLabel();
    this.phoneNumberLabel = new JLabel("phone number: ", SwingConstants.RIGHT);
    this.phoneNumberTextField = new JTextField(20);
    this.blankLabel2 = new JLabel();
    this.blankLabel3 = new JLabel();
    this.createAccountButton = new JButton("create account");

    this.createAccountButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        // get user input
        String newEmail = emailTextField.getText().trim();
        if (newEmail.length() > 45) {
          newEmail = newEmail.substring(0, 45);
        }

        String newPassword = new String(passwordTextField.getPassword()).trim();
        if (newPassword.length() > 45) {
          newPassword = newPassword.substring(0, 45);
        }

        String newFirstName = firstNameTextField.getText().trim();
        if (newFirstName.length() > 45) {
          newFirstName = newFirstName.substring(0, 45);
        }

        String newLastName = lastNameTextField.getText().trim();
        if (newLastName.length() > 45) {
          newLastName = newLastName.substring(0, 45);
        }

        String newPhoneNumber = phoneNumberTextField.getText().trim();
        if (newPhoneNumber.length() > 45) {
          newPhoneNumber = newPhoneNumber.substring(0, 45);
        }

        // called stored procedure with user input
        try {
          createAccount.setString(1, newFirstName);
          createAccount.setString(2, newLastName);
          createAccount.setString(3, newEmail);
          createAccount.setString(4, newPassword);
          createAccount.setString(5, newPhoneNumber);

          if (createAccount.execute()) {
            resultSet = createAccount.getResultSet();
            resultSet.first();
            String message = resultSet.getString(1);
            emailTextField.setText("");
            passwordTextField.setText("");
            firstNameTextField.setText("");
            lastNameTextField.setText("");
            phoneNumberTextField.setText("");
            JOptionPane.showMessageDialog(loginPanel, message, "SQL Exception Encountered", JOptionPane.ERROR_MESSAGE);
            return;
          }

          loginButton.doClick();
        } catch (SQLException e) {
          JOptionPane.showMessageDialog(loginPanel, e.toString(), "SQL Exception Encountered", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    this.northPanel = new JPanel();
    this.northPanel.setLayout(new GridLayout(3, 5));

    this.northPanel.add(this.emailLabel);
    this.northPanel.add(this.emailTextField);
    this.northPanel.add(this.passwordLabel);
    this.northPanel.add(this.passwordTextField);
    this.northPanel.add(this.loginButton);

    this.northPanel.add(this.firstNameLabel);
    this.northPanel.add(this.firstNameTextField);
    this.northPanel.add(this.lastNameLabel);
    this.northPanel.add(this.lastNameTextField);
    this.northPanel.add(this.blankLabel1);

    this.northPanel.add(this.phoneNumberLabel);
    this.northPanel.add(this.phoneNumberTextField);
    this.northPanel.add(this.blankLabel2);
    this.northPanel.add(this.blankLabel3);
    this.northPanel.add(this.createAccountButton);

    this.loginPanel = new JPanel();
    this.loginPanel.setLayout(new BorderLayout());
    this.loginPanel.add(this.northPanel, BorderLayout.NORTH);
    this.tabbedPane.addTab("Log In/Off", this.loginPanel);
    this.frame.getRootPane().setDefaultButton(this.loginButton);
  }

  void createLogoff() {
    this.privilegesLabel = new JLabel("presently logged in with " + (this.personID == 1 ? "admin" : "user") + " privileges");

    this.logoffButton = new JButton("logoff");

    this.logoffButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        personID = null;
        tabbedPane.removeAll();
        createLogin();
      }
    });

    this.loginPanel = new JPanel();
    this.loginPanel.add(this.privilegesLabel);
    this.loginPanel.add(this.logoffButton);

    this.tabbedPane.setComponentAt(LOG_IN_OFF_TAB, this.loginPanel);
    this.frame.getRootPane().setDefaultButton(this.logoffButton);
  }

  void revalidateANDrepaint() {
    if (this.emailLabel != null) {
      this.emailLabel.revalidate();
      this.emailLabel.repaint();
    }
    if (this.emailTextField != null) {
      this.emailTextField.revalidate();
      this.emailTextField.repaint();
    }
    if (this.passwordLabel != null) {
      this.passwordLabel.revalidate();
      this.passwordLabel.repaint();
    }
    if (this.passwordTextField != null) {
      this.passwordTextField.revalidate();
      this.passwordTextField.repaint();
    }
    if (this.loginButton != null) {
      this.loginButton.revalidate();
      this.loginButton.repaint();
    }
    if (this.privilegesLabel != null) {
      this.privilegesLabel.revalidate();
      this.privilegesLabel.repaint();
    }
    if (this.logoffButton != null) {
      this.logoffButton.revalidate();
      this.logoffButton.repaint();
    }
  }

  void resetPersonalInfoPanel() {
    this.personalInfoPanel = new PersonalInfoPanel(this);
    this.tabbedPane.setComponentAt(PERSONAL_INFO_TAB, this.personalInfoPanel.getPanel());
  }

  void resetVehiclesPanel() {
    this.vehiclesPanel = new VehiclesPanel(this);
    this.tabbedPane.setComponentAt(VEHICLES_TAB, this.vehiclesPanel.getPanel());
  }

  void resetRegistrationsPanel() {
    this.registrationsPanel = new RegistrationsPanel(this);
    this.tabbedPane.setComponentAt(REGISTRATIONS_TAB, this.registrationsPanel.getPanel());
  }

  void resetCreditCardsPanel() {
    this.creditCardsPanel = new CreditCardsPanel(this);
    this.tabbedPane.setComponentAt(CREDIT_CARDS_TAB, this.creditCardsPanel.getPanel());
  }

  void resetBookOneWayPanel() {
    this.bookOneWayPanel = new BookOneWayPanel(this);
    this.tabbedPane.setComponentAt(BOOK_ONE_WAY_TAB, this.bookOneWayPanel.getPanel());
  }

  void resetTripHistoryPanel() {
    this.tripHistoryPanel = new TripHistoryPanel(this);
    this.tabbedPane.setComponentAt(TRIP_HISTORY_TAB, this.tripHistoryPanel.getPanel());
  }

  void resetVehicleHistoryPanel() {
    this.vehicleHistoryPanel = new VehicleHistoryPanel(this);
    this.tabbedPane.setComponentAt(VEHICLE_HISTORY_TAB, this.vehicleHistoryPanel.getPanel());
  }

  Connection getConnection() { return this.connection; }
  int getPersonID() { return this.personID; }
  PersonalInfoPanel getPersonalInfoPanel() { return this.personalInfoPanel; }
  VehiclesPanel getVehiclesPanel() { return this.vehiclesPanel; }
  CreditCardsPanel getCreditCardsPanel() { return this.creditCardsPanel; }
  BookOneWayPanel getBookOneWayPanel() { return this.bookOneWayPanel; }
  TripHistoryPanel getTripHistoryPanel() { return this.tripHistoryPanel; }
  VehicleHistoryPanel getVehicleHistoryPanel() { return this.vehicleHistoryPanel; }
  ProjectRenderer getProjectRenderer() { return this.projectRenderer; }
}