package app;

import model.Node;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static model.NodeProperties.KEY_SIZE;
import static utilities.Utilities.sha1;

public class App {

    private static Node node;
    private static Scanner stringScanner = new Scanner(System.in);
    private static Scanner intScanner = new Scanner(System.in); // To avoid burning new line characters
    private static String knownIp;
    private static int knownTcpPort;
    private static final int RESOURCES_NUMBER = 5;
    private static boolean resourcesCreated;

    public static void main(String[] args) {
        System.out.println("Enter \"new\" if you are the Net Generator, otherwise enter IP and port (\"IP\":\"port\") of the Node you know.");

        String input = stringScanner.nextLine();
        if (input.equals("new")) {
            node = new Node();
            node.create();
        } else {
            String[] parts = input.split(":");
            knownIp = parts[0];
            knownTcpPort = Integer.parseInt(parts[1]);

            node = new Node();
            node.join(knownIp, knownTcpPort);
        }

        // Workflow for demo
        int action;

        displayChoices();

        do {
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
                case 4: // offline resources contained by the node
                    if (resourcesCreated)
                        printResources("offline");
                    else
                        printError();
                    break;
                case 5: // online resources contained by the node
                    printResources("online");
                    break;
                case 6: // Look for a key
                    lookup();
                    break;
                case 7: // create the resources and keep it offline
                    if (!resourcesCreated) {
                        createResources();
                        resourcesCreated = true;
                    } else {
                        System.out.println("You already created your resources!");
                    }
                    break;
                case 8: // publish offline resources
                    if (resourcesCreated)
                        node.publishResources();
                    else
                        printError();
                    break;
                case 9: // print successors list
                    node.printSuccessors();
                    break;
                case 10: // print the backup resources
                    printResources("backup");
                    break;
                case 0: // leave the network
                    System.out.println("The node has left the network!");
                    exit();
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
        System.out.println("3. IP address, port of the server and ID of the successor and predecessor;");
        System.out.println("4. File key IDs of the current node still not published;");
        System.out.println("5. File key IDs of the current node;");
        System.out.println("6. Lookup for a resource;");
        System.out.println("7. Create your offline resources;");
        System.out.println("8. Publish your resources on the network;");
        System.out.println("9. Print successor list;");
        System.out.println("10. File key IDs of the backup resources;");
        System.out.println("0. Leave the network.");
        System.out.println("------------------------------------------\n");
    }

    private static void lookup() {
        System.out.println("Insert the key you are looking for (it must be in the range [0," + (Math.pow(2, KEY_SIZE) - 1) + "]):");
        int key = intScanner.nextInt();
        node.lookup(node.getProperties(), key, false, null);
        System.out.println("------------------------------------------\n");
    }

    private static void exit() {
        node.close();
        System.exit(0);
    }

    private static void printResources(String onOrOff) {
        File folder = new File("./node" + node.getProperties().getNodeId() + "/" + onOrOff);
        File[] allFiles = folder.listFiles();

        if (allFiles != null || allFiles.length == 0) {
            System.out.println(allFiles.length + " resources available " + onOrOff + " in node " + node.getProperties().getNodeId() + ":");
            for (File allFile : allFiles) {
                System.out.println("SHA: " + sha1(allFile.getName()) + "\tName: " + allFile.getName());
            }
        } else {
            System.out.println("No resources available");
        }
        System.out.println("------------------------------------------\n");
    }

    private static void createResources() {
        for (int i = 0; i < RESOURCES_NUMBER; i++) {
            String filename = "Node" + node.getProperties().getNodeId() + "-File" + i;
            File f = new File("./node" + node.getProperties().getNodeId() + "/offline/" + filename);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("You correctly created your offline resources.");
        System.out.println("------------------------------------------\n");
    }

    private static void printError() {
        System.out.println("You need to create your own resources in order to see or publish them.");
        System.out.println("------------------------------------------\n");
    }
}