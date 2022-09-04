package com.asma.proj1.robot;

import com.asma.proj1.utilities.Arguments;

import java.io.Serializable;

public class RobotArguments implements Serializable, Arguments {
    private final String name;
    private final int capacity;

    public RobotArguments(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public RobotArguments(Object[] arguments) {
        this.name = (String) arguments[0];
        this.capacity = (int) arguments[1];
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public Object[] getObjectArray() {
        return new Object[] {
            name, capacity
        };
    }
}
