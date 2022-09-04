package com.asma.proj1.robot;

import com.asma.proj1.utilities.Logger;
import com.asma.proj1.utilities.Position;
import com.asma.proj1.utilities.Product;
import com.asma.proj1.utilities.ProductProposal;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Robot extends Agent {
    private RobotArguments robotArguments;
    private final Queue<ProductProposal> productsToGet = new LinkedList<>();
    private final List<ProductProposal> productsToDeliver = new ArrayList<>();
    private Position position = new Position(0, 0);

    public int getCurrentCapacity() {
        int occupied = 0;
        for (ProductProposal proposal : productsToGet) {
            occupied += proposal.getProduct().getWeight();
        }
        int capacity = this.robotArguments.getCapacity() - occupied;
        if (capacity < 0) {
            System.err.println("Current robot capacity is negative!!");
        }
        return Math.max(capacity, 0);
    }

    public Position getPosition() {
        return position;
    }

    public void setup() {
        this.robotArguments = new RobotArguments(this.getArguments());

        addBehaviour(new RobotTickListeningBehaviour(this));
        addBehaviour(new RobotBehaviour(this));

        Logger.logAgentStarted(this);

        this.register();
    }


    private void register() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd  = new ServiceDescription();
        sd.setType("robot");
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd );
        }
        catch (FIPAException fe) { fe.printStackTrace(); }
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    /**
     * The time needed to collect and deliver all products, given an additional position
     */
    public int timeNeededToComplete(Position additionalPosition) {
        int timeNeeded = 0;
        Position lastPosition = this.position;

        for (ProductProposal proposal : productsToGet) {
            timeNeeded += Position.distance(lastPosition, proposal.getProduct().getLocation());
            lastPosition = proposal.getProduct().getLocation();
        }
        timeNeeded += Position.distance(lastPosition, additionalPosition);
        timeNeeded += Position.distance(additionalPosition, Position.deliveryLinePosition);
        return timeNeeded;
    }

    /**
     * Gets the lowest deadline of all the products, that is, the bottleneck in terms of delivery time constraints
     */
    public int getLowestDeadline() {
        int lowestDeadline = 100000000;
        for (ProductProposal proposal : productsToGet) {
            lowestDeadline = Math.min(lowestDeadline, proposal.getDeadline());
        }
        for (ProductProposal proposal : productsToDeliver) {
            lowestDeadline = Math.min(lowestDeadline, proposal.getDeadline());
        }
        return lowestDeadline;
    }

    /**
     * Called at every time tick in the environment
     */
    public void handleTick() {
        // If we have products to get, direct to their position
        if (productsToGet.size() > 0) {
            Product product = productsToGet.peek().getProduct();
            this.position = Position.moveTowards(this.position, product.getLocation());

            if (this.position.equals(product.getLocation())) {
                Logger.logAgent(this, ("Just picked up product: " + product.getName()));
                this.productsToDeliver.add(productsToGet.poll());
            }
        }
        // if we only have products to deliver, direct to the delivery line
        else if (productsToDeliver.size() > 0) {
            Position deliveryLinePosition = Position.deliveryLinePosition;
            this.position = Position.moveTowards(this.position, deliveryLinePosition);

            if (this.position.equals(deliveryLinePosition)) {
                StringBuilder builder = new StringBuilder();
                builder.append("\n").append(Logger.getLogAgent(this, "$ Delivered:")).append("\n");
                for (ProductProposal proposal : this.productsToDeliver) {
                    builder.append("* ").append(proposal.getProduct().getName()).append(", deadline: ").append(proposal.getDeadline()).append("\n");
                }
                System.out.println(builder);
                this.productsToDeliver.clear();
            }
        }

        StringBuilder builder = new StringBuilder();
        builder.append("\n").append(Logger.getLogAgent(this, "%%% Current robot products:")).append("\n");
        for (ProductProposal proposal : productsToGet) {
            builder.append("- ").append(proposal.getProduct().getName()).append(", deadline: ").append(proposal.getDeadline()).append("\n");
            proposal.decrementDeadline();
        }
        for (ProductProposal proposal : productsToDeliver) {
            builder.append("- ").append(proposal.getProduct().getName()).append(", deadline: ").append(proposal.getDeadline()).append("\n");
            proposal.decrementDeadline();
        }

        builder.append(Logger.getLogAgent(this, "%%% Current position: " + this.position)).append("\n");
        System.out.println(builder);
    }

    public void handleNewProduct(ProductProposal product) {
        this.productsToGet.add(product);
    }

    public RobotArguments getRobotArguments() {
        return robotArguments;
    }
}
