package Model;

public class UtilityTile extends Tile {

    private int price;

    public UtilityTile(String name, int position, int price) {
        super(name, position);
        this.price = price;
    }

    @Override
    public void action(Player player) {
        // Implement utility tile behavior here
        System.out.println("Player " + player.getName() + " landed on Utility: " + name 
                           + ". It costs " + price + " to purchase.");
    }

    public int getPrice() {
        return price;
    }
}
