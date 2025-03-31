package View;

import Controller.Controller;
import Model.AIPlayer;
import Model.Player;
import Model.MonopolyGame;
import Model.PropertyTile;
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
                // Resume main menu music and show main window.
                _mainWindow.getMusicPlayer().playMusic("resources/Dark_is_the_Night_-_Soviet_WW2_Song.wav");
                _mainWindow.setVisible(true);
            }
        });

        // If the first player is AI, trigger its turn automatically.
        checkAndTriggerAITurn();
    }

    private void initGUI() {
        // Top panel for current player & balance.
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

        // Bottom panel for buttons.
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.RED);

        // Roll Dice button for human players.
        JButton rollDiceButton = createImageButton(
                "resources/dicesRolling.png",
                "Roll the dice to move",
                e -> {
                    int[] diceValues = _controller.rollDice();
                    dualDicePanel.startAnimation(diceValues[0], diceValues[1]);
                    new Timer(2000, evt -> {
                        ((Timer) evt.getSource()).stop();
                        String result = _controller.movePlayerAfterDiceRoll(diceValues);
                        updateCurrentPlayerLabel();
                        updateCurrentBalanceLabel();
                        boardPanel.refreshBoard();
                        checkAndTriggerAITurn();
                    }).start();
                }
        );
        bottomPanel.add(rollDiceButton);

        // Leave Game button with save prompt.
        JButton leaveButton = createImageButton(
                "resources/goBack.png",
                "Leave Game",
                e -> {
                    MusicPlayer.playSoundEffect(filePath);
                    int choice = JOptionPane.showConfirmDialog(
                            this,
                            "Do you want to save the game before leaving?",
                            "Save Game",
                            JOptionPane.YES_NO_CANCEL_OPTION
                    );
                    if (choice == JOptionPane.CANCEL_OPTION) {
                        return;
                    } else if (choice == JOptionPane.YES_OPTION) {
                        String saveName = JOptionPane.showInputDialog(
                                this,
                                "Enter save game file name (e.g., mySave.json):",
                                "Save Game",
                                JOptionPane.QUESTION_MESSAGE
                        );
                        if (saveName != null && !saveName.trim().isEmpty()) {
                            _controller.saveGame(saveName.trim());
                            JOptionPane.showMessageDialog(this, "Game saved successfully!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Invalid file name. Game not saved.");
                            return;
                        }
                    }
                    dispose();
                }
        );
        bottomPanel.add(leaveButton);

        // Player Info button.
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

        // Undo button.
        JButton undoButton = createImageButton(
                "resources/undo.png", // use undo.png as the icon
                "Undo Last Action",
                e -> {
                    _controller.undoLastCommand();
                    updateCurrentPlayerLabel();
                    updateCurrentBalanceLabel();
                    boardPanel.refreshBoard();
                }
        );
        bottomPanel.add(undoButton);


        // Center panel with board and dice.
        JPanel centerPanel = new JPanel(new BorderLayout());
        boardPanel = new BoardPanel(_controller);
        centerPanel.add(boardPanel, BorderLayout.CENTER);
        dualDicePanel = new DualDicePanel();
        dualDicePanel.setPreferredSize(new Dimension(200, 200));
        centerPanel.add(dualDicePanel, BorderLayout.EAST);

        // Assemble everything.
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        updateCurrentPlayerLabel();
        updateCurrentBalanceLabel();
    }

    // Checks if the current player is AI and triggers its turn after a delay.
    private void checkAndTriggerAITurn() {
        Player current = _controller.getCurrentPlayer();
        if (current instanceof Model.AIPlayer) {
            new Timer(2000, evt -> {
                ((Timer) evt.getSource()).stop();
                int[] diceValues = _controller.rollDice();
                dualDicePanel.startAnimation(diceValues[0], diceValues[1]);
                new Timer(2000, evt2 -> {
                    ((Timer) evt2.getSource()).stop();
                    ((Model.AIPlayer) current).takeTurnWithDice(diceValues, _controller.getMonopolyGame(), _controller);
                    updateCurrentPlayerLabel();
                    updateCurrentBalanceLabel();
                    boardPanel.refreshBoard();
                    checkAndTriggerAITurn();
                }).start();
            }).start();
        }
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
