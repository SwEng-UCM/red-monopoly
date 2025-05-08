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

    public Player getOwner()              { return owner; }
    public void   setOwner(Player owner)  { this.owner = owner; }
    public int    getRent()               { return rent; }
    public int    getPrice()              { return price; }

    /* ───── helper used by GUI & server ─────────────────────────── */
    public void completePurchase(Player buyer) {
        owner = buyer;
        buyer.deductMoney(price);
        buyer.addProperty(this);
    }

    /* ───── main action ─────────────────────────────────────────── */
    @Override
    public String action(Player player) {

        /* already owned */
        if (owner != null) {
            if (owner == player)
                return player.getName() + " landed on their own property: " + getName() + ".";
            player.deductMoney(rent);
            owner.addMoney(rent);
            return player.getName() + " paid " + rent + " ₽ rent to "
                    + owner.getName() + " for " + getName() + ".";
        }

        /* unowned – AI decides instantly, humans get ASKBUY flag */
        if (player instanceof AIPlayer ai) {
            if (ai.getStrategy().shouldBuyTile(ai, this) && player.getMoney() >= price) {
                completePurchase(player);
                return player.getName() + " (AI) bought " + getName() + " for " + price + " ₽.";
            }
            return player.getName() + " (AI) declined to buy " + getName() + ".";
        }

        /* Human: defer the choice */
        return "ASKBUY:" + getName() + ":" + price;   //  <<<<  NEW
    }
}
