package Model;

import javax.swing.JOptionPane;
import java.util.List;
import java.util.stream.Collectors;

public class TurnHandler {
    private static final int JAIL_RELEASE_FEE = 50;
    private MonopolyGame game;

    public TurnHandler(MonopolyGame game) {
        this.game = game;
    }

    /**
     * Processes the turn of a player given the dice results.
     * @param player the player whose turn is processed.
     * @param dice an array of two integers representing the dice values.
     * @return A message summarizing the outcome of the turn.
     */
    public String processTurn(Player player, int[] dice) {
        StringBuilder message = new StringBuilder();

        if (player.isInJail()) {
            message.append(processJailTurn(player, dice));
        } else {
            message.append(processNormalTurn(player, dice));
        }

        message.append(checkPlayerElimination());

        // Advance turn if there is more than one player remaining.
        if (game.getPlayers().size() > 1) {
            game.nextTurn();
        }

        return message.toString();
    }

    private String processJailTurn(Player player, int[] dice) {
        StringBuilder message = new StringBuilder();
        int jailTurns = player.getJailTurnCount();

        String baseMessage = player.getName() + " is in Gulag (Turn " + (jailTurns + 1) +
                ") and rolled " + dice[0] + " and " + dice[1] + ".";
        JOptionPane.showMessageDialog(null, baseMessage);
        message.append(baseMessage).append("\n");

        if (jailTurns < 2) {
            if (dice[0] == dice[1]) {
                String msg = "Doubles! You're released from Gulag.";
                JOptionPane.showMessageDialog(null, msg);
                message.append(msg).append("\n");

                player.setInJail(false);
                player.resetJailTurn();
                int roll = dice[0] + dice[1];
                game.movePlayer(player, roll);
            } else {
                String msg = "No doubles. You remain in Gulag.";
                JOptionPane.showMessageDialog(null, msg);
                message.append(msg).append("\n");

                player.incrementJailTurn();
            }
        } else {
            if (dice[0] == dice[1]) {
                String msg = "Doubles! You're released from Gulag.";
                JOptionPane.showMessageDialog(null, msg);
                message.append(msg).append("\n");

                player.setInJail(false);
                player.resetJailTurn();
                int roll = dice[0] + dice[1];
                game.movePlayer(player, roll);
            } else {
                if (player.getMoney() >= JAIL_RELEASE_FEE) {
                    String msg = "No doubles. You've been in Gulag for 3 turns so you pay a fee of " +
                            JAIL_RELEASE_FEE + " ₽ to get out.";
                    JOptionPane.showMessageDialog(null, msg);
                    message.append(msg).append("\n");

                    player.deductMoney(JAIL_RELEASE_FEE);
                    player.setInJail(false);
                    player.resetJailTurn();
                    int feeDie1 = game.rollSingleDie();
                    int feeDie2 = game.rollSingleDie();
                    int roll = feeDie1 + feeDie2;
                    game.movePlayer(player, roll);
                } else {
                    String msg = "No doubles and you cannot afford the fee. You remain in Gulag.";
                    JOptionPane.showMessageDialog(null, msg);
                    message.append(msg).append("\n");
                }
            }
        }

        return message.toString();
    }



    private String processNormalTurn(Player player, int[] dice) {
        StringBuilder message = new StringBuilder();
        int roll = dice[0] + dice[1];

        game.movePlayer(player, roll);
        Tile tile = game.getBoard().getTile(player.getPosition());
        message.append(player.getName())
                .append(" rolled a ")
                .append(dice[0])
                .append(" and a ")
                .append(dice[1])
                .append(" (total: ")
                .append(roll)
                .append(") and landed on ")
                .append(tile.getName())
                .append(".\n");

        // Interaction based on tile type.
        if (tile instanceof FreeParkingTile) {
            message.append("You landed on Free Parking. Nothing happens.\n");
        }

        if (tile instanceof GoTile) {
            message.append("You passed GO. Collect 200 ₽.\n");
            player.addMoney(200);
        }

        if (tile instanceof GoToJailTile || tile instanceof JailTile) {
            player.setInJail(true);
            player.resetJailTurn();
            if (tile instanceof GoToJailTile) {
                player.setPosition(10);
            }
            message.append("You landed on Go To Gulag. You are now in Gulag.\n");
        }

        if (tile instanceof PropertyTile) {
            PropertyTile propertyTile = (PropertyTile) tile;
            if (propertyTile.getOwner() == null) {
                message.append("This property is unowned. Price: ")
                        .append(propertyTile.getPrice())
                        .append(" ₽.\n");
            } else if (!propertyTile.getOwner().equals(player)) {
                message.append("This property is owned by ")
                        .append(propertyTile.getOwner().getName())
                        .append(". Rent: ")
                        .append(propertyTile.getRent())
                        .append(" ₽.\n");
            } else {
                message.append("This property is owned by you.\n");
            }
        }

        if (tile instanceof TaxTile) {
            TaxTile taxTile = (TaxTile) tile;
            message.append("You landed on ")
                    .append(taxTile.getName())
                    .append(". Paying tax of ")
                    .append(taxTile.getTaxAmount())
                    .append(" ₽.\n");
            player.deductMoney(taxTile.getTaxAmount());
        }
        message.append("Current balance: ")
                .append(player.getMoney())
                .append(" ₽\n");
        return message.toString();
    }

    private String checkPlayerElimination() {
        List<Player> players = game.getPlayers();
        List<String> eliminatedNames = players.stream()
                .filter(p -> p.getMoney() < 0)
                .map(Player::getName)
                .collect(Collectors.toList());

        StringBuilder message = new StringBuilder();

        // Check eliminations
        if (!eliminatedNames.isEmpty()) {
            String eliminatedMsg = "Eliminated players due to negative balance: " + String.join(", ", eliminatedNames);
            message.append(eliminatedMsg).append("\n"); // Append to return string
            JOptionPane.showMessageDialog(null, eliminatedMsg, "Eliminations", JOptionPane.INFORMATION_MESSAGE); // Show dialog
            players.removeIf(p -> p.getMoney() < 0); // Remove eliminated players
        }

        // Check winner
        if (players.size() == 1) {
            String winnerMsg = "Game Over! Winner: " + players.get(0).getName();
            message.append(winnerMsg); // Append to return string
            JOptionPane.showMessageDialog(null, winnerMsg, "Game Over", JOptionPane.INFORMATION_MESSAGE); // Show dialog
        }

        return message.toString().trim(); // Return the combined message
    }

    private static int GLOBAL_DELAY_MS = 1000;

    public static void setGlobalDelay(int ms) {
        GLOBAL_DELAY_MS = ms;
    }

    public static int getGlobalDelay() {
        return GLOBAL_DELAY_MS;
    }


}
