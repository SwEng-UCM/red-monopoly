package View;

import Controller.Controller;
import Model.AIPlayer;
import Model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.event.*;

public class GameWindow extends JFrame {
    private Controller _controller;
    private MainWindow _mainWindow;
    private JLabel currentPlayerLabel;
    private JLabel currentBalanceLabel;
    private MusicPlayer inGameMusic;

    private BoardView boardView;
    private DualDicePanel dualDicePanel;
    private PlayerInfoWindow playerInfoWindow;

    /** Called by GameManager after a roll that came from a socket client. */
    public void refreshUIAfterExternalMove() {
        updateCurrentPlayerLabel();
        updateCurrentBalanceLabel();
        boardView.refreshBoard();
    }


    public GameWindow(Controller controller, MainWindow mainWindow) {
        _controller = controller;
        _mainWindow = mainWindow;
        setTitle("Game - [RED MONOPOLY]");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initGUI();

        inGameMusic = new MusicPlayer();
        inGameMusic.playMusic("resources/Russia-Theme-Atomic-_Civilization-6-OST_-Kalinka_1.wav");
        inGameMusic.setVolume(0.7f);



        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                inGameMusic.stopMusic();
                _mainWindow.getMusicPlayer().playMusic("resources/Dark_is_the_Night_-_Soviet_WW2_Song.wav");
                _mainWindow.setVisible(true);
            }
        });

        checkAndTriggerAITurn();
    }

    private void applySovietDialogStyle() {
        UIManager.put("OptionPane.messageFont", new Font("Serif", Font.BOLD, 18));
        UIManager.put("OptionPane.buttonFont", new Font("Serif", Font.PLAIN, 16));
        UIManager.put("OptionPane.messageForeground", new Color(200, 30, 30));
        UIManager.put("Panel.background", new Color(30, 0, 0));
    }

    private void initGUI() {
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(new Color(60, 0, 0));
        currentPlayerLabel = new JLabel("Current Player: (not set yet)", SwingConstants.CENTER);
        currentPlayerLabel.setFont(new Font("Serif", Font.BOLD, 24));
        currentPlayerLabel.setForeground(Color.WHITE);
        topPanel.add(currentPlayerLabel);

        currentBalanceLabel = new JLabel("Current Balance: 0 ₽", SwingConstants.CENTER);
        currentBalanceLabel.setFont(new Font("Serif", Font.BOLD, 18));
        currentBalanceLabel.setForeground(Color.WHITE);
        topPanel.add(currentBalanceLabel);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(new Color(30, 0, 0));

        // Roll Dice Button
        JButton rollDiceButton = createImageButton("resources/dicesRolling.png", "Roll the dice to move", e -> {
            int[] diceValues = _controller.rollDice();
            dualDicePanel.startAnimation(diceValues[0], diceValues[1]);
            new Timer(2000, evt -> {
                ((Timer) evt.getSource()).stop();
                String result = _controller.movePlayerAfterDiceRoll(diceValues);
                updateCurrentPlayerLabel();
                updateCurrentBalanceLabel();
                boardView.refreshBoard();

                if (result != null && !result.isBlank()) {
                    applySovietDialogStyle();
                    JOptionPane.showMessageDialog(this, result, "Turn Result", JOptionPane.INFORMATION_MESSAGE);
                }

                checkAndTriggerAITurn();
            }).start();
        });

        JButton leaveButton = createImageButton("resources/goBack.png", "Leave Game", e -> {
            MusicPlayer.playSoundEffect(MainWindow.filePath);
            applySovietDialogStyle();
            int choice = JOptionPane.showConfirmDialog(this,
                    "Do you want to save the game before leaving?",
                    "Save Game", JOptionPane.YES_NO_CANCEL_OPTION);
            if (choice == JOptionPane.CANCEL_OPTION) return;

            if (choice == JOptionPane.YES_OPTION) {
                String saveName = JOptionPane.showInputDialog(this,
                        "Enter save game file name (e.g., mySave.json):",
                        "Save Game", JOptionPane.QUESTION_MESSAGE);
                if (saveName != null && !saveName.trim().isEmpty()) {
                    _controller.saveGame(saveName.trim());
                    JOptionPane.showMessageDialog(this, "Game saved successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid file name. Game not saved.");
                    return;
                }
            }
            dispose();
        });

        JButton playerInfoButton = createImageButton("resources/playerInfoWindow.png", "Show player status, money, position, and properties", e -> {
            MusicPlayer.playSoundEffect(MainWindow.filePath);
            if (playerInfoWindow == null || !playerInfoWindow.isDisplayable()) {
                playerInfoWindow = new PlayerInfoWindow(_controller);
            }
            playerInfoWindow.setVisible(true);
            playerInfoWindow.refreshData();
        });

        JButton undoButton = createImageButton("resources/undo.png", "Undo Last Action", e -> {
            _controller.undoLastCommand();
            updateCurrentPlayerLabel();
            updateCurrentBalanceLabel();
            boardView.refreshBoard();
        });

        bottomPanel.add(makeButtonCard(rollDiceButton));
        bottomPanel.add(makeButtonCard(leaveButton));
        bottomPanel.add(makeButtonCard(playerInfoButton));
        bottomPanel.add(makeButtonCard(undoButton));

        boardView = new BoardView(_controller);
        for (int i = 0; i < 40; i++) {
            boardView.getBoardPanel().setManualTileImage(i, new ImageIcon("resources/tiles/" + i + ".png").getImage());
        }
        boardView.getBoardPanel().setManualTileImage(10, new ImageIcon("resources/tiles/10.png").getImage());
        boardView.getBoardPanel().setManualTileImage(20, new ImageIcon("resources/tiles/20.png").getImage());
        boardView.getBoardPanel().setManualTileImage(30, new ImageIcon("resources/tiles/30.png").getImage());

        dualDicePanel = new DualDicePanel();
        dualDicePanel.setPreferredSize(new Dimension(200, 200));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, boardView, dualDicePanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerLocation(0.8);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(splitPane, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        updateCurrentPlayerLabel();
        updateCurrentBalanceLabel();
    }

    private JButton createImageButton(String imagePath, String toolTip, ActionListener listener) {
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image scaledImage = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(scaledImage));
        button.setToolTipText(toolTip);
        button.setBorder(null);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);
        return button;
    }

    private JPanel makeButtonCard(JButton button) {
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(60, 0, 0), 0, getHeight(), new Color(90, 0, 0)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(200, 200, 200, 80));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                super.paintComponent(g);
            }
        };
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(90, 90));
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setOpaque(false);
        button.setContentAreaFilled(false);

        wrapper.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                wrapper.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 90), 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            }
        });

        wrapper.add(button, BorderLayout.CENTER);
        return wrapper;
    }

    private void updateCurrentPlayerLabel() {
        currentPlayerLabel.setText("Current Player: " + _controller.getCurrentPlayerName());
    }

    private void updateCurrentBalanceLabel() {
        int currentBalance = _controller.getCurrentPlayerBalance();
        currentBalanceLabel.setText("Current Balance: " + currentBalance + " ₽");
    }

    private void checkAndTriggerAITurn() {
        Player current = _controller.getCurrentPlayer();
        if (current instanceof AIPlayer) {
            new Timer(2000, evt -> {
                ((Timer) evt.getSource()).stop();
                int[] diceValues = _controller.rollDice();
                dualDicePanel.startAnimation(diceValues[0], diceValues[1]);
                new Timer(2000, evt2 -> {
                    ((Timer) evt2.getSource()).stop();
                    String result = ((AIPlayer) current).takeTurnWithDice(diceValues, _controller.getMonopolyGame(), _controller);

                    if (result != null && !result.isBlank()) {
                        applySovietDialogStyle();
                        JOptionPane.showMessageDialog(this, result, "AI Turn Result", JOptionPane.INFORMATION_MESSAGE);
                    }

                    updateCurrentPlayerLabel();
                    updateCurrentBalanceLabel();
                    boardView.refreshBoard();
                    checkAndTriggerAITurn();
                }).start();
            }).start();
        }
    }
}
