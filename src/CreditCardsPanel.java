import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import javax.swing.*;

public class CreditCardsPanel extends AbstractPanel {
  private final String[] CC_TYPES = { "AMERICAN EXPRESS", "CAPITAL ONE", "CHASE", "CITIBANK", "DISCOVER", "MASTERCARD", "VISA" };
  private final String[] MONTHS = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };
  private final String[] YEARS = { "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026" };

  private View view;

  private CallableStatement addCreditCard;

  //central components
  private JScrollPane tablePane;
  private JTable table;
  private CreditCardsTableModel model;

  //southern components: creditCardName varChar(45), creditCardNumber BIGINT , CVV int, ccType varChar(45), expirationMonth int, expirationYear int
  private JLabel nameLabel;
  private JTextField nameTextField;
  private JLabel numberLabel;
  private JTextField numberTextField;
  private JLabel cvvLabel;
  private JTextField cvvTextField;
  private JLabel typeLabel;
  private JComboBox typeComboBox;
  private JLabel expirationMonthLabel;
  private JComboBox expirationMonthComboBox;
  private JLabel expirationYearLabel;
  private JComboBox expirationYearComboBox;
  private JLabel blankLabel1;
  private JLabel blankLabel2;
  private JLabel blankLabel3;
  private JButton addCreditCardButton;

  private boolean errorEncountered;
  private String errorMessage;

  CreditCardsPanel(View view) {
    super();

    this.view = view;

    try {
      this.addCreditCard = view.getConnection().prepareCall("{ call add_credit_card(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
    } catch (SQLException e) {
      e.printStackTrace();
    }

    createNorthPane();
    createCenterPane();
    createSouthPane();
  }

  @Override
  void createNorthPane() {

  }

  @Override
  void createCenterPane() {
    if (mainPanel != null && centerPane != null) mainPanel.remove(centerPane);
    if (centerPane != null && centerPanel != null) centerPane.remove(centerPanel);
    if (centerPanel != null && tablePane != null) centerPanel.remove(tablePane);
    if (tablePane != null && table != null) tablePane.remove(table);

    //create table model
    try {
      model = new CreditCardsTableModel(view);
    } catch (SQLException sqlException) {
      System.out.println(sqlException.toString());
    }

    //create table
    table = new JTable(model);
    table.setDefaultRenderer(Object.class, this.view.getProjectRenderer());
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setColumnSelectionAllowed(true);
    table.setShowGrid(false);

    //situate table
    tablePane = new JScrollPane(table);
    tablePane.setPreferredSize(new Dimension(FULL_SCREEN));
    centerPanel = new JPanel();
    centerPanel.add(tablePane);
    centerPane = new JScrollPane(centerPanel);

    if (mainPanel != null && centerPane != null) mainPanel.add(centerPane, BorderLayout.CENTER);
  }

  @Override
  void createSouthPane() {
    //remove all existing south components
    if (southPane != null && mainPanel != null) {
      mainPanel.remove(southPane);
    }
    if (southPanel != null && southPane != null) {
      southPane.remove(southPanel);
    }
    if (nameLabel != null && southPanel != null) {
      southPanel.remove(nameLabel);
    }
    if (nameTextField != null && southPanel != null) {
      southPanel.remove(nameTextField);
    }
    if (numberLabel != null && southPanel != null) {
      southPanel.remove(numberLabel);
    }
    if (numberTextField != null && southPanel != null) {
      southPanel.remove(numberTextField);
    }
    if (cvvLabel != null && southPanel != null) {
      southPanel.remove(cvvLabel);
    }
    if (cvvTextField != null && southPanel != null) {
      southPanel.remove(cvvTextField);
    }
    if (typeLabel != null && southPanel != null) {
      southPanel.remove(typeLabel);
    }
    if (typeComboBox != null && southPanel != null) {
      southPanel.remove(typeComboBox);
    }
    if (expirationMonthLabel != null && southPanel != null) {
      southPanel.remove(expirationMonthLabel);
    }
    if (expirationMonthComboBox != null && southPanel != null) {
      southPanel.remove(expirationMonthComboBox);
    }
    if (expirationYearLabel != null && southPanel != null) {
      southPanel.remove(expirationYearLabel);
    }
    if (expirationYearComboBox != null && southPanel != null) {
      southPanel.remove(expirationYearComboBox);
    }
    if (blankLabel1 != null && southPanel != null) {
      southPanel.remove(blankLabel1);
    }
    if (blankLabel2 != null && southPanel != null) {
      southPanel.remove(blankLabel2);
    }
    if (blankLabel3 != null && southPanel != null) {
      southPanel.remove(blankLabel3);
    }
    if (addCreditCardButton != null && southPanel != null) {
      southPanel.remove(addCreditCardButton);
    }

    southPanel = new JPanel();

    nameLabel = new JLabel("NAME:", SwingConstants.RIGHT);
    southPanel.add(nameLabel);

    nameTextField = new JTextField(20);
    southPanel.add(nameTextField);

    numberLabel = new JLabel("NUMBER:", SwingConstants.RIGHT);
    southPanel.add(numberLabel);

    numberTextField = new JTextField(20);
    southPanel.add(numberTextField);

    cvvLabel = new JLabel("CVV:", SwingConstants.RIGHT);
    southPanel.add(cvvLabel);

    cvvTextField = new JTextField(20);
    southPanel.add(cvvTextField);

    typeLabel = new JLabel("TYPE:", SwingConstants.RIGHT);
    southPanel.add(typeLabel);

    typeComboBox = new JComboBox(CC_TYPES);
    southPanel.add(typeComboBox);

    typeComboBox.setSelectedIndex(0);

    expirationMonthLabel = new JLabel("EXP MONTH:", SwingConstants.RIGHT);
    southPanel.add(expirationMonthLabel);

    expirationMonthComboBox = new JComboBox(MONTHS);
    southPanel.add(expirationMonthComboBox);

    expirationMonthComboBox.setSelectedIndex(0);

    expirationYearLabel = new JLabel("EXP YEAR:", SwingConstants.RIGHT);
    southPanel.add(expirationYearLabel);

    expirationYearComboBox = new JComboBox(YEARS);
    southPanel.add(expirationYearComboBox);

    expirationYearComboBox.setSelectedIndex(0);

    blankLabel1 = new JLabel();
    blankLabel2 = new JLabel();
    blankLabel3 = new JLabel();

    addCreditCardButton = new JButton("add credit card");
    southPanel.add(addCreditCardButton);

    addCreditCardButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          addCreditCard.setInt(1, view.getPersonID());
          addCreditCard.setString(2, nameTextField.getText().trim());
          addCreditCard.setLong(3, Long.valueOf(numberTextField.getText().trim()));
          addCreditCard.setInt(4, Integer.valueOf(cvvTextField.getText().trim()));
          addCreditCard.setString(5, (String)typeComboBox.getSelectedItem());
          addCreditCard.setInt(6, Integer.valueOf((String)expirationMonthComboBox.getSelectedItem()));
          addCreditCard.setInt(7, Integer.valueOf((String)expirationYearComboBox.getSelectedItem()));
          addCreditCard.registerOutParameter(8, Types.BOOLEAN);
          addCreditCard.registerOutParameter(9, Types.VARCHAR);
          addCreditCard.execute();

          errorEncountered = addCreditCard.getBoolean(8);
          errorMessage = addCreditCard.getString(9);

          if (errorEncountered) {
            JOptionPane.showMessageDialog(view.getCreditCardsPanel().getPanel(), errorMessage, "SQL Exception Encountered", JOptionPane.ERROR_MESSAGE);
          }

          view.resetCreditCardsPanel();
        } catch (NumberFormatException nfe) {
          JOptionPane.showMessageDialog(view.getCreditCardsPanel().getPanel(), "Credit Card Number and CVV must be integers", "Number Format Exception Encountered", JOptionPane.ERROR_MESSAGE);
          view.resetCreditCardsPanel();
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
      }
    });

    //finalize north components
    southPane = new JScrollPane(southPanel);
    if (mainPanel != null && southPane != null) { mainPanel.add(southPane, BorderLayout.SOUTH); }
  }

  @Override
  void revalidateAll() {
    if (table != null) {
      table.revalidate();
      table.repaint();
    }
    if (tablePane != null) {
      tablePane.revalidate();
      tablePane.repaint();
    }

    revalidateMain();
  }
}


