package Model;

public class TaxTile extends Tile {
    private int taxAmount;

    public TaxTile(String name, int position, int taxAmount) {
        super(name, position);
        this.taxAmount = taxAmount;
    }

    @Override
    public void action(Player player) {
        // Deduct money from the player using `this.taxAmount`
        System.out.println("Player " + player.getName() + " must pay " + taxAmount + " at " + name);
    }

    public int getTaxAmount() {
        return this.taxAmount;
    }
}
