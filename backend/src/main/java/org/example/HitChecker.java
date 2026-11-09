package org.example;

public class HitChecker {
    public static boolean checkHit(double x, double y, double r){
        if (x >= 0 && y >= 0) {
            return x <= r && y <= r / 2;
        }

        if (x <= 0 && y >= 0) {
            return false;
        }

        if (x <= 0 && y <= 0) {
            return x * x + y * y <= r * r;
        }

        if (x >= 0 && y <= 0) {
            return 2 * x - y <= r;
        }

        return false;
    }
}
