package Model;

import Controller.Controller;

public class EasyAIStrategy implements AIStrategy {
    @Override
    public void playTurn(AIPlayer aiPlayer, MonopolyGame game, Controller controller, int[] diceValues) {
        System.out.println(aiPlayer.getName() + " (Easy AI) is taking its turn with dice: "
                + diceValues[0] + " and " + diceValues[1]);
        String result = controller.movePlayerAfterDiceRoll(diceValues);
        System.out.println(result);

    }

    @Override
    public void playTurn(AIPlayer aiPlayer, MonopolyGame game, Controller controller) {
        int[] diceValues = controller.rollDice();
        playTurn(aiPlayer, game, controller, diceValues);
    }

    @Override
    public boolean shouldBuyTile(AIPlayer aiPlayer, Tile tile) {
        int price = (tile instanceof PropertyTile) ? ((PropertyTile) tile).getPrice() :
                (tile instanceof RailroadTile) ? ((RailroadTile) tile).getPrice() : 0;
        return aiPlayer.getMoney() >= price; // Buys property if can afford it
    }

}
