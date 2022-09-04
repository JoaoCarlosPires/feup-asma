package com.asma.proj1.client;

import com.asma.proj1.utilities.Arguments;
import com.asma.proj1.utilities.Package;

import java.io.Serializable;
import java.util.List;

public class ClientArguments implements Serializable, Arguments {
    private final List<Package> packages;
    private final boolean random;
    private final int period;

    public ClientArguments(List<Package> packages, boolean random, int period) {
        this.packages = packages;
        this.random = random;
        this.period = period;
    }

    public List<Package> getPackages() {
        return packages;
    }

    public boolean isRandom() {
        return random;
    }

    public int getPeriod() {
        return period;
    }

    @Override
    public Object[] getObjectArray() {
        return new Object[] {
                this
        };
    }

    @Override
    public String toString() {
        return "ClientArguments{" +
                "packages=" + packages +
                ", random=" + random +
                '}';
    }
}
