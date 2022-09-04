package com.asma.proj1.utilities;

import jade.lang.acl.ACLMessage;

public class RobotCandidate {
    private final ACLMessage message;
    private final RobotResponse response;
    private final int distance;

    public RobotCandidate(RobotResponse response, ACLMessage message, Position productLocation) {
        this.message = message;
        this.response = response;
        this.distance = Position.distance(this.response.getPosition(), productLocation);
    }

    public RobotResponse getResponse() {
        return response;
    }

    public ACLMessage getMessage() {
        return message;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "RobotCandidate{" +
                "message=" + message +
                ", response=" + response +
                '}';
    }
}
