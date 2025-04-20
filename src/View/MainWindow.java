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
    private MusicPlayer musicPlayer;

    public MainWindow(Controller controller) {
        try {
            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        _controller = controller;
        musicPlayer = new MusicPlayer();
        musicPlayer.playMusic("resources/Dark_is_the_Night_-_Soviet_WW2_Song.wav");
        initGUI();
    }

    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }

    private void initGUI() {
        setTitle("[RED MONOPOLY]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

    private JPanel createMainMenu() {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 800));

        JLabel backgroundLabel = new JLabel(new ImageIcon(
                new ImageIcon("resources/redmonopolyLogo.jpg").getImage()
                        .getScaledInstance(800, 800, Image.SCALE_SMOOTH)));
        backgroundLabel.setBounds(0, 0, 800, 800);
        layeredPane.add(backgroundLabel, Integer.valueOf(0));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBounds(250, 200, 300, 400);

        JPanel roundedBackground = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 150));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        roundedBackground.setLayout(new BoxLayout(roundedBackground, BoxLayout.Y_AXIS));
        roundedBackground.setOpaque(false);
        roundedBackground.setBounds(250, 200, 300, 400);

        JButton playButton = createStyledButton("Play Game");
        playButton.addActionListener(e -> startGameFlow());

        JButton loadGameButton = createStyledButton("Load Game");
        loadGameButton.addActionListener(e -> loadSavedGame());

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

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(playButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(loadGameButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(optionsButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(exitButton);
        centerPanel.add(Box.createVerticalGlue());

        roundedBackground.add(centerPanel);
        layeredPane.add(roundedBackground, Integer.valueOf(1));

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

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.RED);
                button.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
                button.setForeground(Color.RED);
            }
        });

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

    private void startGameFlow() {
        MusicPlayer.playSoundEffect(filePath);

        JPanel difficultyPanel = new JPanel(new GridLayout(0, 1));
        difficultyPanel.add(new JLabel("Select AI Difficulty:"));
        String[] difficulties = {"Easy", "Medium", "Hard"};
        JComboBox<String> difficultyCombo = new JComboBox<>(difficulties);
        difficultyPanel.add(difficultyCombo);

        int diffResult = JOptionPane.showConfirmDialog(
                this, difficultyPanel, "AI Difficulty Selection",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (diffResult != JOptionPane.OK_OPTION) return;

        String chosenDifficulty = (String) difficultyCombo.getSelectedItem();
        _controller.setAIDifficulty(chosenDifficulty);

        String input = JOptionPane.showInputDialog(
                this, "How many players? (2-8)",
                "Number of Players", JOptionPane.QUESTION_MESSAGE);

        if (input != null) {
            try {
                int numPlayers = Integer.parseInt(input);
                if (numPlayers < 2 || numPlayers > 8) {
                    JOptionPane.showMessageDialog(this,
                            "Number of players must be between 2 and 8!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                List<String> playerNames = new ArrayList<>();
                for (int i = 1; i <= numPlayers; i++) {
                    String name = JOptionPane.showInputDialog(
                            this, "Enter name for Player " + i + " (enter 'AI' for computer control):",
                            "Player Name", JOptionPane.QUESTION_MESSAGE);

                    if (name == null || name.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this,
                                "Player name cannot be empty!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    playerNames.add(name);
                }

                _controller.setNumberOfPlayers(numPlayers, playerNames);
                musicPlayer.stopMusic();
                GameWindow gameWindow = new GameWindow(_controller, this);
                this.setVisible(false);
                gameWindow.setVisible(true);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid number of players!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadSavedGame() {
        MusicPlayer.playSoundEffect(filePath);
        JFileChooser fileChooser = new JFileChooser("games");
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String saveFile = fileChooser.getSelectedFile().getAbsolutePath();
            _controller.loadGame(saveFile);
            musicPlayer.stopMusic();
            GameWindow gameWindow = new GameWindow(_controller, this);
            this.setVisible(false);
            gameWindow.setVisible(true);
        }
    }
}
