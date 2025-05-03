package Controller;

import java.io.*;
import java.net.Socket;

/**
 * Handles a single TCP client.
 * Only change: constructor now gets the *new* GameManager instance.
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final GameManager gameManager;
    private DataInputStream  in;
    private DataOutputStream out;
    private String playerId = "UNKNOWN";

    public ClientHandler(Socket socket, GameManager gm) {   // signature changed
        this.socket      = socket;
        this.gameManager = gm;
    }

    @Override public void run() {
        try {
            in  = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            // first line must be JOIN:<name>
            playerId = in.readUTF();
            if (!playerId.startsWith("JOIN:"))
                throw new IOException("First message must be JOIN:<name>");

            playerId = playerId.substring(5).trim();
            gameManager.addPlayer(playerId, this);

            String line;
            while ((line = in.readUTF()) != null) {
                gameManager.handleCommand(playerId, line.trim().toUpperCase());
            }
        } catch (IOException e) {
            System.out.println("Client " + playerId + " dropped.");
        } finally {
            cleanup();
        }
    }

    public void send(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();          // <-- add this
        } catch (IOException ignored) {}
    }


    private void cleanup() {
        gameManager.removePlayer(playerId);
        GameServerHolder.INSTANCE.remove(this); // see note below
        try { socket.close(); } catch (IOException ignored) {}
    }

    /* ──────────────────────────────────────────
       We need a way to reach the GameServer from here without a circular ref.
       Easiest quick hack: a tiny holder with a public static reference.
       Put this inner class at bottom of this file or in its own file. */
    private static class GameServerHolder {
        private static GameServer INSTANCE;
        static void set(GameServer srv) { INSTANCE = srv; }
    }
}
