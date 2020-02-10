package bot;

import chars.Bullet;
import chars.Chars;
import helper.PlanHelper;
import player.Obstacle;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.util.List;
import java.util.*;
import java.util.function.BiFunction;

import static chars.Chars.Action.*;

public class PathFindingBot extends ShootBot {

    private Map m = null;
    private Stack<Map.Node> path = null;
    private int nbrIter = 0;
    private Map.Node toGo;
    private boolean blocked;
    private Chars prevClosest;

    @Override
    protected Action nextAction() {
        if(blocked){
            blocked = false;
            return Backward;
        }
        if(m == null) {
            try {
                this.m = new Map(obstacles);
            } catch (Exception e) {
                e.printStackTrace();
                return None;
            }
        }
        Chars closest = getClosest();
        if(closest == null){
            //System.out.println("No Closest");
            return None;
        }
        if(prevClosest == null){
            prevClosest = closest;
        }
        if(prevClosest != closest){
            try {
                m = new Map(obstacles);
            } catch (Exception e) {
                e.printStackTrace();
            }
            nbrIter = 0;
            prevClosest = closest;
        }
        if(canShoot(this.getX(), this.getY(), closest.getX(), closest.getY())){
            return super.nextAction();
        }
        int recalcNbr = 2 * 60;
        if(path == null || nbrIter == recalcNbr || nbrIter == 0 || path.isEmpty()){
            //System.out.println("Recalc path");
            path = pathfinding(closest);
            if(path == null) return super.nextAction();
            path.pop();
        }
        nbrIter++;
        if(path.size() < 1){
            return super.nextAction();
        }
        toGo = path.peek();
        if(Math.abs(this.getX() - toGo.x) < 5 && Math.abs(this.getY() - toGo.y) < 5){
            path.pop();
            if(path.isEmpty()) return super.nextAction();
            toGo = path.peek();
        }
        double aim = aim(toGo.x, toGo.y, 0.5);
        if(aim < 0) return TurnRight;
        else if (aim > 0) return TurnLeft;
        else return  Forward;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if(m != null){
            for(Map.Node n : m.nodes){
                g.setColor(Color.red);
                g.drawRect(n.x-n.width/2, n.y-n.height/2, n.width, n.height);
                for(Map.Node adj : n.adjacent.keySet()){
                    g.setColor(Color.green);
                    g.drawLine(n.x, n.y, adj.x, adj.y);
                }
            }
        }
        g.setColor(this.getColor());
        if(toGo != null){
            g.drawLine((int)this.getX(), (int)this.getY(), toGo.x, toGo.y);
        }
    }

    private static double distance(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private boolean canGo(int x1, int y1, int x2, int y2){
        double size = Math.max(this.getHitBox().getBounds2D().getHeight(), this.getHitBox().getBounds2D().getWidth());
        return isObstructed(x1, y1, x2, y2, size);
    }

    private boolean isObstructed(double x1, double y1, double x2, double y2, double height){
        double length = Math.round(distance(x1, y1, x2, y2));
        double x = (x1+x2)/2.;
        double y = (y1+y2)/2.;
        double angle = Math.atan2(y2-y1, x2-x1) *180/Math.PI;
        Polygon p = PlanHelper.rotatedRectangle(x, y, (int) height, (int) length, angle);
        Area base = new Area(p);
        for(Obstacle o : obstacles){
            if(o instanceof Chars && !((Chars) o).isDead()) continue;
            Area hitbox = new Area(o.getHitBox());
            hitbox.intersect(base);
            if(!hitbox.isEmpty()){
                return false;
            }
        }
        return true;
    }

    private boolean canShoot(double x1, double y1, double x2, double y2){
        return isObstructed(x1, y1, x2, y2, Bullet.width);
    }

    @Override
    protected void blocked(){
        try {
            this.m = new Map(obstacles);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.nbrIter = 0;
        blocked = true;
    }

    private Stack<Map.Node> pathfinding(Chars target){
        Map.Node curr = m.getNode(this);
        Map.Node goal = m.getNode(target);
        return A_Star(curr, goal, this::h);
    }

    private double h(Map.Node current, Map.Node goal){
        return Math.sqrt(Math.pow(current.x - goal.x, 2) + Math.pow(current.y - goal.y, 2));
    }

    private Stack<Map.Node> A_Star(Map.Node start, Map.Node goal, BiFunction<Map.Node, Map.Node, Double> h){
        // The set of discovered nodes that may need to be (re-)expanded.
        // Initially, only the start node is known.
        Set<Map.Node> openSet = new LinkedHashSet<>();
        openSet.add(start);

        // For node n, cameFrom[n] is the node immediately preceding it on the cheapest path from start to n currently known.
        java.util.Map<Map.Node, Map.Node> cameFrom = new LinkedHashMap<>();

        // For node n, gScore[n] is the cost of the cheapest path from start to n currently known.
        java.util.Map<Map.Node, Double> gScore = new LinkedHashMap<>();

        // For node n, fScore[n] := gScore[n] + h(n).
        java.util.Map<Map.Node, Double> fScore = new LinkedHashMap<>();

        for(Map.Node n : this.m.nodes){
            gScore.put(n, Double.MAX_VALUE);
            fScore.put(n, Double.MAX_VALUE);
        }
        gScore.put(start, 0D);
        fScore.put(start, h.apply(start, goal));

        while (!openSet.isEmpty()){
            Map.Node current = null;
            double minScore = Integer.MAX_VALUE;
            for(Map.Node n : openSet){
                double score = fScore.get(n);
                if(score < minScore){
                    minScore = score;
                    current = n;
                }
            }
            if(current == null){
                System.out.println("Erreur fScore");
                return null;
            }

            if(current == goal) {
                return reconstruct_path(cameFrom, current);
            }

            openSet.remove(current);
            for(Map.Node neighbor : current.adjacent.keySet()){
                double tentative_gScore = gScore.get(current) + neighbor.adjacent.get(current);
                if(tentative_gScore < gScore.get(neighbor)){
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentative_gScore);
                    fScore.put(neighbor, gScore.get(neighbor) + h.apply(neighbor, goal));
                    openSet.add(neighbor);
                }
            }
        }
        System.out.println("No path found");
        return null;
    }

    private Stack<Map.Node> reconstruct_path(java.util.Map<Map.Node, Map.Node> cameFrom, Map.Node current){
        Stack<Map.Node> total_path = new Stack<>();
        total_path.push(current);
        while(cameFrom.containsKey(current)){
            current = cameFrom.get(current);
            total_path.push(current);
        }
        return total_path;
    }

    private class Map{

        class Node{
            final int x;
            final int y;
            final int height;
            final int width;
            final java.util.Map<Node, Double> adjacent;

            Node(int x, int y, int height, int width){
                this.x = x;
                this.y = y;
                this.height = height;
                this.width = width;
                this.adjacent = new LinkedHashMap<>();
            }

            @Override
            public String toString() {
                return "Node{" +
                        "x=" + x +
                        ", y=" + y +
                        ", height=" + height +
                        ", width=" + width +
                        '}';
            }
        }

        final List<Node> nodes;
        final List<Obstacle> obstacles;

        public Map(List<Obstacle> obstacles) throws Exception {
            //System.out.println("Calculating Map");
            nodes = new LinkedList<>();
            this.obstacles = obstacles;
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;
            for(Obstacle o : obstacles){
                Rectangle p = o.getHitBox().getBounds();
                minX = Math.min(minX, p.x);
                minY = Math.min(minY, p.y);
                maxX = Math.max(maxX, p.x+p.width);
                maxY = Math.max(maxY, p.y+p.height);
            }
            int width = maxX - minX;
            int heigth = maxY - minY;
            if(width <= 0 || heigth <= 0){
                throw new Exception();
            }


            int size = 30;
            int widthSize = width/size;
            int heightSize = heigth/size;
            for(int i = 0; i < width; i+=widthSize){
                for(int j = 0; j < heigth; j+=heightSize){
                    nodes.add(new Node(i + widthSize / 2, j + heightSize / 2, widthSize, heightSize));
                }
            }
            for(int i = 0; i < nodes.size(); i++) {
                Node n1 = nodes.get(i);
                int pos = Collections.binarySearch(nodes, new Node(n1.x + n1.width, n1.y, 0, 0), (node, t1) -> {
                    int comp = Integer.compare(node.x, t1.x);
                    if (comp == 0) return Integer.compare(node.y, t1.y);
                    else return comp;
                });
                double dist;
                if (pos > 0){
                    Node n2 = nodes.get(pos);
                    if(canGo(n1.x, n1.y, n2.x, n2.y)) {
                        dist = PathFindingBot.distance(n1.x, n1.y, n2.x, n2.y);
                        n1.adjacent.put(n2, dist);
                        n2.adjacent.put(n1, dist);
                    }
                }

                int pos2 = Collections.binarySearch(nodes, new Node(n1.x, n1.y + n1.height, 0, 0), (node, t1) -> {
                    int comp = Integer.compare(node.x, t1.x);
                    if(comp == 0) return Integer.compare(node.y, t1.y);
                    else return comp;
                });
                if(pos2 > 0) {
                    Node n3 = nodes.get(pos2);
                    if(canGo(n1.x, n1.y, n3.x, n3.y)) {
                        dist = PathFindingBot.distance(n1.x, n1.y, n3.x, n3.y);
                        n1.adjacent.put(n3, dist);
                        n3.adjacent.put(n1, dist);
                    }
                }

                int pos3 = Collections.binarySearch(nodes, new Node(n1.x + n1.width, n1.y + n1.height, 0, 0), (node, t1) -> {
                    int comp = Integer.compare(node.x, t1.x);
                    if(comp == 0) return Integer.compare(node.y, t1.y);
                    else return comp;
                });
                if(pos3 > 0) {
                    Node n4 = nodes.get(pos3);
                    if(canGo(n1.x, n1.y, n4.x, n4.y)) {
                        dist = PathFindingBot.distance(n1.x, n1.y, n4.x, n4.y);
                        n1.adjacent.put(n4, dist);
                        n4.adjacent.put(n1, dist);
                    }
                }
            }
        }

        Node getNode(Chars c){
            for(Node n : nodes){
                Rectangle r = new Rectangle(n.x-(n.width/2), n.y-(n.height/2), n.width, n.height);
                if(r.contains(c.getX(), c.getY())) return n;
            }
            return null;
        }
    }
}
