package Model;

public class FreeParkingTile extends Tile {

    public FreeParkingTile(String name, int position) {
        super(name, position);
    }

    @Override
    public void action(Player player) {
        // Implement free parking behavior here
        System.out.println("Player " + player.getName() + " landed on Free Parking: " + name);
    }
}
