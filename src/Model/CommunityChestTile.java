package Model;

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
    public String action(Player player) {
        Random random = new Random();
        int index = random.nextInt(COMMUNITY_CARDS.length);
        String card = COMMUNITY_CARDS[index];
        String effect = applyCommunityEffect(player, index);

        return "Community Chest Card: " + card + "\n" + effect;
    }

    private String applyCommunityEffect(Player player, int index) {
        switch (index) {
            case 0:
                player.addMoney(200);
                return "You collected 200 ₽ for your contribution.";
            case 1:
                player.addMoney(100);
                return "You collected 100 ₽ for exposing saboteurs.";
            case 2:
                player.deductMoney(50);
                return "You paid 50 ₽ due to a planning glitch.";
            case 3:
                player.addMoney(150);
                return "You collected 150 ₽ for your innovation.";
            case 4:
                player.deductMoney(75);
                return "You paid 75 ₽ for reorganization.";
            case 5:
                player.setPosition(0);
                player.addMoney(200);
                return "You advanced to Red Square and collected 200 ₽.";
            case 6:
                if (player.isInJail()) {
                    player.setInJail(false);
                    return "You used a Get Out of Gulag Free card.";
                } else {
                    return "You received a Get Out of Gulag Free card (but are not in jail).";
                }
            case 7:
                MonopolyGame game = MonopolyGame.getInstance();
                for (Player other : game.getPlayers()) {
                    if (!other.equals(player)) {
                        other.deductMoney(50);
                        player.addMoney(50);
                    }
                }
                return "You collected 50 ₽ from each player.";
            case 8:
                player.skipNextTurn();
                return "Bureaucratic delay! You will skip your next turn.";
            case 9:
                player.deductMoney(100);
                return "You paid 100 ₽ for state security damages.";
            default:
                return "";
        }
    }
}
