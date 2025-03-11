    package Controller;

    import Model.*;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.stream.Collectors;

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
                int jailTurns = current.getJailTurnCount();
                if (jailTurns < 2) { // First and second turns in Gulag: attempt doubles
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
                    } else {
                        message += "No doubles. You remain in Gulag.\n";
                        current.incrementJailTurn();
                        _game.nextTurn();
                        message += checkPlayerElimination();
                        return message;
                    }
                } else { // Third turn in Gulag
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
                        if (current.getMoney() >= JAIL_RELEASE_FEE) {
                            message += "\n" + "No doubles. You've been in Gulag for 3 turns so you pay a fee of " + JAIL_RELEASE_FEE + " ₽ to get out.\n";
                            current.deductMoney(JAIL_RELEASE_FEE);
                            current.setInJail(false);
                            current.resetJailTurn();
                            int roll = _game.rollDice();
                            _game.movePlayer(current, roll);
                            Tile tile = _game.getBoard().getTile(current.getPosition());
                            message += current.getName() + " rolled a " + roll + " and landed on " + tile.getName() + ".\n";
                        } else {
                            message += "No doubles and you cannot afford the fee. You remain in Gulag.\n";
                        }
                    }
                }
            } else { // Normal turn (player not in Gulag)
                int roll = _game.rollDice();
                _game.movePlayer(current, roll);
                Tile tile = _game.getBoard().getTile(current.getPosition());
                message += current.getName() + " rolled a " + roll + " and landed on " + tile.getName() + ".\n";

                if (tile instanceof GoToJailTile || tile instanceof JailTile) {
                    current.setInJail(true);
                    current.resetJailTurn();
                    message += "You landed on Go To Gulag. You are now in Gulag.\n";
                }

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

            // Check and remove any player with negative balance.
            message += checkPlayerElimination();

            // Only proceed with turn change if game not over.
            if (_game.getPlayers().size() > 1) {
                _game.nextTurn();
            }

            return message;
        }


        private String checkPlayerElimination() {
            StringBuilder eliminationMessage = new StringBuilder();
            List<Player> players = _game.getPlayers();

            // Use streams with Collectors.toList() instead of toList()
            List<String> eliminatedNames = players.stream()
                    .filter(p -> p.getMoney() < 0)
                    .map(Player::getName)
                    .collect(Collectors.toList());

            if (!eliminatedNames.isEmpty()) {
                eliminationMessage.append("Eliminated players due to negative balance: ");
                eliminationMessage.append(String.join(", ", eliminatedNames));
                eliminationMessage.append("\n");
                // Remove eliminated players from the game.
                players.removeIf(p -> p.getMoney() < 0);
            }

            // Check if only one player remains.
            if (players.size() == 1) {
                eliminationMessage.append("Game Over! Winner: ").append(players.get(0).getName()).append("\n");
                // Optionally, perform any additional game-over logic here.
            }
            return eliminationMessage.toString();
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
    }
