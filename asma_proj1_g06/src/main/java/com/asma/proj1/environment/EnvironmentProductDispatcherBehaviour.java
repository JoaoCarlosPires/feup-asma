package com.asma.proj1.environment;

import com.asma.proj1.utilities.ProductProposal;
import com.asma.proj1.utilities.RobotCandidate;
import com.asma.proj1.utilities.RobotResponse;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;

import java.util.*;

/**
 * Dispatches products using ContractNet
 */
public class EnvironmentProductDispatcherBehaviour extends ContractNetInitiator {
    private final ProductProposal productProposal;
    private final Environment environmentAgent;

    public EnvironmentProductDispatcherBehaviour(Environment environmentAgent, ACLMessage cfp, ProductProposal proposal) {
        super(environmentAgent, cfp);
        this.productProposal = proposal;
        this.environmentAgent = environmentAgent;
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        PriorityQueue<RobotCandidate> candidates = new PriorityQueue<>(this.environmentAgent.getRobots().size(), Comparator.comparingInt(RobotCandidate::getDistance));

        for (Object response : responses) {
            ACLMessage robotMessage = (ACLMessage) response;

            try {
                switch (robotMessage.getPerformative()) {
                    case (ACLMessage.PROPOSE) -> {
                        RobotResponse responseContent = (RobotResponse) robotMessage.getContentObject();
                        System.out.println("[ENVIRONMENT] Robot '" + robotMessage.getSender().getLocalName() + "' proposed: " + responseContent);
                        candidates.add(new RobotCandidate(responseContent, robotMessage, this.productProposal.getProduct().getLocation()));
                    }
                    case (ACLMessage.REFUSE) ->
                        System.out.println("[ENVIRONMENT] Robot '" + robotMessage.getSender().getLocalName() + "' refused.");
                }
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n%%%%%");
        for (RobotCandidate candidate : candidates) {
            System.out.println("Got candidate: " + candidate.getDistance());
        }
        System.out.println("%%%%%\n");

        if (candidates.size() < 1) {
            System.err.println("Didn't get enough candidates!");
            // Re-adding the product proposal to the environment, because it couldn't be attributed to any robot
            this.environmentAgent.addProductProposal(this.productProposal);
            return;
        }

        acceptances.add(this.getAcceptMessage(candidates.poll()));
        acceptances.addAll(this.getRejectionMessages(new ArrayList<>(candidates)));
    }

    @Override
    protected void handleInform(ACLMessage inform) {
        this.environmentAgent.getBehaviour().removeSubBehaviour(this);
    }

    @Override
    protected void handleAllResultNotifications(Vector resultNotifications) {
        this.environmentAgent.getBehaviour().removeSubBehaviour(this);
    }

    private List<ACLMessage> getRejectionMessages(List<RobotCandidate> candidates) {
        List<ACLMessage> replies = new ArrayList<>();
        for (RobotCandidate candidate : candidates) {
            ACLMessage reply = candidate.getMessage().createReply();
            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
            replies.add(reply);
        }
        return replies;
    }

    private ACLMessage getAcceptMessage(RobotCandidate candidate) {
        ACLMessage reply = candidate.getMessage().createReply();
        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);

        return reply;
    }
}
