package com.asma.proj1.utilities;

import java.io.Serializable;

public class ProductProposal implements Serializable {
    private final Product product;
    private int deadline;

    public ProductProposal(Product product, int deadline) {
        this.product = product;
        this.deadline = deadline;
    }

    public Product getProduct() {
        return product;
    }

    public int getDeadline() {
        return deadline;
    }

    public void decrementDeadline() {
        this.deadline -= 1;
    }

    @Override
    public String toString() {
        return "ProductProposal{" +
                "product=" + product +
                ", deadline=" + deadline +
                '}';
    }
}
