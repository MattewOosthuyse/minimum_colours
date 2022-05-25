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

    private int min_count = 10000;
    private int current_index = 0;
    private int global_depth = 0;
    private Node[] best;

    public static void main(String[] args) {
        //new Min_Colour(8, 5, 0);
        new Min_Colour(10, 30, 1);
        //new Min_Colour(40, 90, 2);
        //new Min_Colour(50, 150, 3);
        //new Min_Colour(60, 250, 4);
        //new Min_Colour(60, 500, 5);
        // System.out.println("A real life size problem as suggested in the document: 1500_60");
        // new Min_Colour(60, 1500, 6);
    }

    /**
     * Min colour that finds the minimum number of
     *
     * @param node_count   the number of nodes that the current iteration will use
     * @param colour_count the number of colours the current iteration will use
     * @param sheet_num    the sheet number that the data will be retrieved from
     */
    public Min_Colour(int node_count, int colour_count, int sheet_num) {


        // Initialising the colours
        Colour[] colours = new Colour[colour_count];
        for (int i = 0; i < colours.length; i++)
            colours[i] = new Colour();


        // Setting up the colours for the simple examples
//        colours[0].set_colour(StdDraw.BLUE);
//        colours[1].set_colour(StdDraw.RED);
//        colours[2].set_colour(StdDraw.GREEN);
//        colours[3].set_colour(StdDraw.ORANGE);
//        colours[4].set_colour(StdDraw.CYAN);


        //  Read in excel files
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File("data/data.xls"));

            HSSFWorkbook wb = null;
            try {
                wb = new HSSFWorkbook(fis);
                HSSFSheet sheet = wb.getSheetAt(sheet_num);

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


        // Doing some preprocessing
        // One way to do it - find the nodes that have the least colours, place them in one sector
        // Option 1:
        // get the biggest colour and divide it between the nodes.
        // get the smallest nodes and try

        // Pre-processing

        // Initialising the nodes
        Node[] nodes = new Node[node_count];
        for (int i = 0; i < nodes.length; i++)
            nodes[i] = new Node(i);

        // Go through the colours and set the colour count to the node objects
        for (int i = 1; i < colours.length; i++) {
            ArrayList<Integer> n = colours[i].get_adj_list();
            for (int j = 0; j < n.size(); j++) {
                nodes[n.get(j)].inc_colour_count();
            }
        }

        // Sort the nodes by the number of colours that go to them.
        // Thereafter - find the nodes that are part of the large colours and sort.
        for (int i = 0; i < nodes.length - 1; i++) {
            for (int j = i + 1; j < nodes.length; j++) {
                if (nodes[i].get_nodes_colour_count() < nodes[j].get_nodes_colour_count()) {
                    Node temp = nodes[i];
                    nodes[i] = nodes[j];
                    nodes[j] = temp;
                }
            }
        }

        best = nodes;
        min_count = evaluate_colour_cross(best, colours);

        swap(nodes, colours);


        StringBuilder sb = new StringBuilder();
        // sb.append("Time taken: ").append(s.elapsedTime()).append(" milli seconds\n");
        sb.append("Group 1: ");
        for (int i = 0; i < best.length; i++) {
            sb.append(best[i].get_Node() + 1);
            if (i == best.length / 2 - 1) {
                sb.append("\nGroup 2: ");
            } else if (i < best.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("\nMinimum colours cross: ").append(min_count).append("\n");
        System.out.println(sb);

        //draw_model(best, colours);

    }

    /**
     * An evaluation function that determines how many colours cross the midline
     *
     * @param nodes   the array of the nodes positions
     * @param colours the colours with their node lists
     * @return the number of colours that cross the midline
     */
    public int evaluate_colour_cross(Node[] nodes, Colour[] colours) {
        int count = 0;
        // Inside each colour, find which half of the array the nodes are situated
        for (Colour colour : colours) {
            ArrayList<Integer> n = colour.get_adj_list();
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
                        colour.set_sec(false);
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
    public void basic_strat(Node[] nodes, Colour[] colours) {
        for (Colour colour : colours) {
            if (!colour.one_sector()) {
                ArrayList<Integer> n = colour.get_adj_list();
                int sec_1 = 0, sec_2 = 0;
                for (int j = 0; j < n.size(); j++) {
                    for (int x = 0; x < nodes.length; x++)
                        if (nodes[x].get_Node() == n.get(j)) if (x < nodes.length / 2) sec_1++;
                        else sec_2++;
                }
                // After the sectors have been determined
                // get the difference in sectors for later evaluation
                int diff = Math.abs(sec_1 - sec_2);
                if (sec_1 == 0) colour.set_sec(true);
                else if (sec_2 == 0) colour.set_sec(true);
                else if (sec_1 > sec_2) colour.set_majority_sec(1);
                else if (sec_2 > sec_1) colour.set_majority_sec(2);
                colour.set_sec_diff(diff);
            }
        }

        // Get the colour that has the biggest % difference
        Colour c = null;
        int current_diff_percent = 0;
        for (Colour colour : colours) {
            // Get the colours diff %
            int percent = colour.diff_percent();
            if (current_diff_percent < percent) {
                c = colour;
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
     * Non-recursive swapping function
     */
    public void swap(Node[] nodes, Colour[] colours) {
        // This will keep track of the amount of times I do the test
        // Do not currently need this though
        Node[] temp = copy_nodes(nodes);
        int max_depth = 100000000;
        for (int z = 0; z < max_depth; z++) {
            int i = (int) (Math.random() * nodes.length / 2);
            int j = (int) (Math.random() * nodes.length / 2) + nodes.length / 2;
            // Add in the tracker here ?
            swap(temp, i, j);
            int current_count = evaluate_colour_cross(temp, colours);
            if (current_count < min_count) {
                min_count = current_count;
                best = temp;
            }
        }
    }

    public void advanced_solver(Node[] nodes, Colour[] colours, int i, int j) {

        Node[] temp = copy_nodes(nodes);
        swap(temp, i, j);
        int current_count = evaluate_colour_cross(nodes, colours);
        if (current_count < min_count) {
            min_count = current_count;
            best = temp;
        }

    }

    /**
     * Perform a simple swap function that swaps the nodes until a minimum is found
     * Recursive that searches the tree to find the best solution.
     *
     * @param nodes   The current state of the nodes
     * @param colours The object that stores the colours
     */
    public void simple_swap(Node[] nodes, Colour[] colours) {
        //  similar to minimax, perform an iteration and get the minimum
        // like minimax but wanting to constantly min.
        // go through all moves perform a swap, get the best outcome.
        // Need a copy nodes function to be able to store the best moves.
        Node[] current_best = new Node[nodes.length];
        for (int i = nodes.length / 2; i < nodes.length; i++) {
            Node[] temp;
            temp = copy_nodes(nodes);
            swap(temp, current_index, i);

            try {
                int current_count = evaluate_colour_cross(temp, colours);
                // Set the global best to the current best if it is better that the old best
                if (min_count > current_count) {
                    min_count = current_count;
                    best = temp;
                }
                // Check to see if the current index is at the end of the array
                if (current_index < nodes.length / 2) {
                    simple_swap(current_best, colours);
                } else if (global_depth < 100) {
                    global_depth++;
                    simple_swap(current_best, colours);
                }
            } catch (Exception e) {
                if (++current_index == nodes.length / 2) {
                    return;
                }
                global_depth = 0;
            }
        }


    }

    public void simple_swap_reverse(Node[] nodes, Colour[] colours) {
        //  similar to minimax, perform an iteration and get the minimum
        // like minimax but wanting to constantly min.
        // go through all moves perform a swap, get the best outcome.
        // Need a copy nodes function to be able to store the best moves.
        Node[] current_best = new Node[nodes.length];
        int current_min = 10000;
        for (int i = 0; i < nodes.length / 2; i++) {
            Node[] temp = copy_nodes(nodes);
            swap(temp, --current_index, i + nodes.length / 2);
            int current_count = evaluate_colour_cross(temp, colours);
            if (current_min > current_count) {
                current_min = current_count;
                current_best = temp;
            }
        }

        // Set the global best to the current best if it is better that the old best
        if (min_count > current_min) {
            min_count = current_min;
            best = current_best;
        }

        // Check to see if the current index is at the end of the array
        if (current_index < 0) {
            simple_swap(current_best, colours);
        } else if (global_depth < 16) {
            // set current index to the beginning and try again
            current_index = nodes.length / 2;
            global_depth++;
            simple_swap(current_best, colours);
        }
    }

    public void simple_swap_d_c(Node[] nodes, Colour[] colours) {
        //  similar to minimax, perform an iteration and get the minimum
        // like minimax but wanting to constantly min.
        // go through all moves perform a swap, get the best outcome.
        // Need a copy nodes function to be able to store the best moves.
        Node[] current_best = new Node[nodes.length];
        int current_min = 10000;
        for (int i = nodes.length - 1; i >= 0; i--) {
            Node[] temp = copy_nodes(nodes);
            swap(temp, current_index++, i);
            int current_count = evaluate_colour_cross(temp, colours);
            if (current_min > current_count) {
                current_min = current_count;
                current_best = temp;
            }
        }

        // Set the global best to the current best if it is better that the old best
        if (min_count > current_min) {
            min_count = current_min;
            best = current_best;
        }

        // Check to see if the current index is at the end of the array
        if (current_index < nodes.length) {
            simple_swap(current_best, colours);
        } else if (global_depth < 16) {
            // set current index to the beginning and try again
            current_index = 0;
            global_depth++;
            simple_swap(current_best, colours);
        }
    }


    public void brute(Node[] nodes, Colour[] colours) {
        int current_min = 1000;
        Node[] current_best = new Node[nodes.length];
        for (int i = 0; i < nodes.length / 2; i++) {
            for (int j = nodes.length - 1; j > nodes.length / 2; j--) {
                Node[] temp = copy_nodes(nodes);
                swap(temp, i, j);
                int current_count = evaluate_colour_cross(temp, colours);
                if (current_count < current_min) {
                    current_best = temp;
                    current_min = current_count;
                }
            }
        }
        if (current_min < min_count) {
            best = current_best;
            min_count = current_min;
        }
    }

    public Node[] copy_nodes(Node[] nodes) {
        Node[] temp = new Node[nodes.length];
        System.arraycopy(nodes, 0, temp, 0, nodes.length);
        return temp;
    }

    /**
     * Function that swaps two nodes in the list
     *
     * @param nodes array of the nodes
     * @param x     node pos to be swapped
     * @param y     node pos to be swapped
     */
    public void swap(Node[] nodes, int x, int y) {
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
    public void draw_model(Node[] nodes, Colour[] colours) {
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


    }

    /**
     * This draws the colours on the canvas
     *
     * @param nodes   array of the nodes
     * @param colours array of the colours
     */
    public void draw_colours(Node[] nodes, Colour[] colours) {
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
                    if (nodes[x].get_Node() == n.get(j)) for (int k = 0; k < n.size(); k++)
                        for (int y = 0; y < nodes.length; y++)
                            if (nodes[y].get_Node() == n.get(k)) {
                                int adapt_x;
                                int adapt_y;
                                // Find whether x and y are at the top or bottom half
                                // Finding x
                                if (x < nodes.length / 2) {
                                    if (x == 0 || x == nodes.length / 2 - 1) adapt_x = 225;
                                    else adapt_x = 150;
                                } else {
                                    if (x - half == 0 || x - half == nodes.length / 2 - 1) adapt_x = 375;
                                    else adapt_x = 450;
                                }
                                // Finding y
                                if (y < nodes.length / 2) {
                                    if (y == 0 || y == nodes.length / 2 - 1) adapt_y = 225;
                                    else adapt_y = 150;
                                } else {
                                    if (y - half == 0 || y - half == nodes.length / 2 - 1) adapt_y = 375;
                                    else adapt_y = 450;
                                }

                                // have the nodes that are in the bottom half first
                                if (x < nodes.length / 2 && y < nodes.length / 2)
                                    StdDraw.line(800 / half + 150 * x, adapt_x, 800 / half + 150 * y, adapt_y);
                                else if (x < half)
                                    StdDraw.line(800 / half + 150 * x, adapt_x, 800 / half + 150 * (y - half), adapt_y);
                                else if (y < half)
                                    StdDraw.line(800 / half + 150 * (x - half), adapt_x, 800 / half + 150 * y, adapt_y);
                                else
                                    StdDraw.line(800 / half + 150 * (x - half), adapt_x, 800 / half + 150 * (y - half), adapt_y);
                            }
            }
        }
    }

    /**
     * Draws the nodes on the canvas using StdDraw
     *
     * @param nodes   list of the nodes to be drawn
     * @param colours list of the colour objects with their properties
     */
    public void draw_nodes(Node[] nodes, Colour[] colours) {
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
