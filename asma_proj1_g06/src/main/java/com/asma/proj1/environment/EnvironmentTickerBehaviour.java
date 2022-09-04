package com.asma.proj1.environment;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * The passing of time in the system
 */
public class EnvironmentTickerBehaviour extends TickerBehaviour {
    private final Environment environmentAgent;

    public EnvironmentTickerBehaviour(Agent environmentAgent) {
        super(environmentAgent, 1000);
        this.environmentAgent = (Environment) environmentAgent;
    }

    @Override
    public void onStart() {
        System.out.println("[START] environment ticker behaviour");
    }

    @Override
    protected void onTick() {
        System.out.println("\n\n\n\n----------------------------------------------------------\n\n[ENVIRONMENT] tick");
        ACLMessage msg = new ACLMessage(Environment.TICK);

        this.environmentAgent.onTick();
        for (AID robot : this.environmentAgent.robots) {
            msg.addReceiver(robot);
        }

        this.environmentAgent.send(msg);
    }
}
