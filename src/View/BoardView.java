// src/View/BoardView.java
package View;

import Controller.Controller;
import Model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BoardView extends JLayeredPane {
    private final Controller controller;
    private final BoardPanel boardPanel;
    private final TokenOverlayPanel overlayPanel;

    public BoardView(Controller controller) {
        this.controller = controller;

        // Use null layout so both panels fill the same area
        setLayout(null);

        // BoardPanel draws the tiles
        boardPanel = new BoardPanel(controller);
        boardPanel.setBounds(0, 0, BoardPanel.DESIGN_SIZE, BoardPanel.DESIGN_SIZE);
        add(boardPanel, JLayeredPane.DEFAULT_LAYER);

        // TokenOverlayPanel draws the player tokens on top
        overlayPanel = new TokenOverlayPanel(controller, boardPanel);
        overlayPanel.setBounds(0, 0, BoardPanel.DESIGN_SIZE, BoardPanel.DESIGN_SIZE);
        add(overlayPanel, JLayeredPane.PALETTE_LAYER);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BoardPanel.DESIGN_SIZE, BoardPanel.DESIGN_SIZE);
    }

    @Override
    public void doLayout() {
        // Make both child panels fill this View
        boardPanel.setBounds(0, 0, getWidth(), getHeight());
        overlayPanel.setBounds(0, 0, getWidth(), getHeight());
    }

    /** Repaint just the board (tiles). */
    public void refreshBoard() {
        boardPanel.repaint();
    }

    /** Reload icons and repaint tokens for the given players. */
    public void drawPlayerTokens(List<Player> players) {
        overlayPanel.loadPlayerIcons();
        overlayPanel.repaint();
    }

    /** Expose boardPanel for raw access. */
    public BoardPanel getBoardPanel() {
        return boardPanel;
    }
}
