package View;

import Controller.Controller;
import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private Controller _controller;
    private MainWindow _mainWindow; // Reference to the main menu window
    private JLabel currentPlayerLabel;
    private JTextArea gameMessagesArea;

    public GameWindow(Controller controller, MainWindow mainWindow) {
        _controller = controller;
        _mainWindow = mainWindow;
        setTitle("Game - [RED MONOPOLY]");
        setSize(800, 600);
        setLocationRelativeTo(null);
        //setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initGUI();
    }

    private void initGUI() {
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBackground(Color.RED);

        // Top Panel: Current player label
        currentPlayerLabel = new JLabel("Current Player: (not set yet)");
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        currentPlayerLabel.setForeground(Color.WHITE);
        currentPlayerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gamePanel.add(currentPlayerLabel, BorderLayout.NORTH);

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
            String result = _controller.rollDiceAndMove();
            gameMessagesArea.append(result + "\n");
            updateCurrentPlayerLabel();
        });
        bottomPanel.add(rollDiceButton);

        JButton backButton = createStyledButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            dispose();               // Close the game window
            _mainWindow.setVisible(true); // Show the main menu window again
        });
        bottomPanel.add(backButton);

        gamePanel.add(bottomPanel, BorderLayout.SOUTH);

        add(gamePanel);
        updateCurrentPlayerLabel();
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
}
