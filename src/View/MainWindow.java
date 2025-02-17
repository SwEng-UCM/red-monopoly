package View;

import Controller.Controller;
import java.awt.*;
import javax.swing.*;

public class MainWindow extends JFrame {
    private Controller _controller;
    private CardLayout _cardLayout;
    private JPanel _mainPanel;
    private MusicPlayer musicPlayer;

    // --- New fields for our game UI components ---
    private JLabel currentPlayerLabel;     // Shows "Current Player: X"
    private JTextArea gameMessagesArea;    // Displays dice roll messages, etc.

    public MainWindow(Controller controller) {
        _controller = controller;
        musicPlayer = new MusicPlayer();
        musicPlayer.playMusic("resources/Dark_is_the_Night_-_Soviet_WW2_Song.wav"); // Path to your music file
        initGUI();
    }

    private void initGUI() {
        setTitle("[RED MONOPOLY]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        _cardLayout = new CardLayout();
        _mainPanel = new JPanel(_cardLayout);

        _mainPanel.add(createMainMenu(), "Main Menu");
        _mainPanel.add(createOptionsMenu(), "Options");
        _mainPanel.add(createGameScreen(), "Game");

        add(_mainPanel);

        _cardLayout.show(_mainPanel, "Main Menu");
        setVisible(true);
    }

    private JPanel createMainMenu() {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 600));

        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setIcon(new ImageIcon(
                new ImageIcon("resources/redmonopolyLogo.jpg").getImage()
                        .getScaledInstance(800, 600, Image.SCALE_SMOOTH)));
        backgroundLabel.setBounds(0, 0, 800, 600);
        layeredPane.add(backgroundLabel, Integer.valueOf(0));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBounds(0, 0, 800, 600);

        JButton playButton = createStyledButton("Play Game");
        // ---------------------
        // On "Play Game", ask how many players, then set the number in the controller
        // and go to the "Game" screen.
        playButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(
                    this,
                    "How many players? (1-8)",
                    "Number of Players",
                    JOptionPane.QUESTION_MESSAGE
            );
            if (input != null) {
                try {
                    int numPlayers = Integer.parseInt(input);
                    _controller.setNumberOfPlayers(numPlayers);
                    // Optional: Could do a check if numPlayers < 1 or > 8
                    // Then show the "Game" panel
                    _cardLayout.show(_mainPanel, "Game");
                    updateCurrentPlayerLabel(); // Initialize label for the new game
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid number of players!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        // ---------------------

        JButton optionsButton = createStyledButton("Options");
        optionsButton.addActionListener(e -> _cardLayout.show(_mainPanel, "Options"));

        JButton exitButton = createStyledButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(playButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(optionsButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(exitButton);
        buttonPanel.add(Box.createVerticalGlue());

        layeredPane.add(buttonPanel, Integer.valueOf(1));

        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.add(layeredPane, BorderLayout.CENTER);
        return menuPanel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.RED);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private JPanel createOptionsMenu() {
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BorderLayout());
        optionsPanel.setBackground(Color.RED);

        JLabel optionsLabel = new JLabel("Options Menu", JLabel.CENTER);
        optionsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        optionsLabel.setForeground(Color.WHITE);
        optionsPanel.add(optionsLabel, BorderLayout.NORTH);

        // Volume Slider
        JPanel volumePanel = new JPanel();
        volumePanel.setOpaque(false);
        JLabel volumeLabel = new JLabel("Volume:");
        volumeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        volumeLabel.setForeground(Color.WHITE);

        JSlider volumeSlider = new JSlider(0, 100, 75); // Min: 0, Max: 100, Default: 75
        volumeSlider.addChangeListener(e -> {
            int value = volumeSlider.getValue();
            float volume = value / 100f; // Convert to range 0.0 - 1.0
            musicPlayer.setVolume(volume);
        });

        volumePanel.add(volumeLabel);
        volumePanel.add(volumeSlider);

        optionsPanel.add(volumePanel, BorderLayout.CENTER);

        JButton backButton = createStyledButton("Back to Main Menu");
        backButton.addActionListener(e -> _cardLayout.show(_mainPanel, "Main Menu"));
        optionsPanel.add(backButton, BorderLayout.SOUTH);

        return optionsPanel;
    }

    /**
     * Creates the main "Game" screen with a layout:
     *  - Top: label showing current player
     *  - Center: a text area with messages about dice rolls, etc.
     *  - Bottom: buttons (Roll Dice, Back to Menu)
     */
    private JPanel createGameScreen() {
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBackground(Color.RED);

        // ---------- Top Panel: current player label ----------
        currentPlayerLabel = new JLabel("Current Player: (not set yet)");
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        currentPlayerLabel.setForeground(Color.WHITE);
        currentPlayerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gamePanel.add(currentPlayerLabel, BorderLayout.NORTH);

        // ---------- Center: text area for game messages ----------
        gameMessagesArea = new JTextArea();
        gameMessagesArea.setEditable(false);
        gameMessagesArea.setBackground(Color.BLACK);
        gameMessagesArea.setForeground(Color.WHITE);
        gameMessagesArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(gameMessagesArea);
        gamePanel.add(scrollPane, BorderLayout.CENTER);

        // ---------- Bottom Panel: buttons ----------
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);

        // Roll Dice button
        JButton rollDiceButton = createStyledButton("Roll Dice");
        rollDiceButton.addActionListener(e -> {
            // We ask the controller to do the dice roll, move player, etc.
            String result = _controller.rollDiceAndMove();
            // Display the result in the text area
            gameMessagesArea.append(result + "\n");

            // Update the label for the new current player after the move
            updateCurrentPlayerLabel();
        });
        bottomPanel.add(rollDiceButton);

        // Back to main menu button
        JButton backButton = createStyledButton("Back to Main Menu");
        backButton.addActionListener(e -> _cardLayout.show(_mainPanel, "Main Menu"));
        bottomPanel.add(backButton);

        gamePanel.add(bottomPanel, BorderLayout.SOUTH);

        return gamePanel;
    }

    /**
     * Helper method to refresh the label showing which player's turn it is.
     */
    private void updateCurrentPlayerLabel() {
        String currentPlayer = _controller.getCurrentPlayerName();
        currentPlayerLabel.setText("Current Player: " + currentPlayer);
    }
}
