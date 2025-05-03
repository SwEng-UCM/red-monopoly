package Model;

import java.util.ArrayList;
import java.util.List;

public class RailroadTile extends Tile {
    private transient Player owner;
    private int price;
    private static final int BASE_RENT = 25;
    private static final List<RailroadTile> allRailroads = new ArrayList<>();
    private int rent;

    public RailroadTile(String name, int position, int rent, int price) {
        super(name, position);
        this.rent = rent;
        this.price = price;
        this.owner = null;
        allRailroads.add(this);
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

    public int getRent() {
        return rent;
    }

    private int calculateRent() {
        if (owner == null) return 0;

        int ownedCount = 0;
        for (RailroadTile railroad : allRailroads) {
            if (railroad.getOwner() == owner) {
                ownedCount++;
            }
        }
        return BASE_RENT * (int) Math.pow(2, ownedCount - 1);
    }

    @Override
    public String action(Player player) {
        if (owner == null) {
            if (player.getMoney() >= price) {
                owner = player;
                player.deductMoney(price);
                player.addRailroad(this);
                return player.getName() + " bought railroad " + getName() + " for " + price + " ₽.";
            } else {
                return player.getName() + " cannot afford to buy " + getName() + ".";
            }
        } else if (owner != player) {
            int rent = calculateRent();
            player.deductMoney(rent);
            owner.addMoney(rent);
            return player.getName() + " paid " + rent + " ₽ rent to " + owner.getName() + " for " + getName() + ".";
        } else {
            return player.getName() + " landed on their own railroad: " + getName() + ".";
        }
    }
}
