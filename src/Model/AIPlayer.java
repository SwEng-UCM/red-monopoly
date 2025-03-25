package Model;
import Controller.Controller;

public class AIPlayer extends Player {
    private AIStrategy strategy;

    public AIPlayer(String name, AIStrategy strategy) {
        super(name);
        this.strategy = strategy;
    }

    public void setStrategy(AIStrategy strategy) {
        this.strategy = strategy;
        System.out.println(getName() + " AI strategy set to " + strategy.getClass().getSimpleName());
    }

    /**
     * Called when it is the AI player's turn.
     */
    public void takeTurn(MonopolyGame game, Controller controller) {
        strategy.playTurn(this, game, controller);
    }
}
