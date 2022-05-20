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
            colours[i] = new Colour();

        // Setting up the colours for the simple examples
        colours[0].set_colour(StdDraw.CYAN);
        colours[1].set_colour(StdDraw.GREEN);
        colours[2].set_colour(StdDraw.YELLOW);
        colours[3].set_colour(StdDraw.RED);
        colours[4].set_colour(StdDraw.BLUE);


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
        draw_model(nodes, colours);
        while (!StdDraw.hasNextKeyTyped()) ;

        basic_strat(nodes, colours);
        draw_model(nodes, colours);
        while (!StdDraw.hasNextKeyTyped()) ;
        // Check after one iteration
        System.out.println(evaluate_colour_cross(nodes, colours));

        basic_strat(nodes, colours);
        draw_model(nodes, colours);
        while (!StdDraw.hasNextKeyTyped()) ;
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
        for (Colour colour : colours) {
            if (!colour.one_sector()) {
                ArrayList<Integer> n = colour.get_adj_list();
                int sec_1 = 0, sec_2 = 0;
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
                    colour.set_sec(true);
                else if (sec_2 == 0)
                    colour.set_sec(true);
                else if (sec_1 > sec_2)
                    colour.set_majority_sec(1);
                else if (sec_2 > sec_1)
                    colour.set_majority_sec(2);
                colour.set_sec_diff(diff);
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
        assert c != null;
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

    /**
     * Using StdDraw to visually demonstrate the algorithm and to show the solution that has been obtained
     *
     * @param nodes   the list of the nodes
     * @param colours the colours that are connected to the different nodes
     */
    public static void draw_model(Node[] nodes, Colour[] colours) {
        StdDraw.setCanvasSize(800, 600);

        StdDraw.setXscale(0, 800);
        StdDraw.setYscale(600, 0);

        // Setting up the GUI
        // Half the nodes at the top, a line through the middle and half the
        // nodes at the bottom with lines connecting the different nodes
        // TODO figure out how to set up the colours to the different coordinates
        // this could be done by using the same formula as I use for setting the coordinates of the nodes
        // this will be more to do with the node rank and not the actual value of the nodes

        // setup background
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(400, 400, 400, 400);

        StdDraw.enableDoubleBuffering();

        draw_colours(nodes, colours);
        draw_nodes(nodes, colours);

        StdDraw.show();

        // Setting up the colours for the

    }

    public static void draw_colours(Node[] nodes, Colour[] colours) {
        // Setting the positions that all the nodes will be in.
        int half = nodes.length / 2;
        // Loop through the colours, start at the first node, draw a line to all the other colours and then do the same at the next node
        for (Colour colour : colours) {
            // Go through the list of colours and thereafter get the positions of the nodes in the graph
            StdDraw.setPenColor(colour.get_colour());
            ArrayList<Integer> n = colour.get_adj_list();
            for (int j = 0; j < n.size(); j++) {
                // Find the nodes in the graph
                for (int x = 0; x < nodes.length; x++)
                    if (nodes[x].get_Node() == n.get(j))
                        for (int k = 0; k < n.size(); k++)
                            for (int y = 0; y < nodes.length; y++)
                                if (nodes[y].get_Node() == n.get(k))
                                    // have the nodes that are in the bottom half first
                                    if (x < nodes.length / 2 && y < nodes.length / 2)
                                        StdDraw.line(800 / half + 150 * x, 200, 800 / half + 150 * y, 200);
                                    else if (x < half)
                                        StdDraw.line(800 / half + 150 * x, 200, 800 / half + 150 * (y / 2), 400);
                                    else if (y < half)
                                        StdDraw.line(800 / half + 150 * (x / 2), 400, 800 / half + 150 * y, 200);
                                    else
                                        StdDraw.line(800 / half + 150 * (x / 2), 400, 800 / half + 150 * (y / 2), 400);
            }
        }
    }

    /**
     * Draws the nodes on the canvas using StdDraw
     *
     * @param nodes   list of the nodes to be drawn
     * @param colours list of the colour objects with their properties
     */
    public static void draw_nodes(Node[] nodes, Colour[] colours) {
        // Set the positions of all the nodes
        int half = nodes.length / 2;
        int adapt_y = 225;
        // Top section of nodes
        for (int i = 0; i < nodes.length / 2; i++) {
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.filledCircle(800 / half + 150 * i, adapt_y, 25);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(800 / half + 150 * i, adapt_y, (nodes[i].get_Node() + 1) + "");
            adapt_y = 150;
            if (i == half - 2) adapt_y = 225;
        }

        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.line(0, 300, 800, 300);

        // Bottom section of nodes
        adapt_y = 375;
        for (int i = 0; i < nodes.length / 2; i++) {
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.filledCircle(800 / half + 150 * i, adapt_y, 25);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(800 / half + 150 * i, adapt_y, (nodes[i + half].get_Node() + 1) + "");
            adapt_y = 450;
            if (i == half - 2) adapt_y = 375;
        }
    }
}
