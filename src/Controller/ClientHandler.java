package Controller;

import java.io.*;
import java.net.Socket;

/** Handles one TCP client connection. */
public class ClientHandler implements Runnable {

    private final Socket        socket;
    private final GameManager   gameManager;

    private DataInputStream  in;
    private DataOutputStream out;
    private String           playerId = "UNKNOWN";

    public ClientHandler(Socket socket, GameManager gm) {
        this.socket      = socket;
        this.gameManager = gm;
    }

    @Override public void run() {
        try {
            in  = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            /* first line must be JOIN:<name> */
            playerId = in.readUTF();
            if (!playerId.startsWith("JOIN:"))
                throw new IOException("First message must be JOIN:<name>");
            playerId = playerId.substring(5).trim();

            gameManager.addPlayer(playerId, this);

            String line;
            while ((line = in.readUTF()) != null)
                gameManager.handleCommand(playerId, line.trim().toUpperCase());

        } catch (IOException ignored) {
            System.out.println("Client " + playerId + " dropped.");
        } finally {
            cleanup();
        }
    }

    /* send one UTF line */
    public void send(String msg) {
        try { out.writeUTF(msg); out.flush(); } catch (IOException ignored) {}
    }

    /* tidy up */
    private void cleanup() {
        gameManager.removePlayer(playerId);          // update model
        gameManager.getServer().remove(this);        // remove from client list
        try { socket.close(); } catch (IOException ignored) {}
    }
}
