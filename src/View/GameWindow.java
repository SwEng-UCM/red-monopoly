// src/View/GameWindow.java
package View;

import Controller.Controller;
import Controller.NetworkClient;
import Model.GameState;
import Model.Player;
import Model.AIPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

public class GameWindow extends JFrame {
    private final Controller    _controller;
    private final MainWindow    _mainWindow;
    private final NetworkClient netClient;
    private final String        playerId;

    private JLabel        currentPlayerLabel;
    private JLabel        currentBalanceLabel;
    private JButton       rollDiceButton;
    private MusicPlayer   inGameMusic;
    private BoardView     boardView;
    private DualDicePanel dualDicePanel;
    private PlayerInfoWindow playerInfoWindow;

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

        // In-game music
        inGameMusic = new MusicPlayer();
        inGameMusic.playMusic("resources/Russia-Theme-Atomic-_Civilization-6-OST_-Kalinka_1.wav");
        inGameMusic.setVolume(0.7f);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                inGameMusic.stopMusic();
                _mainWindow.getMusicPlayer().playMusic(
                        "resources/Dark_is_the_Night_-_Soviet_WW2_Song.wav"
                );
                _mainWindow.setVisible(true);
            }
        });

        // Always listen to the server
        netClient.setListener(new NetworkClient.GameMessageListener() {
            @Override public void onFullState(GameState state) {
                SwingUtilities.invokeLater(() -> {
                    _controller.loadGameState(state);
                    refreshUIAfterExternalMove();
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
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(GameWindow.this, outcome)
                );
            }
            @Override public void onTurn(String id) {
                SwingUtilities.invokeLater(() -> {
                    refreshUIAfterExternalMove();
                    rollDiceButton.setEnabled(id.equals(playerId));
                });
            }
            @Override public void onBalance(String id, int money, int pos, int props) {
                SwingUtilities.invokeLater(() -> {
                    if (id.equals(playerId)) {
                        currentBalanceLabel.setText("Current Balance: " + money + " ₽");
                    }
                });
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

    private void initGUI() {
        // Top panel
        JPanel topPanel = new JPanel(new GridLayout(2,1));
        topPanel.setBackground(new Color(60,0,0));
        currentPlayerLabel = new JLabel("Current Player: (loading...)", SwingConstants.CENTER);
        currentPlayerLabel.setFont(new Font("Arial",Font.BOLD,24));
        currentPlayerLabel.setForeground(Color.WHITE);
        topPanel.add(currentPlayerLabel);
        currentBalanceLabel = new JLabel("Current Balance: –", SwingConstants.CENTER);
        currentBalanceLabel.setFont(new Font("Arial",Font.BOLD,18));
        currentBalanceLabel.setForeground(Color.WHITE);
        topPanel.add(currentBalanceLabel);

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,20,10));
        bottomPanel.setBackground(new Color(30,0,0));

        rollDiceButton = createImageButton(
                "resources/dicesRolling.png","Roll the dice to move",
                e -> {
                    rollDiceButton.setEnabled(false);
                    try { netClient.roll(); }
                    catch(IOException ex) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Network error: "+ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
        );
        rollDiceButton.setEnabled(false);

        JButton leaveButton = createImageButton(
                "resources/goBack.png","Leave Game",
                e -> {
                    MusicPlayer.playSoundEffect(MainWindow.filePath);
                    int c = JOptionPane.showConfirmDialog(
                            this,"Save game before leaving?","Save Game",
                            JOptionPane.YES_NO_CANCEL_OPTION
                    );
                    if (c==JOptionPane.CANCEL_OPTION) return;
                    if (c==JOptionPane.YES_OPTION) {
                        String fn = JOptionPane.showInputDialog(
                                this,"Enter save file name:","Save Game",
                                JOptionPane.QUESTION_MESSAGE
                        );
                        if (fn!=null && !fn.trim().isEmpty()) {
                            _controller.saveGame(fn.trim());
                            JOptionPane.showMessageDialog(this,"Saved!");
                        }
                    }
                    dispose();
                }
        );

        JButton infoBtn = createImageButton(
                "resources/playerInfoWindow.png","Player Info",
                e -> {
                    MusicPlayer.playSoundEffect(MainWindow.filePath);
                    if (playerInfoWindow==null||!playerInfoWindow.isDisplayable()) {
                        playerInfoWindow=new PlayerInfoWindow(_controller);
                    }
                    playerInfoWindow.setVisible(true);
                    playerInfoWindow.refreshData();
                }
        );

        JButton undoBtn = createImageButton(
                "resources/undo.png","Undo Last Action",
                e -> {
                    _controller.undoLastCommand();
                    refreshUIAfterExternalMove();
                }
        );

        bottomPanel.add(makeButtonCard(rollDiceButton));
        bottomPanel.add(makeButtonCard(leaveButton));
        bottomPanel.add(makeButtonCard(infoBtn));
        bottomPanel.add(makeButtonCard(undoBtn));

        // Board + dice
        boardView = new BoardView(_controller);
        for(int i=0;i<40;i++){
            boardView.getBoardPanel()
                    .setManualTileImage(i,
                            new ImageIcon("resources/tiles/"+i+".png").getImage());
        }
        dualDicePanel = new DualDicePanel();
        dualDicePanel.setPreferredSize(new Dimension(200,200));

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, boardView, dualDicePanel
        );
        split.setOneTouchExpandable(true);
        split.setResizeWeight(1.0);
        split.setDividerLocation(0.8);

        setLayout(new BorderLayout());
        add(topPanel,    BorderLayout.NORTH);
        add(split,       BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshUIAfterExternalMove() {
        updateCurrentPlayerLabel();
        updateCurrentBalanceLabel();
        boardView.refreshBoard();
        boardView.drawPlayerTokens(_controller.getAllPlayers());
    }

    private void updateCurrentPlayerLabel(){
        List<Player> ps = _controller.getAllPlayers();
        currentPlayerLabel.setText(
                ps.isEmpty()
                        ? "Current Player: (loading...)"
                        : "Current Player: " + _controller.getCurrentPlayerName()
        );
    }

    private void updateCurrentBalanceLabel(){
        List<Player> ps = _controller.getAllPlayers();
        currentBalanceLabel.setText(
                ps.isEmpty()
                        ? "Current Balance: –"
                        : "Current Balance: " + _controller.getCurrentPlayerBalance() + " ₽"
        );
    }

    private void checkAndTriggerAITurn(){
        Player cur = _controller.getCurrentPlayer();
        if(cur instanceof AIPlayer) {
            new Timer(2000,t->{
                ((Timer)t.getSource()).stop();
                int[] dice = _controller.rollDice();
                dualDicePanel.startAnimation(dice[0],dice[1]);
                new Timer(2000,t2->{
                    ((Timer)t2.getSource()).stop();
                    ((AIPlayer)cur).takeTurnWithDice(
                            dice,_controller.getMonopolyGame(),_controller);
                    refreshUIAfterExternalMove();
                    checkAndTriggerAITurn();
                }).start();
            }).start();
        }
    }

    private JButton createImageButton(
            String path, String tip, ActionListener l
    ) {
        ImageIcon ico = new ImageIcon(path);
        Image img = ico.getImage().getScaledInstance(60,60,Image.SCALE_SMOOTH);
        JButton btn = new JButton(new ImageIcon(img));
        btn.setToolTipText(tip);
        btn.setBorder(null);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(l);
        return btn;
    }

    private JPanel makeButtonCard(JButton btn){
        JPanel p = new JPanel(new BorderLayout()){
            @Override protected void paintComponent(Graphics g){
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
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(90,90));
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        p.addMouseListener(new MouseAdapter(){
            @Override public void mouseEntered(MouseEvent e){
                p.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,90),2));
            }
            @Override public void mouseExited(MouseEvent e){
                p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            }
        });
        p.add(btn, BorderLayout.CENTER);
        return p;
    }
}
