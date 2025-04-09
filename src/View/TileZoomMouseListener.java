package View;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class TileZoomMouseListener extends MouseAdapter {
    private final JComponent component; // The tile label.
    private JWindow zoomWindow;
    private final double zoomFactor;  // E.g., 2.0 for 200% zoom.

    public TileZoomMouseListener(JComponent component, double zoomFactor) {
        this.component = component;
        this.zoomFactor = zoomFactor;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        Icon icon = ((JLabel) component).getIcon();
        if (icon instanceof ImageIcon) {
            Image image = ((ImageIcon) icon).getImage();
            int newWidth = (int) (icon.getIconWidth() * zoomFactor);
            int newHeight = (int) (icon.getIconHeight() * zoomFactor);
            Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            JLabel zoomLabel = new JLabel(new ImageIcon(scaledImage));
            zoomLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

            zoomWindow = new JWindow();
            zoomWindow.getContentPane().add(zoomLabel);
            zoomWindow.pack();

            // Position the popup near the mouse pointer.
            Point locationOnScreen = e.getLocationOnScreen();
            // Adjust the offset as needed.
            zoomWindow.setLocation(locationOnScreen.x + 10, locationOnScreen.y + 10);
            zoomWindow.setVisible(true);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (zoomWindow != null) {
            zoomWindow.dispose();
            zoomWindow = null;
        }
    }
}
