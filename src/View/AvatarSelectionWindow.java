package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AvatarSelectionWindow extends JDialog {
    private String selectedAvatarPath = null;

    public AvatarSelectionWindow(JFrame parent) {
        super(parent, "Select Your Avatar", true);
        setUndecorated(true); // Remove default OS title bar
        setSize(700, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Rounded panel background
        JPanel content = new RoundedPanel(25, new Color(30, 30, 30, 230));
        content.setLayout(new BorderLayout(20, 20));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(content);

        // Title
        JLabel titleLabel = new JLabel("Choose Your Token", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        content.add(titleLabel, BorderLayout.NORTH);

        // Avatar grid
        JPanel avatarPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        avatarPanel.setOpaque(false);

        for (int i = 1; i <= 8; i++) {
            String path = "resources/players/player" + i + ".png";
            ImageIcon icon = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));

            JButton button = new JButton(icon);
            button.setToolTipText("Player " + i);
            button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
            button.setBackground(new Color(0, 0, 0, 0));
            button.setOpaque(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);

            int avatarIndex = i;
            button.addActionListener(e -> {
                selectedAvatarPath = path;
                dispose();
            });

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3, true));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
                }
            });

            avatarPanel.add(button);
        }

        content.add(avatarPanel, BorderLayout.CENTER);

        // Exit button (optional)
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(100, 0, 0));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> {
            selectedAvatarPath = null;
            dispose();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(cancelButton);
        content.add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public String getSelectedAvatarPath() {
        return selectedAvatarPath;
    }

    // Utility inner class for rounded background panel
    static class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color backgroundColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(backgroundColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }
}
