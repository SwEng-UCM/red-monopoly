package Model;

import javax.swing.JOptionPane;
import java.util.Random;

public class ChanceTile extends Tile {
    private static final String[] CHANCE_CARDS = {
            "Five-Year Plan Success! Collect 200 ₽.",
            "Comrade, You've Been Promoted! Advance to Moscow and collect 50 ₽.",
            "State Inspection Finds Your Factory Inefficient! Pay 100 ₽.",
            "Your Loyalty to the Party is Rewarded! Gain 100 ₽.",
            "Commissar Orders a Housing Relocation! Move to the nearest unowned property.",
            "Glorious Agricultural Harvest! Collect 50 ₽ from each player.",
            "Your Work Unit Has Overperformed! Move forward 3 spaces and collect 25 ₽.",
            "KGB Investigation—Stay Quiet! Lose a turn while under review.",
            "Power Outage at Your Factory! Pay 75 ₽ for emergency repairs.",
            "You Are Sent to Siberia! Go to Jail. Do not pass GO, do not collect 200 ₽.",
            "State Infrastructure Project Needs Funds! Pay 150 ₽ to contribute to a new dam.",
            "Worker Uprising Delays Production! Skip your next turn.",
            "Bribe Party Official for Favorable Trade Deal! Pay 50 ₽, then take another turn.",
            "Censorship Bureau Approves Your Propaganda Poster! Collect 50 ₽.",
            "Shortage of Consumer Goods! Pay 25 ₽ for ration coupons."
    };

    public ChanceTile(String name, int position) {
        super(name, position);
    }

    @Override
    public void action(Player player) {
        Random random = new Random();
        int index = random.nextInt(CHANCE_CARDS.length);
        String card = CHANCE_CARDS[index];

        JOptionPane.showMessageDialog(null,
                "Chance Card: " + card,
                "Chance!",
                JOptionPane.INFORMATION_MESSAGE);

        applyChanceEffect(player, index);
    }

    private void applyChanceEffect(Player player, int index) {
        switch (index) {
            case 0: // Five-Year Plan Success!
                player.addMoney(200);
                break;
            case 1: // Promotion - Move to Moscow (assuming position 0 for Moscow)
                player.setPosition(0);
                player.addMoney(50);
                break;
            case 2: // Factory Inefficiency
                player.deductMoney(100);
                break;
            case 3: // Loyalty Reward
                player.addMoney(100);
                break;
            case 4: // Housing Relocation - Move to the nearest unowned property.
                Board board = Board.getInstance();
                int currentPos = player.getPosition();
                int boardSize = board.getSize();
                boolean found = false;
                for (int i = 1; i < boardSize; i++) {
                    int newPos = (currentPos + i) % boardSize;
                    Tile tile = board.getTile(newPos);
                    if (tile instanceof PropertyTile) {
                        PropertyTile propertyTile = (PropertyTile) tile;
                        if (propertyTile.getOwner() == null) {
                            player.setPosition(newPos);
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    JOptionPane.showMessageDialog(null,
                            "No unowned property found. You remain at your current position.",
                            "Housing Relocation",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case 5: // Agricultural Harvest - Collect 50 ₽ from each player.
                MonopolyGame game = MonopolyGame.getInstance();
                for (Player other : game.getPlayers()) {
                    if (!other.equals(player)) {
                        other.deductMoney(50);
                        player.addMoney(50);
                    }
                }
                break;
            case 6: // Work Unit Overperformance - Move forward 3 spaces and collect 25 ₽.
                player.setPosition(player.getPosition() + 3);
                player.addMoney(25);
                break;
            case 7: // KGB Investigation - Lose a turn.
                player.skipNextTurn();
                break;
            case 8: // Power Outage
                player.deductMoney(75);
                break;
            case 9: // Sent to Siberia - Go to Jail.
                player.goToJail();
                break;
            case 10: // Infrastructure Project
                player.deductMoney(150);
                break;
            case 11: // Worker Uprising - Skip your next turn.
                player.skipNextTurn();
                break;
            case 12: // Bribe Official - Pay 50 ₽, then take another turn.
                player.deductMoney(50);
                player.setExtraTurn(true);
                break;
            case 13: // Propaganda Poster
                player.addMoney(50);
                break;
            case 14: // Consumer Goods Shortage
                player.deductMoney(25);
                break;
        }
    }
}
