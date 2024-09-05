package org.brownsolutions.data;

import com.aspose.cells.*;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import org.brownsolutions.model.Line;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataProcessor implements Runnable {

    private final ArrayList<Line> remainingLines = new ArrayList<>();
    private Workbook workbook;

    // UI Elements
    private final Button generateButton;
    private final Button saveButton;
    private final Button selectFilesButton;
    private final Button exampleFileButton;

    // Queue for processing files
    private final FileQueue queue;

    // Constructor
    public DataProcessor(Button generateButton, Button saveButton,
                         Button selectFilesButton, Button exampleFileButton) {
        this.queue = new FileQueue();
        this.generateButton = generateButton;
        this.saveButton = saveButton;
        this.selectFilesButton = selectFilesButton;
        this.exampleFileButton = exampleFileButton;
    }

    // Adds a file to the queue for processing
    public void addToQueue(Object o) {
        queue.enqueue(((File) o).getPath());
    }

    // Main processing method
    private void process() {
        loadWorkbook();

        while (!queue.isEmpty()) {
            String filePath = queue.dequeue();

            if (filePath != null) {
                HashMap<Integer, HashMap<Integer, Line>> lines = new DataReader().execute(filePath);
                updateWorksheet(lines);
            }
        }
    }

    // Updates the worksheet with the processed data
    private void updateWorksheet(HashMap<Integer, HashMap<Integer, Line>> lines) {
        Worksheet sheet = workbook.getWorksheets().get(0);
        Cells cells = sheet.getCells();
        int maxDataRow = cells.getMaxDataRow();

        String cnpj = "";
        HashMap<Integer, Line> branchLines = null;

        for (int i = 1; i <= maxDataRow; i++) {
            Cell cell = cells.get(i, 0);
            if (cell == null) continue;

            String currentCNPJ = cell.getStringValue();

            if (!cnpj.equals(currentCNPJ)) {
                if (branchLines != null && !branchLines.isEmpty()) {
                    for (Map.Entry<Integer, Line> entry : branchLines.entrySet()) {
                        entry.getValue().setCnpj(cnpj);
                        remainingLines.add(entry.getValue());
                    }
                }

                cnpj = currentCNPJ;
                int branch = getBranchFromCNPJ(cnpj);
                if (branch == 0) continue;
                branchLines = lines.get(branch);
            }

            if (branchLines == null) continue;

            int cfop = cells.get(i, 3).getIntValue();

            Line line = branchLines.get(cfop);

            if (line != null) {
                cells.get(i, 6).setValue(line.getTotal());
                cells.get(i, 7).setValue(line.getBase());
                cells.get(i, 8).setValue(line.getIcms());

                branchLines.remove(cfop);
            }
        }

        addRemainingLinesToWorksheet(cells, maxDataRow);
        remainingLines.clear();
    }

    // Adds remaining lines to the worksheet
    private void addRemainingLinesToWorksheet(Cells cells, int currentRow) {
        for (Line entry : remainingLines) {
            cells.get(currentRow, 0).setValue(entry.getCnpj());
            cells.get(currentRow, 3).setValue(entry.getCfop());
            cells.get(currentRow, 4).setValue(entry.getDescription());
            cells.get(currentRow, 6).setValue(entry.getTotal());
            cells.get(currentRow, 7).setValue(entry.getBase());
            cells.get(currentRow, 8).setValue(entry.getIcms());

            setCellColor(cells.get(currentRow, 0), Color.getRed());
            setCellColor(cells.get(currentRow, 3), Color.getRed());
            setCellColor(cells.get(currentRow, 4), Color.getRed());
            setCellColor(cells.get(currentRow, 6), Color.getRed());
            setCellColor(cells.get(currentRow, 7), Color.getRed());
            setCellColor(cells.get(currentRow, 8), Color.getRed());

            currentRow++;
        }
    }

    // Sets the color of a cell
    private void setCellColor(Cell cell, Color color) {
        Style style = cell.getStyle();
        style.setForegroundColor(color);
        style.setPattern(BackgroundType.SOLID);
        cell.setStyle(style);
    }

    // Retrieves the branch code based on the CNPJ
    private int getBranchFromCNPJ(String cnpj) {
        return switch (cnpj) {
            case "86900925/0001-04" -> 1000;
            case "86900925/0003-76" -> 1102;
            case "86900925/0004-57" -> 1003;
            case "86900925/0005-38" -> 1004;
            case "86900925/0006-19" -> 1005;
            case "86900925/0008-80" -> 1107;
            case "86900925/0010-03" -> 1209;
            case "86900925/0011-86" -> 1010;
            case "86900925/0012-67" -> 1011;
            case "86900925/0013-48" -> 1112;
            default -> 0;
        };
    }

    // Loads the workbook from a predefined path
    private void loadWorkbook() {
        String filePath = System.getProperty("user.home") + "\\Documents\\PresumedCalculation\\example.xlsx";
        try (InputStream inputStream = new FileInputStream(filePath)) {
            workbook = new Workbook(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Error loading workbook", e);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing Aspose Cells Workbook", e);
        }
    }

    // Saves the workbook to the specified file path
    public void save(String filePath) {
        try {
            System.out.println(remainingLines.size());
            workbook.calculateFormula();
            workbook.save(filePath, SaveFormat.XLSX);

            exampleFileButton.setDisable(false);
            selectFilesButton.setDisable(false);
            generateButton.setVisible(true);
            saveButton.setVisible(false);

        } catch (IOException e) {
            throw new RuntimeException("Error saving workbook", e);
        } catch (Exception e) {
            throw new RuntimeException("Error saving Aspose Cells Workbook", e);
        }
    }

    @Override
    public void run() {
        try {
            process();
            Thread.sleep(5000);
            updateUIOnCompletion();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Updates the UI elements upon completion of processing
    private void updateUIOnCompletion() {
        generateButton.setVisible(false);
        saveButton.setVisible(true);
    }
}
