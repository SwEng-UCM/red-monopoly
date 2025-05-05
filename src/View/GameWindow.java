// src/View/GameWindow.java
package View;

import Controller.Controller;
import Controller.NetworkClient;
import Model.GameState;
import Model.Player;
import Model.AIPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

public class GameWindow extends JFrame {
    private final Controller _controller;
    private final MainWindow _mainWindow;
    private final NetworkClient netClient;
    private final String playerId;

    private JLabel currentPlayerLabel;
    private JLabel currentBalanceLabel;
    private JButton rollDiceButton;
    private MusicPlayer inGameMusic;
    private BoardView boardView;
    private DualDicePanel dualDicePanel;
    private PlayerInfoWindow playerInfoWindow;

    /**
     * @param controller your Controller
     * @param mainWindow for returning music/UI
     * @param netClient  non-null for multiplayer, null for single-player
     * @param playerId   your player name (can be null in single-player)
     */
    public GameWindow(Controller controller,
                      MainWindow mainWindow,
                      NetworkClient netClient,
                      String playerId) {
        _controller = controller;
        _mainWindow = mainWindow;
        this.netClient = netClient;
        this.playerId  = playerId;

        setTitle("Game - [RED MONOPOLY]");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initGUI();
        initMusic();
        initNetworkingOrAI();

        // Always refresh UI once at start
        updateCurrentPlayerLabel();
        updateCurrentBalanceLabel();
        boardView.refreshBoard();
        boardView.drawPlayerTokens(_controller.getAllPlayers());
    }

    private void initGUI() {
        // Top info panel
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(new Color(60, 0, 0));
        currentPlayerLabel = new JLabel("Current Player: (loading...)", SwingConstants.CENTER);
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        currentPlayerLabel.setForeground(Color.WHITE);
        topPanel.add(currentPlayerLabel);

        currentBalanceLabel = new JLabel("Current Balance: –", SwingConstants.CENTER);
        currentBalanceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        currentBalanceLabel.setForeground(Color.WHITE);
        topPanel.add(currentBalanceLabel);

        // Bottom control buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(new Color(30, 0, 0));

        // Roll Dice button
        rollDiceButton = createImageButton(
                "resources/dicesRolling.png",
                "Roll the dice to move",
                e -> onRollDice()
        );
        // disable by default for multiplayer; will be enabled if it's your turn
        rollDiceButton.setEnabled(netClient == null);

        // Leave
        JButton leaveBtn = createImageButton(
                "resources/goBack.png",
                "Leave Game",
                e -> onLeave()
        );

        // Player Info
        JButton infoBtn = createImageButton(
                "resources/playerInfoWindow.png",
                "Show player status, money, position, and properties",
                e -> onPlayerInfo()
        );

        // Undo
        JButton undoBtn = createImageButton(
                "resources/undo.png",
                "Undo Last Action",
                e -> onUndo()
        );

        bottomPanel.add(makeButtonCard(rollDiceButton));
        bottomPanel.add(makeButtonCard(leaveBtn));
        bottomPanel.add(makeButtonCard(infoBtn));
        bottomPanel.add(makeButtonCard(undoBtn));

        // Board + Dice
        boardView = new BoardView(_controller);
        for (int i = 0; i < 40; i++) {
            boardView.getBoardPanel()
                    .setManualTileImage(i,
                            new ImageIcon("resources/tiles/" + i + ".png").getImage()
                    );
        }
        dualDicePanel = new DualDicePanel();
        dualDicePanel.setPreferredSize(new Dimension(200, 200));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, boardView, dualDicePanel);
        split.setOneTouchExpandable(true);
        split.setResizeWeight(1.0);
        split.setDividerLocation(0.8);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(split,    BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void initMusic() {
        inGameMusic = new MusicPlayer();
        inGameMusic.playMusic("resources/Russia-Theme-Atomic-_Civilization-6-OST_-Kalinka_1.wav");
        inGameMusic.setVolume(0.7f);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                inGameMusic.stopMusic();
                _mainWindow.getMusicPlayer()
                        .playMusic("resources/Dark_is_the_Night_-_Soviet_WW2_Song.wav");
                _mainWindow.setVisible(true);
            }
        });
    }

    private void initNetworkingOrAI() {
        if (netClient == null) {
            // single-player AI loop
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
                        // ◀ re-enable Roll-Dice if it's this client's turn
                        rollDiceButton.setEnabled(
                                _controller.getCurrentPlayerName().equals(playerId)
                        );
                    });
                }
                @Override public void onMove(String id, int d1, int d2) {
                    SwingUtilities.invokeLater(() -> dualDicePanel.startAnimation(d1, d2));
                }
                @Override public void onState(String outcome) {
                    // Only show dialogs to the active player
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
                                JOptionPane.showMessageDialog(
                                        GameWindow.this,
                                        "Error: " + err,
                                        "Server Error",
                                        JOptionPane.ERROR_MESSAGE
                                )
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
                JOptionPane.showMessageDialog(
                        this, "Network error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE
                );
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
        MusicPlayer.playSoundEffect(MainWindow.filePath);
        int choice = JOptionPane.showConfirmDialog(
                this, "Do you want to save the game before leaving?",
                "Save Game", JOptionPane.YES_NO_CANCEL_OPTION
        );
        if (choice == JOptionPane.CANCEL_OPTION) return;
        if (choice == JOptionPane.YES_OPTION) {
            String saveName = JOptionPane.showInputDialog(
                    this, "Enter save file name (e.g., mySave.json):"
            );
            if (saveName != null && !saveName.trim().isEmpty()) {
                _controller.saveGame(saveName.trim());
                JOptionPane.showMessageDialog(this, "Game saved successfully!");
            } else {
                return;
            }
        }
        dispose();
    }

    private void onPlayerInfo() {
        MusicPlayer.playSoundEffect(MainWindow.filePath);
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
        List<Player> players = _controller.getAllPlayers();
        if (players.isEmpty()) {
            currentPlayerLabel.setText("Current Player: (loading...)");
        } else {
            currentPlayerLabel.setText("Current Player: " + _controller.getCurrentPlayerName());
        }
    }

    private void updateCurrentBalanceLabel() {
        List<Player> players = _controller.getAllPlayers();
        if (players.isEmpty()) {
            currentBalanceLabel.setText("Current Balance: –");
        } else {
            currentBalanceLabel.setText("Current Balance: " + _controller.getCurrentPlayerBalance() + " ₽");
        }
    }

    public void refreshUIAfterExternalMove() {
        updateCurrentPlayerLabel();
        updateCurrentBalanceLabel();
        boardView.refreshBoard();
        boardView.drawPlayerTokens(_controller.getAllPlayers());
    }

    private void checkAndTriggerAITurn() {
        List<Player> players = _controller.getAllPlayers();
        if (players.isEmpty()) return;
        Player current = _controller.getCurrentPlayer();
        if (current instanceof AIPlayer) {
            new Timer(2000, evt -> {
                ((Timer)evt.getSource()).stop();
                int[] dice = _controller.rollDice();
                dualDicePanel.startAnimation(dice[0], dice[1]);
                new Timer(2000, evt2 -> {
                    ((Timer)evt2.getSource()).stop();
                    ((AIPlayer)current).takeTurnWithDice(
                            dice, _controller.getMonopolyGame(), _controller
                    );
                    refreshUIAfterExternalMove();
                    checkAndTriggerAITurn();
                }).start();
            }).start();
        }
    }

    private JButton createImageButton(String imagePath, String toolTip, ActionListener listener) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        JButton btn = new JButton(new ImageIcon(img));
        btn.setToolTipText(toolTip);
        btn.setBorder(null);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(listener);
        return btn;
    }

    private JPanel makeButtonCard(JButton button) {
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                GradientPaint gp = new GradientPaint(
                        0,0,new Color(60,0,0),
                        0,getHeight(),new Color(90,0,0)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                g2.setColor(new Color(200,200,200,80));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,20,20);
                super.paintComponent(g);
            }
        };
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(90,90));
        wrapper.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        wrapper.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                wrapper.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,90),2));
            }
            @Override public void mouseExited(MouseEvent e) {
                wrapper.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            }
        });
        wrapper.add(button, BorderLayout.CENTER);
        return wrapper;
    }
}
