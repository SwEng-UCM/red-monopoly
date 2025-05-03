package Model;

public class GoTile extends Tile {

    public GoTile(String name, int position) {
        super(name, position);
    }

    @Override
    public String action(Player player) {
        player.addMoney(200); // Optional, depends on rules
        return player.getName() + " landed on GO and collected 200 ₽.";
    }
}
