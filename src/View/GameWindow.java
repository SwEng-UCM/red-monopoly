package View;

import Controller.Controller;
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

        // Bottom Panel: Buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);

        JButton rollDiceButton = createStyledButton("Roll Dice");
        rollDiceButton.addActionListener(e -> {
            MusicPlayer.playSoundEffect(filePath);
            String result = _controller.rollDiceAndMove();
            gameMessagesArea.append(result + "\n");
            updateCurrentPlayerLabel();
            updateCurrentBalanceLabel();
        });
        bottomPanel.add(rollDiceButton);

        JButton backButton = createStyledButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            MusicPlayer.playSoundEffect(filePath);
            dispose(); // This will trigger our window listener to resume main menu music.
        });
        bottomPanel.add(backButton);

        gamePanel.add(bottomPanel, BorderLayout.SOUTH);

        add(gamePanel);
        updateCurrentPlayerLabel();
        updateCurrentBalanceLabel();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.RED);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
