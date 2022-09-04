package com.asma.proj1.utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Package implements Serializable {
    private final List<Product> products;
    private final int deadline;

    public Package(List<Product> products, int deadline) {
        this.products = products;
        this.deadline = deadline;
    }

    public List<Product> getProducts() {
        return products;
    }

    public int getDeadline() {
        return deadline;
    }

    public static Package getRandPackage() {
        Random r = new Random();
        // Num products [1, 5], weight, x, y [1,10]
        List<Product> products = new ArrayList<>();
        String uniqueName;
        int weight;
        Position position;

        int num_products = r.nextInt(4) + 1;
        int deadline = r.nextInt(30) + 20;
        for (int i = 0; i < num_products; i++) {
            uniqueName = UUID.randomUUID().toString();
            weight = r.nextInt(9) + 1;
            position = new Position(r.nextInt(9) + 1, r.nextInt(9) + 1);
            products.add(new Product(weight, uniqueName, position));
        }

        // deadline [20,50]
        return new Package(products, deadline);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("\n####\nPackage:\n");
        for (Product product : products) {
            builder.append("- ").append(product.toString()).append("\n");
        }
        builder.append("####\n\n");

        return builder.toString();
    }
}
