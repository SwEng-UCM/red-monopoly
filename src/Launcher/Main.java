package Launcher;

import Model.MonopolyGame;
import Controller.Controller;
import Controller.GameServer;
import View.MainWindow;

public class Main {

    public static void main(String[] args) throws Exception {

        // 1. normal MVC startup
        Controller controller = new Controller(MonopolyGame.getInstance());
        new MainWindow(controller);

        // 2. fire up the socket server in the background
        Thread netThread = new Thread(() -> new GameServer(controller).start(), "GameServer");
        netThread.setDaemon(true);          // kills itself when GUI exits
        netThread.start();
    }
}
