package View;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;

public class AvatarSelectionWindow extends JDialog {
    // Stores the classpath resource path, e.g. "/players/player3.png"
    private String selectedAvatarPath = null;

    public AvatarSelectionWindow(JFrame parent) {
        super(parent, "Select Your Avatar", true);
        setUndecorated(true);
        setSize(700, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Rounded background
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
            String resourcePath = "/players/player" + i + ".png";
            URL url = getClass().getResource(resourcePath);
            Image iconImg = null;
            if (url != null) {
                try {
                    iconImg = ImageIO.read(url)
                            .getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ImageIcon icon = iconImg != null
                    ? new ImageIcon(iconImg)
                    : new ImageIcon();  // fallback empty

            JButton button = new JButton(icon);
            button.setToolTipText("Player " + i);
            button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.setFocusPainted(false);

            button.addActionListener(e -> {
                selectedAvatarPath = resourcePath;
                dispose();
            });
            button.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    button.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3, true));
                }
                @Override public void mouseExited(MouseEvent e) {
                    button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
                }
            });

            avatarPanel.add(button);
        }
        content.add(avatarPanel, BorderLayout.CENTER);

        // Cancel
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

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(cancelButton);
        content.add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    /** Returns a classpath resource path like "/players/player3.png" */
    public String getSelectedAvatarPath() {
        return selectedAvatarPath;
    }

    // Rounded background panel
    static class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color bgColor;
        RoundedPanel(int radius, Color bg) {
            this.cornerRadius = radius;
            this.bgColor = bg;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D)g;
            g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2d.setColor(bgColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(),
                    cornerRadius, cornerRadius);
        }
    }
}
