package View;

import Controller.Controller;
import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MainWindow extends JFrame {
    protected static String filePath = "resources/click-buttons-ui-menu-sounds-effects-button-8-205394.wav";
    private Controller _controller;
    private CardLayout _cardLayout;
    private JPanel _mainPanel;
    private MusicPlayer musicPlayer;  // Main menu music player

    public MainWindow(Controller controller) {

        try {
            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        _controller = controller;
        musicPlayer = new MusicPlayer();
        // Start main menu music
        musicPlayer.playMusic("resources/Dark_is_the_Night_-_Soviet_WW2_Song.wav");
        initGUI();
    }

    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }

    private void initGUI() {
        setTitle("[RED MONOPOLY]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Resize to a square frame for the board – adjust as needed.
        setSize(800, 800);
        setResizable(false);
        setLocationRelativeTo(null);

        _cardLayout = new CardLayout();
        _mainPanel = new JPanel(_cardLayout);
        _mainPanel.add(createMainMenu(), "Main Menu");
        _mainPanel.add(createOptionsMenu(), "Options");

        add(_mainPanel);
        _cardLayout.show(_mainPanel, "Main Menu");
        setVisible(true);
    }

    // (The rest of MainWindow remains largely the same.)
    private JPanel createMainMenu() {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 800));

        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setIcon(new ImageIcon(
                new ImageIcon("resources/redmonopolyLogo.jpg").getImage()
                        .getScaledInstance(800, 800, Image.SCALE_SMOOTH)));
        backgroundLabel.setBounds(0, 0, 800, 800);
        layeredPane.add(backgroundLabel, Integer.valueOf(0));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBounds(0, 0, 800, 800);

        JButton playButton = createStyledButton("Play Game");
        playButton.addActionListener(e -> {
            MusicPlayer.playSoundEffect(filePath);

            // First, show a custom dialog with a combo box for AI difficulty selection.
            JPanel difficultyPanel = new JPanel(new GridLayout(0, 1));
            difficultyPanel.add(new JLabel("Select AI Difficulty:"));
            String[] difficulties = {"Easy", "Medium", "Hard"};
            JComboBox<String> difficultyCombo = new JComboBox<>(difficulties);
            difficultyPanel.add(difficultyCombo);

            int diffResult = JOptionPane.showConfirmDialog(
                    this,
                    difficultyPanel,
                    "AI Difficulty Selection",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (diffResult != JOptionPane.OK_OPTION) {
                return; // User canceled the dialog.
            }

            // Store the chosen difficulty in the controller.
            String chosenDifficulty = (String) difficultyCombo.getSelectedItem();
            _controller.setAIDifficulty(chosenDifficulty);

            // Next, prompt for the number of players.
            String input = JOptionPane.showInputDialog(
                    this,
                    "How many players? (2-8)",
                    "Number of Players",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (input != null) {
                try {
                    int numPlayers = Integer.parseInt(input);
                    if (numPlayers < 2 || numPlayers > 8) {
                        JOptionPane.showMessageDialog(this,
                                "Number of players must be between 2 and 8!",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    List<String> playerNames = new ArrayList<>();
                    for (int i = 1; i <= numPlayers; i++) {
                        String name = JOptionPane.showInputDialog(
                                this,
                                "Enter name for Player " + i + " (enter 'AI' for computer control):",
                                "Player Name",
                                JOptionPane.QUESTION_MESSAGE
                        );
                        if (name == null || name.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(this,
                                    "Player name cannot be empty!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        playerNames.add(name);
                    }
                    _controller.setNumberOfPlayers(numPlayers, playerNames);
                    // Stop main menu music and open the game window.
                    musicPlayer.stopMusic();
                    GameWindow gameWindow = new GameWindow(_controller, this);
                    this.setVisible(false);
                    gameWindow.setVisible(true);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid number of players!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        JButton optionsButton = createStyledButton("Options");
        optionsButton.addActionListener(e -> {
            MusicPlayer.playSoundEffect(filePath);
            _cardLayout.show(_mainPanel, "Options");
        });

        JButton exitButton = createStyledButton("Exit");
        exitButton.addActionListener(e -> {
            MusicPlayer.playSoundEffect(filePath);
            System.exit(0);
        });

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

        JPanel volumePanel = new JPanel();
        volumePanel.setOpaque(false);
        JLabel volumeLabel = new JLabel("Volume:");
        volumeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        volumeLabel.setForeground(Color.WHITE);

        JSlider volumeSlider = new JSlider(0, 100, 75);
        volumeSlider.addChangeListener(e -> {
            int value = volumeSlider.getValue();
            float volume = value / 100f;
            musicPlayer.setVolume(volume);
        });

        volumePanel.add(volumeLabel);
        volumePanel.add(volumeSlider);
        optionsPanel.add(volumePanel, BorderLayout.CENTER);

        JButton backButton = createStyledButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            MusicPlayer.playSoundEffect(filePath);
            _cardLayout.show(_mainPanel, "Main Menu");
        });
        optionsPanel.add(backButton, BorderLayout.SOUTH);

        return optionsPanel;
    }
}
