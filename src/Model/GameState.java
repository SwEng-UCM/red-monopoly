package Model;

import java.util.List;

public class GameState {
    private List<Player> players;
    private Board board;
    private int currentPlayerIndex;

    // A no-argument constructor is required for deserialization.
    public GameState() {
    }

    public GameState(List<Player> players, Board board, int currentPlayerIndex) {
        this.players = players;
        this.board = board;
        this.currentPlayerIndex = currentPlayerIndex;
    }

    // Public getters and setters
    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }
}
