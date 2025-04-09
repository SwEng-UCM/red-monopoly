package View;

import Controller.Controller;
import Model.*;
import java.awt.*;
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

    // A map to hold manual override images for any tile index (0-39).
    private Map<Integer, Image> manualTileImages = new HashMap<>();

    // Flag to use image design rather than the default HTML cell design.
    protected boolean useImageDesign = true;

    // A map of tileIndex -> JLabel (the visual for that tile).
    protected Map<Integer, JLabel> tileLabels = new HashMap<>();

    // Player color management (for overlay tokens, etc.)
    protected Map<Player, Color> playerColors = new HashMap<>();
    private final Color[] PLAYER_COLORS = {
            new Color(231, 76, 60),    // Red
            new Color(52, 152, 219),   // Blue
            new Color(46, 204, 113),   // Green
            new Color(241, 196, 15),   // Yellow
            new Color(230, 126, 34),   // Orange
            new Color(155, 89, 182),   // Purple
            new Color(26, 188, 156),   // Teal
            new Color(233, 30, 99)     // Pink
    };

    // The board’s “design” dimensions. (A 40-tile board is typically ~728×728.)
    public static final int DESIGN_SIZE = 728;

    public BoardPanel(Controller controller) {
        this.controller = controller;
        setLayout(null);
        setPreferredSize(new Dimension(DESIGN_SIZE, DESIGN_SIZE));

        // Load default images.
        cornerImage = new ImageIcon("resources/GULAG.png").getImage();
        propertyUpDownImage = new ImageIcon("resources/property_norilsk.png").getImage();
        propertyLeftRightImage = new ImageIcon("resources/property_norilsk_left.png").getImage();

        initPlayerColors();
        initTiles();
    }

    /**
     * Assigns a unique color to each player for highlighting.
     */
    private void initPlayerColors() {
        List<Player> players = controller.getAllPlayers();
        for (int i = 0; i < players.size(); i++) {
            playerColors.put(players.get(i), PLAYER_COLORS[i % PLAYER_COLORS.length]);
        }
    }

    /**
     * Creates JLabels for all tiles (0..39) and adds them to this panel.
     */
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

    /**
     * Creates a JLabel for a tile.
     * If useImageDesign is true, it will later be assigned an Icon in doLayout,
     * and we attach a mouse listener for hover-zoom.
     */
    private JLabel createTileLabel(int index) {
        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        if (!useImageDesign) {
            Tile tile = controller.getBoardTiles().get(index);
            label.setBackground(tile instanceof PropertyTile ? Color.WHITE : Color.LIGHT_GRAY);
            label.setText(generateTileLabelText(tile, index));
        } else {
            // Add the zoom mouse listener so when the mouse hovers, a zoomed image appears.
            label.addMouseListener(new TileZoomMouseListener(label, 2.0)); // 2x zoom; adjust as needed
        }
        return label;
    }

    /**
     * Returns the JLabel for the given tile index.
     */
    public JLabel getTileLabel(int tileIndex) {
        return tileLabels.get(tileIndex);
    }

    /**
     * Returns the assigned color for the given player's token.
     */
    public Color getPlayerColor(Player player) {
        return playerColors.getOrDefault(player, Color.BLACK);
    }

    /**
     * Sets a manual image for any tile index (0 to 39).
     */
    public void setManualTileImage(int tileIndex, Image image) {
        if (tileIndex >= 0 && tileIndex < 40) {
            manualTileImages.put(tileIndex, image);
        } else {
            throw new IllegalArgumentException("Tile index " + tileIndex + " is out of range.");
        }
    }

    /**
     * Override doLayout to position tiles using fixed design dimensions and scale to fit the panel.
     */
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
        if (index >= 0 && index <= 10) { // Bottom row.
            if (index == 0) {
                return new Rectangle(DESIGN_SIZE - 112, DESIGN_SIZE - 112, 112, 112);
            } else if (index == 10) {
                return new Rectangle(0, DESIGN_SIZE - 112, 112, 112);
            } else {
                int x = (DESIGN_SIZE - 112) - (index * 56);
                return new Rectangle(x, DESIGN_SIZE - 112, 56, 112);
            }
        } else if (index >= 11 && index <= 19) { // Left column.
            int offset = index - 10;
            int y = DESIGN_SIZE - 112 - (offset * 56);
            return new Rectangle(0, y, 112, 56);
        } else if (index >= 20 && index <= 30) { // Top row.
            if (index == 20) {
                return new Rectangle(0, 0, 112, 112);
            } else if (index == 30) {
                return new Rectangle(DESIGN_SIZE - 112, 0, 112, 112);
            } else {
                int offset = index - 20;
                int x = 112 + (offset - 1) * 56;
                return new Rectangle(x, 0, 56, 112);
            }
        } else if (index >= 31 && index <= 39) { // Right column.
            int offset = index - 30;
            int y = 112 + (offset - 1) * 56;
            return new Rectangle(DESIGN_SIZE - 112, y, 112, 56);
        }
        return new Rectangle(0, 0, 0, 0);
    }

    /**
     * Returns the tile’s image icon.
     * If a manual image has been provided for this tile index, that image is used.
     * Otherwise, fallback to default images according to tile position.
     */
    private ImageIcon getTileImageIcon(int index, int w, int h) {
        if (manualTileImages.containsKey(index)) {
            Image image = manualTileImages.get(index);
            Image scaled = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        // Corners: indices 0, 10, 20, 30.
        if (index == 0 || index == 10 || index == 20 || index == 30) {
            Image scaled = cornerImage.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        // Bottom row: indices 1 to 9.
        if (index >= 1 && index <= 9) {
            Image scaled = propertyUpDownImage.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        // Top row: indices 21 to 29.
        if (index >= 21 && index <= 29) {
            return getRotatedImageIcon(propertyUpDownImage, Math.PI, w, h);
        }
        // Left column: indices 11 to 19.
        if (index >= 11 && index <= 19) {
            Image scaled = propertyLeftRightImage.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        // Right column: indices 31 to 39.
        if (index >= 31 && index <= 39) {
            return getRotatedImageIcon(propertyLeftRightImage, Math.PI, w, h);
        }
        // Fallback.
        Image scaled = propertyUpDownImage.getScaledInstance(w, h, Image.SCALE_SMOOTH);
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
        // Optionally, draw a background fill or image if needed.
    }
}
