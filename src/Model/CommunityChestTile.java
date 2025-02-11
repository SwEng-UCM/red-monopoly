package Model;

public class CommunityChestTile extends Tile {

    public CommunityChestTile(String name, int position) {
        super(name, position);
    }

    @Override
    public void action(Player player) {
        // Implement community chest tile behavior here
        System.out.println("Player " + player.getName() + " landed on Community Chest: " + name);
    }
}
