package View;

import Controller.Controller;
import java.awt.Dimension;
import javax.swing.JLayeredPane;

public class BoardView extends JLayeredPane {
    private BoardPanel boardPanel;
    private TokenOverlayPanel tokenOverlay;

    public BoardView(Controller controller) {
        // Create the board panel as before
        boardPanel = new BoardPanel(controller);
        // Create the token overlay and pass a reference to boardPanel
        tokenOverlay = new TokenOverlayPanel(controller, boardPanel);

        setLayout(null);
        // Add both at different layers
        add(boardPanel, Integer.valueOf(0));      // lower layer
        add(tokenOverlay, Integer.valueOf(1));    // upper layer

        // Match the board's base size, so layout managers can size us properly
        setPreferredSize(new Dimension(BoardPanel.DESIGN_SIZE, BoardPanel.DESIGN_SIZE));
    }

    @Override
    public void doLayout() {
        super.doLayout();
        int w = getWidth();
        int h = getHeight();

        double scale = Math.min(w / (double)BoardPanel.DESIGN_SIZE, h / (double)BoardPanel.DESIGN_SIZE);
        int scaledSize = (int) (BoardPanel.DESIGN_SIZE * scale);
        int offsetX = (w - scaledSize) / 2;
        int offsetY = (h - scaledSize) / 2;

        boardPanel.setBounds(offsetX, offsetY, scaledSize, scaledSize);
        tokenOverlay.setBounds(offsetX, offsetY, scaledSize, scaledSize);
    }


    /**
     * Call this after each turn so that tiles & tokens get updated.
     */
    public void refreshBoard() {
        boardPanel.refreshBoard();
        tokenOverlay.repaint();
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }
}
