package gui;

import player.Chars;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

public class Game extends JFrame implements Runnable{

    protected JPanel panel;
    private boolean gameRunning;
    private double lastFpsTime;
    private double fps;

    public Game(){
        super("Chars");
        this.setSize(600, 600);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.panel = new Menu(this);
        this.add(panel);
        this.gameRunning = false;
        this.setFocusable(true);
        this.requestFocusInWindow();
        this.setResizable(false);
    }

    public void addChar(Chars c){
        if(panel instanceof  GamePanel) {
            if (((GamePanel) panel).addChar(c)) {
                System.out.println("new char");
                this.addKeyListener(c);
            }
        }
    }

    private void restart(){
        if(panel instanceof GamePanel) {
            GamePanel panel = (GamePanel) this.panel;
            List<Chars> chars = panel.getChars();
            this.remove(panel);
            this.panel = new GamePanel(panel.getMap());
            this.add(panel);
            for (Chars c : chars) panel.addChar(c);
            Thread t = new Thread(this);
            t.start();
        }
    }

    @Override
    public void run(){
        if(gameRunning) return;
        System.out.println("run");
        gameRunning = true;
        gameLoop();
    }

    private void gameLoop()
    {
        long lastLoopTime = System.nanoTime();
        final int TARGET_FPS = 60;
        final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

        // keep looping round til the game ends
        while (gameRunning)
        {
            // work out how long its been since the last update, this
            // will be used to calculate how far the entities should
            // move this loop
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            lastLoopTime = now;
            double delta = updateLength / ((double)OPTIMAL_TIME);

            // update the frame counter
            lastFpsTime += updateLength;
            fps++;

            // update our FPS counter if a second has passed since
            // we last recorded
            if (lastFpsTime >= 1000000000)
            {
                //System.out.println("(FPS: "+fps+")");
                lastFpsTime = 0;
                fps = 0;
            }

            // update the game logic
            boolean cont = doGameUpdates(delta);
            if(!cont) gameRunning = false;

            // draw everyting
            render();

            // we want each frame to take 10 milliseconds, to do this
            // we've recorded when we started the frame. We add 10 milliseconds
            // to this and then factor in the current time to give
            // us our final value to wait for
            // remember this is in ms, whereas our lastLoopTime etc. vars are in ns.
            long time = (lastLoopTime-System.nanoTime() + OPTIMAL_TIME)/1000000;
            try{
                if(time > 0) Thread.sleep(time);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Object[] options = {"Restart", "Quit", "Change map"};

        int n = JOptionPane.showOptionDialog(this, "Game Over\n Restart?",
                "Game Over",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        switch (n){
            case 0:
                this.restart();
                break;
            case 1:
            case -1:
                for(Chars c : ((GamePanel) panel).getChars()){
                    this.removeKeyListener(c);
                }
                this.changeMenu(new Menu(this));
                break;
            case 2:
                for(Chars c : ((GamePanel) panel).getChars()){
                    this.removeKeyListener(c);
                }
                changeMenu(new Map.MapMenu(this));
                break;
            default:
                System.err.println(n);
                System.exit(1);
                break;
        }
    }

    private boolean doGameUpdates(double delta)
    {
        if(panel instanceof GamePanel) return ((GamePanel) panel).doGameUpdate(delta);
        else return false;
    }

    private void render(){
        panel.paintImmediately(0, 0, this.getWidth(), this.getHeight());
        this.revalidate();
    }

    void changeMenu(JPanel newMenu){
        this.remove(panel);
        this.panel = newMenu;
        this.add(newMenu);
        this.revalidate();
    }
}
