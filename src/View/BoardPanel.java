package View;

import Controller.Controller;
import Model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class BoardPanel extends JPanel {
    private Controller controller;

    private Image cornerImage;
    private Image propertyUpDownImage;
    private Image propertyLeftRightImage;
    private Image backgroundImage = new ImageIcon("resources/background_board.png").getImage();

    private Map<Integer, Image> manualTileImages = new HashMap<>();
    protected boolean useImageDesign = true;
    protected Map<Integer, JLabel> tileLabels = new HashMap<>();
    protected Map<Player, Color> playerColors = new HashMap<>();

    private final Color[] PLAYER_COLORS = {
            new Color(231, 76, 60), new Color(52, 152, 219), new Color(46, 204, 113),
            new Color(241, 196, 15), new Color(230, 126, 34), new Color(155, 89, 182),
            new Color(26, 188, 156), new Color(233, 30, 99)
    };

    public static final int DESIGN_SIZE = 728;

    public BoardPanel(Controller controller) {
        this.controller = controller;
        setLayout(null);
        setPreferredSize(new Dimension(DESIGN_SIZE, DESIGN_SIZE));

        cornerImage = new ImageIcon("resources/GULAG.png").getImage();
        propertyUpDownImage = new ImageIcon("resources/property_norilsk.png").getImage();
        propertyLeftRightImage = new ImageIcon("resources/property_norilsk_left.png").getImage();

        initPlayerColors();
        initTiles();
    }

    private void initPlayerColors() {
        List<Player> players = controller.getAllPlayers();
        List<Color> availableColors = new ArrayList<>(Arrays.asList(PLAYER_COLORS));
        for (Player player : players) {
            Color selectedColor = availableColors.remove(0);
            playerColors.put(player, selectedColor);
        }
    }

    public Color getPlayerColor(Player player) {
        return playerColors.getOrDefault(player, Color.BLACK);
    }

    private void initTiles() {
        List<Tile> tiles = controller.getBoardTiles();
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
        Tile tile = controller.getBoardTiles().get(index);

        if (!useImageDesign) {
            label.setBackground(tile instanceof PropertyTile ? Color.WHITE : Color.LIGHT_GRAY);
            label.setText(generateTileLabelText(tile, index));
        } else {
            label.addMouseListener(new TileZoomMouseListener(label, 2.0));
        }
        return label;
    }

    public JLabel getTileLabel(int tileIndex) {
        return tileLabels.get(tileIndex);
    }

    public void setManualTileImage(int tileIndex, Image image) {
        if (tileIndex >= 0 && tileIndex < 40) {
            manualTileImages.put(tileIndex, image);
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
        final int OUTER_MARGIN = 30;
        final int TILE_WIDTH = 56;
        final int TILE_HEIGHT = 112;

        if (index == 0) return new Rectangle(DESIGN_SIZE - 112 - OUTER_MARGIN, DESIGN_SIZE - 112 - OUTER_MARGIN, 112, 112);
        if (index == 10) return new Rectangle(0 + OUTER_MARGIN, DESIGN_SIZE - 112 - OUTER_MARGIN, 112, 112);
        if (index == 20) return new Rectangle(0 + OUTER_MARGIN, 0 + OUTER_MARGIN, 112, 112);
        if (index == 30) return new Rectangle(DESIGN_SIZE - 112 - OUTER_MARGIN, 0 + OUTER_MARGIN, 112, 112);

        if (index > 0 && index < 10) {
            int x = (DESIGN_SIZE - 112) - (index * TILE_WIDTH) - OUTER_MARGIN;
            return new Rectangle(x, DESIGN_SIZE - 112 - OUTER_MARGIN, TILE_WIDTH, TILE_HEIGHT);
        }
        if (index > 10 && index < 20) {
            int y = DESIGN_SIZE - 112 - ((index - 10) * TILE_WIDTH) - OUTER_MARGIN;
            return new Rectangle(0 + OUTER_MARGIN, y, TILE_HEIGHT, TILE_WIDTH);
        }
        if (index > 20 && index < 30) {
            int x = 112 + ((index - 21) * TILE_WIDTH) + OUTER_MARGIN;
            return new Rectangle(x, 0 + OUTER_MARGIN, TILE_WIDTH, TILE_HEIGHT);
        }
        if (index > 30 && index < 40) {
            int y = 112 + ((index - 31) * TILE_WIDTH) + OUTER_MARGIN;
            return new Rectangle(DESIGN_SIZE - 112 - OUTER_MARGIN, y, TILE_HEIGHT, TILE_WIDTH);
        }
        return new Rectangle(0, 0, 0, 0);
    }

    private ImageIcon getTileImageIcon(int index, int w, int h) {
        Image image;
        if (manualTileImages.containsKey(index)) {
            image = manualTileImages.get(index);
        } else if (index == 0 || index == 10 || index == 20 || index == 30) {
            image = cornerImage;
        } else if (index >= 1 && index <= 9 || index >= 21 && index <= 29) {
            image = propertyUpDownImage;
        } else {
            image = propertyLeftRightImage;
        }

        if ((index >= 21 && index <= 29) || (index >= 31 && index <= 39)) {
            return getRotatedImageIcon(image, Math.PI, w, h);
        } else {
            Image scaled = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public void refreshBoard() {
        List<Tile> tiles = controller.getBoardTiles();
        for (int i = 0; i < tiles.size(); i++) {
            JLabel label = tileLabels.get(i);
            if (label != null) {
                label.setText(generateTileLabelText(tiles.get(i), i));
            }
        }
        repaint();
    }

    private String generateTileLabelText(Tile tile, int index) {
        return "<html><div style='text-align:center;'>" +
                tile.getName() + " (" + index + ")</div></html>";
    }
}
