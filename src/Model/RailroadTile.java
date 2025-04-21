package Model;

import javax.swing.*;
import java.awt.*;
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
    public void action(Player player) {
        if (player instanceof AIPlayer) {
            AIPlayer ai = (AIPlayer) player;
            if (((AIStrategy) ai.getStrategy()).shouldBuyTile(ai, this)) {
                if (ai.getMoney() >= price) {
                    owner = ai;
                    ai.deductMoney(price);

                    // ✅ ADD THIS LINE
                    ai.addRailroad(this);

                    JOptionPane.showMessageDialog(
                            null,
                            ai.getName() + " bought " + getName() + " for " + price + " ₽",
                            "AI Purchase",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            ai.getName() + " couldn't afford " + getName() + ".",
                            "AI Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        ai.getName() + " chose not to buy " + getName() + ".",
                        "AI Decision",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } else {
            if (owner == null) {
                UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 18));
                UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 16));

                int choice = JOptionPane.showConfirmDialog(
                        null,
                        player.getName() + " landed on " + getName() + " Railroad. Buy for " + price + " ₽?",
                        "Buy Railroad",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    if (player.getMoney() >= price) {
                        owner = player;
                        player.deductMoney(price);

                        // ✅ ADD THIS LINE
                        player.addRailroad(this);

                        JOptionPane.showMessageDialog(
                                null,
                                player.getName() + " bought " + getName() + " for " + price + " ₽",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                                null,
                                player.getName() + " doesn't have enough money to buy " + getName() + ".",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            player.getName() + " chose not to buy " + getName() + ".",
                            "Decision",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            } else if (owner != player) {
                int rent = calculateRent();
                player.deductMoney(rent);
                owner.addMoney(rent);
                JOptionPane.showMessageDialog(
                        null,
                        player.getName() + " paid " + rent + " ₽ rent to " + owner.getName() + " for " + getName(),
                        "Rent Paid",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        player.getName() + " landed on their own railroad: " + getName(),
                        "Your Property",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }
}
