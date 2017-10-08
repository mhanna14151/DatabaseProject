import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;

public class VehicleHistoryPanel extends AbstractPanel {
  private View view;

  //central components
  private JScrollPane tablePane;
  private JTable table;
  private VehicleHistoryTableModel model;

  VehicleHistoryPanel(View view) {
    super();

    this.view = view;

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
      model = new VehicleHistoryTableModel(view);
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


