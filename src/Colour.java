import java.util.ArrayList;

public class colour {
    private int colour_val;
    private ArrayList<int> adj = new ArrayList<int>();

    public colour(int colour_val) {
        this.colour_val = colour_val;
    }

    public void add_colour(int node_val) {
        adj.add(node_val);
    }

    public int get_colour() {
        return colour_val;
    }

    public int[] get_adj_list() {
        int size = adj.size();
        int[] temp = new int[size];
        for (int i = 0; i < size; i++) {
            temp[i] = adj.get(i);
        }
    }
}
