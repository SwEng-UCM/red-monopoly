package View;

import Model.Player;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public class FigurePicker extends JDialog {
    private static Map<String, Color> selectedFigures = new HashMap<>();
    private static final Color[] PLAYER_COLORS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE,
            Color.MAGENTA, Color.CYAN, Color.PINK, Color.GRAY
    };

    public FigurePicker(JFrame parent, Player player, int playerIndex) {
        super(parent, "Choose Your Figure", true); // Modal dialog
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(2, 4, 10, 10)); // 2 rows, 4 columns, spacing 10px

        // Loop to add figure buttons
        for (int i = 1; i <= 8; i++) {
            String imagePath = "resources/players/player" + i + ".png";
            ImageIcon icon = new ImageIcon(imagePath);

            // Resize the icon to make sure it's small enough
            Image img = icon.getImage(); // transform it
            Image newImg = img.getScaledInstance(120, 120, Image.SCALE_SMOOTH); // resizing
            icon = new ImageIcon(newImg); // new icon with resized image

            JButton button = new JButton(icon);
            button.setPreferredSize(new Dimension(120, 120)); // optional: size for each button
            button.setFocusPainted(false); // makes it look cleaner
            final String selectedImagePath = imagePath;

            // Check if figure is already selected
            if (selectedFigures.containsKey(selectedImagePath)) {
                Color color = selectedFigures.get(selectedImagePath);
                button.setBorder(new LineBorder(color, 5)); // already selected
            } else {
                button.setBorder(BorderFactory.createEmptyBorder()); // no border
            }

            button.addActionListener((ActionEvent e) -> {
                if (selectedFigures.containsKey(selectedImagePath)) {
                    JOptionPane.showMessageDialog(this, "This figure is already selected by another player!", "Figure Taken", JOptionPane.WARNING_MESSAGE);
                } else {
                    player.setFigureImagePath(selectedImagePath);
                    Color playerColor = PLAYER_COLORS[playerIndex % PLAYER_COLORS.length];
                    selectedFigures.put(selectedImagePath, playerColor);
                    button.setBorder(new LineBorder(playerColor, 5));
                    dispose();
                }
            });

            gridPanel.add(button);
        }

        add(gridPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}
