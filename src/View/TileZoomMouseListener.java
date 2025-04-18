package View;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
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
    public void mouseClicked(MouseEvent e) {
        if (zoomWindow != null && zoomWindow.isVisible()) {
            // Already open → close it
            zoomWindow.dispose();
            zoomWindow = null;
            return;
        }

        // Show zoom window
        Icon icon = ((JLabel) component).getIcon();
        if (icon instanceof ImageIcon) {
            Image image = ((ImageIcon) icon).getImage();
            int newWidth = (int) (icon.getIconWidth() * zoomFactor);
            int newHeight = (int) (icon.getIconHeight() * zoomFactor);

            double rotationAngle = getRotationAngleFor(component);
            Image scaledImage = getHighQualityScaledImageWithRotation(image, newWidth, newHeight, rotationAngle);

            JLabel zoomLabel = new JLabel(new ImageIcon(scaledImage));
            zoomLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

            zoomWindow = new JWindow();
            zoomWindow.getContentPane().add(zoomLabel);
            zoomWindow.pack();

            Point locationOnScreen = e.getLocationOnScreen();
            zoomWindow.setLocation(locationOnScreen.x + 10, locationOnScreen.y + 10);
            zoomWindow.setOpacity(0f);
            zoomWindow.setVisible(true);

            // Fade in
            Timer fadeInTimer = new Timer(30, null);
            fadeInTimer.addActionListener(evt -> {
                float currentOpacity = zoomWindow.getOpacity();
                if (currentOpacity < 1.0f) {
                    zoomWindow.setOpacity(Math.min(1.0f, currentOpacity + 0.1f));
                } else {
                    ((Timer) evt.getSource()).stop();
                }
            });
            fadeInTimer.start();
        }
    }


    private Image getHighQualityScaledImageWithRotation(Image src, int targetWidth, int targetHeight, double degrees) {
        // 1. First scale the original image smoothly to desired zoom size
        BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(src, 0, 0, targetWidth, targetHeight, null);
        g.dispose();

        // 2. Now rotate the scaled image
        double radians = Math.toRadians(degrees);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));

        int rotatedWidth = (int) Math.floor(targetWidth * cos + targetHeight * sin);
        int rotatedHeight = (int) Math.floor(targetHeight * cos + targetWidth * sin);

        BufferedImage rotated = new BufferedImage(rotatedWidth, rotatedHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = rotated.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform at = new AffineTransform();
        at.translate(rotatedWidth / 2.0, rotatedHeight / 2.0); // center of new image
        at.rotate(radians);
        at.translate(-targetWidth / 2.0, -targetHeight / 2.0); // center of scaled image
        g2.drawImage(scaled, at, null);
        g2.dispose();

        return rotated;
    }


    private double getRotationAngleFor(JComponent component) {
        String name = component.getName(); // E.g., "tile1", "tile11", etc.
        if (name == null || !name.startsWith("tile")) return 0;

        try {
            int tileNumber = Integer.parseInt(name.replace("tile", ""));

            // Tiles 1-9 → Bottom row (vertical)
            if (tileNumber >= 1 && tileNumber <= 9) return 0;

            // Tiles 11-19 → Right column (rotated 90° counter-clockwise)
            if (tileNumber >= 11 && tileNumber <= 19) return -90;

            // Tiles 21-29 → Top row (upside down)
            if (tileNumber >= 21 && tileNumber <= 29) return 180;

            // Tiles 31-39 → Left column (rotated 90° clockwise)
            if (tileNumber >= 31 && tileNumber <= 39) return 90;

        } catch (NumberFormatException e) {
            // If name format is incorrect
            return 0;
        }

        return 0;
    }


}
