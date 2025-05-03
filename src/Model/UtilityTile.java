package Model;

public class UtilityTile extends Tile {

    private transient Player owner;
    private int price;

    public UtilityTile(String name, int position, int price) {
        super(name, position);
        this.price = price;
        this.owner = null;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String action(Player player) {
        if (owner == null) {
            if (player.getMoney() >= price) {
                owner = player;
                player.deductMoney(price);
                return player.getName() + " bought utility " + name + " for " + price + " ₽.";
            } else {
                return player.getName() + " cannot afford to buy utility " + name + ".";
            }
        } else if (owner != player) {
            int rent = (int) ((Math.random() * 6 + 1) + (Math.random() * 6 + 1)) * 4; // roll 2 dice, rent = 4×sum
            player.deductMoney(rent);
            owner.addMoney(rent);
            return player.getName() + " paid " + rent + " ₽ to " + owner.getName() + " for using utility " + name + ".";
        } else {
            return player.getName() + " landed on their own utility: " + name + ".";
        }
    }
}
