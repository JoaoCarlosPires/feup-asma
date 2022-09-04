package com.asma.proj1.utilities;

import java.io.Serializable;

public class RobotResponse implements Serializable {
    private final int freeCapacity;
    private final Position position;

    public RobotResponse(int freeCapacity, Position position) {
        this.freeCapacity = freeCapacity;
        this.position = position;
    }

    public int getFreeCapacity() {
        return freeCapacity;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "RobotResponse{" +
                "freeCapacity=" + freeCapacity +
                ", position=" + position +
                '}';
    }
}
