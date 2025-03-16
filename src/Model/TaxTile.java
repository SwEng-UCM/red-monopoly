package Model;

public class TaxTile extends Tile {
    private static int TAX_AMOUNT = 100;
    public TaxTile(String name, int position) {
        super(name, position);
    }

    @Override
    public void action(Player player) {
        // Implement tax tile behavior here (e.g., deduct money)
        System.out.println("Player " + player.getName() + " must pay tax at " + name);
    }

    public static int getTaxAmount() {
        return TAX_AMOUNT;
    }

}
