package Model;

public class MoveCommand implements Command {
    private MonopolyGame game;
    private Player player;
    private int[] diceValues;
    private int prevPosition;
    private int prevMoney;

    public MoveCommand(MonopolyGame game, Player player, int[] diceValues) {
        this.game = game;
        this.player = player;
        this.diceValues = diceValues;
        // Capture state before executing the move.
        this.prevPosition = player.getPosition();
        this.prevMoney = player.getMoney();
    }

    @Override
    public void execute() {
        // Execute the move.
        int total = diceValues[0] + diceValues[1];
        game.movePlayer(player, total);
    }

    @Override
    public void undo() {
        // Restore player's previous position and money.
        player.setPosition(prevPosition);
        // Adjust money: For simplicity, restore exactly the previous amount.
        // In a full implementation, you'd want to reverse any side-effects (e.g. property purchase, rent, etc.)
        if(player.getMoney() != prevMoney) {
            if(player.getMoney() < prevMoney) {
                player.addMoney(prevMoney - player.getMoney());
            } else {
                player.deductMoney(player.getMoney() - prevMoney);
            }
        }
        System.out.println("Undo move: Restored " + player.getName() + " to position " + prevPosition);
    }
}
