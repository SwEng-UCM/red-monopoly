package Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Tiny TCP server that relays console commands into the real Monopoly game.
 * Starts once from Launcher.Main.
 */
public class GameServer {

    public static final int PORT = 6666;

    private final Controller controller;                 // <-- new
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final GameManager gameManager;               // now needs controller

    public GameServer(Controller ctrl) {
        this.controller  = ctrl;
        this.gameManager = new GameManager(this, ctrl);
        // <<< ADD THIS LINE >>>
        ClientHandler.GameServerHolder.set(this);
    }


    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("🟢  Server listening on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, gameManager); // sig changed
                clients.add(handler);
                new Thread(handler, "Client-" + clients.size()).start();
                System.out.println("➕  Client connected  (" + clients.size() + " total)");
            }
        } catch (IOException e) {
            System.err.println("🔥 Server crashed: " + e.getMessage());
        }
    }

    /* === called from GameManager/ClientHandler === */
    public void broadcast(String msg) {
        for (ClientHandler c : clients) c.send(msg);
    }

    public void remove(ClientHandler dead) {
        clients.remove(dead);
        System.out.println("➖  Client disconnected (" + clients.size() + " left)");
    }
}