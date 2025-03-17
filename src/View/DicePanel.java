package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class DicePanel extends JPanel {
    private JLabel diceLabel;
    private Timer timer;
    private int finalResult;
    private Random random = new Random();

    // Duration of the animation in milliseconds and update interval.
    private int animationDuration = 2000;
    private int updateInterval = 100;

    public DicePanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
        diceLabel = new JLabel("", SwingConstants.CENTER);
        diceLabel.setFont(new Font("Arial", Font.BOLD, 48));
        diceLabel.setForeground(Color.YELLOW);
        add(diceLabel, BorderLayout.CENTER);
    }

    public void startAnimation(int finalResult) {
        this.finalResult = finalResult;
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        timer = new Timer(updateInterval, new ActionListener() {
            long startTime = System.currentTimeMillis();
            @Override
            public void actionPerformed(ActionEvent e) {
                if (System.currentTimeMillis() - startTime >= animationDuration) {
                    timer.stop();
                    setDiceImage(finalResult);
                } else {
                    // Display a random face while animating.
                    int randomFace = random.nextInt(6) + 1;
                    setDiceImage(randomFace);
                }
            }
        });
        timer.start();
    }

    private void setDiceImage(int face) {
        // Make sure "die1.png" to "die6.png" exist in your resources folder.
        ImageIcon icon = new ImageIcon("resources/die" + face + ".png");
        diceLabel.setIcon(icon);
    }
}
