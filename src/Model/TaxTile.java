package Model;

public class TaxTile extends Tile {
    private int taxAmount;

    public TaxTile(String name, int position, int taxAmount) {
        super(name, position);
        this.taxAmount = taxAmount;
    }

    @Override
    public String action(Player player) {
        player.deductMoney(taxAmount);
        return player.getName() + " paid " + taxAmount + " ₽ in taxes at " + name + ".";
    }

    public int getTaxAmount() {
        return this.taxAmount;
    }
}
