package Model;

public class RailroadTile extends Tile {

    public RailroadTile(String name, int position) {
        super(name, position);
    }

    @Override
    public void action(Player player) {
        // Implement railroad tile behavior here
        System.out.println("Player " + player.getName() + " landed on Railroad: " + name);
    }
}
