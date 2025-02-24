package Model;

public class PropertyTile extends Tile {
    private Player owner;
    private int rent;

    public PropertyTile(String name, int position, int rent) {
        super(name, position);
        this.rent = rent;
        this.owner = null; // Initially, no owner
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public int getRent() {
        return rent;
    }

    @Override
    public void action(Player player) {
        if (owner == null) {
            // Property is unowned
            System.out.println(player.getName() + " landed on " + name + ". This property is unowned.");
        } else if (owner != player) {
            // Property is owned by another player, pay rent
            System.out.println(player.getName() + " landed on " + name + " owned by " + owner.getName() + ". Paying rent: $" + rent);
            player.deductMoney(rent); // Deduct rent from the player
            owner.addMoney(rent);     // Add rent to the owner's balance
        } else {
            // Property is owned by the current player
            System.out.println(player.getName() + " landed on their own property: " + name);
        }
    }
}