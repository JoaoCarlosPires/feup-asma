package com.asma.proj1.client;

import com.asma.proj1.utilities.Package;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;

/**
 * Ticking behaviour for the creation of packages
 */
public class ClientTickerBehaviour extends TickerBehaviour {

    private final Client clientAgent;
    private int currentPackage = 0;

    public ClientTickerBehaviour(Client clientAgent) {
        super(clientAgent, clientAgent.clientArguments.getPeriod());
        this.clientAgent = clientAgent;
    }

    @Override
    public void onStart() {
        System.out.println("[START] client ticker behaviour. Random ? " + this.clientAgent.clientArguments.isRandom() + ", period = " + clientAgent.clientArguments.getPeriod());
    }

    @Override
    protected void onTick() {
        Package pkg;
        if (this.clientAgent.clientArguments.isRandom()) {
            pkg = Package.getRandPackage();
        } else {
            if (this.currentPackage >= this.clientAgent.clientArguments.getPackages().size()) {
                System.out.println("\n##############\n\nNO MORE PACKAGES TO SEND\n\n###############\n");
                this.stop();
                return;
            }
            pkg = this.clientAgent.clientArguments.getPackages().get(this.currentPackage);
            this.currentPackage += 1;
        }

        System.out.println("[CLIENT] Sending package: " + pkg);

        ACLMessage message = new ACLMessage(ACLMessage.INFORM); // inform environment of the new package
        message.addReceiver(new AID("environment", AID.ISLOCALNAME));
        try {
            message.setContentObject(pkg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        clientAgent.send(message);
    }
}
