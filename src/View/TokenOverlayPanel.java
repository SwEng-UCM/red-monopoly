// src/View/TokenOverlayPanel.java
package View;

import Controller.Controller;
import Model.Player;

import javax.swing.*;
import java.awt.*;
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

    /** Call this any time the player list changes. */
    public void loadPlayerIcons() {
        playerIcons.clear();
        List<Player> players = controller.getAllPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            String avatarPath = player.getAvatarPath();
            if (avatarPath == null) {
                avatarPath = "resources/players/player1.png";
            }
            ImageIcon icon = new ImageIcon(avatarPath);
            Image scaled = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            playerIcons.put(i, scaled);
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

            //Rectangle bounds = tileLabel.getBounds();
            Rectangle bounds = tileLabel.getBounds();
            //SwingUtilities.convertRectangle(boardPanel, bounds, this);


            int iconSize = 60;
            int x = bounds.x + bounds.width - iconSize - 4;
            int y = bounds.y + 4;

            Image icon = playerIcons.get(i);
            if (icon != null) {
                g.drawImage(icon, x, y, null);
            }
        }
    }
}
