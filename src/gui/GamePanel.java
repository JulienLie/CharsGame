package gui;

import chars.Bullet;
import chars.Chars;
import chars.CharsListener;
import player.Obstacle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GamePanel extends JPanel implements CharsListener {

    private final List<Chars> chars;
    private final ConcurrentLinkedQueue<Bullet> bullets;
    private final Map map;
    private final List<Obstacle> obstacles;

    GamePanel(Map map){
        this.chars = new ArrayList<>(2);
        this.bullets = new ConcurrentLinkedQueue<>();
        this.map = map;
        this.obstacles = new ArrayList<>();
        this.obstacles.addAll(map.getObstacles());
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);

        for(Obstacle c :obstacles){
            c.paint(g);
        }

        for(Bullet b : bullets){
            b.paint(g);
        }
    }

    public void start(){
        for(Chars c : chars){
            c.start();
        }
    }

    boolean doGameUpdate(double delta){
        int nbrAlive = 0;
        for(Chars c : chars){
            nbrAlive += c.isDead() ? 0 : 1;
        }

        bullets.removeIf(bullet -> bullet.doAction(map.getObstacles(), chars));

        return nbrAlive > 1;
    }

    public List<Chars> getChars(){
        return chars;
    }

    public boolean addChar(Chars c){
        if(map.getSpawns().size() <= chars.size()) return false;
        c.spawn(map.getSpawns().get(chars.size()), obstacles);
        this.chars.add(c);
        this.obstacles.add(c);
        this.repaint();
        c.addListener(this);
        return true;
    }

    public Map getMap(){
        return map;
    }

    @Override
    public void shoot(Chars source, Bullet bullet) {
        this.bullets.add(bullet);
    }
}
