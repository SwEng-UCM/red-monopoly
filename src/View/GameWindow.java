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
    private MainWindow _mainWindow;
    private JLabel currentPlayerLabel;
    private JLabel currentBalanceLabel;
    private MusicPlayer inGameMusic;
    private BoardPanel boardPanel; // Embedded board panel
    private PlayerInfoWindow playerInfoWindow;

    public GameWindow(Controller controller, MainWindow mainWindow) {
        _controller = controller;
        _mainWindow = mainWindow;
        setTitle("Game - [RED MONOPOLY]");
        setSize(1200, 800); // Increased size to accommodate the board
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initGUI();

        inGameMusic = new MusicPlayer();
        inGameMusic.playMusic("resources/Russia-Theme-Atomic-_Civilization-6-OST_-Kalinka_1.wav");
        inGameMusic.setVolume(0.7f);

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
        // Top panel for current player and balance info
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(Color.RED);
        currentPlayerLabel = new JLabel("Current Player: (not set yet)", SwingConstants.CENTER);
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        currentPlayerLabel.setForeground(Color.WHITE);
        topPanel.add(currentPlayerLabel);

        currentBalanceLabel = new JLabel("Current Balance: 0 ₽", SwingConstants.CENTER);
        currentBalanceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        currentBalanceLabel.setForeground(Color.WHITE);
        topPanel.add(currentBalanceLabel);

        // Center: BoardPanel displays the game board
        boardPanel = new BoardPanel(_controller);

        // Bottom panel for buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.RED);

        // Roll Dice Button
        JButton rollDiceButton = createImageButton(
                "resources/dicesRolling.png",
                "Roll the dice to move",
                e -> {
                    String result = _controller.rollDiceAndMove();
                    // Optionally, you might display the result in a popup:
                    // JOptionPane.showMessageDialog(this, result);
                    updateCurrentPlayerLabel();
                    updateCurrentBalanceLabel();
                    boardPanel.refreshBoard();
                }
        );
        bottomPanel.add(rollDiceButton);

        // Back to Main Menu Button
        JButton backButton = createImageButton(
                "resources/goBack.png",
                "Return to the main menu",
                e -> {
                    MusicPlayer.playSoundEffect(filePath);
                    dispose();
                }
        );
        bottomPanel.add(backButton);

        // Player Info Button
        JButton playerInfoButton = createImageButton(
                "resources/playerInfoWindow.png",
                "Show player status, money, position, and properties",
                e -> {
                    MusicPlayer.playSoundEffect(filePath);
                    if (playerInfoWindow == null || !playerInfoWindow.isDisplayable()) {
                        playerInfoWindow = new PlayerInfoWindow(_controller);
                    }
                    playerInfoWindow.setVisible(true);
                    playerInfoWindow.refreshData();
                }
        );
        bottomPanel.add(playerInfoButton);



        // Set layout of GameWindow and add components
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        updateCurrentPlayerLabel();
        updateCurrentBalanceLabel();
    }

    private JButton createImageButton(String imagePath, String toolTip, java.awt.event.ActionListener listener) {
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
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
