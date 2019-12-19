package multi;

import gui.Game;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class MultiGame extends Game {

    protected static final int PORT = 12345;

    public static MultiGame host(){
        MultiGame game = null;
        try {
            game = new HostGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return game;
    }

    public static MultiGame join(String ip){
        Socket s = null;
        try {
            s = new Socket(ip, PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JoinedGame(s);
    }

    protected MultiGame(){
        super();
    }

    public abstract void stop();

    String recv(BufferedInputStream reader) throws IOException {
        String rep;
        int stream;
        byte[] b = new byte[4096];
        stream = reader.read(b);
        rep = new String(b, 0, stream);
        return rep;
    }

    void send(PrintWriter writer, String msg){
        writer.write(msg);
        writer.flush();
    }
}
