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
    private BoardPanel boardPanel;
    private DualDicePanel dualDicePanel;
    private PlayerInfoWindow playerInfoWindow;

    public GameWindow(Controller controller, MainWindow mainWindow) {
        _controller = controller;
        _mainWindow = mainWindow;
        setTitle("Game - [RED MONOPOLY]");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initGUI();

        // Start in-game music
        inGameMusic = new MusicPlayer();
        inGameMusic.playMusic("resources/Russia-Theme-Atomic-_Civilization-6-OST_-Kalinka_1.wav");
        inGameMusic.setVolume(0.7f);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                inGameMusic.stopMusic();
                // Resume main menu music
                _mainWindow.getMusicPlayer().playMusic("resources/Dark_is_the_Night_-_Soviet_WW2_Song.wav");
                _mainWindow.setVisible(true);
            }
        });
    }

    private void initGUI() {
        // Top panel for current player & balance
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

        // Bottom panel for buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.RED);

        // Roll Dice Button
        JButton rollDiceButton = createImageButton(
                "resources/dicesRolling.png",
                "Roll the dice to move",
                e -> {
                    // Roll the dice (without moving yet) and store the values.
                    int[] diceValues = _controller.rollDice();

                    // Start the dice animation using the rolled values.
                    dualDicePanel.startAnimation(diceValues[0], diceValues[1]);

                    // Create a timer to delay the player movement until after the dice animation.
                    new Timer(2000, evt -> {
                        ((Timer) evt.getSource()).stop();
                        // Now move the player using the previously rolled dice.
                        String result = _controller.movePlayerAfterDiceRoll(diceValues);
                        // Optionally, display 'result' in a dialog or log it.
                        // For example: JOptionPane.showMessageDialog(null, result);

                        // Update UI elements.
                        updateCurrentPlayerLabel();
                        updateCurrentBalanceLabel();
                        boardPanel.refreshBoard();
                    }).start();
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

        // Main center panel: Board on the left, dice on the right
        JPanel centerPanel = new JPanel(new BorderLayout());
        boardPanel = new BoardPanel(_controller);
        centerPanel.add(boardPanel, BorderLayout.CENTER);

        // Create & place the DualDicePanel on the right side (EAST).
        dualDicePanel = new DualDicePanel();
        // Set a preferred size so it's not too large
        dualDicePanel.setPreferredSize(new Dimension(200, 200));
        centerPanel.add(dualDicePanel, BorderLayout.EAST);

        // Put it all together
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
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
