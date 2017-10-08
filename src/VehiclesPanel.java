import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.swing.*;

public class VehiclesPanel extends AbstractPanel {
  private View view;
  private CallableStatement getMakes;
  private CallableStatement getModels;
  private CallableStatement getYears;
  private CallableStatement addVehicle;

  private ResultSet getMakesResultSet;
  private ResultSet getModelsResultSet;
  private ResultSet getYearsResultSet;

  private String[] make_names;
  private String[] model_names;
  private String[] years;

  //central components
  private JScrollPane tablePane;
  private JTable table;
  private VehiclesTableModel model;

  //southern components
  private JButton addVehicleButton;
  private JLabel vinLabel;
  private JTextField vinTextField;
  private JLabel plateNumberLabel;
  private JTextField plateNumberTextField;
  private JLabel colorLabel;
  private JTextField colorTextField;
  private JLabel makeLabel;
  private JComboBox makeComboBox;
  private JLabel modelLabel;
  private JComboBox modelComboBox;
  private JLabel yearLabel;
  private JComboBox yearComboBox;

  private boolean errorEncountered;
  private String errorMessage;

  VehiclesPanel(View view) {
    super();

    this.view = view;

    try {
      this.getMakes = view.getConnection().prepareCall("{ call get_makes()}");
      this.getModels = view.getConnection().prepareCall("{ call get_models(?)}");
      this.getYears = view.getConnection().prepareCall("{ call get_years(?)}");
      this.addVehicle = view.getConnection().prepareCall("{ call add_vehicle(?, ?, ?, ?, ?, ?, ?, ?)}");

      // execute getMakes
      if (this.getMakes.execute()) {
        this.getMakesResultSet = getMakes.getResultSet();
        this.getMakesResultSet.first();

        this.getMakesResultSet.first();
        int first = this.getMakesResultSet.getRow();
        System.out.println("first = " + first);

        this.getMakesResultSet.last();
        int last = this.getMakesResultSet.getRow();
        System.out.println("last = " + last);

        make_names = new String[last];
        this.getMakesResultSet.first();

        for (int i = first; i <= last; i++) {
          make_names[i - 1] = this.getMakesResultSet.getString(1);
          this.getMakesResultSet.next();
        }
      }
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
      model = new VehiclesTableModel(view);
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
    if (makeLabel != null && southPanel != null) {
      southPanel.remove(makeLabel);
    }
    if (makeComboBox != null && southPanel != null) {
      southPanel.remove(makeComboBox);
    }
    if (modelLabel != null && southPanel != null) {
      southPanel.remove(modelLabel);
    }
    if (modelComboBox != null && southPanel != null) {
      southPanel.remove(modelComboBox);
    }
    if (yearLabel != null && southPanel != null) {
      southPanel.remove(yearLabel);
    }
    if (yearComboBox != null && southPanel != null) {
      southPanel.remove(yearComboBox);
    }
    if (colorLabel != null && southPanel != null) {
      southPanel.remove(colorLabel);
    }
    if (colorTextField != null && southPanel != null) {
      southPanel.remove(colorTextField);
    }
    if (vinLabel != null && southPanel != null) {
      southPanel.remove(vinLabel);
    }
    if (vinTextField != null && southPanel != null) {
      southPanel.remove(vinTextField);
    }
    if (plateNumberLabel != null && southPanel != null) {
      southPanel.remove(plateNumberLabel);
    }
    if (plateNumberTextField != null && southPanel != null) {
      southPanel.remove(plateNumberTextField);
    }

    southPanel = new JPanel();

    addVehicleButton = new JButton("add vehicle");
    southPanel.add(addVehicleButton);

    addVehicleButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          addVehicle.setString(1, vinTextField.getText());
          addVehicle.setString(2, colorTextField.getText());
          addVehicle.setString(3, plateNumberTextField.getText());
          addVehicle.setString(4, (String)modelComboBox.getSelectedItem());
          addVehicle.setInt(5, Integer.valueOf((String)yearComboBox.getSelectedItem()));
          addVehicle.setInt(6, view.getPersonID());
          addVehicle.registerOutParameter(7, Types.BOOLEAN);
          addVehicle.registerOutParameter(8, Types.VARCHAR);
          addVehicle.execute();

          errorEncountered = addVehicle.getBoolean(7);
          errorMessage = addVehicle.getString(8);

          if (errorEncountered) {
            JOptionPane.showMessageDialog(view.getVehiclesPanel().getPanel(), errorMessage, "SQL Exception Encountered", JOptionPane.ERROR_MESSAGE);
          }

          view.resetVehiclesPanel();
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
      }
    });

    vinLabel = new JLabel("VIN:", SwingConstants.RIGHT);
    southPanel.add(vinLabel);

    vinTextField = new JTextField(20);
    southPanel.add(vinTextField);

    plateNumberLabel = new JLabel("PLATE #:", SwingConstants.RIGHT);
    southPanel.add(plateNumberLabel);

    plateNumberTextField = new JTextField(20);
    southPanel.add(plateNumberTextField);

    colorLabel = new JLabel("COLOR:", SwingConstants.RIGHT);
    southPanel.add(colorLabel);

    colorTextField = new JTextField(20);
    southPanel.add(colorTextField);

    makeLabel = new JLabel("MAKE:", SwingConstants.RIGHT);
    southPanel.add(makeLabel);

    makeComboBox = new JComboBox(make_names);
    southPanel.add(makeComboBox);

    //create action listenter
    makeComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        if (makeComboBox.getSelectedIndex() != -1) {
          try {
            getModels.setString(1, (String) makeComboBox.getSelectedItem());

            // execute getModels
            if (getModels.execute()) {
              getModelsResultSet = getModels.getResultSet();
              getModelsResultSet.first();

              getModelsResultSet.first();
              int first = getModelsResultSet.getRow();

              getModelsResultSet.last();
              int last = getModelsResultSet.getRow();

              model_names = new String[last];
              getModelsResultSet.first();

              for (int i = 0; i < last; i++) {
                model_names[i] = getModelsResultSet.getString(1);
                getModelsResultSet.next();
              }
            }
          } catch (SQLException e) {

          }

          if (modelLabel != null) {
            southPanel.remove(modelLabel);
          }
          if (modelComboBox != null) {
            southPanel.remove(modelComboBox);
          }

          if (yearLabel != null) {
            southPanel.remove(yearLabel);
          }
          if (yearComboBox != null) {
            southPanel.remove(yearComboBox);
          }

          modelLabel = new JLabel("MODEL:");
          southPanel.add(modelLabel);

          modelComboBox = new JComboBox(model_names);
          southPanel.add(modelComboBox);

          //create action listenter
          modelComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
              if (modelComboBox.getSelectedIndex() != -1) {
                try {
                  getYears.setString(1, (String) modelComboBox.getSelectedItem());

                  // execute getYears
                  if (getYears.execute()) {
                    getYearsResultSet = getYears.getResultSet();
                    getYearsResultSet.first();

                    getYearsResultSet.first();
                    int first = getYearsResultSet.getRow();

                    getYearsResultSet.last();
                    int last = getYearsResultSet.getRow();

                    years = new String[last];
                    getYearsResultSet.first();

                    for (int i = 0; i < last; i++) {
                      years[i] = String.valueOf(getYearsResultSet.getInt(1));
                      getYearsResultSet.next();
                    }
                  }
                } catch (SQLException e) {

                }

                if (yearLabel != null) {
                  southPanel.remove(yearLabel);
                }
                if (yearComboBox != null) {
                  southPanel.remove(yearComboBox);
                }

                yearLabel = new JLabel("YEAR:");
                southPanel.add(yearLabel);

                yearComboBox = new JComboBox(years);
                southPanel.add(yearComboBox);

                southPanel.revalidate();
                southPanel.repaint();
              }
            }
          });

          //initialize makeComboBox selection
          if (model_names.length > 0) {
            modelComboBox.setSelectedIndex(0);
          } else {
            modelComboBox.setSelectedIndex(-1);
          }

          southPanel.revalidate();
          southPanel.repaint();
        }
      }
    });

    //initialize makeComboBox selection
    if (make_names.length > 0) {
      makeComboBox.setSelectedIndex(0);
    } else {
      makeComboBox.setSelectedIndex(-1);
    }

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


