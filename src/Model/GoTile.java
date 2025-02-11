package Model;

public class GoTile extends Tile {

    public GoTile(String name, int position) {
        super(name, position);
    }

    @Override
    public void action(Player player) {
        // Implement go tile behavior here (e.g., give money for passing GO)
        System.out.println("Player " + player.getName() + " landed on GO: " + name);
    }
}
