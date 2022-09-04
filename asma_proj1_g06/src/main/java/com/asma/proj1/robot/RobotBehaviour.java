package com.asma.proj1.robot;

import com.asma.proj1.utilities.Logger;
import com.asma.proj1.utilities.ProductProposal;
import com.asma.proj1.utilities.RobotMessages;
import com.asma.proj1.utilities.RobotResponse;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;

/**
 * Responds to Call for Proposals regarding the distribution of products, using ContractNet
 */
public class RobotBehaviour extends ContractNetResponder {
    private final Robot robotAgent;

    public RobotBehaviour(Robot robotAgent) {
        super(robotAgent, MessageTemplate.MatchPerformative(ACLMessage.CFP));
        this.robotAgent = robotAgent;
    }

    @Override
    public ACLMessage handleCfp(ACLMessage cfp) {
        ProductProposal proposal = null;
        try {
            proposal = (ProductProposal) cfp.getContentObject();
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
        assert proposal != null;

        Logger.logAgent(this.robotAgent, "Received cfp: " + proposal);
        ACLMessage reply = cfp.createReply();

        int freeCapacity = this.robotAgent.getCurrentCapacity();
        //Logger.logAgent(this.robotAgent, "lowest deadline: " + this.robotAgent.getLowestDeadline() + ", time needed: " + this.robotAgent.timeNeededToComplete(proposal.getProduct().getLocation()) + ", " + (this.robotAgent.getLowestDeadline() - this.robotAgent.timeNeededToComplete(proposal.getProduct().getLocation())));

        // If the robot doesn't have enough capacity, refuse
        if (this.robotAgent.getCurrentCapacity() < proposal.getProduct().getWeight()) {
            reply.setPerformative(ACLMessage.REFUSE);
            reply.setContent(RobotMessages.IS_FULL);
            Logger.logAgent(this.robotAgent, "Replied with IS_FULL");
        }
        // If accepting the package would make the robot miss other packages' deadlines, refuse
        else if (this.robotAgent.getLowestDeadline() - this.robotAgent.timeNeededToComplete(proposal.getProduct().getLocation()) < 0) {
            reply.setPerformative(ACLMessage.REFUSE);
            reply.setContent(RobotMessages.IS_BUSY);
            Logger.logAgent(this.robotAgent, "Replied with IS_BUSY");
        }
        // Else, propose
        else {
            reply.setPerformative(ACLMessage.PROPOSE);
            try {
                reply.setContentObject(new RobotResponse(freeCapacity, this.robotAgent.getPosition()));
                Logger.logAgent(this.robotAgent, "Replied with PROPOSE");
            } catch (Exception e) {
                System.err.println("Couldn't set PROPOSE content object");
                e.printStackTrace();
            }

        }

        return reply;
    }

    @Override
    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        Logger.logAgent(this.robotAgent, "Proposal was rejected...");
    }

    @Override
    public ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
        ProductProposal proposal = null;
        try {
            proposal = (ProductProposal) cfp.getContentObject();
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
        assert proposal != null;

        Logger.logAgent(this.robotAgent, "--> Handling product: " + proposal.getProduct().getName());
        this.robotAgent.handleNewProduct(proposal);

        ACLMessage reply = accept.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        return reply;
    }
}
