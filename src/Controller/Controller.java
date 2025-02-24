package Controller;

import Model.*;
import java.util.List;

public class Controller {
    private MonopolyGame _game;

    public Controller(MonopolyGame game) {
        this._game = game;
    }

    public void run_game(){

    }

    /**
     * Sets the number of players in the MonopolyGame model.
     */
    public void setNumberOfPlayers(int numPlayers, List<String> playerNames) {
        _game.setNumberOfPlayers(numPlayers, playerNames);
    }

    /**
     * Returns the name of the current player (for display in the UI).
     */
    public String getCurrentPlayerName() {
        return _game.getCurrentPlayer().getName();
    }

    /**
     * Returns the current player's balance.
     */
    public int getCurrentPlayerBalance() {
        return _game.getCurrentPlayer().getMoney();
    }

    /**
     * Rolls dice, moves the current player, performs the tile action,
     * and then advances to the next turn.
     * Returns a message about what happened, so the UI can display it.
     */
    public String rollDiceAndMove() {
        Player current = _game.getCurrentPlayer();
        int roll = _game.rollDice();
        _game.movePlayer(current, roll);

        // Get the tile the player landed on
        Tile tile = _game.getBoard().getTile(current.getPosition());
        String tileName = tile.getName();
        String message = current.getName() + " rolled a " + roll + " and landed on " + tileName + ".\n";

        // Check if the tile is a PropertyTile
        if (tile instanceof PropertyTile) {
            PropertyTile propertyTile = (PropertyTile) tile;
            if (propertyTile.getOwner() == null) {
                // Property is unowned
                message += "This property is unowned. Price: $" + propertyTile.getPrice() + ".\n";
            } else if (propertyTile.getOwner() != current) {
                // Property is owned by another player
                message += "This property is owned by " + propertyTile.getOwner().getName() + ". Rent: $" + propertyTile.getRent() + ".\n";
            } else {
                // Property is owned by the current player
                message += "This property is owned by you.\n";
            }
        }

        // Add the player's current balance to the message
        message += "Current balance: $" + current.getMoney() + "\n";

        // Advance to next player's turn
        _game.nextTurn();

        return message;
    }

    /**
     * An optional helper to get how many players are in the game,
     * if you need it in the UI for any reason.
     */
    public int getNumberOfPlayers() {
        return _game.getNumberOfPlayers();
    }
}