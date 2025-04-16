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

    @Override
    public boolean shouldBuyTile(AIPlayer aiPlayer, Tile tile) {
        int price = 0, rent = 0;
        if (tile instanceof PropertyTile) {
            price = ((PropertyTile) tile).getPrice();
            rent = ((PropertyTile) tile).getRent();
        } else if (tile instanceof RailroadTile) {
            price = ((RailroadTile) tile).getPrice();
            rent = ((RailroadTile) tile).getRent();
        }

        double roi = (double) rent / price; // simple return-on-investment check
        return aiPlayer.getMoney() > price && roi > 0.05; // buy only if ROI > 5%
    }

}
