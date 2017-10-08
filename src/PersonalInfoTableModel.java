import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class PersonalInfoTableModel extends AbstractTableModel {
  private View view;

  private CallableStatement getPersonalInfo;

  private CallableStatement updateFirstName;
  private CallableStatement updateLastName;
  private CallableStatement updateEmail;
  private CallableStatement updatePassword;
  private CallableStatement updatePhoneNumber;
  private CallableStatement updateRoutingNumber;

  private boolean errorEncountered;
  private String errorMessage;

  private int personID;
  private ResultSet resultSet;
  private ResultSetMetaData resultSetMetaData;

  PersonalInfoTableModel(View view) throws SQLException {
    this.view = view;
    this.personID = view.getPersonID();

    try {
      this.getPersonalInfo = view.getConnection().prepareCall("{ call get_personal_info(?)}");
      this.getPersonalInfo.setInt(1, this.personID);

      if(this.getPersonalInfo.execute()) {
        this.resultSet = this.getPersonalInfo.getResultSet();
        this.resultSetMetaData = this.resultSet.getMetaData();
      }

      this.updateFirstName = view.getConnection().prepareCall("{ call update_first_name(?, ?, ?, ?)}");
      this.updateLastName = view.getConnection().prepareCall("{ call update_last_name(?, ?, ?, ?)}");
      this.updateEmail = view.getConnection().prepareCall("{ call update_email(?, ?, ?, ?)}");
      this.updatePassword = view.getConnection().prepareCall("{ call update_password(?, ?, ?, ?)}");
      this.updatePhoneNumber = view.getConnection().prepareCall("{ call update_phone_number(?, ?, ?, ?)}");
      this.updateRoutingNumber = view.getConnection().prepareCall("{ call update_routing_number(?, ?, ?, ?)}");
    } catch (SQLException sqlException) {
      System.out.println(sqlException.toString());
    }
  }

  @Override
  public int getColumnCount() {
    try {
      return this.resultSetMetaData.getColumnCount();
    } catch (SQLException e) {
      e.printStackTrace();
      return 0;
    }
  }

  @Override
  public int getRowCount() {
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
    try {
      switch (column ) {
        case 0:
          this.updateFirstName.setInt(1, this.personID);
          this.updateFirstName.setString(2, ((String)newValue).trim());
          this.updateFirstName.registerOutParameter(3, Types.BOOLEAN);
          this.updateFirstName.registerOutParameter(4, Types.VARCHAR);
          this.updateFirstName.execute();
          this.errorEncountered = updateFirstName.getBoolean(3);
          this.errorMessage = updateFirstName.getString(4);
          break;
        case 1:
          this.updateLastName.setInt(1, this.personID);
          this.updateLastName.setString(2, ((String)newValue).trim());
          this.updateLastName.registerOutParameter(3, Types.BOOLEAN);
          this.updateLastName.registerOutParameter(4, Types.VARCHAR);
          this.updateLastName.execute();
          this.errorEncountered = updateLastName.getBoolean(3);
          this.errorMessage = updateLastName.getString(4);
          break;
        case 2:
          this.updateEmail.setInt(1, this.personID);
          this.updateEmail.setString(2, ((String)newValue).trim());
          this.updateEmail.registerOutParameter(3, Types.BOOLEAN);
          this.updateEmail.registerOutParameter(4, Types.VARCHAR);
          this.updateEmail.execute();
          this.errorEncountered = updateEmail.getBoolean(3);
          this.errorMessage = updateEmail.getString(4);
          break;
        case 3:
          this.updatePassword.setInt(1, this.personID);
          this.updatePassword.setString(2, ((String)newValue).trim());
          this.updatePassword.registerOutParameter(3, Types.BOOLEAN);
          this.updatePassword.registerOutParameter(4, Types.VARCHAR);
          this.updatePassword.execute();
          this.errorEncountered = updatePassword.getBoolean(3);
          this.errorMessage = updatePassword.getString(4);
          break;
        case 4:
          this.updatePhoneNumber.setInt(1, this.personID);
          this.updatePhoneNumber.setString(2, ((String)newValue).trim());
          this.updatePhoneNumber.registerOutParameter(3, Types.BOOLEAN);
          this.updatePhoneNumber.registerOutParameter(4, Types.VARCHAR);
          this.updatePhoneNumber.execute();
          this.errorEncountered = updatePhoneNumber.getBoolean(3);
          this.errorMessage = updatePhoneNumber.getString(4);
          break;
        case 5:
          this.updateRoutingNumber.setInt(1, this.personID);
          this.updateRoutingNumber.setString(2, ((String)newValue).trim());
          this.updateRoutingNumber.registerOutParameter(3, Types.BOOLEAN);
          this.updateRoutingNumber.registerOutParameter(4, Types.VARCHAR);
          this.updateRoutingNumber.execute();
          this.errorEncountered = updateRoutingNumber.getBoolean(3);
          this.errorMessage = updateRoutingNumber.getString(4);
          break;
      }
    } catch (SQLException e) {
      System.out.println("sql error encountered setting field");
    }

    if (this.errorEncountered) {
      JOptionPane.showMessageDialog(this.view.getPersonalInfoPanel().getPanel(), this.errorMessage, "SQL Exception Encountered", JOptionPane.ERROR_MESSAGE);
    }

    this.view.resetPersonalInfoPanel();
  }

  @Override
  public Class<?> getColumnClass(int column) {
    return String.class;
  }

  @Override
  public boolean isCellEditable(final int row, final int column) {
    return true;
  }
}

