package Model;

public class RailroadTile extends Tile {

    private int rent;
    private int price;

    // Now we match the call with four parameters
    public RailroadTile(String name, int position, int rent, int price) {
        super(name, position);
        this.rent = rent;
        this.price = price;
    }

    @Override
    public void action(Player player) {
        // Implement logic for landing on a railroad
        System.out.println("Player " + player.getName() + " landed on Railroad: " + name 
                           + " [Rent: " + rent + ", Price: " + price + "]");
    }

    public int getRent() {
        return rent;
    }

    public int getPrice() {
        return price;
    }
}
