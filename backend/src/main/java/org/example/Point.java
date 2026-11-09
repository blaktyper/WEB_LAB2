package org.example;

public class Point {
    private final double x;
    private final double y;
    private final double r;
    private final boolean hit;

    public Point(double x, double y, double r) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = HitChecker.checkHit(x, y, r);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getR() {
        return r;
    }

    public boolean isHit() {
        return hit;
    }


}
