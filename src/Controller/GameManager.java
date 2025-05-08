package Controller;

import Model.*;
import javax.swing.SwingUtilities;
import java.awt.Frame;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Glue layer: converts text commands from sockets into real Controller calls.
 */
public class GameManager {

    private final GameServer  server;
    private final Controller  controller;

    private final Map<String, Player>       idToPlayer  = new ConcurrentHashMap<>();
    private final Map<String, PropertyTile> pendingBuy  = new ConcurrentHashMap<>();

    public GameManager(GameServer srv, Controller ctrl) {
        this.server     = srv;
        this.controller = ctrl;
    }

    /* --- give others (ClientHandler) access to the server --------- */
    public GameServer getServer() { return server; }

    /* ==============================================================
                          Player lifecycle
       ============================================================== */
    public synchronized void addPlayer(String id, ClientHandler ch) {

        Player p = controller.getAllPlayers()
                .stream()
                .filter(pl -> pl.getName().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);

        if (p == null) {  // name not found in current GUI game
            pm(id, "ERR:No such player in current game. " +
                    "Create '"+id+"' in the GUI first, then reconnect.");
            return;
        }

        idToPlayer.put(id, p);
        server.broadcast("INFO:" + id + " joined from terminal");

        /* send quick snapshot */
        pm(id, "BAL:" + p.getMoney()
                + " POS:" + p.getPosition()
                + " OWN:" + p.getOwnedProperties().size() + " props");

        if (id.equals(controller.getCurrentPlayerName())) {
            pm(id, "YOURTURN");
            server.broadcast("TURN:" + id);
        }
    }

    /* called by ClientHandler.cleanup() */
    public synchronized void removePlayer(String id) {
        pendingBuy.remove(id);
        idToPlayer.remove(id);
        server.broadcast("INFO:" + id + " disconnected");
        refreshGui();
    }

    /* ==============================================================
                              Commands
       ============================================================== */
    public void handleCommand(String playerId, String cmd) {

        /* --- answer to a pending property offer ------------------- */
        if (cmd.equals("BUY") || cmd.equals("SKIP")) {
            PropertyTile offer = pendingBuy.remove(playerId);
            if (offer == null) { pm(playerId,"ERR:No pending offer"); return; }

            if (cmd.equals("BUY") && offer.getOwner() == null) {
                offer.completePurchase(idToPlayer.get(playerId));
                server.broadcast("INFO:" + playerId + " bought " + offer.getName());
            } else {
                server.broadcast("INFO:" + playerId + " declined " + offer.getName());
            }
            refreshGui();
            return;
        }

        /* --- normal ROLL command --------------------------------- */
        if (!"ROLL".equals(cmd))            { pm(playerId,"ERR:Unknown cmd"); return; }
        if (!playerId.equals(controller.getCurrentPlayerName())) {
            pm(playerId,"ERR:Not your turn"); return;
        }

        SwingUtilities.invokeLater(() -> {

            int[] dice    = controller.rollDice();
            String result = controller.movePlayerAfterDiceRoll(dice);

            /* property offer? */
            if (result.startsWith("ASKBUY:")) {
                String[] p = result.split(":");
                String name  = p[1];
                int     cost = Integer.parseInt(p[2]);

                PropertyTile tile = (PropertyTile) controller.getBoardTiles()
                        .stream().filter(t -> t instanceof PropertyTile
                                && t.getName().equals(name))
                        .findFirst().orElse(null);

                if (tile != null) {
                    pendingBuy.put(playerId, tile);
                    pm(playerId, "OFFER:" + name + ":" + cost);       // terminal prompt
                }
            } else {
                server.broadcast("STATE:" + result.replace('\n',' '));
            }

            server.broadcast(String.format("MOVE:%s:%d+%d", playerId, dice[0], dice[1]));
            server.broadcast("TURN:" + controller.getCurrentPlayerName());
            refreshGui();
        });
    }

    /* ==============================================================
                               Helpers
       ============================================================== */
    private void pm(String id, String msg) { server.broadcast("PM:" + id + ":" + msg); }

    private void refreshGui() {
        for (Frame f : Frame.getFrames())
            if (f instanceof View.GameWindow)
                ((View.GameWindow) f).refreshUIAfterExternalMove();
    }
}
