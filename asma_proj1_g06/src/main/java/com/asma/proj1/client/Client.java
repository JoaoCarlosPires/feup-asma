package com.asma.proj1.client;

import com.asma.proj1.utilities.Logger;
import jade.core.Agent;

public class Client extends Agent {
    public ClientArguments clientArguments;

    public void setup() {
        this.clientArguments = (ClientArguments) this.getArguments()[0];

        this.addBehaviour(new ClientTickerBehaviour(this));
        Logger.logAgentStarted(this);
    }
}
