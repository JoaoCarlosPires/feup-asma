package com.asma.proj1;

import com.asma.proj1.client.Client;
import com.asma.proj1.client.ClientArguments;
import com.asma.proj1.environment.Environment;
import com.asma.proj1.robot.Robot;
import com.asma.proj1.robot.RobotArguments;
import com.asma.proj1.utilities.Arguments;
import com.asma.proj1.utilities.Package;
import com.asma.proj1.utilities.Position;
import com.asma.proj1.utilities.Product;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Launches the agents and their configurations
 */
public class AgentLauncher {
    private ContainerController container;

    public static void main(String[] args) throws Exception {
        String configuration = args.length < 1 ? null : args[0];
        if (configuration == null) {
            System.err.println("No configuration has been specified!");
            return;
        }

        System.out.println("\n#######################################################");
        System.out.println("LAUNCHING AGENTS WITH CONFIGURATION: " + configuration);
        System.out.println("#######################################################\n");

        AgentLauncher launcher = new AgentLauncher();
        launcher.launch(configuration);
    }

    private void launch(String configuration) throws Exception {
        JSONObject config = this.readConfiguration(configuration);
        List<RobotArguments> robotArguments = this.parseRobotConfiguration(config);
        ClientArguments clientArguments = this.parseClientConfiguration(config);

        this.createContainer();

        this.startAgent("environment", Environment.class);
        this.startAgent("client", Client.class, clientArguments);

        for (RobotArguments robotArgument : robotArguments)
            this.startAgent(robotArgument.getName(), Robot.class, robotArgument);
    }

    private void createContainer() throws StaleProxyException {
        if (this.container != null) this.container.kill();

        ProfileImpl containerProfile = new ProfileImpl();
        containerProfile.setParameter(Profile.CONTAINER_NAME, "Warehouse");
        this.container = Runtime.instance().createAgentContainer(containerProfile);
    }

    private void startAgent(String nickname, Class<? extends Agent> c) throws StaleProxyException {
        this.startAgent(nickname, c, null);
    }

    private void startAgent(String nickname, Class<? extends Agent> c, Arguments arguments) throws StaleProxyException {
        Object[] argsArray = arguments == null ? null : arguments.getObjectArray();
        AgentController agent = this.container.createNewAgent(nickname, c.getCanonicalName(), argsArray);
        agent.start();
    }

    private JSONObject readConfiguration(String configuration) throws Exception {
        JSONObject configurations = (JSONObject) new JSONParser().parse(new FileReader("configurations.json"));
        JSONObject config = (JSONObject) configurations.get(configuration);
        System.out.println(config + "\n");
        return config;
    }

    private List<RobotArguments> parseRobotConfiguration(JSONObject config) {
        JSONArray robots = (JSONArray) config.get("robots");
        List<RobotArguments> result = new ArrayList<>();

        for (Object robot : robots) {
            JSONObject robotJSON = (JSONObject) robot;
            String name = (String) robotJSON.get("name");
            long capacity = (long) robotJSON.get("capacity");

            result.add(new RobotArguments(name, (int) capacity));
        }

        return result;
    }

    private ClientArguments parseClientConfiguration(JSONObject config) {
        JSONArray initialPackages = (JSONArray) ((JSONObject) config.get("client")).get("initialPackages");
        List<Package> packages = new ArrayList<>();

        for (Object pack : initialPackages) {
            JSONObject packageJSON = (JSONObject) pack;
            JSONArray productsJSON = (JSONArray) packageJSON.get("products");

            List<Product> products = new ArrayList<>();
            for (Object product : productsJSON) {
                JSONObject productJSON = (JSONObject) product;
                JSONObject locationJSON = (JSONObject) productJSON.get("location");

                Position location = new Position((long) locationJSON.get("x"), (long) locationJSON.get("y"));
                products.add(new Product((long) productJSON.get("weight"), (String) productJSON.get("name"), location));
            }
            long deadline = (long) packageJSON.get("deadline");

            packages.add(new Package(products, (int) deadline));
        }

        boolean random = (boolean) ((JSONObject) config.get("client")).get("random");
        long period = (long) ((JSONObject) config.get("client")).get("period");

        return new ClientArguments(packages, random, (int) period);
    }
}