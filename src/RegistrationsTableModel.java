import javax.swing.table.AbstractTableModel;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class RegistrationsTableModel extends AbstractTableModel {
  private View view;

  private CallableStatement getRegistrations;
  private CallableStatement setRegistrationStatus;

  private int personID;
  private ResultSet resultSet;
  private ResultSetMetaData resultSetMetaData;

  RegistrationsTableModel(View view) throws SQLException {
    this.view = view;
    this.personID = view.getPersonID();

    try {
      this.setRegistrationStatus = view.getConnection().prepareCall("{ call set_registration_status(?, ?, ?)}");

      this.getRegistrations = view.getConnection().prepareCall("{ call get_registrations(?)}");
      this.getRegistrations.setInt(1, this.personID);

      if(this.getRegistrations.execute()) {
        this.resultSet = this.getRegistrations.getResultSet();
        this.resultSetMetaData = this.resultSet.getMetaData();
      }
    } catch (SQLException sqlException) {
      System.out.println(sqlException.toString());
    }
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

  @Override
  public void setValueAt(final Object newValue, final int row, final int column) {
    if (column == 9) {
      try {
        setRegistrationStatus.setInt(1, view.getPersonID());
        resultSet.absolute(row + 1);
        int reg_id = resultSet.getInt(1);
        System.out.println("reg_id of changed row: " + reg_id);
        setRegistrationStatus.setInt(2, reg_id);
        setRegistrationStatus.setString(3, (String)newValue);
        setRegistrationStatus.execute();
        view.resetRegistrationsPanel();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public Class<?> getColumnClass(int column) {
    return String.class;
  }

  @Override
  public boolean isCellEditable(final int row, final int column) {
    if (column == 9) { return true; }
    return false;
  }
}

