package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AvatarSelectionWindow extends JDialog {
    private String selectedAvatarPath = null;

    public AvatarSelectionWindow(JFrame parent) {
        super(parent, "Select Your Avatar", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel avatarPanel = new JPanel(new GridLayout(2, 4, 10, 10)); // 2 rows x 4 columns
        avatarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 1; i <= 8; i++) {
            String path = "resources/players/player" + i + ".png";
            ImageIcon icon = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
            JButton button = new JButton(icon);
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setContentAreaFilled(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            int avatarNumber = i;
            button.addActionListener(e -> {
                selectedAvatarPath = path;
                dispose();
            });

            avatarPanel.add(button);
        }

        add(new JLabel("Choose your token:", SwingConstants.CENTER), BorderLayout.NORTH);
        add(avatarPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public String getSelectedAvatarPath() {
        return selectedAvatarPath;
    }
}
