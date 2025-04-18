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

    /**
     * Overloaded method so that the AI can use externally provided dice values
     * (for consistent dice animation).
     * By default, it ignores the passed dice and rolls its own.
     */
    default void playTurn(AIPlayer aiPlayer, MonopolyGame game, Controller controller, int[] diceValues) {
        // Default: ignore diceValues and call the original method.
        playTurn(aiPlayer, game, controller);
    }
    boolean shouldBuyTile(AIPlayer aiPlayer, Tile tile);

}
