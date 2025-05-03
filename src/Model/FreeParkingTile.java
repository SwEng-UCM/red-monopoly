package Model;

public class FreeParkingTile extends Tile {

    public FreeParkingTile(String name, int position) {
        super(name, position);
    }

    @Override
    public String action(Player player) {
        return player.getName() + " landed on Free Parking. Nothing happens.";
    }
}
