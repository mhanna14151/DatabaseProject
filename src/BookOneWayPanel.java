import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.swing.*;

public class BookOneWayPanel extends AbstractPanel {
  private View view;

  private CallableStatement getAvailable;
  private ResultSet resultSet;

  private CallableStatement getCCs;
  private ResultSet creditCards;
  private int[] ccIDs;
  private String[] ccStrings;

  //northern components
  private JComboBox ccComboBox;

  //central components
  private JScrollPane tablePane;
  private JTable table;
  private BookOneWayTableModel model;

  //southern components
  private JLabel startXlabel;
  private JTextField startXtextField;
  private JLabel startYlabel;
  private JTextField startYtextField;
  private JLabel endXlabel;
  private JTextField endXtextField;
  private JLabel endYlabel;
  private JTextField endYtextField;
  private JButton getAvailableButton;

  private double startX;
  private double startY;
  private double endX;
  private double endY;

  private int ccID;

  private boolean errorEncountered;
  private String errorMessage;

  BookOneWayPanel(View view) {
    super();

    this.view = view;

    try {
      this.getAvailable = view.getConnection().prepareCall("{ call get_available(?, ?, ?, ?, ?, ?, ?)}");
      this.getCCs = view.getConnection().prepareCall("{ call get_ccs(?)}");
    } catch (SQLException e) {
      e.printStackTrace();
    }

    createNorthPane();
    createSouthPane();
  }

  @Override
  void createNorthPane() {
    if (mainPanel != null && northPane != null) mainPanel.remove(northPane);
    if (northPane != null && northPanel != null) northPane.remove(northPanel);
    if (northPanel != null && ccComboBox != null) northPanel.remove(ccComboBox);

    try {
      getCCs.setInt(1, view.getPersonID());


      if (getCCs.execute()) {
        creditCards = getCCs.getResultSet();

        boolean hasResults = creditCards.first();
        int first = creditCards.getRow();

        creditCards.last();
        int last = creditCards.getRow();

        ccStrings = new String[last];
        ccIDs = new int[last];

        if (hasResults) {
          for (int i = first; i <= last; i++) {
            creditCards.absolute(i);
            ccStrings[i - 1] = creditCards.getString(2) + " " + creditCards.getString(3);
            ccIDs[i - 1] = creditCards.getInt(1);
            creditCards.next();
          }

          ccComboBox = new JComboBox(ccStrings);

          if (ccComboBox.getItemCount() > 0) {
            ccComboBox.setSelectedIndex(0);
          } else {
            ccComboBox.setSelectedIndex(-1);
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    northPanel = new JPanel();

    if (ccComboBox != null) {
      northPanel.add(ccComboBox);
    }

    northPane = new JScrollPane(northPanel);

    if (mainPanel != null && northPane != null) { mainPanel.add(northPane, BorderLayout.NORTH); }
  }

  @Override
  void createCenterPane() {
    if (mainPanel != null && centerPane != null) mainPanel.remove(centerPane);
    if (centerPane != null && centerPanel != null) centerPane.remove(centerPanel);
    if (centerPanel != null && tablePane != null) centerPanel.remove(tablePane);
    if (tablePane != null && table != null) tablePane.remove(table);

    if (ccComboBox == null || ccComboBox.getSelectedIndex() < 0) {
      JOptionPane.showMessageDialog(view.getBookOneWayPanel().getPanel(), "Must have/select a credit card to see available vehicles.", "Credit Card Needed", JOptionPane.ERROR_MESSAGE);
      return;
    }

    //create table model
    try {
      model = new BookOneWayTableModel(view, resultSet, startX, startY, endX, endY, ccIDs[ccComboBox.getSelectedIndex()]);
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
    if (startXlabel != null && southPanel != null) {
      southPanel.remove(startXlabel);
    }
    if (startXtextField != null && southPanel != null) {
      southPanel.remove(startXtextField);
    }
    if (startYlabel != null && southPanel != null) {
      southPanel.remove(startYlabel);
    }
    if (startYtextField != null && southPanel != null) {
      southPanel.remove(startYtextField);
    }
    if (endXlabel != null && southPanel != null) {
      southPanel.remove(endXlabel);
    }
    if (endXtextField != null && southPanel != null) {
      southPanel.remove(endXtextField);
    }
    if (endYlabel != null && southPanel != null) {
      southPanel.remove(endYlabel);
    }
    if (endYtextField != null && southPanel != null) {
      southPanel.remove(endYtextField);
    }
    if (getAvailableButton != null && southPanel != null) {
      southPanel.remove(getAvailableButton);
    }

    southPanel = new JPanel();

    startXlabel = new JLabel("START X:", SwingConstants.RIGHT);
    southPanel.add(startXlabel);

    startXtextField = new JTextField(20);
    southPanel.add(startXtextField);

    startYlabel = new JLabel("START Y:", SwingConstants.RIGHT);
    southPanel.add(startYlabel);

    startYtextField = new JTextField(20);
    southPanel.add(startYtextField);

    endXlabel = new JLabel("END X:", SwingConstants.RIGHT);
    southPanel.add(endXlabel);

    endXtextField = new JTextField(20);
    southPanel.add(endXtextField);

    endYlabel = new JLabel("END Y:", SwingConstants.RIGHT);
    southPanel.add(endYlabel);

    endYtextField = new JTextField(20);
    southPanel.add(endYtextField);

    getAvailableButton = new JButton("see available vehicles");
    southPanel.add(getAvailableButton);

    getAvailableButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          startX = Double.valueOf((String)startXtextField.getText().trim());
          startY = Double.valueOf((String)startYtextField.getText().trim());
          endX = Double.valueOf((String)endXtextField.getText().trim());
          endY = Double.valueOf((String)endYtextField.getText().trim());

          getAvailable.setDouble(1, startX);
          getAvailable.setDouble(2, startY);
          getAvailable.setDouble(3, endX);
          getAvailable.setDouble(4, endY);
          getAvailable.setInt(5, view.getPersonID());
          getAvailable.registerOutParameter(6, Types.BOOLEAN);
          getAvailable.registerOutParameter(7, Types.VARCHAR);

          if (getAvailable.execute()) {
            errorEncountered = getAvailable.getBoolean(6);
            errorMessage = getAvailable.getString(7);

            if (errorEncountered) {
              JOptionPane.showMessageDialog(view.getBookOneWayPanel().getPanel(), errorMessage, "SQL Exception Encountered", JOptionPane.ERROR_MESSAGE);
              view.resetBookOneWayPanel();
              return;
            }

            resultSet = getAvailable.getResultSet();
            createCenterPane();
            revalidateAll();
          }
        } catch (NumberFormatException nfe) {
          JOptionPane.showMessageDialog(view.getBookOneWayPanel().getPanel(), "Coordinates must be numbers", "Number Format Exception Encountered", JOptionPane.ERROR_MESSAGE);
          view.resetBookOneWayPanel();
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
      }
    });

    //finalize south components
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


