package Model;
import Controller.Controller;


public class AIPlayer extends Player {
    {
        this.type = "AI";
    }


    private transient AIStrategy strategy; // <-- won't be serialized
    private String difficulty = "Easy";    // <-- helper for restoring strategy

    public AIPlayer(String name, AIStrategy strategy) {
        super(name);
        this.strategy = strategy;
        this.type = "AI";
        setDifficultyFromStrategy(strategy);
    }

    public void setStrategy(AIStrategy strategy) {
        this.strategy = strategy;
        setDifficultyFromStrategy(strategy);
    }

    public AIStrategy getStrategy() {
        return strategy;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    private void setDifficultyFromStrategy(AIStrategy strategy) {
        if (strategy instanceof EasyAIStrategy) difficulty = "Easy";
        else if (strategy instanceof MidAIStrategy) difficulty = "Medium";
        else if (strategy instanceof HardAIStrategy) difficulty = "Hard";
        else difficulty = "Easy";
    }

    public void restoreStrategyFromDifficulty() {
        switch (difficulty.toLowerCase()) {
            case "hard" -> strategy = new HardAIStrategy();
            case "medium" -> strategy = new MidAIStrategy();
            case "easy" -> strategy = new EasyAIStrategy();
            default -> strategy = new EasyAIStrategy();
        }
    }

    public void takeTurn(MonopolyGame game, Controller controller) {
        strategy.playTurn(this, game, controller);
    }

    public String takeTurnWithDice(int[] dice, MonopolyGame game, Controller controller) {
        int total = dice[0] + dice[1];
        game.movePlayer(this, total);
        Tile tile = game.getBoard().getTile(this.getPosition());
        return tile.action(this); // Now returns summary
    }


    @Override
    @com.google.gson.annotations.SerializedName("type")
    public String getType() {
        return "AI";
    }

}
