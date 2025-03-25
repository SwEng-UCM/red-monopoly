package View;

import Controller.Controller;
import Model.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;

/**
 * A single JPanel that lays out all 40 Monopoly tiles around the edges,
 * dynamically scaling to fill the entire window.
 */
public class BoardPanel extends JPanel {
    private Controller controller;
    private Image backgroundImage;

    // A map of tileIndex -> JLabel (the visual for that tile)
    private Map<Integer, JLabel> tileLabels = new HashMap<>();

    public BoardPanel(Controller controller) {
        this.controller = controller;

        // We do our own manual sizing of components, so turn off automatic layout
        setLayout(null);

        // Optional: a default size if the parent doesn't set one
        setPreferredSize(new Dimension(1000, 1000));

        // Load your background image
        backgroundImage = new ImageIcon("resources/backgroundBoard.png").getImage();

        initTiles();
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
            Tile tile = tiles.get(i);
            JLabel tileLabel = createTileLabel(tile, i);
            tileLabels.put(i, tileLabel);
            // Add the label to this panel. We'll position/size it in doLayout().
            add(tileLabel);
        }
    }

    /**
     * Creates a JLabel for a specific tile index.
     */
    private JLabel createTileLabel(Tile tile, int index) {
        JLabel label = new JLabel("", SwingConstants.CENTER);

        // Make it opaque so we can see its background
        label.setOpaque(true);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // We won't rely on label.setBackground for property color anymore
        // because we'll do the color strip via HTML.
        // Let's set property tiles to white, others to LIGHT_GRAY:
        if (tile instanceof PropertyTile) {
            label.setBackground(Color.WHITE);
        } else {
            label.setBackground(Color.LIGHT_GRAY);
        }

        // Set icon for the tile (optional)
        label.setIcon(getTileIcon(tile));
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setVerticalTextPosition(SwingConstants.BOTTOM);

        // Initial text (tile name + index + color header if property).
        label.setText(generateTileLabelText(tile, index));

        return label;
    }

    /**
     * Lays out the tile labels so they form a ring around the edges,
     * automatically scaling to fill the entire panel.
     */
    @Override
    public void doLayout() {
        super.doLayout();

        // Figure out how big each "cell" in an 11×11 ring is
        int w = getWidth();
        int h = getHeight();
        int cellW = w / 11;   // integer division
        int cellH = h / 11;

        // Position each tile
        for (int i = 0; i < 40; i++) {
            JLabel label = tileLabels.get(i);
            if (label == null) continue;

            Point rc = getTileRowCol(i); // row, col
            int row = rc.x;
            int col = rc.y;

            int x = col * cellW;
            int y = row * cellH;

            // If col=10, we might want x = w - cellW (to avoid leftover px)
            // If row=10, we might want y = h - cellH
            // But simplest is just col * cellW, row * cellH.

            label.setBounds(x, y, cellW, cellH);
        }
    }

    /**
     * Returns a (row,col) for the given tile index in "ring" order.
     * 
     *  - Indices 0..10 => bottom row, but reversed so 0 is at col=10, 10 is col=0
     *  - Indices 11..19 => left column bottom->top
     *  - Indices 20..30 => top row left->right
     *  - Indices 31..39 => right column top->bottom
     *
     * We'll return as Point(row, col).
     */
    private Point getTileRowCol(int index) {
        if (index >= 0 && index <= 10) {
            // Bottom row (reverse): row=10, col=10 - index
            int col = 10 - index;
            return new Point(10, col);

        } else if (index >= 11 && index <= 19) {
            // Left column bottom->top
            // col=0, row from 9..1
            int offset = index - 11;      // 0..8
            int row = 9 - offset;         // 9..1
            return new Point(row, 0);

        } else if (index >= 20 && index <= 30) {
            // Top row left->right
            // row=0, col=0..10
            int offset = index - 20;      // 0..10
            return new Point(0, offset);

        } else if (index >= 31 && index <= 39) {
            // Right column top->bottom
            // col=10, row=1..9
            int offset = index - 31;      // 0..8
            int row = 1 + offset;         // 1..9
            return new Point(row, 10);
        }

        // Fallback if something outside 0..39
        return new Point(0, 0);
    }

    /**
     * Build the text for the tile label. 
     * If it's a PropertyTile, we insert a colored "header" bar at the top.
     */
    private String generateTileLabelText(Tile tile, int index) {
        // We build HTML that has a top "header" (possibly colored),
        // then a body with tile name, index, and any players.

        StringBuilder sb = new StringBuilder();
        sb.append("<html>");

        if (tile instanceof PropertyTile) {
            // We'll do a little colored bar at the top
            Color headerColor = getPropertyHeaderColorByIndex(index);
            String colorHex = toHexString(headerColor);
            sb.append("<div style='background-color:")
              .append(colorHex)
              .append("; width:100%; height:14px;'></div>");
        }

        // Now the body (white background, or default if you like)
        sb.append("<div style='padding:2px; text-align:center;'>");
        sb.append(tile.getName()).append(" (").append(index).append(")<br>");

        // Show any players on this tile
        List<Player> playersOnTile = controller.getAllPlayers().stream()
                .filter(p -> p.getPosition() == index)
                .collect(Collectors.toList());

// Remove the conflict markers, keep Helgi’s for-loop
for (Player p : playersOnTile) {
    // Example: a colored circle
    Color c = getPlayerColor(p);
    sb.append(String.format(
        "<span style='color:rgb(%d,%d,%d); font-size:18px;'>&#9679;</span> ",
        c.getRed(), c.getGreen(), c.getBlue()
    ));
}

        }

        sb.append("</div>"); // close body div
        sb.append("</html>");

        return sb.toString();
    }

    /**
     * Convert a Color to #RRGGBB hex string.
     */
    private String toHexString(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    /**
     * Example color for each player.  You can replace with your own logic.
     */
    private Color getPlayerColor(Player p) {
        switch (p.getName().toLowerCase()) {
            case "player1": return Color.RED;
            case "player2": return Color.BLUE;
            case "player3": return Color.GREEN;
            default:        return Color.MAGENTA;
        }
    }

 /****
 * Returns a Color for the "header strip" of each property tile,
 * using a custom Soviet-inspired palette.
 */
private Color getPropertyHeaderColorByIndex(int index) {
    // Brown group (2 properties)
    if (index == 1 || index == 3) {
        // A dark russet/brown
        return new Color(0x7B3F00);
    }

    // Light Blue group (3 properties)
    if (index == 6 || index == 8 || index == 9) {
        // Muted Soviet teal/blue
        return new Color(0x5085A5);
    }

    // Pink group (3 properties)
    if (index == 11 || index == 13 || index == 14) {
        // A warm pink/red
        return new Color(0xC94C62);
    }

    // Orange group (3 properties)
    if (index == 16 || index == 18 || index == 19) {
        // Earthy Soviet orange
        return new Color(0xC7771E);
    }

    // Red group (3 properties)
    if (index == 21 || index == 23 || index == 24) {
        // Deep Soviet red
        return new Color(0xA40000);
    }

    // Yellow group (3 properties)
    if (index == 26 || index == 27 || index == 29) {
        // A bold golden hue
        return new Color(0xFFD700);
    }

    // Green group (3 properties)
    if (index == 31 || index == 32 || index == 34) {
        // Dark, military-style green
        return new Color(0x3C7D3C);
    }

    // Dark Blue group (2 properties)
    if (index == 37 || index == 39) {
        // Deep navy / midnight blue
        return new Color(0x14213D);
    }

    // Fallback / default for anything else
    return new Color(200, 200, 200);
}


    /**
     * Decide which icon to use based on tile type.
     */
    private Icon getTileIcon(Tile tile) {
        if (tile instanceof PropertyTile) {
            return new ImageIcon("resources/icons/property.png");
        } else if (tile instanceof JailTile) {
            return new ImageIcon("resources/icons/jail.png");
        } else if (tile instanceof GoTile) {
            return new ImageIcon("resources/icons/go.png");
        } else if (tile instanceof TaxTile) {
            return new ImageIcon("resources/icons/tax.png");
        } else if (tile instanceof ChanceTile) {
            return new ImageIcon("resources/icons/chance.png");
        } else if (tile instanceof CommunityChestTile) {
            return new ImageIcon("resources/icons/community_chest.png");
        } else {
            return new ImageIcon("resources/icons/default.png");
        }
    }

    /**
     * Paint the background image stretched to fill this panel.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    /**
     * Call this after each turn so tile labels can be updated 
     * (for example, to add the current positions of each player).
     */
    public void refreshBoard() {
        List<Tile> tiles = controller.getBoardTiles();
        for (int i = 0; i < tiles.size(); i++) {
            Tile tile = tiles.get(i);
            JLabel label = tileLabels.get(i);
            if (label != null) {
                label.setText(generateTileLabelText(tile, i));
            }
        }
        repaint();
    }
}
