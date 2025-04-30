package View;

import Controller.Controller;
import Model.Player;
import Model.PropertyTile;
import Model.RailroadTile;
import Model.Tile;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerInfoWindow extends JFrame {
    private Controller controller;
    private JTable playerTable;

    public PlayerInfoWindow(Controller controller) {
        this.controller = controller;
        setTitle("Player Info - Properties & Railroads");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(30, 30, 30));

        String[] columnNames = {"Player", "Money (₽)", "Position", "In Gulag?", "Properties", "Railroads"};
        Object[][] data = buildPlayerData();

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        playerTable = new JTable(tableModel);
        playerTable.setFont(new Font("Arial", Font.PLAIN, 16));
        playerTable.setRowHeight(40);
        playerTable.setForeground(Color.WHITE);
        playerTable.setBackground(new Color(40, 40, 40));
        playerTable.setGridColor(new Color(70, 70, 70));
        playerTable.setSelectionBackground(new Color(80, 0, 0));
        playerTable.setSelectionForeground(Color.WHITE);

        JTableHeader header = playerTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(100, 0, 0));
        header.setForeground(Color.WHITE);

        playerTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String currentPlayerName = controller.getCurrentPlayerName();
                String rowPlayerName = table.getModel().getValueAt(row, 0).toString();

                if (rowPlayerName.equals(currentPlayerName)) {
                    c.setBackground(new Color(80, 40, 0));
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                    c.setForeground(Color.YELLOW);
                } else {
                    c.setBackground(isSelected ? table.getSelectionBackground() : new Color(40, 40, 40));
                    c.setFont(c.getFont().deriveFont(Font.PLAIN));
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(playerTable);
        scrollPane.getViewport().setBackground(new Color(30, 30, 30));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        adjustColumnWidths(playerTable);
        add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 16));
        refreshButton.setBackground(new Color(150, 0, 0));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        refreshButton.addActionListener(e -> {
            refreshPlayerData();
            adjustColumnWidths(playerTable);
        });

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(30, 30, 30));
        bottom.add(refreshButton);

        add(bottom, BorderLayout.SOUTH);
    }

    private Object[][] buildPlayerData() {
        List<Player> players = controller.getAllPlayers();
        List<RailroadTile> allRailroads = getAllRailroads();
        Object[][] data = new Object[players.size()][6];

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            data[i][0] = p.getName();
            data[i][1] = String.format("%,d ₽", p.getMoney());
            data[i][2] = p.getPosition();
            data[i][3] = p.isInJail() ? "Yes" : "No";
            data[i][4] = getPropertyNamesAsString(p);
            data[i][5] = getRailroadNamesAsString(p, allRailroads);
        }

        return data;
    }

    private List<RailroadTile> getAllRailroads() {
        List<RailroadTile> railroads = new ArrayList<>();
        for (Tile tile : controller.getBoardTiles()) {
            if (tile instanceof RailroadTile) {
                railroads.add((RailroadTile) tile);
            }
        }
        return railroads;
    }

    private String getPropertyNamesAsString(Player p) {
        return p.getOwnedProperties().isEmpty() ? "None"
                : p.getOwnedProperties().stream().map(PropertyTile::getName).collect(Collectors.joining(", "));
    }

    private String getRailroadNamesAsString(Player p, List<RailroadTile> allRailroads) {
        List<String> ownedNames = new ArrayList<>();
        for (RailroadTile railroad : allRailroads) {
            if (railroad.getOwner() == p) {
                ownedNames.add(railroad.getName());
            }
        }
        return ownedNames.isEmpty() ? "None" : String.join(", ", ownedNames);
    }

    private void refreshPlayerData() {
        DefaultTableModel model = (DefaultTableModel) playerTable.getModel();
        model.setRowCount(0);

        List<RailroadTile> allRailroads = getAllRailroads();
        List<Player> players = controller.getAllPlayers();

        for (Player p : players) {
            model.addRow(new Object[]{
                    p.getName(),
                    String.format("%,d ₽", p.getMoney()),
                    p.getPosition(),
                    p.isInJail() ? "Yes" : "No",
                    getPropertyNamesAsString(p),
                    getRailroadNamesAsString(p, allRailroads)
            });
        }
    }

    private void adjustColumnWidths(JTable table) {
        for (int col = 0; col < table.getColumnCount(); col++) {
            TableColumn tableColumn = table.getColumnModel().getColumn(col);
            int preferredWidth = 50;
            int maxWidth = 300;

            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, col);
                Component c = table.prepareRenderer(cellRenderer, row, col);
                int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
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
