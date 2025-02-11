package Model;

public class StandardDice implements DiceAction {
    @Override
    public int rollDice() {
        return (int) (Math.random() * 6) + 1;
    } //i think monopoly is double dice?
}
