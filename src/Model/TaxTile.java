package Model;

import javax.swing.JOptionPane;

public class TaxTile extends Tile {
    private int taxAmount;

    public TaxTile(String name, int position, int taxAmount) {
        super(name, position);
        this.taxAmount = taxAmount;
    }

    @Override
    public void action(Player player) {
        JOptionPane.showMessageDialog(null,
                "You landed on " + name + " tax tile. Pay " + taxAmount + " ₽.",
                "Tax Tile",
                JOptionPane.INFORMATION_MESSAGE);
        player.deductMoney(taxAmount);
    }

    public int getTaxAmount() {
        return this.taxAmount;
    }
}
