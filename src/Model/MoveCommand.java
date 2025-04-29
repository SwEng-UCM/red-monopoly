package Model;

import java.util.ArrayList;
import java.util.List;

public class MoveCommand implements Command {
    private MonopolyGame game;
    private Player player;
    private int[] dice;
    private int oldPosition;
    private int oldMoney;
    private boolean wasInJail;
    private int oldJailTurns;
    private List<PropertyTile> propertiesBought;
    private List<RailroadTile> railroadsBought;
    private int jailFinePaid;
    private boolean passedGo;

    public MoveCommand(MonopolyGame game, Player player, int[] dice) {
        this.game = game;
        this.player = player;
        this.dice = dice;
        this.propertiesBought = new ArrayList<>();
        this.railroadsBought = new ArrayList<>();
        this.jailFinePaid = 0;
        this.passedGo = false;
    }

    @Override
    public void execute() {
        // Save important state BEFORE moving
        oldPosition = player.getPosition();
        oldMoney = player.getMoney();
        wasInJail = player.isInJail();
        oldJailTurns = player.getJailTurnCount();

        // Detect passing GO before move
        int boardSize = game.getBoard().getSize();
        int newPosition = (player.getPosition() + (dice[0] + dice[1])) % boardSize;
        if (newPosition < oldPosition) { // passed GO
            passedGo = true;
        }

        // Execute the actual move
        game.movePlayer(player, dice[0] + dice[1]);

        // After moving, detect if player bought something or paid something:
        Tile tile = game.getBoard().getTile(player.getPosition());

        // If bought property
        if (tile instanceof PropertyTile propertyTile) {
            if (propertyTile.getOwner() == player) {
                propertiesBought.add(propertyTile);
            }
        }
        if (tile instanceof RailroadTile railroadTile) {
            if (railroadTile.getOwner() == player) {
                railroadsBought.add(railroadTile);
            }
        }

        // Jail fine check (if moved from jail after paying)
        if (!wasInJail && player.isInJail()) {
            jailFinePaid = 50; // Assume standard fine
        }
    }

    @Override
    public void undo() {
        // Undo move and status
        player.setPosition(oldPosition);
        player.setMoney(oldMoney);
        player.setInJail(wasInJail);
        player.setJailTurnCount(oldJailTurns);

        // Undo passing GO bonus
        if (passedGo) {
            player.deductMoney(200);
        }

        // Undo purchases: Unset ownership of bought properties
        for (PropertyTile property : propertiesBought) {
            property.setOwner(null);
            player.getOwnedProperties().remove(property);
        }

        for (RailroadTile railroad : railroadsBought) {
            railroad.setOwner(null);
            player.getOwnedRailroads().remove(railroad);
        }

        // Undo jail fine payment
        if (jailFinePaid > 0) {
            player.addMoney(jailFinePaid);
        }
    }
}
