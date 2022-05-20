import java.util.ArrayList;

public class Colour {
    private int colour_val;
    private ArrayList<Integer> adj = new ArrayList<Integer>();

    public Colour(int colour_val) {
        this.colour_val = colour_val;
    }

    public void add_node(int node_val) {
        adj.add(node_val);
    }

    public int get_colour() {
        return colour_val;
    }

    public ArrayList<Integer> get_adj_list() {
        return adj;
    }
}
