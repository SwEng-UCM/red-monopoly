package Model;

public class UtilityTile extends Tile {

    public UtilityTile(String name, int position) {
        super(name, position);
    }

    @Override
    public void action(Player player) {
        // Implement utility tile behavior here
        System.out.println("Player " + player.getName() + " landed on Utility: " + name);
    }
}
