import javax.swing.table.AbstractTableModel;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class CreditCardsTableModel extends AbstractTableModel {
  private View view;

  private CallableStatement getCreditCards;

  private ResultSet resultSet;
  private ResultSetMetaData resultSetMetaData;

  CreditCardsTableModel(View view) throws SQLException {
    this.view = view;

    try {
      this.getCreditCards = view.getConnection().prepareCall("{ call get_credit_cards(?)}");
      this.getCreditCards.setInt(1, this.view.getPersonID());

      if(this.getCreditCards.execute()) {
        this.resultSet = this.getCreditCards.getResultSet();
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
  public void setValueAt(final Object newValue, final int row, final int column) { }

  @Override
  public Class<?> getColumnClass(int column) {
    return String.class;
  }

  @Override
  public boolean isCellEditable(final int row, final int column) {
    return false;
  }
}

