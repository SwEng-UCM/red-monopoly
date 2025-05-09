package View;

import Controller.Controller;
import Model.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenOverlayPanel extends JPanel {
    private final Controller controller;
    private final BoardPanel boardPanel;
    private final Map<Integer, Image> playerIcons = new HashMap<>();

    public TokenOverlayPanel(Controller controller, BoardPanel boardPanel) {
        this.controller = controller;
        this.boardPanel = boardPanel;
        setOpaque(false);
        loadPlayerIcons();
    }

    public void loadPlayerIcons() {
        playerIcons.clear();
        List<Player> players = controller.getAllPlayers();
        for (int i = 0; i < players.size(); i++) {
            String avatarPath = players.get(i).getAvatarPath();
            URL avatarUrl = null;

            // 1) Strip any "resources/" prefix then ensure leading '/'
            if (avatarPath != null) {
                String resourcePath = avatarPath;
                if (resourcePath.startsWith("resources/"))
                    resourcePath = resourcePath.substring("resources".length());
                if (!resourcePath.startsWith("/"))
                    resourcePath = "/" + resourcePath;
                avatarUrl = getClass().getResource(resourcePath);
            }

            // 2) Fallback only if that failed
            if (avatarUrl == null)
                avatarUrl = getClass().getResource("/players/player1.png");

            // 3) Read & scale
            try {
                Image img = ImageIO.read(avatarUrl)
                        .getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                playerIcons.put(i, img);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        repaint();  // force a redraw with the new icons
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

            Rectangle bounds = tileLabel.getBounds();
            // Convert to this panel's coordinate space if needed:
            // SwingUtilities.convertRectangle(boardPanel, bounds, this);

            int iconSize = 60;
            int x = bounds.x + bounds.width - iconSize - 4;
            int y = bounds.y + 4;

            Image icon = playerIcons.get(i);
            if (icon != null) {
                g.drawImage(icon, x, y, this);
            }
        }
    }
}
