package Controller;

import Model.*;

public class Controller {
    private MonopolyGame _game;

    public Controller(MonopolyGame game) {
        this._game = game;
    }

    public void run_game(){
        // You can still leave this method empty or implement console logic if you want
    }

    /**
     * Sets the number of players in the MonopolyGame model.
     */
    public void setNumberOfPlayers(int numPlayers) {
        _game.setNumberOfPlayers(numPlayers);
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

        // Build a message about the player's move
        String tileName = _game.getBoard().getTile(current.getPosition()).getName();
        String message = current.getName() + " rolled a " + roll + " and landed on " + tileName + ".\n";
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