package Model;

public class PropertyTile extends Tile {
    private transient Player owner;
    private final int rent;
    private final int price;

    public PropertyTile(String name, int position, int rent, int price) {
        super(name, position);
        this.rent = rent;
        this.price = price;
        this.owner = null;
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

    public int getPrice() {
        return price;
    }

    @Override
    public String action(Player player) {
        if (owner == null) {
            if (player.getMoney() >= price) {
                owner = player;
                player.deductMoney(price);
                player.addProperty(this);
                return player.getName() + " bought " + getName() + " for " + price + " ₽.";
            } else {
                return player.getName() + " cannot afford to buy " + getName() + ".";
            }
        } else if (owner != player) {
            player.deductMoney(rent);
            owner.addMoney(rent);
            return player.getName() + " paid " + rent + " ₽ rent to " + owner.getName() + " for " + getName() + ".";
        } else {
            return player.getName() + " landed on their own property: " + getName() + ".";
        }
    }
}
