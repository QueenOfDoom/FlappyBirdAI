package com.qod.flappy.network;

import com.qod.flappy.util.Util;

import java.util.*;

import static com.qod.flappy.network.Map.logger;

class Genome {

    private final HashMap<Integer, Connection> connections = new HashMap<>();
    private final HashMap<Integer, Node> nodes = new HashMap<>();

    private static final double NodeGeneMutationRate = 0.3;
    private static final double ConnectionGeneMutationRate = 0.4;
    private static final double NodeGeneMutationMultiplication = 0.4;
    private static final double ConnectionGeneMutationMultiplication = 0.2;

    Genome(){ }

    void addConnection(Connection connection){
        connections.put(connection.getInnovationNumber(), connection);
    }

    void addNode(Node node){
        nodes.put(node.getId(), node);
    }

    HashMap<Integer, Connection> getConnectionGenes(){
        return this.connections;
    }

    HashMap<Integer, Node> getNodeGenes(){
        return this.nodes;
    }

    void mutateNodesQ(Random r){
        for(Node node: nodes.values()){
            if (r.nextDouble() > NodeGeneMutationRate) {
                node.setQ((node.getQ() + r.nextDouble() * 2f - 1f) * NodeGeneMutationMultiplication);
            }
        }
    }

    void mutateConnectionWeights(Random r){
        for(Connection connection :connections.values()){
            if (r.nextDouble() > ConnectionGeneMutationRate) {
                connection.setWeight(
                        (connection.getWeight() + r.nextDouble() * 2f - 1f) * ConnectionGeneMutationMultiplication);
            }
        }
    }

    void addNodeMutation(Random r){
        Connection con = (Connection) connections.values().toArray()[r.nextInt(connections.size())];

        Node in = nodes.get(con.getInNodeId());
        Node out = nodes.get(con.getOutNodeId());

        con.disable();

        Node newNode = new Node(InnovationGeneration.getNextCounterForNodes(), r.nextDouble() * 2f - 1f, Node.Type.HIDDEN);
        Connection newConnectionsGene1 = new Connection(in.getId(), newNode.getId(),con.getWeight(), true, InnovationGeneration.getNextCounterForConnections());
        Connection newConnectionsGene2 = new Connection(newNode.getId(), out.getId(),1.0, true, InnovationGeneration.getNextCounterForConnections());

        connections.put(newConnectionsGene1.getInnovationNumber(), newConnectionsGene1);
        connections.put(newConnectionsGene2.getInnovationNumber(), newConnectionsGene2);
        nodes.put(newNode.getId(),newNode);
    }

    void addConnectionMutation(Random r, int maxTries){
        boolean repeat = true;
        Node g1;
        Node g2;
        int count = 0;
        while (repeat) {
            g1 = nodes.get(Util.getRandomElementFromSet(nodes.keySet(),r));
            g2 = nodes.get(Util.getRandomElementFromSet(nodes.keySet(),r));

            boolean reversed = false;
            if ((g1.getType() == Node.Type.OUTPUT) &&
                    (g2.getType() == Node.Type.HIDDEN)) {
                reversed = true;
            } else if ((g1.getType() == Node.Type.OUTPUT) && (g2.getType() == Node.Type.INPUT)) {
                reversed = true;
            } else if ((g1.getType() == Node.Type.HIDDEN) && (g2.getType() == Node.Type.INPUT)) {
                reversed = true;
            }

            boolean possible = true;
            if ((g1.getType() == Node.Type.OUTPUT) && (g2.getType() == Node.Type.OUTPUT)) {
                possible = false;
            } else if (
                    (g1.getType() == Node.Type.INPUT) &&
                            (g2.getType() == Node.Type.INPUT)) {
                possible = false;
            } else if (g1.getId() == g2.getId()) {
                possible = false;
            }

            boolean exists = false;
            boolean changed = false;
            for (Connection con : connections.values()) {
                if ((con.getInNodeId() == g1.getId()) && (con.getOutNodeId() == g2.getId())) {
                    exists = true;
                    if (!con.isEnabled()){
                        changed = true;
                        con.enable();
                    }
                    break;
                } else if ((con.getInNodeId() == g2.getId()) && (con.getOutNodeId() == g1.getId())) {
                    exists = true;
                    if (!con.isEnabled()){
                        changed = true;
                        con.enable();
                    }
                    break;
                }
            }

            if (count > maxTries){
                repeat = false;
            }

            if(changed){
                repeat = false;
                continue;
            }

            if(exists||(!possible)){
                count ++;
                continue;
            }

            Connection newConnection = new Connection(
                    reversed ? g1.getId(): g2.getId(),
                    reversed ? g2.getId(): g1.getId(),
                    r.nextDouble()*2f - 1f,
                    true,
                    InnovationGeneration.getNextCounterForConnections()
            );

            Genome helper = this.copy();
            helper.addConnection(newConnection);

            if(!containsLoop(helper, helper.nodes.get(newConnection.getOutNodeId()), new ArrayList<Node>())){
                logger.debug("Loop Detected.");
            } else {
                connections.put(newConnection.getInnovationNumber(), newConnection);
                repeat = false;
            }

        }
    }

    static Genome crossoverSameRights(Genome parent1, Genome parent2, Random r){
        Genome child = new Genome();
        int counterNodesMax = InnovationGeneration.getCounterForNodes();
        Node parent1Node, parent2Node;


        for(int i = 0; i < counterNodesMax; i++){
            parent1Node = parent1.getNodeGenes().get(i + 1);
            parent2Node = parent2.getNodeGenes().get(i + 1);

            if((parent1Node != null)&&(parent2Node != null)){
                Node n = new Node(parent1Node.getId(),r.nextBoolean() ? parent1Node.getQ(): parent2Node.getQ(), parent1Node.getType());
                child.addNode(n);
            } else if(parent1Node != null){
                Node n = new Node(parent1Node.getId(),parent1Node.getQ(), parent1Node.getType());
                child.addNode(n);
            } else if(parent2Node != null){
                Node n = new Node(parent2Node.getId(),parent2Node.getQ(), parent2Node.getType());
                child.addNode(n);
            }
        }

        int counterConnectionsMax = InnovationGeneration.getCounterForConnections();
        Connection parent1Connection, parent2Connection;

        for(int i = 0; i < counterConnectionsMax; i++){
            parent1Connection = parent1.getConnectionGenes().get(i + 1);
            parent2Connection = parent2.getConnectionGenes().get(i + 1);
            if((parent1Connection != null)&&(parent2Connection != null)){
                Connection c = new Connection(parent1Connection.getInNodeId(),
                        parent1Connection.getOutNodeId(),
                        r.nextBoolean() ? parent1Connection.getWeight(): parent2Connection.getWeight(),
                        r.nextBoolean() ? parent1Connection.isEnabled(): parent2Connection.isEnabled(),
                        parent1Connection.getInnovationNumber());
                child.addConnection(c);
            }
            else if((parent1Connection == null)&&(parent2Connection != null)){
                Connection c = new Connection(
                        parent2Connection.getInNodeId(),
                        parent2Connection.getOutNodeId(),
                        parent2Connection.getWeight(),
                        parent2Connection.isEnabled(),
                        parent2Connection.getInnovationNumber());
                child.addConnection(c);
            }
            else if(parent1Connection != null){
                Connection c = new Connection(parent1Connection.getInNodeId(),
                        parent1Connection.getOutNodeId(),
                        parent1Connection.getWeight(),
                        parent1Connection.isEnabled(),
                        parent1Connection.getInnovationNumber());
                child.addConnection(c);
            }
        }

        return child;
    }

    static Genome crossover(Genome parent1, Genome parent2, Random r){
        Genome child = new Genome();
        int counterNodesMax = InnovationGeneration.getCounterForNodes();
        Node parent1Node, parent2Node;


        for(int i = 0; i < counterNodesMax; i++){
            parent1Node = parent1.getNodeGenes().get(i + 1);
            parent2Node = parent2.getNodeGenes().get(i + 1);
            if((parent1Node != null)&&(parent2Node != null)){
                Node n = new Node(parent1Node.getId(),r.nextBoolean() ? parent1Node.getQ(): parent2Node.getQ(), parent1Node.getType());
                child.addNode(n);
            }
            else if(parent1Node != null){
                Node n = new Node(parent1Node.getId(),parent1Node.getQ(), parent1Node.getType());
                child.addNode(n);
            }

        }

        int counterConnectionsMax = InnovationGeneration.getCounterForConnections();
        Connection parent1Connection, parent2Connection;

        for(int i = 0; i < counterConnectionsMax; i++){
            parent1Connection = parent1.getConnectionGenes().get(i + 1);
            parent2Connection = parent2.getConnectionGenes().get(i + 1);
            if((parent1Connection != null)&&(parent2Connection != null)){
                Connection c = new Connection(
                        parent1Connection.getInNodeId(),
                        parent1Connection.getOutNodeId(),
                        r.nextBoolean() ? parent1Connection.getWeight():parent2Connection.getWeight(),
                        true,
                        parent1Connection.getInnovationNumber());
                child.addConnection(c);
            }
            else if(parent1Connection != null){
                Connection c = new Connection(parent1Connection.getInNodeId(),
                        parent1Connection.getOutNodeId(),
                        parent1Connection.getWeight(),
                        parent1Connection.isEnabled(),
                        parent1Connection.getInnovationNumber());
                child.addConnection(c);
            }
        }

        return child;
    }

    /**
     * Copies Genome.
     * @return genome with same properties as {@code this}.
     */
    Genome copy(){
        Genome genome = new Genome();
        for (Connection c: connections.values()){
            genome.addConnection(c.copy());
        }
        for(Node n: nodes.values()){
            genome.addNode(n.copy());
        }
        return genome;
    }

    private boolean containsLoop(Genome genome, Node StartingNode, ArrayList<Node> UsedGenes){
        if(StartingNode.getType() == Node.Type.INPUT){
            return true;
        } else if(StartingNode.getType() == Node.Type.OUTPUT) {
            return false;
        } else if(UsedGenes.contains(StartingNode)) {
            return false;
        } else {
            boolean answer = true;
            for(Connection c:genome.connections.values()){
                if(c.getOutNodeId() == StartingNode.getId()){
                    UsedGenes.add(StartingNode);
                    answer = containsLoop(genome, genome.nodes.get(c.getInNodeId()),UsedGenes );
                    if(!answer) break;
                    UsedGenes.remove(StartingNode);
                }
            }
            return answer;
        }
    }
}