package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MonopolyGame {
    private List<Player> players;
    private Board board;
    private int currentPlayerIndex;
    private Random dice;

    public MonopolyGame() {
        players = new ArrayList<>();
        board = new Board();
        currentPlayerIndex = 0;
        dice = new Random();
        // We won't call initPlayers() here anymore, because we will let the user pick the # of players
    }

    /**
     * Sets the number of players in the game (from 1 up to 8) and creates them.
     * Resets the current player index to 0.
     */
    public void setNumberOfPlayers(int count, List<String> playerNames) {
        if (count < 1) count = 1;
        if (count > 8) count = 8;

        players.clear();
        for (int i = 0; i < count; i++) {
            players.add(new Player(playerNames.get(i))); // Use the provided player names
        }
        currentPlayerIndex = 0;
    }


    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Rolls two dice (2 x 1-6) and returns the sum
     */
    public int rollDice() {
        return (dice.nextInt(6) + 1) + (dice.nextInt(6) + 1);
    }


    // In MonopolyGame.java
    public int rollSingleDie() {
        return dice.nextInt(6) + 1;
    }


    /**
     * Moves a player by the specified steps. If passing beyond the board size,
     * it wraps around using modulo. Then triggers the tile action.
     */
    public void movePlayer(Player player, int steps) {
        int boardSize = board.getSize();
        int newPosition = (player.getPosition() + steps) % boardSize;
        player.setPosition(newPosition);

        // Perform the action of the tile just landed on
        Tile tile = board.getTile(newPosition);
        tile.action(player);
    }

    /**
     * Moves to the next player's turn in a round-robin fashion.
     */
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public Board getBoard() {
        return board;
    }

    /**
     * Returns the total number of players. Might be useful in the UI.
     */
    public int getNumberOfPlayers() {
        return players.size();
    }
}
