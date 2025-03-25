package Model;
import Controller.Controller;

public interface AIStrategy {
    /**
     * Defines how the AI player takes a turn.
     * @param aiPlayer The AI-controlled player.
     * @param game The Monopoly game instance.
     * @param controller The game controller (to use existing logic for dice and movement).
     */
    void playTurn(AIPlayer aiPlayer, MonopolyGame game, Controller controller);
}
