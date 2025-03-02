package View;

import Controller.Controller;
import Model.Player;
import Model.PropertyTile; // if needed for property names

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerInfoWindow extends JFrame {
    private Controller controller;
    private JTable playerTable;

    public PlayerInfoWindow(Controller controller) {
        this.controller = controller;
        setTitle("Player Info");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create the table model with column headers
        String[] columnNames = { "Name", "Money (₽)", "Position", "In Gulag?", "Owned Properties" };
        // Build the initial data from the controller
        Object[][] data = buildPlayerData();

        // Create the table and set it in a scroll pane
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        playerTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(playerTable);

        // Add the scroll pane to the center
        add(scrollPane, BorderLayout.CENTER);

        // (Optional) Add a "Refresh" button at the bottom to update data on demand
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshPlayerData());
        add(refreshButton, BorderLayout.SOUTH);
    }

    /**
     * Builds the 2D array of player data for the JTable.
     */
    private Object[][] buildPlayerData() {
        List<Player> players = controller.getAllPlayers(); // Make sure your Controller has this method
        Object[][] data = new Object[players.size()][5];

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            data[i][0] = p.getName();
            data[i][1] = p.getMoney();
            data[i][2] = p.getPosition();
            data[i][3] = p.isInJail() ? "Yes" : "No";

            // If your Player tracks owned properties, adapt accordingly:
            // E.g., if p.getOwnedProperties() returns List<PropertyTile>
            data[i][4] = getPropertyNamesAsString(p);
        }

        return data;
    }

    /**
     * Refresh the data in the existing table model.
     */
    private void refreshPlayerData() {
        DefaultTableModel model = (DefaultTableModel) playerTable.getModel();
        // Clear existing rows
        model.setRowCount(0);

        // Rebuild data and add rows
        List<Player> players = controller.getAllPlayers();
        for (Player p : players) {
            model.addRow(new Object[] {
                    p.getName(),
                    p.getMoney(),
                    p.getPosition(),
                    p.isInJail() ? "Yes" : "No",
                    getPropertyNamesAsString(p)
            });
        }
    }

    /**
     * Utility to convert a player's owned properties into a string.
     */
    private String getPropertyNamesAsString(Player p) {
        // If your Player doesn't track properties, remove this part.
        // Otherwise, adapt to however you store property data.
        // Example if you have: List<PropertyTile> getOwnedProperties()
        /*
        if (p.getOwnedProperties().isEmpty()) {
            return "None";
        }
        return p.getOwnedProperties().stream()
                 .map(PropertyTile::getName)
                 .collect(Collectors.joining(", "));
        */

        // If you don't yet have owned properties, just return "N/A" for now
        return "N/A";
    }
}
