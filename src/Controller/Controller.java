package Controller;

import Model.*;
import java.util.List;

public class Controller {
    private MonopolyGame _game;
    private static final int JAIL_RELEASE_FEE = 50; // Fee to pay on the third turn

    public Controller(MonopolyGame game) {
        this._game = game;
    }

    public void run_game(){
        // Your game loop logic here.
    }

    public void setNumberOfPlayers(int numPlayers, List<String> playerNames) {
        _game.setNumberOfPlayers(numPlayers, playerNames);
    }

    public String getCurrentPlayerName() {
        return _game.getCurrentPlayer().getName();
    }

    public int getCurrentPlayerBalance() {
        return _game.getCurrentPlayer().getMoney();
    }

    public String rollDiceAndMove() {
        Player current = _game.getCurrentPlayer();
        String message = "";

        if (current.isInJail()) {
            // Player is in Gulag. Check how many turns they've spent here.
            // Assume that the Player class has:
            //   int jailTurnCount;
            //   getJailTurnCount(), incrementJailTurn(), and resetJailTurn() methods.
            int jailTurns = current.getJailTurnCount();

            // On the third turn (jailTurns == 2 already, so this is the third attempt), offer fee option if doubles not rolled.
            if (jailTurns < 2) { // First and second turns in jail: attempt doubles
                int die1 = _game.rollSingleDie();
                int die2 = _game.rollSingleDie();
                message += current.getName() + " is in Gulag (Turn " + (jailTurns + 1) + ") and rolled " + die1 + " and " + die2 + ". ";
                if (die1 == die2) {
                    message += "Doubles! You're released from Gulag.\n";
                    current.setInJail(false);
                    current.resetJailTurn();
                    int roll = die1 + die2;
                    _game.movePlayer(current, roll);
                    Tile tile = _game.getBoard().getTile(current.getPosition());
                    message += "After release, you landed on " + tile.getName() + ".\n";
                    // (Optional) Handle PropertyTile logic if needed.
                } else {
                    message += "No doubles. You remain in Gulag.\n";
                    current.incrementJailTurn();
                    _game.nextTurn();
                    return message;
                }
            } else { // This is the third turn in jail
                int die1 = _game.rollSingleDie();
                int die2 = _game.rollSingleDie();
                message += current.getName() + " is in Gulag (Turn 3) and rolled " + die1 + " and " + die2 + ". ";
                if (die1 == die2) {
                    message += "Doubles! You're released from Gulag.\n";
                    current.setInJail(false);
                    current.resetJailTurn();
                    int roll = die1 + die2;
                    _game.movePlayer(current, roll);
                    Tile tile = _game.getBoard().getTile(current.getPosition());
                    message += "After release, you landed on " + tile.getName() + ".\n";
                } else {
                    // Attempt fee payment
                    if (current.getMoney() >= JAIL_RELEASE_FEE) {
                        message += "No doubles. You've been in Gulag for 3 turns so you pay a fee of " + JAIL_RELEASE_FEE + " ₽ to get out.\n";
                        current.deductMoney(JAIL_RELEASE_FEE);
                        current.setInJail(false);
                        current.resetJailTurn();
                        int roll = _game.rollDice();
                        _game.movePlayer(current, roll);
                        Tile tile = _game.getBoard().getTile(current.getPosition());
                        message += current.getName() + " rolled a " + roll + " and landed on " + tile.getName() + ".\n";
                        // (Optional) Handle PropertyTile logic if needed.
                    } else {
                        message += "No doubles and you cannot afford the fee. You remain in Gulag.\n";
                        // You might want to leave jailTurnCount at 3 or reset it based on your game rules.
                    }
                }
            }
        } else { // Normal turn (player not in Gulag)
            int roll = _game.rollDice();
            _game.movePlayer(current, roll);
            Tile tile = _game.getBoard().getTile(current.getPosition());
            message += current.getName() + " rolled a " + roll + " and landed on " + tile.getName() + ".\n";

            // Check if the player landed on a tile that sends them to Gulag.
            if (tile instanceof GoToJailTile || tile instanceof JailTile) {
                current.setInJail(true);
                current.resetJailTurn(); // Ensure jail turn counter starts at 0.
                message += "You landed on Go To Gulag. You are now in Gulag.\n";
            }

            // Handle PropertyTile specifics.
            if (tile instanceof PropertyTile) {
                PropertyTile propertyTile = (PropertyTile) tile;
                if (propertyTile.getOwner() == null) {
                    message += "This property is unowned. Price: " + propertyTile.getPrice() + " ₽.\n";
                } else if (propertyTile.getOwner() != current) {
                    message += "This property is owned by " + propertyTile.getOwner().getName() + ". Rent: " + propertyTile.getRent() + " ₽.\n";
                } else {
                    message += "This property is owned by you.\n";
                }
            }
        }

        message += "Current balance: " + current.getMoney() + " ₽\n";
        _game.nextTurn();
        return message;
    }

    public int getNumberOfPlayers() {
        return _game.getNumberOfPlayers();
    }
}
