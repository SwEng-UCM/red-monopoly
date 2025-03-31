package Controller;

import Model.*;
import Model.GameState;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

public class Controller {
    private MonopolyGame _game;
    private static final int JAIL_RELEASE_FEE = 50; // Fee to pay on the third turn
    private int lastDie1;
    private int lastDie2;
    private String aiDifficulty = "Easy";
    private Stack<Command> commandHistory = new Stack<>();

    public Controller(MonopolyGame game) {
        this._game = game;
    }

    public void run_game(){
        // Your game loop logic here.
    }

    // --- AI Difficulty and Player Setup ---
    public void setAIDifficulty(String difficulty) {
        aiDifficulty = difficulty;
    }

    public String getAIDifficulty() {
        return aiDifficulty;
    }

    public void setNumberOfPlayers(int count, List<String> playerNames) {
        if (count < 1) count = 1;
        if (count > 8) count = 8;

        _game.getPlayers().clear();
        for (int i = 0; i < count; i++) {
            String name = playerNames.get(i).trim();
            Player p;
            if(name.equalsIgnoreCase("ai")) {
                // Create an AI player with the selected difficulty.
                AIStrategy strategy;
                switch(aiDifficulty.toLowerCase()){
                    case "easy":
                        strategy = new Model.EasyAIStrategy();
                        break;
                    case "hard":
                        strategy = new Model.HardAIStrategy();
                        break;
                    case "medium":
                    default:
                        strategy = new Model.MidAIStrategy();
                        break;
                }
                System.out.println("Creating AI player at index " + i);
                p = new Model.AIPlayer(name, strategy);
            } else {
                System.out.println("Creating Human player '" + name + "' at index " + i);
                p = new Model.Player(name);
            }
            _game.getPlayers().add(p);
        }
        // Reset turn index if needed (ensure your MonopolyGame supports this)
        // For example: _game.setCurrentPlayerIndex(0);
    }

    public String getCurrentPlayerName() {
        return _game.getCurrentPlayer().getName();
    }

    public int getCurrentPlayerBalance() {
        return _game.getCurrentPlayer().getMoney();
    }

    // --- Dice Rolling and Movement ---
    public int[] rollDice() {
        int die1 = _game.rollSingleDie();
        int die2 = _game.rollSingleDie();
        lastDie1 = die1;
        lastDie2 = die2;
        return new int[]{die1, die2};
    }

    public String movePlayerAfterDiceRoll(int[] dice) {
        Player current = _game.getCurrentPlayer();
        String message = "";

        if (current.isInJail()) {
            int jailTurns = current.getJailTurnCount();
            if (jailTurns < 2) { // Attempt doubles
                message = current.getName() + " is in Gulag (Turn " + (jailTurns + 1) + ") and rolled "
                        + dice[0] + " and " + dice[1] + ". ";
                if (dice[0] == dice[1]) {
                    message += "Doubles! You're released from Gulag.\n";
                    current.setInJail(false);
                    current.resetJailTurn();
                    int roll = dice[0] + dice[1];
                    _game.movePlayer(current, roll);
                    Tile tile = _game.getBoard().getTile(current.getPosition());
                    message += "After release, you landed on " + tile.getName() + ".\n";
                } else {
                    message += "No doubles. You remain in Gulag.\n";
                    current.incrementJailTurn();
                    _game.nextTurn();
                    checkPlayerElimination();
                }
                JOptionPane.showMessageDialog(null, message);
            } else { // Third turn in Gulag
                message = current.getName() + " is in Gulag (Turn 3) and rolled "
                        + dice[0] + " and " + dice[1] + ". ";
                if (dice[0] == dice[1]) {
                    message += "Doubles! You're released from Gulag.\n";
                    current.setInJail(false);
                    current.resetJailTurn();
                    int roll = dice[0] + dice[1];
                    _game.movePlayer(current, roll);
                    Tile tile = _game.getBoard().getTile(current.getPosition());
                    message += "After release, you landed on " + tile.getName() + ".\n";
                } else {
                    if (current.getMoney() >= JAIL_RELEASE_FEE) {
                        message += "No doubles. You've been in Gulag for 3 turns so you pay a fee of "
                                + JAIL_RELEASE_FEE + " ₽ to get out.\n";
                        current.deductMoney(JAIL_RELEASE_FEE);
                        current.setInJail(false);
                        current.resetJailTurn();
                        int feeDie1 = _game.rollSingleDie();
                        int feeDie2 = _game.rollSingleDie();
                        lastDie1 = feeDie1;
                        lastDie2 = feeDie2;
                        int roll = feeDie1 + feeDie2;
                        _game.movePlayer(current, roll);
                        Tile tile = _game.getBoard().getTile(current.getPosition());
                        message += current.getName() + " rolled a " + roll
                                + " ([" + feeDie1 + " + " + feeDie2 + "]) and landed on " + tile.getName() + ".\n";
                    } else {
                        message += "No doubles and you cannot afford the fee. You remain in Gulag.\n";
                    }
                }
                JOptionPane.showMessageDialog(null, message);
            }
        } else { // Normal turn
            int roll = dice[0] + dice[1];
            _game.movePlayer(current, roll);
            Tile tile = _game.getBoard().getTile(current.getPosition());
            message += current.getName() + " rolled a " + dice[0] + " and a " + dice[1]
                    + " (total: " + roll + ") and landed on " + tile.getName() + ".\n";

            if (tile instanceof FreeParkingTile){
                message += "You landed on Free Parking. Nothing happens.\n";
            }

            if (tile instanceof GoTile) {
                message += "You passed GO. Collect 200 ₽.\n";
                current.addMoney(200);
            }

            if (tile instanceof GoToJailTile || tile instanceof JailTile) {
                current.setInJail(true);
                current.resetJailTurn();
                if (tile instanceof GoToJailTile){
                    current.setPosition(10);
                }
                message += "You landed on Go To Gulag. You are now in Gulag.\n";
            }

            if (tile instanceof PropertyTile) {
                PropertyTile propertyTile = (PropertyTile) tile;
                if (propertyTile.getOwner() == null) {
                    message += "This property is unowned. Price: " + propertyTile.getPrice() + " ₽.\n";
                } else if (propertyTile.getOwner() != current) {
                    message += "This property is owned by " + propertyTile.getOwner().getName()
                            + ". Rent: " + propertyTile.getRent() + " ₽.\n";
                } else {
                    message += "This property is owned by you.\n";
                }
            }

            if (tile instanceof TaxTile) {
                TaxTile taxTile = (TaxTile) tile;
                message += "You landed on " + taxTile.getName() + ". Paying tax of "
                        + taxTile.getTaxAmount() + " ₽.\n";
                current.deductMoney(taxTile.getTaxAmount());
            }
        }

        message += "Current balance: " + current.getMoney() + " ₽\n";
        checkPlayerElimination();

        if (_game.getPlayers().size() > 1) {
            _game.nextTurn();
        }

        return message;
    }

    private void checkPlayerElimination() {
        List<Player> players = _game.getPlayers();
        List<String> eliminatedNames = players.stream()
                .filter(p -> p.getMoney() < 0)
                .map(Player::getName)
                .collect(Collectors.toList());
        if (!eliminatedNames.isEmpty()) {
            String eliminationMessage = "Eliminated players due to negative balance: " +
                    String.join(", ", eliminatedNames);
            JOptionPane.showMessageDialog(null, eliminationMessage);
            players.removeIf(p -> p.getMoney() < 0);
        }
        if (players.size() == 1) {
            String winnerMessage = "Game Over! Winner: " + players.get(0).getName();
            JOptionPane.showMessageDialog(null, winnerMessage);
        }
    }

    // --- Command Pattern Support ---
    public void executeCommand(Command command) {
        command.execute();
        commandHistory.push(command);
    }

    public void undoLastCommand() {
        if (!commandHistory.isEmpty()) {
            Command command = commandHistory.pop();
            command.undo();
        } else {
            System.out.println("No commands to undo.");
        }
    }

    public void movePlayerWithCommand(int[] dice) {
        MoveCommand command = new MoveCommand(_game, _game.getCurrentPlayer(), dice);
        executeCommand(command);
    }

    public Stack<Command> getCommandHistory() {
        return commandHistory;
    }

    // --- Save/Load using Gson and RuntimeTypeAdapterFactory for Tile ---
    public void saveGame(String filename) {
        File dir = new File("games");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, filename);

        RuntimeTypeAdapterFactory<Tile> tileAdapterFactory = RuntimeTypeAdapterFactory
                .of(Tile.class, "tileType")
                .registerSubtype(GoTile.class, "GoTile")
                .registerSubtype(PropertyTile.class, "PropertyTile")
                .registerSubtype(ChanceTile.class, "ChanceTile")
                .registerSubtype(JailTile.class, "JailTile")
                .registerSubtype(GoToJailTile.class, "GoToJailTile")
                .registerSubtype(TaxTile.class, "TaxTile")
                .registerSubtype(CommunityChestTile.class, "CommunityChestTile")
                .registerSubtype(FreeParkingTile.class, "FreeParkingTile")
                .registerSubtype(RailroadTile.class, "RailroadTile")
                .registerSubtype(UtilityTile.class, "UtilityTile");

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(tileAdapterFactory)
                .setPrettyPrinting()
                .create();

        // Use the current player index from _game (ensure getCurrentPlayerIndex() is defined)
        GameState state = new GameState(
                _game.getPlayers(),
                _game.getBoard(),
                _game.getCurrentPlayerIndex()
        );
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(state, writer);
            System.out.println("Game saved to " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGame(String filename) {
        // Attempt to open the file using the provided filename.
        File file = new File(filename);
        // If the file does not exist, assume it's a relative name and look in "games" folder.
        if (!file.exists()) {
            file = new File("games", filename);
        }

        RuntimeTypeAdapterFactory<Tile> tileAdapterFactory = RuntimeTypeAdapterFactory
                .of(Tile.class, "tileType")
                .registerSubtype(GoTile.class, "GoTile")
                .registerSubtype(PropertyTile.class, "PropertyTile")
                .registerSubtype(ChanceTile.class, "ChanceTile")
                .registerSubtype(JailTile.class, "JailTile")
                .registerSubtype(GoToJailTile.class, "GoToJailTile")
                .registerSubtype(TaxTile.class, "TaxTile")
                .registerSubtype(CommunityChestTile.class, "CommunityChestTile")
                .registerSubtype(FreeParkingTile.class, "FreeParkingTile")
                .registerSubtype(RailroadTile.class, "RailroadTile")
                .registerSubtype(UtilityTile.class, "UtilityTile");

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(tileAdapterFactory)
                .create();

        try (FileReader reader = new FileReader(file)) {
            GameState state = gson.fromJson(reader, GameState.class);
            if (state == null || state.getPlayers() == null || state.getPlayers().isEmpty()) {
                System.err.println("Loaded game state is empty. Check file: " + file.getAbsolutePath());
                return;
            }
            _game.getPlayers().clear();
            _game.getPlayers().addAll(state.getPlayers());
            // Optionally restore board and current turn index:
            // _game.setBoard(state.getBoard());
            // _game.setCurrentPlayerIndex(state.getCurrentPlayerIndex());
            System.out.println("Game loaded from " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MonopolyGame getMonopolyGame() {
        return _game;
    }

    public Player getCurrentPlayer(){
        return _game.getCurrentPlayer();
    }

    public List<Tile> getBoardTiles() {
        return _game.getBoard().getTiles();
    }


    public List<Player> getAllPlayers() {
        return _game.getPlayers();
    }



}

