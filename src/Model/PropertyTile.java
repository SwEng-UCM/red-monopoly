package Model;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.*;

public class PropertyTile extends Tile {
    private transient Player owner;
    private final int rent;
    private final int price;

    public PropertyTile(String name, int position, int rent, int price) {
        super(name, position);
        this.rent = rent;
        this.price = price;
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

    public int getPrice() {
        return price;
    }

    @Override
    public void action(Player player) {
        if (player instanceof AIPlayer) {
            AIPlayer ai = (AIPlayer) player;
            if (((AIStrategy) ai.getStrategy()).shouldBuyTile(ai, this)) {
                if (ai.getMoney() >= price) {
                    owner = ai;
                    ai.deductMoney(price);

                    // ✅ ADD ownership tracking
                    ai.addProperty(this);

                    JOptionPane.showMessageDialog(null,
                            ai.getName() + " bought " + getName() + " for " + price + " ₽",
                            "AI Purchase",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                            ai.getName() + " couldn't afford " + getName() + ".",
                            "AI Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        ai.getName() + " chose not to buy " + getName() + ".",
                        "AI Decision",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            if (owner == null) {
                // Unowned property
                UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 18));
                UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 16));

                int choice = JOptionPane.showConfirmDialog(
                        null,
                        player.getName() + " landed on " + getName() + ". This property is unowned. Buy for " + price + " ₽?",
                        "Buy Property",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    if (player.getMoney() >= price) {
                        owner = player;
                        player.deductMoney(price);

                        // ✅ ADD ownership tracking
                        player.addProperty(this);

                        JOptionPane.showMessageDialog(null,
                                player.getName() + " bought " + getName() + " for " + price + " ₽",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null,
                                player.getName() + " does not have enough money to buy " + getName() + ".",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    System.out.println(player.getName() + " chose not to buy " + getName() + ".");
                }
            } else if (owner != player) {
                // Pay rent

                JOptionPane.showMessageDialog(null,
                        player.getName() + " landed on " + getName() +
                                " owned by " + owner.getName() + ". Paying rent: " + rent + " ₽",
                        "Pay rent",
                        JOptionPane.INFORMATION_MESSAGE);
               /* System.out.println(player.getName() + " landed on " + getName() +
                        " owned by " + owner.getName() + ". Paying rent: " + rent + " ₽");*/
                player.deductMoney(rent);
                owner.addMoney(rent);
            } else {
                // Landed on own property
                System.out.println(player.getName() + " landed on their own property: " + getName());
            }
        }
    }
}
