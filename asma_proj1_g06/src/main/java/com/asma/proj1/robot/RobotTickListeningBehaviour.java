package com.asma.proj1.robot;

import com.asma.proj1.environment.Environment;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * This behaviour lets the agent use the same time frame as the other robots and the environment
 */
class RobotTickListeningBehaviour extends CyclicBehaviour {
    private final Robot robot;
    private final MessageTemplate tickTemplate = MessageTemplate.MatchPerformative(Environment.TICK);

    public RobotTickListeningBehaviour(Robot robot) {
        this.robot = robot;
    }

    public void action() {
        ACLMessage tickMessage = this.myAgent.receive(tickTemplate);
        if(tickMessage != null) {
            this.robot.handleTick();
        } else {
            block();
        }
    }
}