package multi;

import player.Chars;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

class JoinedGame extends MultiGame {

    private final Socket server;
    private boolean hasChar;

    public JoinedGame(Socket server){
        this.server = server;
        this.hasChar = false;
    }

    @Override
    public void addChar(Chars c) {
        if(hasChar) return;
        try {
            PrintWriter writer = new PrintWriter(server.getOutputStream());
            String msg = String.format("JOIN %d %d %d %d %d", c.up, c.down, c.right, c.left, c.shoot);
            super.send(writer, msg);
            writer.close();
            this.hasChar = true;
            this.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {}

                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    try {
                        PrintWriter writer1 = new PrintWriter(server.getOutputStream());

                        String toSend = String.format("KEYPRESSED %d %d %d %c %d", keyEvent.getID(), keyEvent.getWhen(),
                                keyEvent.getModifiersEx(), keyEvent.getKeyChar(), keyEvent.getKeyLocation());
                        JoinedGame.super.send(writer1, toSend);
                        writer1.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void keyReleased(KeyEvent keyEvent) {
                    try {
                        PrintWriter writer1 = new PrintWriter(server.getOutputStream());

                        String toSend = String.format("KEYRELEASED %d %d %d %c %d", keyEvent.getID(), keyEvent.getWhen(),
                                keyEvent.getModifiersEx(), keyEvent.getKeyChar(), keyEvent.getKeyLocation());
                        JoinedGame.super.send(writer1, toSend);
                        writer1.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            PrintWriter writer = new PrintWriter(server.getOutputStream());
            super.send(writer, "EXIT");
            writer.close();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
