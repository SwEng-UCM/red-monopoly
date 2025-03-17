package Model;

import javax.swing.JOptionPane;
import java.util.Random;

public class CommunityChestTile extends Tile {
    private static final String[] COMMUNITY_CARDS = {
            "User Contribution Bonus: Collect 200 ₽.",
            "Critical Bug Report: Collect 100 ₽.",
            "Server Downtime: Pay 50 ₽.",
            "Feature Release Celebration: Collect 150 ₽.",
            "Code Refactoring Required: Pay 75 ₽.",
            "Advance to Community Center: Collect 200 ₽.",
            "Get Out of Bugzilla Free Card.",
            "Community Forum Highlights: Collect 50 ₽ from each player.",
            "Peer Review Delay: Skip your next turn.",
            "Data Breach: Pay 100 ₽ for damages."
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
            case 0: // User Contribution Bonus: Collect 200 ₽.
                player.addMoney(200);
                break;
            case 1: // Critical Bug Report: Collect 100 ₽.
                player.addMoney(100);
                break;
            case 2: // Server Downtime: Pay 50 ₽.
                player.deductMoney(50);
                break;
            case 3: // Feature Release Celebration: Collect 150 ₽.
                player.addMoney(150);
                break;
            case 4: // Code Refactoring Required: Pay 75 ₽.
                player.deductMoney(75);
                break;
            case 5: // Advance to Community Center: Collect 200 ₽.
                // Assuming the Community Center is at position 0
                player.setPosition(0);
                player.addMoney(200);
                break;
            case 6: // Get Out of Bugzilla Free Card.
                if (player.isInJail())
                    player.setInJail(false);
                break;
            case 7: // Community Forum Highlights: Collect 50 ₽ from each player.
                MonopolyGame game = MonopolyGame.getInstance();
                for (Player other : game.getPlayers()) {
                    if (!other.equals(player)) {
                        other.deductMoney(50);
                        player.addMoney(50);
                    }
                }
                break;
            case 8: // Peer Review Delay: Skip your next turn.
                player.skipNextTurn();
                break;
            case 9: // Data Breach: Pay 100 ₽ for damages.
                player.deductMoney(100);
                break;
            default:
                // Default case if needed in future modifications
                break;
        }
    }
}
