package com.asma.proj1.utilities;

import jade.core.Agent;

public class Logger {
    public static String getLogAgent(Agent agent, String msg) {
        return "[" + agent.getLocalName() + "] " + msg;
    }
    public static void logAgent(Agent agent, String msg) {
        System.out.println(Logger.getLogAgent(agent, msg));
    }
    public static void logAgentStarted(Agent agent) {
        System.out.println("[" + agent.getLocalName() + "] Started!");
    }
}
