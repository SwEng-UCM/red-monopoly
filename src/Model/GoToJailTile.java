package Model;

public class GoToJailTile extends Tile {

    public GoToJailTile(String name, int position) {
        super(name, position);
    }

    @Override
    public void action(Player player) {
        // Implement go to jail behavior here
        System.out.println("Player " + player.getName() + " landed on Go To Gulag: " + name);
        // You might want to add logic to move the player to the jail position
        //player.setPosition(position);
        player.setInJail(true);
    }
}
