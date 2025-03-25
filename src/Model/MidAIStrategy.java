package Model;

import Controller.Controller;

public class MidAIStrategy implements AIStrategy {
    @Override
    public void playTurn(AIPlayer aiPlayer, MonopolyGame game, Controller controller) {
        System.out.println(aiPlayer.getName() + " (Medium AI) is taking its turn.");
        // Roll dice and move.
        int[] dice = controller.rollDice();
        String result = controller.movePlayerAfterDiceRoll(dice);
        System.out.println(result);

        // Example heuristic:
        // Check if the AI landed on an unowned property and if it can afford it,
        // then decide to purchase.
        // (Implement actual buying logic as needed in your game.)
        // if(propertyIsUnowned && aiPlayer.getMoney() > propertyPrice * threshold) { buyProperty(); }
    }
}
