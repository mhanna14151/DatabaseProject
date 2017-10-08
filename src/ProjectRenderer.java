import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class ProjectRenderer implements TableCellRenderer, Constants {
  static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    JComponent renderer = (JComponent) DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

    renderer.setForeground(Color.BLACK);
    renderer.setBackground(Color.WHITE);

    if (row % 2 == 1) {
      renderer.setForeground(Color.BLACK);
      renderer.setBackground(LIGHT_GRAY);
    }

    return renderer;
  }
}