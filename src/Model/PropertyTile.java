package Model;

public class PropertyTile extends Tile{

    public PropertyTile(String name, int position) {
        super(name, position);
    }

    @Override
    public void action(Player player) {
        System.out.println(player.getName() + " landed on " + name);
    }
}
