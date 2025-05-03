package Model;

public class JailTile extends Tile {

    public JailTile(String name, int position) {
        super(name, position);
    }

    @Override
    public String action(Player player) {
        if (player.isInJail()) {
            return player.getName() + " is in Gulag.";
        } else {
            return player.getName() + " is just visiting the Gulag.";
        }
    }
}
