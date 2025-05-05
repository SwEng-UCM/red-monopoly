// src/View/MainWindow.java
package View;

import Controller.Controller;
import Controller.GameServer;
import Controller.NetworkClient;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MainWindow extends JFrame {
    protected static String filePath =
            "resources/click-buttons-ui-menu-sounds-effects-button-8-205394.wav";

    private final Controller _controller;
    private final CardLayout _cardLayout;
    private final JPanel     _mainPanel;
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
        musicPlayer.playMusic("resources/Dark_is_the_Night_-_Soviet_WW2_Song.wav");

        _cardLayout = new CardLayout();
        _mainPanel   = new JPanel(_cardLayout);

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

        _mainPanel.add(createMainMenu(),   "Main Menu");
        _mainPanel.add(createOptionsMenu(), "Options");
        add(_mainPanel);

        _cardLayout.show(_mainPanel, "Main Menu");
        setVisible(true);
    }

    private JPanel createMainMenu() {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 800));

        JLabel backgroundLabel = new JLabel(
                new ImageIcon(
                        new ImageIcon("resources/redmonopolyLogo.jpg")
                                .getImage()
                                .getScaledInstance(800, 800, Image.SCALE_SMOOTH)
                )
        );
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
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2.setColor(new Color(0, 0, 0, 150));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        roundedBackground.setLayout(
                new BoxLayout(roundedBackground, BoxLayout.Y_AXIS)
        );
        roundedBackground.setOpaque(false);
        roundedBackground.setBounds(250, 200, 300, 400);

        JButton playButton = createStyledButton("Play Game");
        playButton.addActionListener(e -> {
            MusicPlayer.playSoundEffect(filePath);
            startGameFlow();
        });

        JButton hostButton = createStyledButton("Host Multiplayer");
        hostButton.addActionListener(e -> {
            MusicPlayer.playSoundEffect(filePath);
            hostMultiplayerFlow();
        });

        JButton joinButton = createStyledButton("Join Multiplayer");
        joinButton.addActionListener(e -> {
            MusicPlayer.playSoundEffect(filePath);
            joinMultiplayerFlow();
        });

        JButton loadButton = createStyledButton("Load Game");
        loadButton.addActionListener(e -> {
            MusicPlayer.playSoundEffect(filePath);
            loadSavedGame();
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

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(playButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(hostButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(joinButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(loadButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(optionsButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(exitButton);
        centerPanel.add(Box.createVerticalGlue());

        roundedBackground.add(centerPanel);
        layeredPane.add(roundedBackground, Integer.valueOf(1));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(layeredPane, BorderLayout.CENTER);
        return wrapper;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setForeground(Color.RED);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(Color.RED);
                btn.setForeground(Color.WHITE);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.RED);
            }
        });
        return btn;
    }

    private JPanel createOptionsMenu() {
        JPanel options = new JPanel();
        options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));
        options.setBackground(new Color(30, 0, 0));
        options.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // Volume
        JPanel volPanel = new JPanel(new BorderLayout());
        volPanel.setOpaque(false);
        JLabel volLabel = new JLabel("Music Volume:");
        volLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        volLabel.setForeground(Color.WHITE);
        JSlider volSlider = new JSlider(0, 100, (int)(musicPlayer.getVolume()*100));
        volSlider.setOpaque(false);
        volSlider.addChangeListener(e -> musicPlayer.setVolume(volSlider.getValue()/100f));
        volPanel.add(volLabel, BorderLayout.WEST);
        volPanel.add(volSlider, BorderLayout.CENTER);
        volPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JCheckBox disableMusic = new JCheckBox("Disable Background Music");
        disableMusic.setOpaque(false);
        disableMusic.setForeground(Color.WHITE);
        disableMusic.setFont(new Font("Arial", Font.PLAIN, 16));
        disableMusic.setAlignmentX(Component.CENTER_ALIGNMENT);
        disableMusic.addItemListener(e -> {
            if (disableMusic.isSelected()) musicPlayer.stopMusic();
            else {
                musicPlayer.playMusic("resources/Dark_is_the_Night_-_Soviet_WW2_Song.wav");
                musicPlayer.setVolume(volSlider.getValue()/100f);
            }
        });

        JLabel aiLabel = new JLabel("AI Turn Speed:");
        aiLabel.setForeground(Color.WHITE);
        aiLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        aiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JSlider aiSlider = new JSlider(500, 2000, 1000);
        aiSlider.setOpaque(false);
        aiSlider.setMajorTickSpacing(500);
        aiSlider.setPaintTicks(true);
        aiSlider.setPaintLabels(true);
        aiSlider.setAlignmentX(Component.CENTER_ALIGNMENT);

        aiSlider.addChangeListener(e ->
                System.out.println("Set AI turn delay to " + aiSlider.getValue() + " ms")
        );

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setOpaque(false);
        JButton resetBtn = createStyledButton("Reset to Defaults");
        resetBtn.addActionListener(e -> {
            volSlider.setValue(75);
            aiSlider.setValue(1000);
            disableMusic.setSelected(false);
            musicPlayer.setVolume(0.75f);
            musicPlayer.playMusic("resources/Dark_is_the_Night_-_Soviet_WW2_Song.wav");
        });
        JButton backBtn = createStyledButton("Back to Main Menu");
        backBtn.addActionListener(e -> {
            MusicPlayer.playSoundEffect(filePath);
            _cardLayout.show(_mainPanel, "Main Menu");
        });
        btnPanel.add(resetBtn);
        btnPanel.add(backBtn);

        options.add(volPanel);
        options.add(Box.createRigidArea(new Dimension(0,20)));
        options.add(disableMusic);
        options.add(Box.createRigidArea(new Dimension(0,20)));
        options.add(aiLabel);
        options.add(aiSlider);
        options.add(Box.createRigidArea(new Dimension(0,30)));
        options.add(btnPanel);

        return options;
    }

    private void startGameFlow() {
        // Single-player setup
        JPanel diffPanel = new JPanel(new GridLayout(0,1));
        diffPanel.add(new JLabel("Select AI Difficulty:"));
        JComboBox<String> diffCombo = new JComboBox<>(new String[]{"Easy","Medium","Hard"});
        diffPanel.add(diffCombo);
        if (JOptionPane.showConfirmDialog(
                this, diffPanel, "AI Difficulty",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE
        ) != JOptionPane.OK_OPTION) return;
        _controller.setAIDifficulty((String)diffCombo.getSelectedItem());

        String input = JOptionPane.showInputDialog(
                this, "How many players? (2-8)", "Number of Players",
                JOptionPane.QUESTION_MESSAGE
        );
        if (input == null) return;
        int n;
        try { n = Integer.parseInt(input); }
        catch(NumberFormatException ex) { return; }
        if (n<2||n>8) {
            JOptionPane.showMessageDialog(this,
                    "Number of players must be between 2 and 8!",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        List<String> names = new ArrayList<>();
        List<String> avatars = new ArrayList<>();
        for(int i=1;i<=n;i++){
            JPanel pnl=new JPanel(new GridLayout(0,1));
            JTextField tf=new JTextField();
            pnl.add(new JLabel("Name for Player "+i+" (type 'AI' for AI):"));
            pnl.add(tf);
            if(JOptionPane.showConfirmDialog(
                    this,pnl,"Player "+i, JOptionPane.OK_CANCEL_OPTION
            )!=JOptionPane.OK_OPTION) return;
            String name=tf.getText().trim();
            if(name.isEmpty()){
                JOptionPane.showMessageDialog(this,
                        "Player name cannot be empty!","Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            AvatarSelectionWindow av=new AvatarSelectionWindow(this);
            String path=av.getSelectedAvatarPath();
            if(path==null){
                JOptionPane.showMessageDialog(this,
                        "You must select an avatar!","Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            names.add(name);
            avatars.add(path);
        }
        _controller.setNumberOfPlayers(names.size(), names, avatars);
        musicPlayer.stopMusic();
        GameWindow gw = new GameWindow(_controller, this, null, null);
        setVisible(false);
        gw.setVisible(true);
    }

    private void hostMultiplayerFlow() {
        List<String> names = new ArrayList<>();
        List<String> avatars = new ArrayList<>();
        if (!askPlayerSetup(names, avatars)) return;
        _controller.setNumberOfPlayers(names.size(), names, avatars);

        // start server thread
        new Thread(() -> new GameServer(_controller).start(), "Monopoly-Server")
                .start();

        // connect as player 1
        String myId = names.get(0);
        try {
            NetworkClient net = new NetworkClient("localhost", GameServer.PORT, myId);
            musicPlayer.stopMusic();
            GameWindow gw = new GameWindow(_controller, this, net, myId);
            setVisible(false);
            gw.setVisible(true);
        } catch(IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Unable to start/join server: "+ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void joinMultiplayerFlow() {
        String host = JOptionPane.showInputDialog(
                this, "Enter server IP:", "Join Multiplayer",
                JOptionPane.QUESTION_MESSAGE
        );
        if(host==null||host.isEmpty()) return;
        String myId = JOptionPane.showInputDialog(
                this, "Enter your player name:", "Join Multiplayer",
                JOptionPane.QUESTION_MESSAGE
        );
        if(myId==null||myId.isEmpty()) return;
        try {
            NetworkClient net = new NetworkClient(host, GameServer.PORT, myId);
            musicPlayer.stopMusic();
            GameWindow gw = new GameWindow(_controller, this, net, myId);
            setVisible(false);
            gw.setVisible(true);
        } catch(IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to connect: "+ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private boolean askPlayerSetup(List<String> names, List<String> avatars) {
        JPanel diffPanel = new JPanel(new GridLayout(0,1));
        diffPanel.add(new JLabel("Select AI Difficulty:"));
        JComboBox<String> diffCombo = new JComboBox<>(new String[]{"Easy","Medium","Hard"});
        diffPanel.add(diffCombo);
        if(JOptionPane.showConfirmDialog(
                this, diffPanel, "AI Difficulty",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE
        )!=JOptionPane.OK_OPTION) return false;
        _controller.setAIDifficulty((String)diffCombo.getSelectedItem());

        String input = JOptionPane.showInputDialog(
                this, "How many players? (2-8)", "Number of Players",
                JOptionPane.QUESTION_MESSAGE
        );
        if(input==null) return false;
        int n;
        try { n=Integer.parseInt(input); }
        catch(NumberFormatException ex){ return false; }
        if(n<2||n>8){
            JOptionPane.showMessageDialog(this,
                    "Number of players must be between 2 and 8!",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        for(int i=1;i<=n;i++){
            JPanel pnl=new JPanel(new GridLayout(0,1));
            JTextField tf=new JTextField();
            pnl.add(new JLabel("Name for Player "+i+" (type 'AI' for AI):"));
            pnl.add(tf);
            if(JOptionPane.showConfirmDialog(
                    this,pnl,"Player "+i, JOptionPane.OK_CANCEL_OPTION
            )!=JOptionPane.OK_OPTION) return false;
            String name=tf.getText().trim();
            if(name.isEmpty()){
                JOptionPane.showMessageDialog(this,
                        "Player name cannot be empty!","Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return false;
            }
            AvatarSelectionWindow av=new AvatarSelectionWindow(this);
            String path=av.getSelectedAvatarPath();
            if(path==null){
                JOptionPane.showMessageDialog(this,
                        "You must select an avatar!","Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return false;
            }
            names.add(name);
            avatars.add(path);
        }
        return true;
    }

    private void loadSavedGame() {
        JFileChooser chooser = new JFileChooser("games");
        if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
            String saveFile = chooser.getSelectedFile().getAbsolutePath();
            _controller.loadGame(saveFile);
            musicPlayer.stopMusic();
            GameWindow gw = new GameWindow(_controller, this, null, null);
            setVisible(false);
            gw.setVisible(true);
        }
    }
}
