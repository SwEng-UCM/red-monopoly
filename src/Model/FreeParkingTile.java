package Model;

public class FreeParkingTile extends Tile {

    public FreeParkingTile(String name, int position) {
        super(name, position);
    }

    @Override
    public void action(Player player) {
        System.out.println("Player " + player.getName() + " landed on Free Parking: " + name);
    }
}
