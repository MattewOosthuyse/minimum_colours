import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;


public class Min_Colour {

    public static void main(String[] args) {

        // TODO: Read in excel files
        FileInputStream fis = new FileInputStream(new File("../data/data_file.xlsx"));
        // TODO: Create records for the nodes
        // TODO: The nodes need an adjacency list
        // TODO: Each node has a colour assigned to it
    }
}
