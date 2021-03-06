package es.skastro.gcodepainter.draw.document;

import android.graphics.PointF;

public abstract class PointFUtils {

    public static float distance(PointF a, PointF b) {
        return minus(b, a).length();
    }

    public static double angle(PointF a, PointF b) {
        return Math.atan2(b.y, b.x) - Math.atan2(a.y, a.x);
    }

    /***
     * rotates a vector counterclockwise
     * 
     * @param v
     * @param n
     * @return
     */
    public static PointF rotate(PointF v, double n) {
        float rx = (float) ((v.x * Math.cos(n)) - (v.y * Math.sin(n)));
        float ry = (float) ((v.x * Math.sin(n)) + (v.y * Math.cos(n)));
        return new PointF(rx, ry);
    }

    public static PointF minus(PointF a, PointF b) {
        return new PointF(a.x - b.x, a.y - b.y);
    }

    public static PointF plus(PointF a, PointF b) {
        return new PointF(a.x + b.x, a.y + b.y);
    }

}