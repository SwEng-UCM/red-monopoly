package View;

import Controller.Controller;
import Model.Player;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class TokenOverlayPanel extends JPanel {
    private Controller controller;
    private BoardPanel boardPanel;
    private Map<Integer, Image> playerIcons;

    public TokenOverlayPanel(Controller controller, BoardPanel boardPanel) {
        this.controller = controller;
        this.boardPanel = boardPanel;
        this.playerIcons = new HashMap<>();
        setOpaque(false); // Transparent background

        loadPlayerIcons();
    }

    private void loadPlayerIcons() {
        List<Player> players = controller.getAllPlayers();
        for (int i = 0; i < players.size(); i++) {
            String imagePath = "resources/players/player" + (i + 1) + ".png";
            ImageIcon icon = new ImageIcon(imagePath);
            // Resize if needed (e.g., 24x24)
            Image scaledIcon = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            playerIcons.put(i, scaledIcon);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        List<Player> players = controller.getAllPlayers();

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            int tileIndex = player.getPosition();
            JLabel tileLabel = boardPanel.getTileLabel(tileIndex);

            if (tileLabel == null) continue;

            Rectangle tileBounds = tileLabel.getBounds();

            // Top-right corner offset
            int iconSize = 60;
            int x = tileBounds.x + tileBounds.width - iconSize - 4;
            int y = tileBounds.y + 4;

            Image icon = playerIcons.get(i);
            if (icon != null) {
                g.drawImage(icon, x, y, null);
            }
        }
    }
}
