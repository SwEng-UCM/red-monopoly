package View;

import Controller.Controller;
import Model.Player;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TokenOverlayPanel extends JPanel {
    private Controller controller;
    private BoardPanel boardPanel;

    public TokenOverlayPanel(Controller controller, BoardPanel boardPanel) {
        this.controller = controller;
        this.boardPanel = boardPanel;
        setOpaque(false); // so the BoardPanel is visible underneath
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        List<Player> players = controller.getAllPlayers();
        int tokenDiameter = 12;

        for (Player player : players) {
            int tileIndex = player.getPosition();

            // Get the tile's JLabel from BoardPanel
            JLabel tileLabel = boardPanel.getTileLabel(tileIndex);
            if (tileLabel == null) {
                continue;
            }

            // tileLabel’s getBounds() is relative to BoardPanel
            // Because TokenOverlayPanel has the same (x,y,width,height) as BoardPanel,
            // we can use these coordinates directly to paint the token in the same place.
            Rectangle tileBounds = tileLabel.getBounds();

            // Example: place the token near the tile's top-right corner.
            int x = tileBounds.x + tileBounds.width - tokenDiameter - 4;
            int y = tileBounds.y + 4;

            // Or center it in the tile:
            // int x = tileBounds.x + (tileBounds.width / 2) - (tokenDiameter / 2);
            // int y = tileBounds.y + (tileBounds.height / 2) - (tokenDiameter / 2);

            // Fill the circle
            Color tokenColor = boardPanel.getPlayerColor(player);
            g.setColor(tokenColor);
            g.fillOval(x, y, tokenDiameter, tokenDiameter);

            // Optional outline
            g.setColor(Color.BLACK);
            g.drawOval(x, y, tokenDiameter, tokenDiameter);
        }
    }
}
