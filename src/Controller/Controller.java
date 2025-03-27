package Controller;

import Model.*;
import javax.swing.JOptionPane; // Import JOptionPane for displaying messages

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {
    private MonopolyGame _game;
    private static final int JAIL_RELEASE_FEE = 50; // Fee to pay on the third turn
    // New fields to store the last rolled dice values.
    private int lastDie1;
    private int lastDie2;
    private String aiDifficulty = "Easy";

    public Controller(MonopolyGame game) {
        this._game = game;
    }

    public void run_game(){
        // Your game loop logic here.
    }

    public void setAIDifficulty(String difficulty) {
        aiDifficulty = difficulty;
    }

    public String getAIDifficulty() {
        return aiDifficulty;
    }

    public void setNumberOfPlayers(int count, List<String> playerNames) {
        if (count < 1) count = 1;
        if (count > 8) count = 8;

        // Clear the existing players.
        _game.getPlayers().clear();
        for (int i = 0; i < count; i++) {
            String name = playerNames.get(i).trim();
            Player p;
            // Use a case-insensitive check after trimming.
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
        // Reset the current player index (make sure your game instance resets correctly).
        //
    }


    public String getCurrentPlayerName() {
        return _game.getCurrentPlayer().getName();
    }

    public int getCurrentPlayerBalance() {
        return _game.getCurrentPlayer().getMoney();
    }

    /**
     * Returns the results of the last dice roll as an array of two integers:
     * index 0 is the value of the first die and index 1 is the value of the second die.
     */
    /**
     * Rolls the dice without moving the player.
     * Returns an array of two ints: [die1, die2].
     */
    public int[] rollDice() {
        int die1 = _game.rollSingleDie();
        int die2 = _game.rollSingleDie();
        lastDie1 = die1;
        lastDie2 = die2;
        return new int[]{die1, die2};
    }

    /**
     * Applies the dice result to move the player.
     * This method uses the dice values passed as an argument.
     */
    public String movePlayerAfterDiceRoll(int[] dice) {
        Player current = _game.getCurrentPlayer();
        String message = "";

        if (current.isInJail()) {
            int jailTurns = current.getJailTurnCount();
            if (jailTurns < 2) { // First and second turns in Gulag: attempt doubles
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
                        // Roll dice again for movement after paying fee.
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
        } else { // Normal turn (player not in Gulag)
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

        // Proceed with turn change if game not over.
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
            JOptionPane.showMessageDialog(null, eliminationMessage); // Display elimination message in a dialog box
            players.removeIf(p -> p.getMoney() < 0);
        }

        if (players.size() == 1) {
            String winnerMessage = "Game Over! Winner: " + players.get(0).getName();
            JOptionPane.showMessageDialog(null, winnerMessage); // Display winner message in a dialog box
        }
    }

    public int getNumberOfPlayers() {
        return _game.getNumberOfPlayers();
    }

    public List<Player> getAllPlayers() {
        return _game.getPlayers(); // Assuming MonopolyGame has a getPlayers() method
    }

    public List<PropertyTile> getOwnedProperties(Player p) {
        List<PropertyTile> ownedProperties = new ArrayList<PropertyTile>();
        List<Tile> tiles = _game.getBoard().getTiles();
        for (Tile tile : tiles) {
            if (tile instanceof PropertyTile) {
                PropertyTile propertyTile = (PropertyTile) tile;
                if (propertyTile.getOwner() != null && propertyTile.getOwner().equals(p)) {
                    ownedProperties.add(propertyTile);
                }
            }
        }
        return ownedProperties;
    }

    public List<Tile> getBoardTiles() {
        return _game.getBoard().getTiles();
    }

    public MonopolyGame getMonopolyGame() {
        return _game;
    }

    public Player getCurrentPlayer(){
        return _game.getCurrentPlayer();
    }
}