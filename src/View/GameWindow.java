package View;

import Controller.Controller;
import Controller.NetworkClient;
import Model.GameState;
import Model.Player;
import Model.AIPlayer;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class GameWindow extends JFrame {
    // Reuse the same click and menu URLs from MainWindow
    public static final URL CLICK_SOUND_URL = MainWindow.CLICK_SOUND_URL;
    public static final URL MENU_MUSIC_URL  = MainWindow.MENU_MUSIC_URL;

    private final Controller    _controller;
    private final MainWindow    _mainWindow;
    private final NetworkClient netClient;
    private final String        playerId;

    private JLabel currentPlayerLabel;
    private JLabel currentBalanceLabel;
    private JButton rollDiceButton;
    private MusicPlayer inGameMusic;
    private BoardView boardView;
    private DualDicePanel dualDicePanel;
    private PlayerInfoWindow playerInfoWindow;

    public GameWindow(Controller controller,
                      MainWindow mainWindow,
                      NetworkClient netClient,
                      String playerId) {
        super("Game - [RED MONOPOLY]");
        _controller = controller;
        _mainWindow = mainWindow;
        this.netClient = netClient;
        this.playerId = playerId;

        try {
            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initGUI();
        initMusic();
        initNetworkingOrAI();

        // Final one-time UI sync
        updateCurrentPlayerLabel();
        updateCurrentBalanceLabel();
        boardView.refreshBoard();
        boardView.drawPlayerTokens(_controller.getAllPlayers());
    }

    private void initGUI() {
        // ── Top info panel ─────────────────────────────────
        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setBackground(new Color(60, 0, 0));

        currentPlayerLabel = new JLabel("Current Player: (loading...)", SwingConstants.CENTER);
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        currentPlayerLabel.setForeground(Color.WHITE);
        top.add(currentPlayerLabel);

        currentBalanceLabel = new JLabel("Current Balance: –", SwingConstants.CENTER);
        currentBalanceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        currentBalanceLabel.setForeground(Color.WHITE);
        top.add(currentBalanceLabel);

        // ── Bottom buttons ─────────────────────────────────
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottom.setBackground(new Color(30, 0, 0));

        rollDiceButton = createImageButton("/dicesRolling.png",
                "Roll the dice to move", e -> onRollDice());
        rollDiceButton.setEnabled(netClient == null);
        bottom.add(makeButtonCard(rollDiceButton));

        bottom.add(makeButtonCard(createImageButton("/goBack.png",
                "Leave Game", e -> onLeave())));
        bottom.add(makeButtonCard(createImageButton("/playerInfoWindow.png",
                "Show player info", e -> onPlayerInfo())));
        bottom.add(makeButtonCard(createImageButton("/undo.png",
                "Undo Last Action", e -> onUndo())));

        // ── Board & dice ───────────────────────────────────
        boardView = new BoardView(_controller);
        for (int i = 0; i < 40; i++) {
            URL tileUrl = getClass().getResource("/tiles/" + i + ".png");
            if (tileUrl != null) {
                boardView.getBoardPanel()
                        .setManualTileImage(i, new ImageIcon(tileUrl).getImage());
            }
        }

        dualDicePanel = new DualDicePanel();
        dualDicePanel.setPreferredSize(new Dimension(200, 200));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, boardView, dualDicePanel);
        split.setOneTouchExpandable(true);
        split.setResizeWeight(1.0);
        split.setDividerLocation(0.8);

        setLayout(new BorderLayout());
        add(top,    BorderLayout.NORTH);
        add(split,  BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void initMusic() {
        inGameMusic = new MusicPlayer();
        URL inGameUrl = getClass().getResource("/Russia-Theme-Atomic-_Civilization-6-OST_-Kalinka_1.wav");
        if (inGameUrl != null) {
            inGameMusic.playMusic(String.valueOf(inGameUrl));
            inGameMusic.setVolume(0.7f);
        } else {
            System.err.println("In-game music not found!");
        }

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                inGameMusic.stopMusic();
                if (MENU_MUSIC_URL != null) {
                    _mainWindow.getMusicPlayer().playMusic(String.valueOf(MENU_MUSIC_URL));
                }
                _mainWindow.setVisible(true);
            }
        });
    }

    private void initNetworkingOrAI() {
        if (netClient == null) {
            // Single-player -> AI loop
            checkAndTriggerAITurn();
        } else {
            netClient.setListener(new NetworkClient.GameMessageListener() {
                @Override public void onFullState(GameState state) {
                    SwingUtilities.invokeLater(() -> {
                        _controller.loadGameState(state);
                        updateCurrentPlayerLabel();
                        updateCurrentBalanceLabel();
                        boardView.refreshBoard();
                        boardView.drawPlayerTokens(_controller.getAllPlayers());
                        rollDiceButton.setEnabled(
                                _controller.getCurrentPlayerName().equals(playerId)
                        );
                    });
                }
                @Override public void onMove(String id, int d1, int d2) {
                    SwingUtilities.invokeLater(() ->
                            dualDicePanel.startAnimation(d1, d2)
                    );
                }
                @Override public void onState(String outcome) {
                    if (_controller.getCurrentPlayerName().equals(playerId)) {
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(GameWindow.this, outcome)
                        );
                    }
                }
                @Override public void onTurn(String id) {
                    SwingUtilities.invokeLater(() -> {
                        updateCurrentPlayerLabel();
                        rollDiceButton.setEnabled(id.equals(playerId));
                    });
                }
                @Override public void onBalance(String id, int money, int pos, int props) {
                    if (id.equals(playerId)) {
                        SwingUtilities.invokeLater(() ->
                                currentBalanceLabel.setText("Current Balance: " + money + " ₽")
                        );
                    }
                }
                @Override public void onInfo(String msg) {
                    SwingUtilities.invokeLater(() ->
                            System.out.println("[INFO] " + msg)
                    );
                }
                @Override public void onError(String id, String err) {
                    if (id.equals(playerId)) {
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(GameWindow.this,
                                        "Error: " + err, "Server Error",
                                        JOptionPane.ERROR_MESSAGE)
                        );
                    }
                }
            });
        }
    }

    private void onRollDice() {
        if (netClient != null) {
            rollDiceButton.setEnabled(false);
            try {
                netClient.roll();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Network error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            int[] dice = _controller.rollDice();
            dualDicePanel.startAnimation(dice[0], dice[1]);
            new Timer(2000, evt -> {
                ((Timer)evt.getSource()).stop();
                _controller.movePlayerAfterDiceRoll(dice);
                updateCurrentPlayerLabel();
                updateCurrentBalanceLabel();
                boardView.refreshBoard();
                boardView.drawPlayerTokens(_controller.getAllPlayers());
                checkAndTriggerAITurn();
            }).start();
        }
    }

    private void onLeave() {
        if (CLICK_SOUND_URL != null) MusicPlayer.playSoundEffect(CLICK_SOUND_URL);
        int c = JOptionPane.showConfirmDialog(this,
                "Do you want to save before leaving?", "Save Game",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (c == JOptionPane.CANCEL_OPTION) return;
        if (c == JOptionPane.YES_OPTION) {
            String name = JOptionPane.showInputDialog(this,
                    "Enter save file name:");
            if (name != null && !name.trim().isEmpty()) {
                _controller.saveGame(name.trim());
                JOptionPane.showMessageDialog(this, "Game saved!");
            } else {
                return;
            }
        }
        dispose();
    }

    private void onPlayerInfo() {
        if (CLICK_SOUND_URL != null) MusicPlayer.playSoundEffect(CLICK_SOUND_URL);
        if (playerInfoWindow == null || !playerInfoWindow.isDisplayable()) {
            playerInfoWindow = new PlayerInfoWindow(_controller);
        }
        playerInfoWindow.setVisible(true);
        playerInfoWindow.refreshData();
    }

    private void onUndo() {
        _controller.undoLastCommand();
        updateCurrentPlayerLabel();
        updateCurrentBalanceLabel();
        boardView.refreshBoard();
        boardView.drawPlayerTokens(_controller.getAllPlayers());
    }

    private void updateCurrentPlayerLabel() {
        List<Player> ps = _controller.getAllPlayers();
        if (ps.isEmpty()) {
            currentPlayerLabel.setText("Current Player: (loading...)");
        } else {
            currentPlayerLabel.setText("Current Player: " + _controller.getCurrentPlayerName());
        }
    }

    private void updateCurrentBalanceLabel() {
        List<Player> ps = _controller.getAllPlayers();
        if (ps.isEmpty()) {
            currentBalanceLabel.setText("Current Balance: –");
        } else {
            currentBalanceLabel.setText("Current Balance: " +
                    _controller.getCurrentPlayerBalance() + " ₽");
        }
    }

    public void refreshUIAfterExternalMove() {
        updateCurrentPlayerLabel();
        updateCurrentBalanceLabel();
        boardView.refreshBoard();
        boardView.drawPlayerTokens(_controller.getAllPlayers());
    }

    private void checkAndTriggerAITurn() {
        List<Player> ps = _controller.getAllPlayers();
        if (ps.isEmpty()) return;
        Player cur = _controller.getCurrentPlayer();
        if (cur instanceof AIPlayer) {
            new Timer(2000, evt -> {
                ((Timer)evt.getSource()).stop();
                int[] dice = _controller.rollDice();
                dualDicePanel.startAnimation(dice[0], dice[1]);
                new Timer(2000, evt2 -> {
                    ((Timer)evt2.getSource()).stop();
                    ((AIPlayer)cur).takeTurnWithDice(
                            dice, _controller.getMonopolyGame(), _controller);
                    refreshUIAfterExternalMove();
                    checkAndTriggerAITurn();
                }).start();
            }).start();
        }
    }

    private JButton createImageButton(String resourcePath, String tip, ActionListener l) {
        URL imgUrl = getClass().getResource(resourcePath);
        ImageIcon icon = imgUrl != null
                ? new ImageIcon(new ImageIcon(imgUrl).getImage()
                .getScaledInstance(60, 60, Image.SCALE_SMOOTH))
                : new ImageIcon();
        JButton b = new JButton(icon);
        b.setToolTipText(tip);
        b.setBorder(null);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addActionListener(l);
        return b;
    }

    private JPanel makeButtonCard(JButton btn) {
        JPanel w = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(60, 0, 0),
                        0, getHeight(), new Color(90, 0, 0)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(200, 200, 200, 80));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                super.paintComponent(g);
            }
        };
        w.setOpaque(false);
        w.setPreferredSize(new Dimension(90, 90));
        w.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        w.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                w.setBorder(BorderFactory.createLineBorder(
                        new Color(255, 255, 255, 90), 2));
            }
            @Override public void mouseExited(MouseEvent e) {
                w.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            }
        });
        w.add(btn, BorderLayout.CENTER);
        return w;
    }
}
