// src/Controller/GameManager.java
package Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import Model.GameState;
import Model.Tile;
import Model.GoTile;
import Model.PropertyTile;
import Model.ChanceTile;
import Model.JailTile;
import Model.GoToJailTile;
import Model.TaxTile;
import Model.CommunityChestTile;
import Model.FreeParkingTile;
import Model.RailroadTile;
import Model.UtilityTile;
import Model.RuntimeTypeAdapterFactory;
import Model.Player;
import Model.AIPlayer;

import javax.swing.SwingUtilities;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.Frame;

public class GameManager {

    private final GameServer server;
    private final Controller controller;
    private final Map<String, Player> idToPlayer = new ConcurrentHashMap<>();

    // ─── JSON setup ─────────────────────────────────────────────────────────────
    private final RuntimeTypeAdapterFactory<Tile> tileAdapterFactory =
            RuntimeTypeAdapterFactory.of(Tile.class, "tileType")
                    .registerSubtype(GoTile.class,             "GoTile")
                    .registerSubtype(PropertyTile.class,       "PropertyTile")
                    .registerSubtype(ChanceTile.class,         "ChanceTile")
                    .registerSubtype(JailTile.class,           "JailTile")
                    .registerSubtype(GoToJailTile.class,       "GoToJailTile")
                    .registerSubtype(TaxTile.class,            "TaxTile")
                    .registerSubtype(CommunityChestTile.class, "CommunityChestTile")
                    .registerSubtype(FreeParkingTile.class,    "FreeParkingTile")
                    .registerSubtype(RailroadTile.class,       "RailroadTile")
                    .registerSubtype(UtilityTile.class,        "UtilityTile");

    private final RuntimeTypeAdapterFactory<Player> playerAdapterFactory =
            RuntimeTypeAdapterFactory.of(Player.class, "type")
                    .registerSubtype(Player.class,   "Human")
                    .registerSubtype(AIPlayer.class, "AI");

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(tileAdapterFactory)
            .registerTypeAdapterFactory(playerAdapterFactory)
            .create();

    public GameManager(GameServer srv, Controller ctrl) {
        this.server     = srv;
        this.controller = ctrl;
    }

    /* === player lifecycle === */
    public synchronized void addPlayer(String id, ClientHandler ch) {
        // 1.  find or reject
        Player p = controller.getAllPlayers()
                .stream()
                .filter(pl -> pl.getName().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);

        if (p == null) {
            // auto-create a new human player if not pre-registered
            System.out.println("Auto-creating new Human player '" + id + "'");
            p = new Model.Player(id);
            // you may want to give them a default avatar path here
            p.setAvatarPath("resources/players/8.png");
            controller.getMonopolyGame().getPlayers().add(p);
        }


        idToPlayer.put(id, p);
        server.broadcast("INFO:" + id + " joined from terminal");

        // 2.  tell them their own balance/position snapshot
        pm(id, "BAL:" + p.getMoney()
                + " POS:" + p.getPosition()
                + " OWN:" + p.getOwnedProperties().size() + " props");

        // 3.  whisper “YOURTURN” if it's them
        if (id.equals(controller.getCurrentPlayerName())) {
            pm(id, "YOURTURN");
        }

        // 4.  broadcast EVERYONE whose turn it is now (unconditional!)
        server.broadcast("TURN:" + controller.getCurrentPlayerName());

        // 5.  finally broadcast full state
        broadcastFullState();
    }

    public synchronized void removePlayer(String id) {
        idToPlayer.remove(id);
        server.broadcast("INFO:" + id + " disconnected");
        // n.b. no TURN broadcast here, turn continues as normal
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

        SwingUtilities.invokeLater(() -> {
            int[] dice = controller.rollDice();
            String outcome = controller.movePlayerAfterDiceRoll(dice);

            // update all GUI windows
            for (Frame f : Frame.getFrames()) {
                if (f instanceof View.GameWindow) {
                    ((View.GameWindow) f).refreshUIAfterExternalMove();
                }
            }

            server.broadcast(String.format("MOVE:%s:%d+%d",
                    playerId, dice[0], dice[1]));
            server.broadcast("STATE:" + outcome.replace('\n',' '));

            // **always** broadcast new TURN
            server.broadcast("TURN:" + controller.getCurrentPlayerName());
            broadcastFullState();
        });
    }

    private void pm(String playerId, String msg) {
        server.broadcast("PM:" + playerId + ":" + msg);
    }

    /** Send a JSON snapshot to everyone */
    private void broadcastFullState() {
        GameState state = new GameState(
                controller.getAllPlayers(),
                controller.getMonopolyGame().getBoard(),
                controller.getMonopolyGame().getCurrentPlayerIndex()
        );
        String json = gson.toJson(state);
        server.broadcast("FULLSTATE:" + json);
    }
}
