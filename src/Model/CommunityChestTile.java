package Model;

import javax.swing.JOptionPane;
import java.util.Random;

public class CommunityChestTile extends Tile {
    private static final String[] COMMUNITY_CARDS = {
            "Glorious Contribution to the Motherland: Collect 200 ₽.",
            "Exposing Saboteurs of Progress: Collect 100 ₽.",
            "Central Planning Glitch: Pay 50 ₽.",
            "Celebration of Revolutionary Innovation: Collect 150 ₽.",
            "Mandatory Technological Reorganization: Pay 75 ₽.",
            "Advance to Red Square: Collect 200 ₽.",
            "Get Out of Gulag Free Card.",
            "Comrade Solidarity Contribution: Collect 50 ₽ from each player.",
            "Bureaucratic Delay: Skip your next turn.",
            "Breach of State Security: Pay 100 ₽ for damages."
    };

    public CommunityChestTile(String name, int position) {
        super(name, position);
    }

    @Override
    public void action(Player player) {
        Random random = new Random();
        int index = random.nextInt(COMMUNITY_CARDS.length);
        String card = COMMUNITY_CARDS[index];

        JOptionPane.showMessageDialog(null,
                "Community Chest Card: " + card,
                "Community Chest!",
                JOptionPane.INFORMATION_MESSAGE);

        applyCommunityEffect(player, index);
    }

    private void applyCommunityEffect(Player player, int index) {
        switch (index) {
            case 0: // Glorious Contribution to the Motherland: Collect 200 ₽.
                player.addMoney(200);
                break;
            case 1: // Exposing Saboteurs of Progress: Collect 100 ₽.
                player.addMoney(100);
                break;
            case 2: // Central Planning Glitch: Pay 50 ₽.
                player.deductMoney(50);
                break;
            case 3: // Celebration of Revolutionary Innovation: Collect 150 ₽.
                player.addMoney(150);
                break;
            case 4: // Mandatory Technological Reorganization: Pay 75 ₽.
                player.deductMoney(75);
                break;
            case 5: // Advance to Red Square: Collect 200 ₽.
                // Assuming Red Square is at position 0
                player.setPosition(0);
                player.addMoney(200);
                break;
            case 6: // Get Out of Gulag Free Card.
                if (player.isInJail())
                    player.setInJail(false);
                break;
            case 7: // Comrade Solidarity Contribution: Collect 50 ₽ from each player.
                MonopolyGame game = MonopolyGame.getInstance();
                for (Player other : game.getPlayers()) {
                    if (!other.equals(player)) {
                        other.deductMoney(50);
                        player.addMoney(50);
                    }
                }
                break;
            case 8: // Bureaucratic Delay: Skip your next turn.
                player.skipNextTurn();
                break;
            case 9: // Breach of State Security: Pay 100 ₽ for damages.
                player.deductMoney(100);
                break;
            default:
                // Default case if needed in future modifications
                break;
        }
    }
}
