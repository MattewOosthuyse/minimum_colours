import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class Min_Colour {

    public static void main(String[] args) {

        // Initialising the nodes
        Node[] nodes = new Node[8];
        for (int i = 0; i < nodes.length; i++)
            nodes[i] = new Node(i);


        // Initialising the colours
        Colour[] colours = new Colour[5];
        for (int i = 0; i < colours.length; i++)
            colours[i] = new Colour(i);

        // TODO: Read in excel files
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File("data/data.xls"));

            HSSFWorkbook wb = null;
            try {
                wb = new HSSFWorkbook(fis);
                HSSFSheet sheet = wb.getSheetAt(0);

                FormulaEvaluator formula_eval = wb.getCreationHelper().createFormulaEvaluator();

                for (Row row : sheet) {
                    for (Cell cell : row) {
                        if (cell.getNumericCellValue() == 1) {
                            // Add the node to the colours adj list
                            colours[row.getRowNum()].add_node(cell.getColumnIndex());
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            System.out.println("the file was not found or there was an error when reading the file");
        }


        System.out.println(evaluate_colour_cross(nodes, colours));
        basic_strat(nodes, colours);
        // Check after one iteration
        System.out.println(evaluate_colour_cross(nodes, colours));
        basic_strat(nodes, colours);
        System.out.println(evaluate_colour_cross(nodes, colours));
    }

    /**
     * An evaluation function that determines how many colours cross the midline
     *
     * @param nodes   the array of the nodes positions
     * @param colours the colours with their node lists
     * @return the number of colours that cross the midline
     */
    public static int evaluate_colour_cross(Node[] nodes, Colour[] colours) {
        int count = 0;
        // Inside each colour, find which half of the array the nodes are situated
        for (int i = 0; i < colours.length; i++) {
            ArrayList<Integer> n = colours[i].get_adj_list();
            // Boolean flags to see where the nodes are in the list
            boolean first = false, second = false, found = false;
            for (int j = 0; j < n.size(); j++) {
                // Find the nodes in the list
                for (int x = 0; x < nodes.length; x++) {
                    if (nodes[x].get_Node() == n.get(j)) {
                        // Node is found and needs to be accounted for in which sector it is in
                        if (x < nodes.length / 2) first = true;
                        else second = true;
                    }
                    // If there are nodes in both halves, break out of the loop.
                    if (first && second) {
                        count++;
                        colours[i].set_sec(false);
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }
        }
        return count;
    }

    /**
     * Basic strategy to do a swap algorithm to find the best case where just swapping nodes will give the best solution.
     *
     * @param nodes   array of the nodes
     * @param colours array of the colours
     */
    public static void basic_strat(Node[] nodes, Colour[] colours) {
        for (int i = 0; i < colours.length; i++) {
            if (!colours[i].one_sector()) {
                ArrayList<Integer> n = colours[i].get_adj_list();
                int sec_1 = 0, sec_2 = 0;
                int node_pos;
                for (int j = 0; j < n.size(); j++) {
                    for (int x = 0; x < nodes.length; x++) {
                        if (nodes[x].get_Node() == n.get(j)) {
                            if (x < nodes.length / 2) {
                                sec_1++;
                            } else {
                                sec_2++;
                            }
                        }
                    }
                }
                // After the sectors have been determined
                // get the difference in sectors for later evaluation
                int diff = Math.abs(sec_1 - sec_2);
                if (sec_1 == 0)
                    colours[i].set_sec(true);
                else if (sec_2 == 0)
                    colours[i].set_sec(true);
                else if (sec_1 > sec_2)
                    colours[i].set_majority_sec(1);
                else if (sec_2 > sec_1)
                    colours[i].set_majority_sec(2);
                colours[i].set_sec_diff(diff);
            }
        }

        // Get the colour that has the biggest % difference
        Colour c = null;
        int current_diff_percent = 0;
        for (int i = 0; i < colours.length; i++) {
            // Get the colours diff %
            int percent = colours[i].diff_percent();
            if (current_diff_percent < percent) {
                c = colours[i];
                current_diff_percent = percent;
            }
        }

        // Now that we have the colour that will be chosen
        // find which sector it has the majority in and get a node that is not in that sector
        ArrayList<Integer> current_list = c.get_adj_list();
        int node_index_1 = 0;
        int node_index_2 = 0;
        for (int i = 0; i < current_list.size(); i++) {
            for (int j = 0; j < nodes.length; j++) {
                // find a node that is not in the same sector
                // The nodes are all in sec 1 - look at sec 2
                if (c.majority_sec() == 1) {
                    if (nodes[j].get_Node() == current_list.get(i) && j >= nodes.length / 2) {
                        node_index_1 = j;
                        break;
                    }
                } else {
                    if (nodes[j].get_Node() == current_list.get(i) && j < nodes.length / 2) {
                        node_index_1 = j;
                        break;
                    }
                }
            }
        }

        // Now that the node that will be swapped is found, find the node that it should be swapped with
        int current_count = evaluate_colour_cross(nodes, colours);
        int min_count = current_count;
        Node[] temp = new Node[nodes.length];


        // using the opposite sector to find the node to swap

        if (c.majority_sec() == 2) {
            for (int i = nodes.length / 2; i < nodes.length; i++) {
                // Copying the nodes over to no change the values in the current address of the nodes.
                for (int j = 0; j < nodes.length; j++) {
                    temp[j] = new Node(nodes[j].get_Node());
                }
                swap(temp, node_index_1, i);
                current_count = evaluate_colour_cross(temp, colours);
                if (current_count < min_count) {
                    node_index_2 = i;
                    min_count = current_count;
                }
            }
        } else if (c.majority_sec() == 1) {
            for (int i = 0; i < nodes.length / 2; i++) {
                // Copying the nodes over to no change the values in the current address of the nodes.
                for (int j = 0; j < nodes.length; j++) {
                    temp[j] = new Node(nodes[j].get_Node());
                }
                swap(temp, node_index_1, i);
                current_count = evaluate_colour_cross(temp, colours);
                if (current_count < min_count) {
                    node_index_2 = i;
                    min_count = current_count;
                }
            }
        }

        // Do the swap on the real nodes list. Get the count and do it again.
        swap(nodes, node_index_1, node_index_2);

    }

    /**
     * Function that swaps two nodes in the list
     *
     * @param nodes array of the nodes
     * @param x     node pos to be swapped
     * @param y     node pos to be swapped
     */
    public static void swap(Node[] nodes, int x, int y) {
        Node temp = nodes[x];
        nodes[x] = nodes[y];
        nodes[y] = temp;
    }


    public static void draw_model(Node[] nodes, Colour[] colours) {
        StdDraw.setCanvasSize(800, 800);
        StdDraw.setXscale(0, 800);
        StdDraw.setYscale(800, 0);

        // Setting up the GUI

    }
}
