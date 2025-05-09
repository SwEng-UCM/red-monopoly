// src/View/DicePanel.java
package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Random;

public class DicePanel extends JPanel {
    private final JLabel diceLabel;
    private Timer timer;
    private int finalResult;
    private final Random random = new Random();

    // Duration of the animation in milliseconds and update interval.
    private final int animationDuration = 2000;
    private final int updateInterval    = 100;

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
            final long startTime = System.currentTimeMillis();
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed >= animationDuration) {
                    timer.stop();
                    setDiceFace(finalResult);
                } else {
                    int randomFace = random.nextInt(6) + 1;
                    setDiceFace(randomFace);
                }
            }
        });
        timer.start();
    }

    private void setDiceFace(int face) {
        String resourcePath = "/die" + face + ".png";
        URL imgUrl = getClass().getResource(resourcePath);
        if (imgUrl != null) {
            diceLabel.setIcon(new ImageIcon(imgUrl));
            diceLabel.setText("");
        } else {
            // fallback to text if image missing
            diceLabel.setIcon(null);
            diceLabel.setText(String.valueOf(face));
        }
    }
}
