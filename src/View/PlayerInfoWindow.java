package View;

import Controller.Controller;
import Model.Player;
import Model.PropertyTile; // if needed for property names

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerInfoWindow extends JFrame {
    private Controller controller;
    private JTable playerTable;

    public PlayerInfoWindow(Controller controller) {
        this.controller = controller;
        setTitle("Player Info");
        // Double the frame size from 600x400 to 1200x800
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        // Create the table model with column headers
        String[] columnNames = { "Name", "Money (₽)", "Position", "In Gulag?", "Owned Properties" };
        Object[][] data = buildPlayerData();

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        playerTable = new JTable(tableModel);

        // Increase table font and row height for larger display
        playerTable.setFont(new Font("SansSerif", Font.PLAIN, 16));  // Increase font size
        playerTable.setRowHeight(40); // Double the default row height (assuming 20 was default)

        // Set a custom cell renderer to highlight the current player's row
        playerTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value,
                                                           boolean isSelected,
                                                           boolean hasFocus,
                                                           int row,
                                                           int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String currentPlayerName = controller.getCurrentPlayerName();
                String rowPlayerName = table.getModel().getValueAt(row, 0).toString();
                if (rowPlayerName.equals(currentPlayerName)) {
                    c.setBackground(Color.YELLOW);
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(playerTable);
        adjustColumnWidths(playerTable);
        add(scrollPane, BorderLayout.CENTER);

        // Refresh button to update data
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            refreshPlayerData();
            adjustColumnWidths(playerTable);
        });
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
        // Use the controller to get properties owned by p.
        List<PropertyTile> ownedProps = controller.getOwnedProperties(p);
        if (ownedProps.isEmpty()) {
            return "None";
        }
        return ownedProps.stream()
                .map(PropertyTile::getName)
                .collect(Collectors.joining(", "));
    }

    /**
     * Adjust the column widths to fit content.
     */
    private void adjustColumnWidths(JTable table) {
        for (int col = 0; col < table.getColumnCount(); col++) {
            TableColumn tableColumn = table.getColumnModel().getColumn(col);
            int preferredWidth = 50; // minimum width
            int maxWidth = 300;
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, col);
                Component c = table.prepareRenderer(cellRenderer, row, col);
                int width = c.getPreferredSize().width + 10;
                preferredWidth = Math.max(preferredWidth, width);
                if (preferredWidth >= maxWidth) {
                    preferredWidth = maxWidth;
                    break;
                }
            }
            tableColumn.setPreferredWidth(preferredWidth);
        }
    }

    public void refreshData() {
        refreshPlayerData();
        adjustColumnWidths(playerTable);
    }


}

