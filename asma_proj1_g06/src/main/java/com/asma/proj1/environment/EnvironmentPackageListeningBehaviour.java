package com.asma.proj1.environment;

import com.asma.proj1.utilities.Package;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

/**
 * Listens for new packages
 */
class EnvironmentPackageListeningBehaviour extends CyclicBehaviour {
    private final MessageTemplate informTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
    private final Environment environmentAgent;

    public EnvironmentPackageListeningBehaviour(Environment a) {
        this.environmentAgent = a;
    }

    public void action() {
        ACLMessage informMessage = this.myAgent.receive(informTemplate);
        if(informMessage != null) {
            try {
                if (informMessage.getContentObject() instanceof Package) {
                    this.environmentAgent.handleNewPackage((Package) informMessage.getContentObject());
                }
            } catch (UnreadableException e) {
                System.err.println("Error when handling INFORM message in environment! ");
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}

