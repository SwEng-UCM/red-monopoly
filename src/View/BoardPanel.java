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
    private JPanel boardPanel;
    private JLabel[][] cellLabels;
    private int gridSize; // Computed grid dimension

    public BoardPanel(Controller controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        initGUI();
    }

    private void initGUI() {
        // Retrieve board tiles from the controller.
        List<Tile> tiles = controller.getBoardTiles();
        int numTiles = tiles.size();

        // Compute grid size 'n' such that 4*n - 4 >= numTiles.
        gridSize = (int) Math.ceil((numTiles + 4) / 4.0);

        // Create an n x n grid layout.
        boardPanel = new JPanel(new GridLayout(gridSize, gridSize));
        cellLabels = new JLabel[gridSize][gridSize];

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (isBorderCell(row, col, gridSize)) {
                    int tileIndex = getTileIndexForCell(row, col, gridSize);
                    String labelText = "";
                    if (tileIndex >= 0 && tileIndex < numTiles) {
                        Tile tile = tiles.get(tileIndex);
                        String tileName = tile.getName();

                        // Find players on this tile.
                        List<Player> playersOnTile = controller.getAllPlayers().stream()
                                .filter(p -> p.getPosition() == tileIndex)
                                .collect(Collectors.toList());
                        String playersStr = playersOnTile.isEmpty() ? "" :
                                "<br>Players: " + playersOnTile.stream()
                                        .map(Player::getName)
                                        .collect(Collectors.joining(", "));
                        labelText = "<html><center>" + tileName + "<br>(" + tileIndex + ")" + playersStr + "</center></html>";
                    }
                    JLabel cellLabel = new JLabel(labelText, SwingConstants.CENTER);
                    cellLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    cellLabel.setOpaque(true);
                    cellLabel.setBackground(Color.LIGHT_GRAY);
                    cellLabels[row][col] = cellLabel;
                    boardPanel.add(cellLabel);
                } else {
                    // Interior cells remain empty.
                    JLabel emptyLabel = new JLabel();
                    emptyLabel.setBackground(Color.WHITE);
                    cellLabels[row][col] = emptyLabel;
                    boardPanel.add(emptyLabel);
                }
            }
        }

        add(boardPanel, BorderLayout.CENTER);
    }

    /**
     * Checks if the cell at (row, col) is on the border of an n x n grid.
     */
    private boolean isBorderCell(int row, int col, int n) {
        return row == 0 || row == n - 1 || col == 0 || col == n - 1;
    }

    /**
     * Maps grid coordinates (row, col) in an n x n grid to the board tile index.
     * The ordering is clockwise starting from the bottom-right corner.
     */
    private int getTileIndexForCell(int row, int col, int n) {
        if (row == n - 1) { // Bottom row: right-to-left.
            return (n - 1) - col;
        } else if (col == 0) { // Left column: bottom-to-top.
            return (n - 1) + ((n - 1) - row);
        } else if (row == 0) { // Top row: left-to-right.
            return (2 * n - 2) + col;
        } else if (col == n - 1) { // Right column: top-to-bottom.
            return (3 * n - 3) + row;
        }
        return -1;
    }

    /**
     * Refreshes the board display by updating each border cell with the latest
     * tile name and any players currently on that tile.
     */
    public void refreshBoard() {
        List<Tile> tiles = controller.getBoardTiles();
        int numTiles = tiles.size();
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (isBorderCell(row, col, gridSize)) {
                    int tileIndex = getTileIndexForCell(row, col, gridSize);
                    String labelText = "";
                    if (tileIndex >= 0 && tileIndex < numTiles) {
                        Tile tile = tiles.get(tileIndex);
                        String tileName = tile.getName();
                        List<Player> playersOnTile = controller.getAllPlayers().stream()
                                .filter(p -> p.getPosition() == tileIndex)
                                .collect(Collectors.toList());
                        String playersStr = playersOnTile.isEmpty() ? "" :
                                "<br>Players: " + playersOnTile.stream()
                                        .map(Player::getName)
                                        .collect(Collectors.joining(", "));
                        labelText = "<html><center>" + tileName + "<br>(" + tileIndex + ")" + playersStr + "</center></html>";
                    }
                    cellLabels[row][col].setText(labelText);
                }
            }
        }
    }
}
