import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class TripHistoryTableModel extends AbstractTableModel {
  private final int CANCEL_COLUMN = 12;

  private View view;

  private CallableStatement cancelTrip;
  private boolean cancelErrorEncountered;
  private String cancelErrorMessage;

  private CallableStatement getTripHistory;
  private boolean errorEncountered;
  private String errorMessage;

  private ResultSet resultSet;
  private ResultSetMetaData resultSetMetaData;

  TripHistoryTableModel(View view) throws SQLException {
    this.view = view;

    try {
      this.cancelTrip = view.getConnection().prepareCall("{ call cancel_trip(?, ?, ?)}");

      this.getTripHistory = view.getConnection().prepareCall("{ call get_trip_history(?, ?, ?)}");
      this.getTripHistory.setInt(1, this.view.getPersonID());
      this.getTripHistory.registerOutParameter(2, Types.BOOLEAN);
      this.getTripHistory.registerOutParameter(3, Types.VARCHAR);

      if (this.getTripHistory.execute()) {

        this.errorEncountered = this.getTripHistory.getBoolean(2);

        if (this.errorEncountered) {
          this.errorMessage = this.getTripHistory.getString(3);
          JOptionPane.showMessageDialog(this.view.getTripHistoryPanel().getPanel(), this.errorMessage, "SQL Exception Encountered", JOptionPane.ERROR_MESSAGE);
          return;
        }

        this.resultSet = this.getTripHistory.getResultSet();
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
  public void setValueAt(final Object newValue, final int row, final int column) {
    if (column == CANCEL_COLUMN) {
      try {
        resultSet.absolute(row + 1);

        cancelTrip.setInt(1, resultSet.getInt(1)); // trip_id
        cancelTrip.registerOutParameter(2, Types.BOOLEAN); // error_encountered
        cancelTrip.registerOutParameter(3, Types.VARCHAR); // error_message
        cancelTrip.execute();

        cancelErrorEncountered = cancelTrip.getBoolean(2);
        cancelErrorMessage = cancelTrip.getString(3);

        if (cancelErrorEncountered) {
          JOptionPane.showMessageDialog(view.getTripHistoryPanel().getPanel(), cancelErrorMessage, "SQL Exception Encountered", JOptionPane.ERROR_MESSAGE);
        }
      } catch (NumberFormatException nfe) {
        JOptionPane.showMessageDialog(view.getTripHistoryPanel().getPanel(), "NumberFormatException encountered", "Number Format Exception Encountered", JOptionPane.ERROR_MESSAGE);
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    }

    view.resetTripHistoryPanel();
  }

  @Override
  public Class<?> getColumnClass(int column) {
    return String.class;
  }

  @Override
  public boolean isCellEditable(final int row, final int column)
  {
    if (column == CANCEL_COLUMN) {
      return true;
    }

    return false;
  }
}