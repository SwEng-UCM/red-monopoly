// src/Controller/NetworkClient.java
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Manages a TCP connection to the Monopoly server,
 * parses incoming messages, and notifies a listener.
 */
public class NetworkClient {
    public interface GameMessageListener {
        void onBalance(String playerId, int money, int position, int props);
        void onMove(String playerId, int die1, int die2);
        void onState(String outcome);
        void onTurn(String currentPlayerId);
        void onFullState(GameState state);
        void onInfo(String message);
        void onError(String playerId, String errorMsg);
    }

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Thread readerThread;
    private GameMessageListener listener;

    // Gson setup (must match server adapters!)
    private final RuntimeTypeAdapterFactory<Tile> tileAdapterFactory =
            RuntimeTypeAdapterFactory.of(Tile.class, "tileType")
                    .registerSubtype(GoTile.class, "GoTile")
                    .registerSubtype(PropertyTile.class, "PropertyTile")
                    .registerSubtype(ChanceTile.class, "ChanceTile")
                    .registerSubtype(JailTile.class, "JailTile")
                    .registerSubtype(GoToJailTile.class, "GoToJailTile")
                    .registerSubtype(TaxTile.class, "TaxTile")
                    .registerSubtype(CommunityChestTile.class, "CommunityChestTile")
                    .registerSubtype(FreeParkingTile.class, "FreeParkingTile")
                    .registerSubtype(RailroadTile.class, "RailroadTile")
                    .registerSubtype(UtilityTile.class, "UtilityTile");

    private final RuntimeTypeAdapterFactory<Player> playerAdapterFactory =
            RuntimeTypeAdapterFactory.of(Player.class, "type")
                    .registerSubtype(Player.class, "Human")
                    .registerSubtype(AIPlayer.class, "AI");

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(tileAdapterFactory)
            .registerTypeAdapterFactory(playerAdapterFactory)
            .create();

    /**
     * @param host       server address
     * @param port       server port (e.g. GameServer.PORT)
     * @param playerName must exactly match one of the GUI‐created names
     */
    public NetworkClient(String host, int port, String playerName) throws IOException {
        socket = new Socket(host, port);
        in     = new DataInputStream(socket.getInputStream());
        out    = new DataOutputStream(socket.getOutputStream());

        // IDENTIFY
        out.writeUTF("JOIN:" + playerName);
        out.flush();

        // prepare reader thread but do NOT start it yet
        readerThread = new Thread(this::readLoop, "Monopoly-NetReader");
        readerThread.setDaemon(true);
    }

    /** Set this before doing anything else so you get callbacks. */
    public void setListener(GameMessageListener listener) {
        this.listener = listener;
        // only now start reading incoming server messages
        readerThread.start();
    }

    /** Send a ROLL command to the server. */
    public void roll() throws IOException {
        out.writeUTF("ROLL");
        out.flush();
    }

    /** Close connection. */
    public void close() throws IOException {
        socket.close();
    }

    /** Continuously read lines and dispatch them. */
    private void readLoop() {
        try {
            String line;
            while ((line = in.readUTF()) != null) {
                dispatchLine(line);
            }
        } catch (IOException e) {
            if (listener != null) listener.onInfo("Disconnected from server.");
        }
    }

    private void dispatchLine(String line) {
        try {
            if (line.startsWith("PM:")) {
                // format: PM:<playerId>:<msg>
                String[] parts = line.split(":", 3);
                String pid = parts[1];
                String msg = parts[2];
                if (msg.startsWith("BAL:")) {
                    String[] toks = msg.split("\\s+");
                    int money = Integer.parseInt(toks[0].substring(4));
                    int pos   = Integer.parseInt(toks[1].substring(4));
                    int own   = Integer.parseInt(toks[2].substring(4));
                    if (listener != null) listener.onBalance(pid, money, pos, own);
                } else if (msg.startsWith("ERR:")) {
                    if (listener != null) listener.onError(pid, msg.substring(4));
                } else {
                    if (listener != null) listener.onInfo("PM from " + pid + ": " + msg);
                }
            }
            else if (line.startsWith("MOVE:")) {
                String[] p = line.split(":", 3);
                String pid = p[1];
                String[] dice = p[2].split("\\+");
                int d1 = Integer.parseInt(dice[0]);
                int d2 = Integer.parseInt(dice[1]);
                if (listener != null) listener.onMove(pid, d1, d2);
            }
            else if (line.startsWith("STATE:")) {
                String state = line.substring("STATE:".length());
                if (listener != null) listener.onState(state);
            }
            else if (line.startsWith("TURN:")) {
                String pid = line.substring("TURN:".length());
                if (listener != null) listener.onTurn(pid);
            }
            else if (line.startsWith("FULLSTATE:")) {
                String json = line.substring("FULLSTATE:".length());
                GameState state = gson.fromJson(json, GameState.class);
                if (listener != null) listener.onFullState(state);
            }
            else if (line.startsWith("INFO:")) {
                String info = line.substring("INFO:".length());
                if (listener != null) listener.onInfo(info);
            }
        } catch (Exception ex) {
            if (listener != null) listener.onInfo("Failed to parse line: " + line);
        }
    }
}
