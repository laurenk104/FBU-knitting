package com.example.knitting;

public class Stitch implements Comparable<Stitch> {

    private boolean knit;
    private int x;
    private int y;

    public Stitch(String name, double x, double y) {
        knit = name.equals("knit");
        this.x = (int) (x * 100);
        this.y = (int) (y * 100);
    }

    public boolean isKnit() {
        return knit;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int compareTo(Stitch other) {
        int yDifference = this.y - other.y;
        if (Math.abs(yDifference) < 3) {
            return this.x - other.x;
        }
        return yDifference;
    }

    public String toString() {
        return "value: " + knit + ", (" + x + ", " + y + ")";
    }
}
