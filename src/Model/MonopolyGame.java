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
        initPlayers();
    }

    private void initPlayers() {
        players.add(new Player("Player 1"));
        players.add(new Player("Player 2"));
        // Add more players as needed
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public int rollDice() {
        return dice.nextInt(6) + 1 + dice.nextInt(6) + 1; // Rolling two dice
    }

    public void movePlayer(Player player, int steps) {
        int newPosition = (player.getPosition() + steps) % board.getSize();
        player.setPosition(newPosition);
    }

    public Board getBoard() {
        return board;
    }

    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }
}
