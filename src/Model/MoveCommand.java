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
    private boolean passedGo;

    private List<PropertyTile> previousProperties;
    private List<RailroadTile> previousRailroads;

    public MoveCommand(MonopolyGame game, Player player, int[] dice) {
        this.game = game;
        this.player = player;
        this.dice = dice;

        // Snapshot state BEFORE turn
        this.oldPosition = player.getPosition();
        this.oldMoney = player.getMoney();
        this.wasInJail = player.isInJail();
        this.oldJailTurns = player.getJailTurnCount();
        this.passedGo = false;

        this.previousProperties = new ArrayList<>(player.getOwnedProperties());
        this.previousRailroads = new ArrayList<>(player.getOwnedRailroads());
    }

    @Override
    public void execute() {
        // Intentionally empty.
        // TurnHandler will process the move and update player state.
    }

    @Override
    public void undo() {
        // Restore position and basic state
        player.setPosition(oldPosition);
        player.setMoney(oldMoney);
        player.setInJail(wasInJail);
        player.setJailTurnCount(oldJailTurns);

        // Revert properties
        for (PropertyTile property : player.getOwnedProperties()) {
            property.setOwner(null);
        }
        player.setOwnedProperties(new ArrayList<>(previousProperties));
        for (PropertyTile p : previousProperties) {
            p.setOwner(player);
        }

        // Revert railroads
        for (RailroadTile railroad : player.getOwnedRailroads()) {
            railroad.setOwner(null);
        }
        player.setOwnedRailroads(new ArrayList<>(previousRailroads));
        for (RailroadTile r : previousRailroads) {
            r.setOwner(player);
        }

    }
}
