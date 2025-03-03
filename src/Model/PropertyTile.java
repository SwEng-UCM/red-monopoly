package Model;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.*;

public class PropertyTile extends Tile {
    private Player owner;
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
        if (owner == null) {
            // Property is unowned
            //System.out.println(player.getName() + " landed on " + name + ". This property is unowned.");

            UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 18)); // Increase font size for messages
            UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 16));  // Increase font size for buttons

            // Ask the player if they want to buy the property
            int choice = JOptionPane.showConfirmDialog(
                    null,
                    player.getName() + " landed on " + name + ". This property is unowned. Would you like to buy for " + price + " ₽ ?",
                    "Buy Property",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                if (player.getMoney() >= price) {
                    // Player has enough money, assign property to them and deduct the price
                    owner = player;
                    player.deductMoney(price);
                    JOptionPane.showMessageDialog(null,
                            player.getName() + " bought " + name + " for " + price + " ₽",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                            player.getName() + " does not have enough money to buy " + name + ".",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    // Player doesn't have enough money

                }
            } else {
                // Player chose not to buy the property
                System.out.println(player.getName() + " chose not to buy " + name + ".");
            }
        } else if (owner != player) {
            // Property is owned by another player, pay rent
            System.out.println(player.getName() + " landed on " + name + " owned by " + owner.getName() + ". Paying rent: " + rent + " ₽");
            player.deductMoney(rent); // Deduct rent from the player
            owner.addMoney(rent);     // Add rent to the owner's balance
        } else {
            // Property is owned by the current player
            System.out.println(player.getName() + " landed on their own property: " + name);
        }
    }
}