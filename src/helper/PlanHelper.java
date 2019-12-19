package helper;

import player.Chars;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;

public class PlanHelper {

    public static double[] rotate(double cx, double cy, double x, double y, double deg){
        double tempX = x - cx;
        double tempY = y - cy;

        double rotatedX = tempX*Math.cos(deg) - tempY*Math.sin(deg);
        double rotatedY = tempX*Math.sin(deg) + tempY*Math.cos(deg);

        double finalX = rotatedX + cx;
        double finalY = rotatedY + cy;
        return new double[]{finalX, finalY};
    }

    public static Polygon rotatedRectangle(double x, double y, int height, int width, double deg){
        Polygon hit = new Polygon();
        deg = deg*Math.PI/180.;
        double x1 = x+(width/2.);
        double y1 = y-(height/2.);
        double[] nP = rotate(x, y, x1, y1, deg);
        hit.addPoint((int) nP[0], (int) nP[1]);
        double y2 = y+(height/2.);
        nP = rotate(x, y, x1, y2, deg);
        hit.addPoint((int) nP[0], (int) nP[1]);
        double x3 = x-(width/2.);
        nP = rotate(x, y, x3, y2, deg);
        hit.addPoint((int) nP[0], (int) nP[1]);
        nP = rotate(x, y, x3, y1, deg);
        hit.addPoint((int) nP[0], (int) nP[1]);
        return hit;
    }

    public static Polygon mergePolygons(Polygon... polygons){
        Area area = new Area();
        for (Polygon p : polygons){
            Area add = new Area(p);
            area.add(add);
        }
        PathIterator iterator = area.getPathIterator(null);
        float[] floats = new float[6];
        Polygon polygon = new Polygon();
        while (!iterator.isDone()) {
            int type = iterator.currentSegment(floats);
            int x = (int) floats[0];
            int y = (int) floats[1];
            if(type != PathIterator.SEG_CLOSE) {
                polygon.addPoint(x, y);
            }
            iterator.next();
        }
        return polygon;
    }
}
