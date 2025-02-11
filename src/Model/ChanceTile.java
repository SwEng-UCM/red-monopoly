package Model;

public class ChanceTile extends Tile {

    public ChanceTile(String name, int position) {
        super(name, position);
    }

    @Override
    public void action(Player player) {
        // Implement chance tile behavior here
        System.out.println("Player " + player.getName() + " landed on Chance: " + name);
    }
}
