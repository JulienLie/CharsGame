package multi;

import player.PlayerChars;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

class HostGame extends MultiGame{

    private final ServerSocket server;
    private boolean isRunning;
    private final Map<Socket, PlayerChars> players;
    private boolean haschars;

    HostGame() throws IOException {
        super();
        players = new HashMap<>();
        this.server = new ServerSocket(MultiGame.PORT, 10, InetAddress.getByName("::"));
        isRunning = true;
        haschars = false;
        Thread t = new Thread(() -> {
            while(isRunning){

                try {
                    //On attend une connexion d'un client
                    Socket client = server.accept();

                    //Une fois reçue, on la traite dans un thread séparé
                    System.out.println("Connexion cliente reçue.");
                    Thread t1 = new Thread(() -> handleClient(client));
                    t1.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER){
                    HostGame.this.startGame();
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {}
        });

        t.start();

    }

    @Override
    public void stop() {
        isRunning = false;
    }

    private void startGame(){
        if(!haschars) return;
        this.stop();
        super.run();
    }

    public void addChar(PlayerChars c){
        if(haschars) return;
        super.addChar(c);
        haschars = true;
    }

    private void handleClient(Socket client){
        System.err.println("Connexion recue du client (" + client.toString() + ")");
        BufferedInputStream reader;
        boolean closeConnexion = false;
        while (!client.isClosed()){
            try{
                reader = new BufferedInputStream(client.getInputStream());

                String response = super.recv(reader);

                String[] split = response.split(" ");

                switch (split[0].toUpperCase()){
                    case "JOIN":
                        int up = Integer.parseInt(split[1]);
                        int down = Integer.parseInt(split[2]);
                        int right = Integer.parseInt(split[3]);
                        int left = Integer.parseInt(split[4]);
                        int shoot = Integer.parseInt(split[5]);
                        players.put(client, new PlayerChars(up, down, right, left, shoot));
                        break;
                    case "KEYPRESSED": {
                        players.get(client).keyPressed(parseEvent(split, this));
                        break;
                    }
                    case "KEYRELEASED": {
                        players.get(client).keyReleased(parseEvent(split, this));
                        break;
                    }
                    case "EXIT":
                        players.remove(client);
                        closeConnexion = true;
                        break;
                    default:
                        System.err.println("UNKNOWN Command: " + split[0]);
                        break;
                }

                if(closeConnexion){
                    System.err.println("Connexion fermee avec le client (" + client.toString() + ")");
                }
            } catch (IOException e) {
                System.err.println("Erreur de la connexion avec le client (" + client.toString() + ")");
                e.printStackTrace();
            }
        }
    }

    private static KeyEvent parseEvent(String[] split, Component c){
        int id = Integer.parseInt(split[1]);
        long when = Long.parseLong(split[2]);
        int mod = Integer.parseInt(split[3]);
        int keyCode = Integer.parseInt(split[4]);
        char keyChar = split[5].charAt(0);
        int loc = Integer.parseInt(split[5]);
        //noinspection MagicConstant
        return new KeyEvent(c, id, when, mod, keyCode, keyChar, loc);
    }
}
