package Controller;

import java.io.*;
import java.net.Socket;

public class GameClient {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java GameClient <server-ip> <playerName>");
            return;
        }
        String host = args[0];
        String name = args[1];

        try (Socket socket = new Socket(host, GameServer.PORT);
             DataInputStream  in  = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             BufferedReader   kb  = new BufferedReader(new InputStreamReader(System.in))) {

            /* 1️⃣  identify ourselves */
            out.writeUTF("JOIN:" + name);
            out.flush();                         //  <<<  NEW

            /* 2️⃣  listener thread */
            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readUTF()) != null) {
                        System.out.println("<< " + msg);
                    }
                } catch (IOException e) {
                    System.out.println("📴 Connection closed"); System.exit(0);
                }
            }).start();

            /* 3️⃣  keyboard loop */
            String line;
            while ((line = kb.readLine()) != null) {
                if (line.equalsIgnoreCase("quit")) break;
                out.writeUTF(line.trim().toUpperCase());
                out.flush();                     //  <<<  NEW
            }
        }
    }
}
