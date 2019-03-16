package model;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static model.NodeProperties.KEY_SIZE;
import static utilities.Utilities.sha1;

public class Node {

    private static final Logger logger = Logger.getLogger(Node.class.getName());
    /**
     * Finger table of the node
     */
    private final NodeProperties[] fingers = new NodeProperties[KEY_SIZE];
    /**
     * Represents the "client side" of a node. It sends requests to other nodes
     */
    private Forwarder forwarder;
    /**
     * Contains information about the node
     */
    private NodeProperties properties;

    /**
     * Data contained in the node
     */
    private HashMap<Integer, Serializable> data;

    /**
     * List of adjacent successors of the node
     */
    private List<NodeProperties> successors;

    private NodeProperties predecessor;

    private ServerSocket serverSocket;

    public Node() {
        successors = new ArrayList<>();
        data = new HashMap<>();
    }

    // Getter
    public NodeProperties getProperties() {
        return properties;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public Forwarder getForwarder() {
        return forwarder;
    }

    /**
     * Create a new Chord Ring
     */
    public void create() {
        serverSocket = createServerSocket();
        forwarder = new Forwarder();
        int newPort = serverSocket.getLocalPort();
        String ipAddress = getCurrentIp();

        System.out.println("The IP of this node is : " + ipAddress);
        System.out.println("The server is active on port " + newPort);

        //Create node information
        this.properties = new NodeProperties(sha1(ipAddress + ":" + newPort), ipAddress, newPort);
        this.successor(this.properties);
        this.predecessor = null;

        new Thread(new RequestHandler(this)).start();
    }

    /**
     * Join a Ring containing the known Node
     */
    public void join(String ip, int port) {

        serverSocket = createServerSocket();
        forwarder = new Forwarder();
        int newPort = serverSocket.getLocalPort();
        String newIp = getCurrentIp();

        this.properties = new NodeProperties(sha1(newIp + ":" + newPort), newIp, newPort);
        this.predecessor = null;

        forwarder.makeRequest(ip, port, "ping");
        //this.successors.remove(0);
        //this.successors.add(0, findSuccessor(properties.getNodeId()));

        //NodeProperties successor = forwarder.makeRequest(ip, port, "find_successor:" + properties.getNodeId());

    }

    /**
     * Set the successor of the current node
     *
     * @param node the successor
     */
    private void successor(NodeProperties node) {
        synchronized (this.fingers) {
            this.fingers[0] = node;
        }
    }

    //TODO: Will go into a thread

    /**
     * Veriﬁes n’s immediate successor, and tells the successor about n.
     */
    public void stabilize() {

    }

    /**
     * @param predecessor node that could be the predecessor
     */
    public void notifySuccessor(Node predecessor) {

    }

    //TODO: Will go into a thread

    /**
     * Refresh fingers table
     */
    public void fixFingers() {

    }

    //TODO: Will go into a thread

    /**
     * Check if predecessor has failed
     */
    public void checkPredecessor() {

    }

    /**
     * Find the successor of the node with the given id
     *
     * @param nodeId
     */
    public void findSuccessor(int nodeId) {

    }

    /**
     * Find the highest predecessor of id
     *
     * @param nodeId
     */
    public void closestPrecedingNode(int nodeId) {

    }

    private ServerSocket createServerSocket() {
        ServerSocket serverSocket = null;

        // Create the new serverSocket
        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return serverSocket;
    }

    private String getCurrentIp() {
        //Find Ip address, it will be published later for joining
        InetAddress currentIp = null;
        try {
            currentIp = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        assert currentIp != null;
        return currentIp.getHostAddress();
    }

    public int lookup(int key) {
        // TODO: implement. If the key is not present return -1
        //
        return -1;
    }
}
