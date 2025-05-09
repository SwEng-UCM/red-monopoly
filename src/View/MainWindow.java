package View;

import Controller.Controller;
import Controller.GameServer;
import Controller.NetworkClient;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {
    // Static URLs for resources
    static final URL CLICK_SOUND_URL =
            MainWindow.class.getResource("/audio/click-buttons-ui-menu-sounds-effects-button-8-205394.wav");
    static final URL MENU_MUSIC_URL =
            MainWindow.class.getResource("/Dark_is_the_Night_-_Soviet_WW2_Song.wav");
    private static final URL BG_IMAGE_URL =
            MainWindow.class.getResource("/redmonopolyLogo.jpg");

    private final Controller  _controller;
    private final CardLayout  _cardLayout;
    private final JPanel      _mainPanel;
    private final MusicPlayer musicPlayer;

    public MainWindow(Controller controller) {
        super();
        try {
            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        _controller = controller;
        musicPlayer = new MusicPlayer();

        // Play menu music if available
        if (MENU_MUSIC_URL != null) {
            musicPlayer.playMusic(String.valueOf(MENU_MUSIC_URL));
        } else {
            System.err.println("Menu music not found!");
        }

        _cardLayout = new CardLayout();
        _mainPanel  = new JPanel(_cardLayout);

        initGUI();
    }

    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }

    private void initGUI() {
        setTitle("[RED MONOPOLY]");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 800);
        setResizable(false);
        setLocationRelativeTo(null);

        _mainPanel.add(createMainMenu(),    "Main Menu");
        _mainPanel.add(createOptionsMenu(), "Options");
        add(_mainPanel);

        _cardLayout.show(_mainPanel, "Main Menu");
        setVisible(true);
    }

    private JPanel createMainMenu() {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 800));

        // Background image
        ImageIcon bgIcon = BG_IMAGE_URL != null
                ? new ImageIcon(
                new ImageIcon(BG_IMAGE_URL)
                        .getImage()
                        .getScaledInstance(800, 800, Image.SCALE_SMOOTH)
        )
                : new ImageIcon();
        JLabel bg = new JLabel(bgIcon);
        bg.setBounds(0, 0, 800, 800);
        layeredPane.add(bg, Integer.valueOf(0));

        // Translucent rounded container
        JPanel rounded = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2.setColor(new Color(0, 0, 0, 150));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        rounded.setLayout(new BoxLayout(rounded, BoxLayout.Y_AXIS));
        rounded.setOpaque(false);
        rounded.setBounds(250, 200, 300, 400);

        // Button panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton playBtn = createStyledButton("Play Game");
        playBtn.addActionListener(e -> {
            if (CLICK_SOUND_URL != null) MusicPlayer.playSoundEffect(CLICK_SOUND_URL);
            startGameFlow();
        });

        JButton hostBtn = createStyledButton("Host Multiplayer");
        hostBtn.addActionListener(e -> {
            if (CLICK_SOUND_URL != null) MusicPlayer.playSoundEffect(CLICK_SOUND_URL);
            hostMultiplayerFlow();
        });

        JButton joinBtn = createStyledButton("Join Multiplayer");
        joinBtn.addActionListener(e -> {
            if (CLICK_SOUND_URL != null) MusicPlayer.playSoundEffect(CLICK_SOUND_URL);
            joinMultiplayerFlow();
        });

        JButton loadBtn = createStyledButton("Load Game");
        loadBtn.addActionListener(e -> {
            if (CLICK_SOUND_URL != null) MusicPlayer.playSoundEffect(CLICK_SOUND_URL);
            loadSavedGame();
        });

        JButton optionsBtn = createStyledButton("Options");
        optionsBtn.addActionListener(e -> {
            if (CLICK_SOUND_URL != null) MusicPlayer.playSoundEffect(CLICK_SOUND_URL);
            _cardLayout.show(_mainPanel, "Options");
        });

        JButton exitBtn = createStyledButton("Exit");
        exitBtn.addActionListener(e -> {
            if (CLICK_SOUND_URL != null) MusicPlayer.playSoundEffect(CLICK_SOUND_URL);
            System.exit(0);
        });

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(playBtn);
        centerPanel.add(Box.createRigidArea(new Dimension(0,15)));
        centerPanel.add(hostBtn);
        centerPanel.add(Box.createRigidArea(new Dimension(0,15)));
        centerPanel.add(joinBtn);
        centerPanel.add(Box.createRigidArea(new Dimension(0,15)));
        centerPanel.add(loadBtn);
        centerPanel.add(Box.createRigidArea(new Dimension(0,15)));
        centerPanel.add(optionsBtn);
        centerPanel.add(Box.createRigidArea(new Dimension(0,15)));
        centerPanel.add(exitBtn);
        centerPanel.add(Box.createVerticalGlue());

        rounded.add(centerPanel);
        layeredPane.add(rounded, Integer.valueOf(1));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(layeredPane, BorderLayout.CENTER);
        return wrapper;
    }

    private JButton createStyledButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 20));
        b.setForeground(Color.RED);
        b.setBackground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                b.setBackground(Color.RED);
                b.setForeground(Color.WHITE);
            }
            @Override public void mouseExited(MouseEvent e) {
                b.setBackground(Color.WHITE);
                b.setForeground(Color.RED);
            }
        });
        return b;
    }

    private JPanel createOptionsMenu() {
        JPanel opts = new JPanel();
        opts.setLayout(new BoxLayout(opts, BoxLayout.Y_AXIS));
        opts.setBackground(new Color(30, 0, 0));
        opts.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // Volume slider
        JPanel volP = new JPanel(new BorderLayout());
        volP.setOpaque(false);
        JLabel volL = new JLabel("Music Volume:");
        volL.setFont(new Font("Arial", Font.PLAIN, 18));
        volL.setForeground(Color.WHITE);
        JSlider volS = new JSlider(0, 100, (int)(musicPlayer.getVolume() * 100));
        volS.setOpaque(false);
        volS.addChangeListener(e -> musicPlayer.setVolume(volS.getValue() / 100f));
        volP.add(volL, BorderLayout.WEST);
        volP.add(volS, BorderLayout.CENTER);
        volP.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JCheckBox disableMusic = new JCheckBox("Disable Background Music");
        disableMusic.setOpaque(false);
        disableMusic.setForeground(Color.WHITE);
        disableMusic.setFont(new Font("Arial", Font.PLAIN, 16));
        disableMusic.setAlignmentX(Component.CENTER_ALIGNMENT);
        disableMusic.addItemListener(e -> {
            if (disableMusic.isSelected()) {
                musicPlayer.stopMusic();
            } else if (MENU_MUSIC_URL != null) {
                musicPlayer.playMusic(String.valueOf(MENU_MUSIC_URL));
                musicPlayer.setVolume(volS.getValue() / 100f);
            }
        });

        JLabel aiL = new JLabel("AI Turn Speed:");
        aiL.setForeground(Color.WHITE);
        aiL.setFont(new Font("Arial", Font.PLAIN, 18));
        aiL.setAlignmentX(Component.CENTER_ALIGNMENT);
        JSlider aiS = new JSlider(500, 2000, 1000);
        aiS.setOpaque(false);
        aiS.setMajorTickSpacing(500);
        aiS.setPaintTicks(true);
        aiS.setPaintLabels(true);
        aiS.setAlignmentX(Component.CENTER_ALIGNMENT);
        aiS.addChangeListener(e ->
                System.out.println("Set AI turn delay to " + aiS.getValue() + " ms")
        );

        JPanel btnP = new JPanel(new FlowLayout());
        btnP.setOpaque(false);
        JButton resetB = createStyledButton("Reset to Defaults");
        resetB.addActionListener(e -> {
            volS.setValue(75);
            aiS.setValue(1000);
            disableMusic.setSelected(false);
            musicPlayer.setVolume(0.75f);
            if (MENU_MUSIC_URL != null) musicPlayer.playMusic(String.valueOf(MENU_MUSIC_URL));
        });
        JButton backB = createStyledButton("Back to Main Menu");
        backB.addActionListener(e -> {
            if (CLICK_SOUND_URL != null) MusicPlayer.playSoundEffect(CLICK_SOUND_URL);
            _cardLayout.show(_mainPanel, "Main Menu");
        });
        btnP.add(resetB);
        btnP.add(backB);

        opts.add(volP);
        opts.add(Box.createRigidArea(new Dimension(0,20)));
        opts.add(disableMusic);
        opts.add(Box.createRigidArea(new Dimension(0,20)));
        opts.add(aiL);
        opts.add(aiS);
        opts.add(Box.createRigidArea(new Dimension(0,30)));
        opts.add(btnP);

        return opts;
    }

    private void startGameFlow() {
        List<String> names   = new ArrayList<>();
        List<String> avatars = new ArrayList<>();
        if (!askPlayerSetup(names, avatars)) return;

        _controller.setNumberOfPlayers(names.size(), names, avatars);

        musicPlayer.stopMusic();
        GameWindow gw = new GameWindow(_controller, this, null, null);
        setVisible(false);
        gw.setVisible(true);
    }

    private void hostMultiplayerFlow() {
        List<String> names   = new ArrayList<>();
        List<String> avatars = new ArrayList<>();
        if (!askPlayerSetup(names, avatars)) return;
        _controller.setNumberOfPlayers(names.size(), names, avatars);

        new Thread(() -> new GameServer(_controller).start(), "Server-Thread").start();
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}

        String myId = names.get(0);
        try {
            NetworkClient net = new NetworkClient("localhost", GameServer.PORT, myId);
            musicPlayer.stopMusic();
            new GameWindow(_controller, this, net, myId).setVisible(true);
            setVisible(false);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Unable to start/join server: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void joinMultiplayerFlow() {
        String host = JOptionPane.showInputDialog(
                this, "Enter server IP:", "Join Multiplayer",
                JOptionPane.QUESTION_MESSAGE
        );
        if (host == null || host.isBlank()) return;

        String myId = JOptionPane.showInputDialog(
                this, "Enter your player name:", "Join Multiplayer",
                JOptionPane.QUESTION_MESSAGE
        );
        if (myId == null || myId.isBlank()) return;

        try {
            NetworkClient net = new NetworkClient(host.trim(), GameServer.PORT, myId.trim());
            musicPlayer.stopMusic();
            new GameWindow(_controller, this, net, myId.trim()).setVisible(true);
            setVisible(false);
        } catch(IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to connect: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private boolean askPlayerSetup(List<String> names, List<String> avatars) {
        JPanel diffP = new JPanel(new GridLayout(0,1));
        diffP.add(new JLabel("Select AI Difficulty:"));
        JComboBox<String> diffC = new JComboBox<>(new String[]{"Easy","Medium","Hard"});
        diffP.add(diffC);
        if (JOptionPane.showConfirmDialog(
                this, diffP, "AI Difficulty",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE
        ) != JOptionPane.OK_OPTION) return false;
        _controller.setAIDifficulty((String)diffC.getSelectedItem());

        String input = JOptionPane.showInputDialog(
                this, "How many players? (2-8)", "Number of Players",
                JOptionPane.QUESTION_MESSAGE
        );
        if (input == null) return false;
        int n;
        try { n = Integer.parseInt(input); }
        catch (NumberFormatException ex) { return false; }
        if (n < 2 || n > 8) {
            JOptionPane.showMessageDialog(this,
                    "Number of players must be between 2 and 8!",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        for (int i = 1; i <= n; i++) {
            JPanel p = new JPanel(new GridLayout(0,1));
            JTextField tf = new JTextField();
            p.add(new JLabel("Name for Player " + i + " (type 'AI'):"));
            p.add(tf);
            if (JOptionPane.showConfirmDialog(
                    this, p, "Player " + i,
                    JOptionPane.OK_CANCEL_OPTION
            ) != JOptionPane.OK_OPTION) return false;
            String nm = tf.getText().trim();
            if (nm.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Player name cannot be empty!",
                        "Error", JOptionPane.ERROR_MESSAGE
                );
                return false;
            }
            AvatarSelectionWindow av = new AvatarSelectionWindow(this);
            String ap = av.getSelectedAvatarPath();
            if (ap == null) {
                JOptionPane.showMessageDialog(this,
                        "You must select an avatar!",
                        "Error", JOptionPane.ERROR_MESSAGE
                );
                return false;
            }
            names.add(nm);
            avatars.add(ap);
        }
        return true;
    }

    private void loadSavedGame() {
        JFileChooser chooser = new JFileChooser("games");
        chooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            _controller.loadGame(path);
            musicPlayer.stopMusic();
            new GameWindow(_controller, this, null, null).setVisible(true);
            setVisible(false);
        }
    }
}
