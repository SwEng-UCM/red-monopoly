package Controller;

import Model.Player;
import javax.swing.SwingUtilities;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.awt.Frame;   //  <-- add


/**
 * Glue layer: converts text commands from sockets into real Controller calls.
 * NO game logic lives here – it just delegates to your existing Controller/TurnHandler.
 */
public class GameManager {

    private final GameServer  server;
    private final Controller  controller;
    private final Map<String, Player> idToPlayer = new ConcurrentHashMap<>();

    public GameManager(GameServer srv, Controller ctrl) {
        this.server     = srv;
        this.controller = ctrl;
    }

    /* === player lifecycle === */
    public synchronized void addPlayer(String id, ClientHandler ch) {

        // 1.  look ONLY for already-created players
        Player p = controller.getAllPlayers()
                .stream()
                .filter(pl -> pl.getName().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);

        if (p == null) {                     // ← name wasn’t in the GUI list
            pm(id, "ERR:No such player in current game. "
                    + "Create '"+id+"' in the GUI first, then reconnect.");
            return;                          // refuse the join
        }

        idToPlayer.put(id, p);
        server.broadcast("INFO:" + id + " joined from terminal");

        // 2.  send that player a one-line snapshot
        pm(id, "BAL:" + p.getMoney()
                + " POS:" + p.getPosition()
                + " OWN:" + p.getOwnedProperties().size() + " props");

        // 3.  if it’s already their turn, prompt them right away
        if (id.equals(controller.getCurrentPlayerName())) {
            pm(id, "YOURTURN");              // personal prompt
            server.broadcast("TURN:" + id);  // global info
        }
    }


    public synchronized void removePlayer(String id) {
        idToPlayer.remove(id);
        server.broadcast("INFO:" + id + " disconnected");
    }

    /* === command router === */
    public void handleCommand(String playerId, String cmd) {

        if (!"ROLL".equalsIgnoreCase(cmd)) {
            pm(playerId, "ERR:Unknown cmd");
            return;
        }

        if (!playerId.equals(controller.getCurrentPlayerName())) {
            pm(playerId, "ERR:Not your turn");
            return;
        }

        /*  Run real game logic on the EDT so Swing stays happy  */
        SwingUtilities.invokeLater(() -> {
            int[] dice = controller.rollDice();
            String outcome = controller.movePlayerAfterDiceRoll(dice);

            /* ────────── GUI refresh for every open GameWindow ────────── */
            for (Frame f : Frame.getFrames()) {
                if (f instanceof View.GameWindow) {
                    ((View.GameWindow) f).refreshUIAfterExternalMove();   // classic cast
                }
            }


            /*  Broadcast to consoles  */
            server.broadcast(String.format("MOVE:%s:%d+%d", playerId, dice[0], dice[1]));
            server.broadcast("STATE:" + outcome.replace('\n', ' '));

        /*  TurnHandler.processTurn already advanced the turn.
            DON’T call controller.endTurn() again or you’ll double-skip. */
            server.broadcast("TURN:" + controller.getCurrentPlayerName());
        });
    }


    private void pm(String playerId, String msg) {
        server.broadcast("PM:" + playerId + ":" + msg);
    }
}
