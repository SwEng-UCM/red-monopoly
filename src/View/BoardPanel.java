package View;

import Controller.Controller;
import Model.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class BoardPanel extends JPanel {
    private Controller controller;

    // Images for tiles – can be swapped out easily.
    private Image cornerImage;
    private Image propertyUpDownImage;
    private Image propertyLeftRightImage;

    private Image backgroundImage = new ImageIcon("resources/background_board.png").getImage();

    // A map to hold manual override images for any tile index (0-39).
    private Map<Integer, Image> manualTileImages = new HashMap<>();

    protected boolean useImageDesign = true;

    protected Map<Integer, JLabel> tileLabels = new HashMap<>();

    protected Map<Player, Color> playerColors = new HashMap<>();
    private final Color[] PLAYER_COLORS = {
            new Color(231, 76, 60),
            new Color(52, 152, 219),
            new Color(46, 204, 113),
            new Color(241, 196, 15),
            new Color(230, 126, 34),
            new Color(155, 89, 182),
            new Color(26, 188, 156),
            new Color(233, 30, 99)
    };

    public static final int DESIGN_SIZE = 728;
    private final JWindow zoomWindow = new JWindow();
    private final JLabel zoomLabel = new JLabel();

    public BoardPanel(Controller controller) {
        this.controller = controller;
        setLayout(null);
        setPreferredSize(new Dimension(DESIGN_SIZE, DESIGN_SIZE));

        // Load default images.
        cornerImage = new ImageIcon("resources/GULAG.png").getImage();
        propertyUpDownImage = new ImageIcon("resources/property_norilsk.png").getImage();
        propertyLeftRightImage = new ImageIcon("resources/property_norilsk_left.png").getImage();

        zoomWindow.getContentPane().add(zoomLabel);
        zoomWindow.setAlwaysOnTop(true);
        zoomWindow.setBackground(new Color(0, 0, 0, 0));
        zoomWindow.setType(Window.Type.POPUP);

        initPlayerColors();
        initTiles();
    }

    private void initPlayerColors() {
        List<Player> players = controller.getAllPlayers();
        for (int i = 0; i < players.size(); i++) {
            playerColors.put(players.get(i), PLAYER_COLORS[i % PLAYER_COLORS.length]);
        }
    }

    private void initTiles() {
        List<Tile> tiles = controller.getBoardTiles();
        if (tiles.size() != 40) {
            System.err.println("Warning: Expected 40 tiles, got " + tiles.size());
        }
        for (int i = 0; i < tiles.size(); i++) {
            JLabel tileLabel = createTileLabel(i);
            tileLabels.put(i, tileLabel);
            add(tileLabel);
        }
    }

    private JLabel createTileLabel(int index) {
        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        if (!useImageDesign) {
            Tile tile = controller.getBoardTiles().get(index);
            label.setBackground(tile instanceof PropertyTile ? Color.WHITE : Color.LIGHT_GRAY);
            label.setText(generateTileLabelText(tile, index));
        } else {
            label.addMouseListener(new MouseAdapter() {
                private final double ZOOM_FACTOR = 2.0;

                @Override
                public void mouseEntered(MouseEvent e) {
                    int w = label.getWidth();
                    int h = label.getHeight();

                    Image image = manualTileImages.get(index);
                    if (image == null) image = getRawTileImage(index);

                    int zoomW = (int) (w * ZOOM_FACTOR);
                    int zoomH = (int) (h * ZOOM_FACTOR);
                    ImageIcon zoomedIcon = new ImageIcon(image.getScaledInstance(zoomW, zoomH, Image.SCALE_SMOOTH));

                    zoomLabel.setIcon(zoomedIcon);
                    zoomWindow.setSize(zoomW, zoomH);

                    Point loc = label.getLocationOnScreen();
                    zoomWindow.setLocation(loc.x + w / 2, loc.y + h / 2);
                    zoomWindow.setVisible(true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    zoomWindow.setVisible(false);
                }
            });
        }
        return label;
    }

    private Image getRawTileImage(int index) {
        if (manualTileImages.containsKey(index)) return manualTileImages.get(index);
        if (index == 0 || index == 10 || index == 20 || index == 30) return cornerImage;
        if (index >= 1 && index <= 9) return propertyUpDownImage;
        if (index >= 11 && index <= 19) return propertyLeftRightImage;
        if (index >= 21 && index <= 29) return propertyUpDownImage;
        if (index >= 31 && index <= 39) return propertyLeftRightImage;
        return propertyUpDownImage;
    }

    public JLabel getTileLabel(int tileIndex) {
        return tileLabels.get(tileIndex);
    }

    public Color getPlayerColor(Player player) {
        return playerColors.getOrDefault(player, Color.BLACK);
    }

    public void setManualTileImage(int tileIndex, Image image) {
        if (tileIndex >= 0 && tileIndex < 40) {
            manualTileImages.put(tileIndex, image);
        } else {
            throw new IllegalArgumentException("Tile index " + tileIndex + " is out of range.");
        }
    }

    @Override
    public void doLayout() {
        super.doLayout();

        int panelW = getWidth();
        int panelH = getHeight();
        double scale = Math.min(panelW / (double) DESIGN_SIZE, panelH / (double) DESIGN_SIZE);
        int boardSize = (int) (DESIGN_SIZE * scale);
        int offsetX = (panelW - boardSize) / 2;
        int offsetY = (panelH - boardSize) / 2;

        for (int i = 0; i < 40; i++) {
            Rectangle designBounds = getDesignBoundsForTile(i);
            int xScaled = (int) (designBounds.x * scale) + offsetX;
            int yScaled = (int) (designBounds.y * scale) + offsetY;
            int wScaled = (int) (designBounds.width * scale);
            int hScaled = (int) (designBounds.height * scale);

            JLabel label = tileLabels.get(i);
            if (label != null) {
                label.setBounds(xScaled, yScaled, wScaled, hScaled);
                if (useImageDesign) {
                    label.setIcon(getTileImageIcon(i, wScaled, hScaled));
                    label.setText("");
                }
            }
        }
    }

    private Rectangle getDesignBoundsForTile(int index) {
        if (index >= 0 && index <= 10) {
            if (index == 0) return new Rectangle(DESIGN_SIZE - 112, DESIGN_SIZE - 112, 112, 112);
            if (index == 10) return new Rectangle(0, DESIGN_SIZE - 112, 112, 112);
            int x = (DESIGN_SIZE - 112) - (index * 56);
            return new Rectangle(x, DESIGN_SIZE - 112, 56, 112);
        } else if (index >= 11 && index <= 19) {
            int offset = index - 10;
            int y = DESIGN_SIZE - 112 - (offset * 56);
            return new Rectangle(0, y, 112, 56);
        } else if (index >= 20 && index <= 30) {
            if (index == 20) return new Rectangle(0, 0, 112, 112);
            if (index == 30) return new Rectangle(DESIGN_SIZE - 112, 0, 112, 112);
            int offset = index - 20;
            int x = 112 + (offset - 1) * 56;
            return new Rectangle(x, 0, 56, 112);
        } else if (index >= 31 && index <= 39) {
            int offset = index - 30;
            int y = 112 + (offset - 1) * 56;
            return new Rectangle(DESIGN_SIZE - 112, y, 112, 56);
        }
        return new Rectangle(0, 0, 0, 0);
    }

    private ImageIcon getTileImageIcon(int index, int w, int h) {
        Image image;
        if (manualTileImages.containsKey(index)) {
            image = manualTileImages.get(index);
        } else if (index == 0 || index == 10 || index == 20 || index == 30) {
            image = cornerImage;
        } else if (index >= 1 && index <= 9) {
            image = propertyUpDownImage;
        } else if (index >= 21 && index <= 29) {
            return getRotatedImageIcon(propertyUpDownImage, Math.PI, w, h);
        } else if (index >= 11 && index <= 19) {
            image = propertyLeftRightImage;
        } else if (index >= 31 && index <= 39) {
            return getRotatedImageIcon(propertyLeftRightImage, Math.PI, w, h);
        } else {
            image = propertyUpDownImage;
        }
        Image scaled = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private ImageIcon getRotatedImageIcon(Image src, double angle, int width, int height) {
        BufferedImage buff = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buff.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.translate(width / 2.0, height / 2.0);
        g2d.rotate(angle);
        g2d.translate(-width / 2.0, -height / 2.0);
        g2d.drawImage(src, 0, 0, width, height, null);
        g2d.dispose();
        return new ImageIcon(buff);
    }

    public void refreshBoard() {
        repaint();
    }

    private String generateTileLabelText(Tile tile, int index) {
        return "<html><div style='text-align:center;'>" +
                tile.getName() + " (" + index + ")</div></html>";
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            int panelW = getWidth();
            int panelH = getHeight();
            // same scale/offset logic as in doLayout()
            double scale = Math.min(panelW / (double) DESIGN_SIZE,
                    panelH / (double) DESIGN_SIZE);
            int boardSize = (int) (DESIGN_SIZE * scale);
            int offsetX = (panelW - boardSize) / 2;
            int offsetY = (panelH - boardSize) / 2;

            g.drawImage(
                    backgroundImage,
                    offsetX, offsetY,
                    boardSize, boardSize,
                    this
            );
        }
    }

}
