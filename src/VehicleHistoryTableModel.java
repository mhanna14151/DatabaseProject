import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class VehicleHistoryTableModel extends AbstractTableModel {
  private View view;

  private CallableStatement getVehicleHistory;
  private boolean errorEncountered;
  private String errorMessage;

  private ResultSet resultSet;
  private ResultSetMetaData resultSetMetaData;

 VehicleHistoryTableModel(View view) throws SQLException {
    this.view = view;

    try {
      this.getVehicleHistory = view.getConnection().prepareCall("{ call get_vehicle_history(?, ?, ?)}");
      this.getVehicleHistory.setInt(1, this.view.getPersonID());
      this.getVehicleHistory.registerOutParameter(2, Types.BOOLEAN);
      this.getVehicleHistory.registerOutParameter(3, Types.VARCHAR);

      if (this.getVehicleHistory.execute()) {

        this.errorEncountered = this.getVehicleHistory.getBoolean(2);

        if (this.errorEncountered) {
          this.errorMessage = this.getVehicleHistory.getString(3);
          JOptionPane.showMessageDialog(this.view.getVehicleHistoryPanel().getPanel(), this.errorMessage, "SQL Exception Encountered", JOptionPane.ERROR_MESSAGE);
          return;
        }

        this.resultSet = this.getVehicleHistory.getResultSet();
        this.resultSetMetaData = this.resultSet.getMetaData();
      }
    } catch (SQLException sqlException) {
      System.out.println(sqlException.toString());
    }
  }

  @Override
  public int getColumnCount() {
    if (this.resultSetMetaData == null) {
      return 0;
    }

    try {
      return this.resultSetMetaData.getColumnCount();
    } catch (SQLException e) {
      e.printStackTrace();
      return 0;
    }
  }

  @Override
  public int getRowCount() {
    if (this.resultSetMetaData == null) {
      return 0;
    }

    try {
      this.resultSet.last();
      return this.resultSet.getRow();
    } catch (SQLException e) {
      e.printStackTrace();
      return 0;
    }
  }

  @Override
  public String getColumnName(final int column) {
    try {
      return this.resultSetMetaData.getColumnName(column + 1);
    } catch (SQLException e) {
      e.printStackTrace();
      return e.toString();
    }
  }

  @Override
  public Object getValueAt(final int row, final int column) {
    try {
      this.resultSet.absolute(row + 1);
      return this.resultSet.getString(column + 1);
    } catch (SQLException e) {
      e.printStackTrace();
      return e.toString();
    }
  }

  @Override
  public void setValueAt(final Object newValue, final int row, final int column) { }

  @Override
  public Class<?> getColumnClass(int column) {
    return String.class;
  }

  @Override
  public boolean isCellEditable(final int row, final int column)
  {
    return false;
  }
}