package com.asma.proj1.environment;
import com.asma.proj1.utilities.Product;
import com.asma.proj1.utilities.ProductProposal;
import jade.core.AID;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;

import java.io.IOException;

/**
 * Listens to new packages and in parallel starts dispatcher behaviours for each of those packages
 */
public class EnvironmentBehaviour extends ParallelBehaviour {
    private final Environment environmentAgent;

    public EnvironmentBehaviour(Environment environmentAgent) {
        super(WHEN_ALL);
        this.environmentAgent = environmentAgent;
        addSubBehaviour(new EnvironmentPackageListeningBehaviour(environmentAgent));
    }

    public void callForProductProposals(ProductProposal productProposal) {
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        for (AID robotAID : this.environmentAgent.getRobots()) {
            cfp.addReceiver(robotAID);
        }
        cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);

        try {
            cfp.setContentObject(productProposal);
        } catch (IOException e) {
            System.err.println("Error building cfp message!");
            e.printStackTrace();
        }

        addSubBehaviour(new EnvironmentProductDispatcherBehaviour(this.environmentAgent, cfp, productProposal));
    }
}
