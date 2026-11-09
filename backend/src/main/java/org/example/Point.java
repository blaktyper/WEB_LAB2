package org.example;

public class Point {
    private final double x;
    private final double y;
    private final double r;
    private final boolean hit;
    private final long timestamp;
    private final double executionTime;

    public Point(double x, double y, double r, long timestamp, double executionTime) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = HitChecker.checkHit(x, y, r);
        this.timestamp = timestamp;
        this.executionTime = executionTime;
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

    public long getTimestamp() {
        return timestamp;
    }

    public double getExecutionTime() {
        return executionTime;
    }
}