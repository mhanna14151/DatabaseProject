import java.awt.*;
import javax.swing.*;

abstract class AbstractPanel implements Constants
{
  static JPanel mainPanel;
  JScrollPane northPane;
  JPanel northPanel;
  JScrollPane centerPane;
  JPanel centerPanel;
  JScrollPane southPane;
  JPanel southPanel;

  BorderLayout borderLayout = new BorderLayout();

  AbstractPanel()
  {
    mainPanel = new JPanel();
    mainPanel.setLayout(borderLayout);
    mainPanel.setMaximumSize(FULL_SCREEN);
  }

  abstract void createNorthPane();
  abstract void createCenterPane();
  abstract void createSouthPane();

  abstract void revalidateAll();

  final void revalidateMain()
  {
    if(southPanel != null) { southPanel.revalidate(); southPanel.repaint(); }
    if(southPane != null) { southPane.revalidate(); southPane.repaint(); }
    if(centerPanel != null) { centerPanel.revalidate(); centerPanel.repaint(); }
    if(centerPane != null) { centerPane.revalidate(); centerPane.repaint(); }
    if(northPanel != null) { northPanel.revalidate(); northPanel.repaint(); }
    if(northPane != null) { northPane.revalidate(); northPane.repaint(); }
    if(mainPanel != null) { mainPanel.revalidate(); mainPanel.repaint(); }
  }

  final JPanel getPanel() { return mainPanel; }
}