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
        int taxToDeduct;
        taxToDeduct = player.getMoney()/taxAmount;
        JOptionPane.showMessageDialog(null,
                "You landed on " + name + " tile. Pay " + taxAmount + " % of your balance(" + taxToDeduct + "₽)",
                "Tax Tile",
                JOptionPane.INFORMATION_MESSAGE);

        player.deductMoney(taxToDeduct);
    }

    public int getTaxAmount() {
        return this.taxAmount;
    }
}
