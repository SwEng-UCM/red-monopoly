package Model;

import Controller.Controller;

public class HardAIStrategy implements AIStrategy {


    @Override
    public void playTurn(AIPlayer aiPlayer, MonopolyGame game, Controller controller) {
        System.out.println(aiPlayer.getName() + " (Hard AI) is taking its turn.");
        // Hard strategy: more advanced decision-making can
        int[] dice = controller.rollDice();
        String result = controller.movePlayerAfterDiceRoll(dice);
        System.out.println(result);

        // Advanced decision-making might include:
        // - Evaluating board positions.
        // - Analyzing owned properties.
        // - Strategic buying decisions based on risk/reward calculations.
        // Implement additional logic as needed.



    }
}
