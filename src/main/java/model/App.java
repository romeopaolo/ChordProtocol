package model;

import java.util.Scanner;
import java.util.logging.Logger;

import static model.NodeProperties.KEY_SIZE;

public class App {

    private static final Logger logger = Logger.getLogger(App.class.getName()); // TODO: cancel if not necessary
    private static Node node;
    private static Scanner stringScanner = new Scanner(System.in);
    private static Scanner intScanner = new Scanner(System.in); // To avoid burning new line characters

    public static void main(String[] args) {
        System.out.println("Enter \"new\" if you are the Net Generator, otherwise enter IP and port (\"IP\":\"port\") of the Node you know.");

        String input = stringScanner.nextLine();
        if (input.equals("new")) {
            node = new Node();
            node.create();
        } else {
            String[] parts = input.split(":");
            String knownIp = parts[0];
            int knownPort = Integer.parseInt(parts[1]);
            node = new Node();
            node.join(knownIp, knownPort);
        }

        // Workflow for demo
        int action;

        displayChoices(); // TODO: cancel this line

        do {
            // displayChoices(); TODO: commented just for testing
            action = intScanner.nextInt();
            switch (action) {
                case 1: // server coordinates
                    node.printServerCoordinates();
                    break;
                case 2: // finger table
                    node.printFingerTable();
                    break;
                case 3: // predecessor and successor coordinates
                    node.printPredecessorAndSuccessor();
                    break;
                case 4: // resources contained by the node
                    node.printResources();
                    break;
                case 5: // Look for a key
                    lookup();
                    break;
                case 6: // Ping request
                    ping();
                    break;
                case 0: // Leave the network
                    System.out.println("The node has left the network!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice! Try again...");
            }
        } while (action != 0);
    }

    private static void displayChoices() {
        System.out.println("Select your choice:");
        System.out.println("1. Own IP address, port of the server and ID");
        System.out.println("2. Own finger table");
        System.out.println("3. The IP address, port of the server and ID of the successor and predecessor;");
        System.out.println("4. The file key IDs and content contained by the current node;");
        System.out.println("5. Lookup for a resource;");
        System.out.println("6. Ping a node;");
        System.out.println("0. Leave the network");
    }

    private static void ping() {
        int port = 0;

        System.out.println("Insert ip and port (\"IP\":\"port\") of the node you want to reach:");
        String address = stringScanner.nextLine();
        String[] parts = address.split(":");

        if (parts.length == 2) {
            String ip = parts[0];
            port = Integer.parseInt(parts[1]);
            node.forward(node.getProperties(), ip, port, "ping", 0, 0, 0);
        } else {
            System.out.println("Please check the correctness of the input and try again");
        }
        System.out.println("------------------------------------------\n");
    }

    private static void lookup() {
        System.out.println("Insert the key you are looking for (it must be in the range [0," + KEY_SIZE + "]):");
        int key = intScanner.nextInt();
        String nodeIp = node.lookup(key);
        if (nodeIp != null) {
            System.out.println("The resource is kept by node " + nodeIp);
        } else {
            System.out.println("The resource doesn't exist in the net.");
        }
        System.out.println("------------------------------------------\n");
    }
}