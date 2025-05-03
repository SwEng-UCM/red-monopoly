package Model;

public class GoToJailTile extends Tile {

    public GoToJailTile(String name, int position) {
        super(name, position);
    }

    @Override
    public String action(Player player) {
        player.setInJail(true);
        player.setPosition(10); // Assuming jail is at index 10
        return player.getName() + " was sent to Gulag!";
    }
}
