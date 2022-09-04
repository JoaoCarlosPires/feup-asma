package com.asma.proj1.utilities;

import java.io.Serializable;

public class Product implements Serializable {
    private final int weight;
    private final String name;
    private final Position location;

    public Product(int weight, String name, Position location) {
        this.weight = weight;
        this.name = name;
        this.location = location;
    }
    public Product(long weight, String name, Position location) {
        this((int) weight, name, location);
    }

    public int getWeight() {
        return weight;
    }

    public String getName() {
        return name;
    }

    public Position getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Product{" +
                "weight=" + weight +
                ", name='" + name + '\'' +
                ", location=" + location +
                '}';
    }
}
