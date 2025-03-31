package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MonopolyGame {

    private static volatile MonopolyGame INSTANCE; // Volatile ensures visibility across threads

    private List<Player> players;
    private Board board;
    private int currentPlayerIndex;
    private Random dice;

    private MonopolyGame() { 
        players = new ArrayList<>();
        board = new Board();
        currentPlayerIndex = 0;
        dice = new Random();
    }

    public static MonopolyGame getInstance() {
        if (INSTANCE == null) { 
            synchronized (MonopolyGame.class) { 
                if (INSTANCE == null) {
                    INSTANCE = new MonopolyGame();
                }
            }
        }
        return INSTANCE;
    }

    public void setNumberOfPlayers(int count, List<String> playerNames) {
        if (count < 1) count = 1;
        if (count > 8) count = 8;

        players.clear();
        for (int i = 0; i < count; i++) {
            players.add(new Player(playerNames.get(i)));
        }
        currentPlayerIndex = 0;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public int rollDice() {
        return (dice.nextInt(6) + 1) + (dice.nextInt(6) + 1);
    }

    public int rollSingleDie() {
        return dice.nextInt(6) + 1;
    }

    public void movePlayer(Player player, int steps) {
        int boardSize = board.getSize();
        int newPosition = (player.getPosition() + steps) % boardSize;
        player.setPosition(newPosition);

        Tile tile = board.getTile(newPosition);
        tile.action(player);
    }

    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public Board getBoard() {
        return board;
    }

    public int getNumberOfPlayers() {
        return players.size();
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
}
