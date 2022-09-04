package com.asma.proj1.environment;

import com.asma.proj1.utilities.*;
import com.asma.proj1.utilities.Package;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.*;
import java.util.stream.Collectors;

public class Environment extends Agent {
    public static final int TICK = 10000;
    private final EnvironmentBehaviour behaviour;
    public List<AID> robots;
    public PriorityQueue<ProductProposal> productProposals = new PriorityQueue<>(10, Comparator.comparingInt(ProductProposal::getDeadline));

    public Environment() {
        this.behaviour = new EnvironmentBehaviour(this);
    }

    public void setup() {
        this.addBehaviour(this.behaviour);
        this.addBehaviour(new EnvironmentTickerBehaviour(this));
        Logger.logAgentStarted(this);

        try {
            this.findAgents();
        } catch (InterruptedException e) {
            System.err.println("Error trying to find Robot agents!");
            e.printStackTrace();
        }
    }

    private void findAgents() throws InterruptedException {
        Thread.sleep(2000);

        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("robot");
        dfd.addServices(sd);

        while (true) {
            try {
                DFAgentDescription[] result = DFService.search(this, dfd);
                if (result == null || result.length == 0) {
                    Thread.sleep(1000);
                    System.err.println("Couldn't get robots, retrying after timeout.");
                    continue;
                }
                this.robots = Arrays.stream(result).map(DFAgentDescription::getName).collect(Collectors.toList());
                System.out.println("Indexed robots!");
                break;
            } catch (FIPAException e) {
                e.printStackTrace();
                System.err.println("Critical error: couldn't get robots!");
            }
        }
    }

    public void handleNewPackage(Package pack) {
        System.out.println("[ENVIRONMENT] received new package! " + pack.toString());
        for (Product product : pack.getProducts()) {
            productProposals.add(new ProductProposal(product, pack.getDeadline()));
        }
    }

    public void addProductProposal(ProductProposal productProposal) {
        productProposals.add(productProposal);
    }

    public void onTick() {
        ProductProposal productProposal = productProposals.poll();
        if (productProposal == null) return;
        this.behaviour.callForProductProposals(productProposal);
    }

    public EnvironmentBehaviour getBehaviour() {
        return behaviour;
    }

    public List<AID> getRobots() {
        return robots;
    }
}
