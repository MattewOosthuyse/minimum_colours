import java.awt.*;
import java.util.ArrayList;

public class Colour {
    private int sec_diff, sec;
    private Color col;
    private boolean sector;
    private ArrayList<Integer> adj = new ArrayList<Integer>();

    public Colour() {

    }

    public void add_node(int node_val) {
        adj.add(node_val);
    }

    public Color get_colour() {
        return col;
    }

    public void set_colour(Color col) {
        this.col = col;
    }

    public void set_sec(boolean sector) {
        this.sector = sector;
    }

    public boolean one_sector() {
        return sector;
    }

    public void set_sec_diff(int n) {
        this.sec_diff = n;
    }

    public int get_sec_diff() {
        return sec_diff;
    }

    public int majority_sec() {
        return sec;
    }

    public void set_majority_sec(int sec) {
        this.sec = sec;
    }

    public int diff_percent() {
        return (int) ((double) sec_diff / (double) adj.size() * 100);
    }


    public ArrayList<Integer> get_adj_list() {
        return adj;
    }
}
