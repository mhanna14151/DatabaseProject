import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class BookOneWayTableModel extends AbstractTableModel {
  private final int BOOK_COLUMN = 9;

  private View view;

  private CallableStatement bookOneWay;
  private boolean errorEncountered;
  private String errorMessage;

  private ResultSet resultSet;
  private ResultSetMetaData resultSetMetaData;

  private double startX;
  private double startY;
  private double endX;
  private double endY;

  private int ccID;

  BookOneWayTableModel(View view, ResultSet resultSet, double startX, double startY, double endX, double endY, int ccID) throws SQLException {
    this.view = view;
    this.resultSet = resultSet;
    this.resultSetMetaData = this.resultSet.getMetaData();
    this.bookOneWay = view.getConnection().prepareCall("{ call book_one_way(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
    this.startX = startX;
    this.startY = startY;
    this.endX = endX;
    this.endY = endY;
    this.ccID = ccID;
  }

  @Override
  public int getColumnCount() {
    if (this.resultSetMetaData == null) { return 0; }

    try {
      return this.resultSetMetaData.getColumnCount();
    } catch (SQLException e) {
      e.printStackTrace();
      return 0;
    }
  }

  @Override
  public int getRowCount() {
    if (this.resultSetMetaData == null) { return 0; }

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

  // start_x, start_y, start_time, vehicle_id, end_x, end_y, distance, avg_mph, person_id, cc_id, booked_rate, error_encountered, error_message

  @Override
  public void setValueAt(final Object newValue, final int row, final int column) {
    if (column == BOOK_COLUMN) {
      try {
        resultSet.absolute(row + 1);

        bookOneWay.setDouble(1, startX); // start_x
        bookOneWay.setDouble(2, startY); // start_y
        bookOneWay.setTimestamp(3, resultSet.getTimestamp(4));  // start_time
        bookOneWay.setInt(4, resultSet.getInt(1)); // vehicle_id
        bookOneWay.setDouble(5, endX); // end_x
        bookOneWay.setDouble(6, endY); // end_y
        bookOneWay.setDouble(7, resultSet.getDouble(8)); // distance
        bookOneWay.setDouble(8, resultSet.getDouble(7)); // avg_mph
        bookOneWay.setInt(9, view.getPersonID()); // person_id
        bookOneWay.setInt(10, ccID);  // cc_id
        bookOneWay.setDouble(11, resultSet.getDouble(9));  // booked_rate
        bookOneWay.registerOutParameter(12, Types.BOOLEAN); // error_encountered
        bookOneWay.registerOutParameter(13, Types.VARCHAR); // error_message

        if (bookOneWay.execute()) {
          errorEncountered = bookOneWay.getBoolean(12);
          errorMessage = bookOneWay.getString(13);

          if (errorEncountered) {
            JOptionPane.showMessageDialog(view.getBookOneWayPanel().getPanel(), errorMessage, "SQL Exception Encountered", JOptionPane.ERROR_MESSAGE);
          }
        }
      } catch (NumberFormatException nfe) {
        JOptionPane.showMessageDialog(view.getBookOneWayPanel().getPanel(), "Coordinates must be numbers", "Number Format Exception Encountered", JOptionPane.ERROR_MESSAGE);
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    }

    view.resetBookOneWayPanel();
  }

  @Override
  public Class<?> getColumnClass(int column) {
    return String.class;
  }

  @Override
  public boolean isCellEditable(final int row, final int column) {
    if (column == BOOK_COLUMN) { return true; }
    return false;
  }
}