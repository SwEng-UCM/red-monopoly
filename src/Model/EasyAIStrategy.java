package Model;

import Controller.Controller;

public class EasyAIStrategy implements AIStrategy {
    @Override
    public void playTurn(AIPlayer aiPlayer, MonopolyGame game, Controller controller, int[] diceValues) {
        System.out.println(aiPlayer.getName() + " (Easy AI) is taking its turn with dice: "
                + diceValues[0] + " and " + diceValues[1]);
        String result = controller.movePlayerAfterDiceRoll(diceValues);
        System.out.println(result);
        // Additional simple decisions can be added here.
    }

    @Override
    public void playTurn(AIPlayer aiPlayer, MonopolyGame game, Controller controller) {
        int[] diceValues = controller.rollDice();
        playTurn(aiPlayer, game, controller, diceValues);
    }
}
