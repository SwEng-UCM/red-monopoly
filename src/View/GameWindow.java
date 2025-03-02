package View;

import Controller.Controller;
import Model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static View.MainWindow.filePath;

public class GameWindow extends JFrame {
    private Controller _controller;
    private MainWindow _mainWindow; // Reference to the main menu window
    private JLabel currentPlayerLabel;
    private JLabel currentBalanceLabel;
    private JTextArea gameMessagesArea;
    private MusicPlayer inGameMusic; // In-game music player

    public GameWindow(Controller controller, MainWindow mainWindow) {
        _controller = controller;
        _mainWindow = mainWindow;
        setTitle("Game - [RED MONOPOLY]");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initGUI();

        // Start in-game soundtrack
        inGameMusic = new MusicPlayer();
        inGameMusic.playMusic("resources/Russia-Theme-Atomic-_Civilization-6-OST_-Kalinka_1.wav");
        inGameMusic.setVolume(0.7f);

        // When the game window is closed, resume main menu music.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                inGameMusic.stopMusic();
                _mainWindow.getMusicPlayer().playMusic("resources/Dark_is_the_Night_-_Soviet_WW2_Song.wav");
                _mainWindow.setVisible(true);
            }
        });
    }

    private void initGUI() {
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBackground(Color.RED);

        // Top Panel: Current player label and balance
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setOpaque(false);

        currentPlayerLabel = new JLabel("Current Player: (not set yet)");
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        currentPlayerLabel.setForeground(Color.WHITE);
        currentPlayerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(currentPlayerLabel);

        currentBalanceLabel = new JLabel("Current Balance: 0 ₽");
        currentBalanceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        currentBalanceLabel.setForeground(Color.WHITE);
        currentBalanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(currentBalanceLabel);

        gamePanel.add(topPanel, BorderLayout.NORTH);

        // Center: Game messages area
        gameMessagesArea = new JTextArea();
        gameMessagesArea.setEditable(false);
        gameMessagesArea.setBackground(Color.BLACK);
        gameMessagesArea.setForeground(Color.WHITE);
        gameMessagesArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(gameMessagesArea);
        gamePanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel: Image Buttons with tooltips
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);

        // Roll Dice Button
        JButton rollDiceButton = createImageButton(
                "resources/dicesRolling.png",
                "Roll the dice to move",
                e -> {
                    MusicPlayer.playSoundEffect(filePath);
                    String result = _controller.rollDiceAndMove();
                    gameMessagesArea.append(result + "\n");
                    updateCurrentPlayerLabel();
                    updateCurrentBalanceLabel();
                }
        );
        bottomPanel.add(rollDiceButton);

        // Back to Main Menu Button
        JButton backButton = createImageButton(
                "resources/goBack.png",
                "Return to the main menu",
                e -> {
                    MusicPlayer.playSoundEffect(filePath);
                    dispose(); // This triggers your window listener to resume main menu music.
                }
        );
        bottomPanel.add(backButton);

        // Player Info Button
        JButton playerInfoButton = createImageButton(
                "resources/playerInfoWindow.png",
                "Show player status, money, position, and properties",
                e -> {
                    MusicPlayer.playSoundEffect(filePath);
                    PlayerInfoWindow infoWindow = new PlayerInfoWindow(_controller);
                    infoWindow.setVisible(true);
                }
        );
        bottomPanel.add(playerInfoButton);

        // Add the bottom panel to the game panel at the SOUTH position
        gamePanel.add(bottomPanel, BorderLayout.SOUTH);

        add(gamePanel);
        updateCurrentPlayerLabel();
        updateCurrentBalanceLabel();
    }

    /**
     * Helper method to create an image-based button with a tooltip.
     *
     * @param imagePath the file path to the image icon
     * @param toolTip the tooltip text to display on hover
     * @param listener the ActionListener for the button
     * @return a configured JButton
     */
    private JButton createImageButton(String imagePath, String toolTip, java.awt.event.ActionListener listener) {
        ImageIcon originalIcon = new ImageIcon(imagePath);
        // Adjust the size as needed
        Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JButton button = new JButton(scaledIcon);
        button.setToolTipText(toolTip);
        button.setBorder(null);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);
        return button;
    }

    private void updateCurrentPlayerLabel() {
        String currentPlayer = _controller.getCurrentPlayerName();
        currentPlayerLabel.setText("Current Player: " + currentPlayer);
    }

    private void updateCurrentBalanceLabel() {
        int currentBalance = _controller.getCurrentPlayerBalance();
        currentBalanceLabel.setText("Current Balance: " + currentBalance + " ₽");
    }
}
