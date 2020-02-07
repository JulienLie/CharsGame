package helper;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;

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

    public static BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {

        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2., (newHeight - h) / 2.);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    public static BufferedImage changeColor(BufferedImage image, Color color){
        BufferedImage reColor = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for(int i = 0; i < image.getWidth(); i++){
            for(int j = 0; j < image.getHeight(); j++){
                if((image.getRGB(i, j)) == 0xff00ff00){
                    reColor.setRGB(i, j, color.getRGB());
                }
                else{
                    reColor.setRGB(i, j, image.getRGB(i, j));
                }
            }
        }
        return reColor;
    }
}
