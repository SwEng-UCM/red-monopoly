package Model;

import Controller.Controller;

public class EasyAIStrategy implements AIStrategy {

    @Override
    public void playTurn(AIPlayer aiPlayer, MonopolyGame game, Controller controller) {
        System.out.println(aiPlayer.getName() + " (Easy AI) is taking its turn.");
        // Simple behavior: just roll dice and move using default game logic.
        int[] dice = controller.rollDice();
        String result = controller.movePlayerAfterDiceRoll(dice);
        System.out.println(result);
        // Additional simple decisions (like a random property purchase) can be added here.
    }
}
