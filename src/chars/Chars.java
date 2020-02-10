package chars;

import gui.Map;
import helper.PlanHelper;
import player.Obstacle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public abstract class Chars extends Thread implements Obstacle{

    private static final long reloadTime = 1500;
    private static final int width = 30, height = 20;
    private static int nbr = 0;
    private static final BufferedImage charImg;

    static {
        BufferedImage charImgTemp;
        try {
            charImgTemp = ImageIO.read(new File("assets/Chars.png"));
        } catch (IOException e) {
            charImgTemp = null;
            e.printStackTrace();
        }
        charImg = charImgTemp;
    }

    public enum Action{
        Forward,
        Backward,
        TurnLeft,
        TurnRight,
        Shoot,
        None
    }


    private double x,y;
    private double rota; // En degré
    private long lastShoot;
    private Color color;
    private boolean isDead;
    private final int serial;
    private BufferedImage image;
    protected List<Obstacle> obstacles;
    private List<CharsListener> listeners;

    public Chars(){
        this.x = 0;
        this.y = 0;
        this.rota = 0;
        this.lastShoot = System.currentTimeMillis();
        color = Color.gray;
        isDead = true;
        this.serial = ++nbr;
        if(charImg != null) image = PlanHelper.changeColor(charImg, color);
        this.listeners = new LinkedList<>();
    }

    @Override
    public void run() {
        long minTime = 16;
        while (!isDead){
            long before = System.currentTimeMillis();
            Action a = nextAction();
            switch (a){
                case Forward:
                    forward();
                    break;
                case Backward:
                    backward();
                    break;
                case TurnLeft:
                    turnLeft();
                    break;
                case TurnRight:
                    turnRight();
                    break;
                case Shoot:
                    shoot();
                    break;
                case None:
                    break;
            }
            long after = System.currentTimeMillis();
            long time = after - before;
            long wait = minTime - time;
            if(wait > 0) {
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    this.isDead = true;
                }
            }
        }
    }

    protected abstract Action nextAction();

    protected abstract void blocked();

    public final void spawn(Map.Spawn spawn, List<Obstacle> obstacles){
        this.x = spawn.pos.x;
        this.y = spawn.pos.y;
        this.rota = spawn.dir;
        this.color = spawn.color;
        image = PlanHelper.changeColor(charImg, color);
        isDead = false;
        this.obstacles = obstacles;
    }

    private void forward(){
        double rad = Math.toRadians(rota);
        move(x+Math.cos(rad), y+Math.sin(rad), rota);
    }

    private void backward(){
        double rad = Math.toRadians(rota);
        move(x-Math.cos(rad), y-Math.sin(rad), rota);
    }

    private void turnLeft(){
        move(x, y, (rota-1) < 0 ? 359 : rota-1);
    }

    private void turnRight(){
        move(x, y, (rota+1)%360);
    }

    private void move(double x, double y, double rota){
        double lastX = this.x;
        double lastY = this.y;
        double lastRota = this.rota;
        this.x = x;
        this.y = y;
        this.rota = rota;
        Area thisA = new Area(this.getHitBox());
        for(Obstacle o : obstacles){
            Area a = new Area(o.getHitBox());
            a.intersect(thisA);
            if(!a.isEmpty() && o!=this) {
                this.x = lastX;
                this.y = lastY;
                this.rota = lastRota;
                blocked();
                return;
            }
        }
    }

    public final double getX(){
        return x;
    }

    public final double getY(){
        return y;
    }

    public final double getRota(){
        return rota;
    }

    private void shoot(){
        long now = System.currentTimeMillis();
        if(lastShoot+reloadTime <= now){
            this.lastShoot = now;
            double[] pos = PlanHelper.rotate(x, y, x+width/2., y, Math.toRadians(rota));
            Bullet b = new Bullet((int) Math.round(pos[0]), (int) Math.round(pos[1]), rota, color);
            for(CharsListener l : listeners)
                l.shoot(this, b);
        }
    }

    public void paint(Graphics g){
        if(charImg == null) {
            g.setColor(color);
            g.fillPolygon(this.getHitBox());
        }
        else{
            BufferedImage image = PlanHelper.rotateImageByDegrees(this.image, rota);
            g.drawImage(image, (int) (x-(image.getWidth()/2)), (int) (y-(image.getHeight()/2)), null);
        }
    }

    final void hit(){
        this.isDead = true;
        this.color = Color.gray;
        this.image = PlanHelper.changeColor(charImg, color);
    }

    public final boolean isDead(){
        return isDead;
    }

    public final Color getColor(){
        return color;
    }

    public final void addListener(CharsListener listener){
        listeners.add(listener);
    }

    @Override
    public final Polygon getHitBox(){
        return PlanHelper.rotatedRectangle(x, y, height, width, rota);
    }

    public final String toString(){
        return String.format("char%d(%s;%d;%d;%d;%d;%f)", serial, color, (int)x, (int)y, height, width, rota);
    }

}
