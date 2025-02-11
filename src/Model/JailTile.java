package Model;

public class JailTile extends Tile {

    public JailTile(String name, int position) {
        super(name, position);
    }

    @Override
    public void action(Player player) {
        // Implement jail tile behavior here
        // This could be just a "visiting" tile if not in jail
        System.out.println("Player " + player.getName() + " is just visiting Jail: " + name);
    }
}
