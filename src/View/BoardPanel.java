package View;

import Controller.Controller;
import Model.Tile;
import Model.Player;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class BoardPanel extends JPanel {
    private Controller controller;
    private Image backgroundImage;

    // Define preferred sizes:
    // For example, we use a larger square for corners and smaller rectangles for edge tiles.
    private final Dimension cornerSize = new Dimension(120, 120);
    // For horizontal edge tiles (top and bottom), height equals corner height.
    private final Dimension horizontalTileSize = new Dimension(62, 120);
    // For vertical edge tiles (left and right), width equals corner width.
    private final Dimension verticalTileSize = new Dimension(120, 62);

    public BoardPanel(Controller controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        backgroundImage = new ImageIcon("resources/backgroundBoard.png").getImage();
        initGUI();
    }

    private void initGUI() {
        List<Tile> tiles = controller.getBoardTiles();
        if (tiles.size() != 40) {
            System.err.println("Warning: Expected 40 tiles for a standard board, got " + tiles.size());
        }

        // Create panels for each side.
        // The top and bottom rows include the corner tiles.
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setOpaque(false);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setOpaque(false);

        // The left and right columns contain only the non‑corner tiles.
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        // --- Top row (from left to right): top‑left corner, top edge non‑corners, top‑right corner ---
        // Top‑left corner: index 20
        topPanel.add(createTileLabel(tiles.get(20), 20, cornerSize));
        // Top edge non‑corner tiles: indices 21 to 29 (displayed in natural order)
        for (int i = 21; i <= 29; i++) {
            topPanel.add(createTileLabel(tiles.get(i), i, horizontalTileSize));
        }
        // Top‑right corner: index 30
        topPanel.add(createTileLabel(tiles.get(30), 30, cornerSize));

        // --- Bottom row (from left to right): bottom‑left corner, bottom edge non‑corners, bottom‑right corner ---
        // Note: For the correct clockwise order the bottom row is reversed relative to the list.
        // Bottom‑left corner: index 10
        bottomPanel.add(createTileLabel(tiles.get(10), 10, cornerSize));
        // Bottom edge non‑corner tiles: indices 9 down to 1 (so that left-to‑right on screen is from bottom‑left to bottom‑right)
        for (int i = 9; i >= 1; i--) {
            bottomPanel.add(createTileLabel(tiles.get(i), i, horizontalTileSize));
        }
        // Bottom‑right corner: index 0
        bottomPanel.add(createTileLabel(tiles.get(0), 0, cornerSize));

        // --- Left column (vertical; between bottom‑left and top‑left corners) ---
        // In clockwise order the left side goes from bottom‑left (index 10) to top‑left (index 20) via indices 11..19.
        // To display top-to‑bottom (top adjacent to top‑left) we reverse these: 19 down to 11.
        for (int i = 19; i >= 11; i--) {
            leftPanel.add(createTileLabel(tiles.get(i), i, verticalTileSize));
        }

        // --- Right column (vertical; between top‑right and bottom‑right corners) ---
        // Clockwise order on the right side is: top‑right (index 30), then indices 31..39, then bottom‑right (index 0).
        // We display the non‑corner tiles (indices 31 to 39) in natural order.
        for (int i = 31; i <= 39; i++) {
            rightPanel.add(createTileLabel(tiles.get(i), i, verticalTileSize));
        }

        // Assemble the board layout using BorderLayout.
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);

        // Center panel (could be used for board art or left blank)
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Helper method to create a JLabel for a tile.
     * It sets a border, preferred size, and displays the tile name, index, and any players on it.
     */
    private JLabel createTileLabel(Tile tile, int index, Dimension size) {
        String tileName = tile.getName();
        List<Player> playersOnTile = controller.getAllPlayers().stream()
                .filter(p -> p.getPosition() == index)
                .collect(Collectors.toList());
        String playersStr = playersOnTile.isEmpty() ? "" :
                "<br>Players: " + playersOnTile.stream()
                        .map(Player::getName)
                        .collect(Collectors.joining(", "));
        String labelText = "<html><center>" + tileName + "<br>(" + index + ")" + playersStr + "</center></html>";
        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.setOpaque(false);
        label.setPreferredSize(size);
        return label;
    }

    /**
     * Call this method to update the board. (You might want to maintain references to each label
     * so that you can update their text rather than recreating the panels.)
     */
    public void refreshBoard() {
        // For brevity, you might consider keeping a Map<Integer, JLabel> of labels keyed by tile index.
        // Then you can update each label's text based on the current game state.
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
