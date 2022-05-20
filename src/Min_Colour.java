import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


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
            System.out.println("Got to the other side of the file reading in");

            HSSFWorkbook wb = null;
            try {
                wb = new HSSFWorkbook(fis);
                HSSFSheet sheet = wb.getSheetAt(0);

                FormulaEvaluator formula_eval = wb.getCreationHelper().createFormulaEvaluator();

                for (Row row : sheet) {
                    for (Cell cell : row) {
                        if (cell.getNumericCellValue() == 1) {
                            // Add the node to the colours adj list
                            colours[row.getRowNum()].add_node(cell.getColumnIndex() + 1);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        } catch (FileNotFoundException e) {
            System.out.println("the file was not found or there was an error when reading the file");
        }


        // Printing the values to test if the input is correct
        for (int i = 0; i < colours.length; i++)
            System.out.println(colours[i].get_adj_list());

        // TODO Have an evaluation function that determines how many colours cross the midpoint
        //
        // TODO: Each node has a colour assigned to it
    }
}
