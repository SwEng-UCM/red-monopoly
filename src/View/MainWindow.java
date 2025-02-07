package View;

import java.awt.*;
import javax.swing.*;
import Controller.*;

public class MainWindow extends JFrame {
    private Controller _controller;

    public MainWindow(Controller controller) {
        _controller = controller;
        initGUI();
    }

    private void initGUI() {
        // Set layout and title for the current JFrame instance (this)
        setTitle("[RED MONOPOLY]");
        setLayout(new BorderLayout());

        // Add main content panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        // Set the default close operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set frame size and visibility
        setSize(800, 600); // Set a default size (e.g., 800x600)
        setVisible(true); // Make the frame visible
    }
}
