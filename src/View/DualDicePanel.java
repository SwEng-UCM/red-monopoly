package View;

import javax.swing.*;
import java.awt.*;

public class DualDicePanel extends JPanel {
    private DicePanel dicePanel1;
    private DicePanel dicePanel2;

    public DualDicePanel() {
        // Use a 1x2 grid with some horizontal gap for spacing.
        setLayout(new GridLayout(1, 2, 10, 0));
        setOpaque(false);

        dicePanel1 = new DicePanel();
        dicePanel2 = new DicePanel();

        add(dicePanel1);
        add(dicePanel2);
    }

    /**
     * Starts the animation for both dice with the given final face values.
     */
    public void startAnimation(int die1, int die2) {
        dicePanel1.startAnimation(die1);
        dicePanel2.startAnimation(die2);
    }
}
