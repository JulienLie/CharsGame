package player;

import gui.Map;
import gui.OptionsMenu;
import helper.PlanHelper;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Area;
import java.util.List;

public class Chars implements KeyListener, Obstacle {


    private static final long reloadTime = 1500;
    private static final int width = 30, height = 20;
    private static int nbr = 0;

    public final int up, down, right, left, shoot;
    private double x,y;
    private double rota;
    private long lastShoot;
    private int input;
    private Color color;
    private boolean isDead;
    private final int serial;

    public Chars(OptionsMenu.PlayerMove moove){
        this(moove.up, moove.down, moove.right, moove.left, moove.shoot);
        System.out.println("new char(" + moove.toString() + ")");
    }

    public Chars(int up, int down, int right, int left, int shoot){
        this.up = up;
        this.down = down;
        this.right = right;
        this.left = left;
        this.shoot = shoot;
        this.x = 0;
        this.y = 0;
        this.rota = 0;
        this.lastShoot = System.currentTimeMillis();
        this.input = KeyEvent.VK_UNDEFINED;
        color = Color.gray;
        isDead = true;
        this.serial = ++nbr;
    }

    public void spawn(Map.Spawn spawn){
        this.x = spawn.pos.x;
        this.y = spawn.pos.y;
        this.rota = spawn.dir;
        this.color = spawn.color;
        isDead = false;
    }

    @Override
    public Polygon getHitBox(){
        Polygon base = PlanHelper.rotatedRectangle(x, y, height, width, rota);
        double[] first = PlanHelper.rotate(x, y, x, y+2, rota*Math.PI/180.);
        double[] sec = PlanHelper.rotate(x, y, x+height+1, y+2, rota*Math.PI/180.);
        double[] third = PlanHelper.rotate(x, y, x+height+1, y-2, rota*Math.PI/180.);
        double[] fourth = PlanHelper.rotate(x, y, x, y-2, rota*Math.PI/180.);
        Polygon canon = new Polygon(new int[]{(int) first[0], (int) sec[0], (int) third[0], (int) fourth[0]},
                new int[]{(int) first[1], (int) sec[1], (int) third[1], (int) fourth[1]}, 4);
        return PlanHelper.mergePolygons(base, canon);
    }

    public Obstacle doAction(List<Obstacle> obstacles){
        if(isDead) return null;
        double rad = rota*Math.PI/180.;
        double lastX = x;
        double lastY = y;
        double lastRota = rota;
        if(input == up){
            x+= Math.cos(rad);
            y+= Math.sin(rad);
        }
        else if(input == down){
            x-= Math.cos(rad);
            y-= Math.sin(rad);
        }
        else if(input == left){
            rota = (rota+1)%360;
        }
        else if(input == right){
            rota = (rota-1) < 0 ? 359 : rota-1;
        }
        else if(input == shoot){
            long now = System.currentTimeMillis();
            if(lastShoot+reloadTime <= now){
                this.lastShoot = now;
                double[] pos = PlanHelper.rotate(x, y, x+width/2., y, rad);
                return new Bullet((int) Math.round(pos[0]), (int) Math.round(pos[1]), rota, color);
            }
        }
        Area thisA = new Area(this.getHitBox());
        for(Obstacle o : obstacles){

            Area a = new Area(o.getHitBox());
            a.intersect(thisA);
            if(!a.isEmpty() && o!=this) {
                this.x = lastX;
                this.y = lastY;
                this.rota = lastRota;
                return null;
            }
        }
        return null;
    }

    public void paint(Graphics g){
        g.setColor(color);
        g.fillPolygon(this.getHitBox());
    }

    void hit(){
        this.isDead = true;
        this.color = Color.gray;
    }

    public boolean isDead(){
        return isDead;
    }

    Color getColor(){
        return color;
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        int code = keyEvent.getKeyCode();
        if(code == up || code == down || code == left || code == right || code == shoot) {
            this.input = keyEvent.getKeyCode();
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        if(this.input == keyEvent.getKeyCode()) input = KeyEvent.VK_UNDEFINED;
    }

    public String toString(){
        return String.format("char%d(%s;%d;%d;%d;%d;%f)", serial, color, (int)x, (int)y, height, width, rota);
    }
}
