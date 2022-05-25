public class Node {
    private final int node_val;
    private int colour_count = 0;

    /**
     * Setting up the node
     *
     * @param node_val the number of the node.
     */
    public Node(int node_val) {
        this.node_val = node_val;
    }

    /**
     * Getting the value of the node
     *
     * @return the node number
     */
    public int get_Node() {
        return node_val;
    }

    public int get_nodes_colour_count() {
        return colour_count;
    }

    public void inc_colour_count() {
        colour_count++;
    }
}
