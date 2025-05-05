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
        // 1.  look ONLY for already-created players
        Player p = controller.getAllPlayers()
                .stream()
                .filter(pl -> pl.getName().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);

        if (p == null) {
            pm(id, "ERR:No such player in current game. "
                    + "Create '" + id + "' in the GUI first, then reconnect.");
            return;
        }

        idToPlayer.put(id, p);
        server.broadcast("INFO:" + id + " joined from terminal");

        // 2.  send that player a one-line snapshot
        pm(id, "BAL:" + p.getMoney()
                + " POS:" + p.getPosition()
                + " OWN:" + p.getOwnedProperties().size() + " props");

        // 3.  if it’s already their turn, prompt them right away
        if (id.equals(controller.getCurrentPlayerName())) {
            pm(id, "YOURTURN");
            server.broadcast("TURN:" + id);
        }

        // 4.  send everyone a fresh snapshot of the entire game
        broadcastFullState();
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

        SwingUtilities.invokeLater(() -> {
            int[] dice = controller.rollDice();
            String outcome = controller.movePlayerAfterDiceRoll(dice);

            // GUI refresh for every open GameWindow
            for (Frame f : Frame.getFrames()) {
                if (f instanceof View.GameWindow) {
                    ((View.GameWindow) f).refreshUIAfterExternalMove();
                }
            }

            server.broadcast(String.format("MOVE:%s:%d+%d",
                    playerId, dice[0], dice[1]));
            server.broadcast("STATE:" + outcome.replace('\n', ' '));
            server.broadcast("TURN:" + controller.getCurrentPlayerName());

            // 4.  broadcast the updated full game state
            broadcastFullState();
        });
    }

    private void pm(String playerId, String msg) {
        server.broadcast("PM:" + playerId + ":" + msg);
    }

    /** Serialize the current GameState and send as FULLSTATE:… */
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
