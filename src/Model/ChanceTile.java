package Model;

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
    public String action(Player player) {
        Random random = new Random();
        int index = random.nextInt(CHANCE_CARDS.length);
        String card = CHANCE_CARDS[index];
        String effect = applyChanceEffect(player, index);

        return "Chance Card: " + card + "\n" + effect;
    }

    private String applyChanceEffect(Player player, int index) {
        switch (index) {
            case 0:
                player.addMoney(200);
                return "You received 200 ₽.";
            case 1:
                player.setPosition(0);
                player.addMoney(50);
                return "You advanced to Moscow and received 50 ₽.";
            case 2:
                player.deductMoney(100);
                return "You paid 100 ₽ for factory inefficiency.";
            case 3:
                player.addMoney(100);
                return "You gained 100 ₽ for party loyalty.";
            case 4:
                Board board = Board.getInstance();
                int currentPos = player.getPosition();
                int boardSize = board.getSize();
                for (int i = 1; i < boardSize; i++) {
                    int newPos = (currentPos + i) % boardSize;
                    Tile tile = board.getTile(newPos);
                    if (tile instanceof PropertyTile propertyTile && propertyTile.getOwner() == null) {
                        player.setPosition(newPos);
                        return "You moved to the nearest unowned property: " + tile.getName();
                    }
                }
                return "No unowned property found. You stay in place.";
            case 5:
                MonopolyGame game = MonopolyGame.getInstance();
                for (Player other : game.getPlayers()) {
                    if (!other.equals(player)) {
                        other.deductMoney(50);
                        player.addMoney(50);
                    }
                }
                return "You collected 50 ₽ from each player.";
            case 6:
                player.setPosition(player.getPosition() + 3);
                player.addMoney(25);
                return "You moved forward 3 spaces and collected 25 ₽.";
            case 7:
                player.skipNextTurn();
                return "You are under KGB investigation and will skip your next turn.";
            case 8:
                player.deductMoney(75);
                return "You paid 75 ₽ for power outage repairs.";
            case 9:
                player.goToJail();
                return "You were sent to Siberia (Jail).";
            case 10:
                player.deductMoney(150);
                return "You paid 150 ₽ for the state infrastructure project.";
            case 11:
                player.skipNextTurn();
                return "Worker uprising! You will skip your next turn.";
            case 12:
                player.deductMoney(50);
                player.setExtraTurn(true);
                return "You bribed a party official. Pay 50 ₽ and take another turn.";
            case 13:
                player.addMoney(50);
                return "You earned 50 ₽ for your propaganda poster.";
            case 14:
                player.deductMoney(25);
                return "You paid 25 ₽ for ration coupons.";
            default:
                return "";
        }
    }
}
