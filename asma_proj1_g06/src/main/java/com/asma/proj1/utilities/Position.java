package com.asma.proj1.utilities;

import java.io.Serializable;
import java.util.Objects;

public class Position implements Serializable {
    private final int x;
    private final int y;
    public final static Position deliveryLinePosition = new Position(0, 0);

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Position(long x, long y) {
        this.x = (int) x;
        this.y = (int) y;
    }

    public static int distance(Position p1, Position p2) {
        return Math.abs(p2.getX() - p1.getX() + p2.getY() - p1.getY());
    }

    public static Position moveTowards(Position p1, Position p2) {
        int x = p1.getX(), y = p1.getY();
        if (p2.getX() - p1.getX() != 0) {
            x += (p2.getX() - p1.getX()) / Math.abs(p2.getX() - p1.getX());
        } else if (p2.getY() - p1.getY() != 0) {
            y += (p2.getY() - p1.getY()) / Math.abs(p2.getY() - p1.getY());
        }
        return new Position(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
