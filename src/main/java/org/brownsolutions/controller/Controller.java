package org.brownsolutions.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.brownsolutions.data.DataProcessor;
import org.brownsolutions.model.FileChooserType;
import org.brownsolutions.utils.AlertPane;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class Controller {

    private final String exampleFilePath = System.getProperty("user.home") + "\\Documents\\PresumedCalculation\\example.xlsx";

    private DataProcessor dataProcessor;

    public Button generateButton;
    public Button saveButton;
    public Button selectButton;
    public Button templateButton;

    public void onGenerateButtonClick(ActionEvent actionEvent) {
        if (!templateFileExists()) {
            AlertPane.showAlert("Erro!", "Não existe arquivo template para iniciar o processo...!");
            return;
        }

        new Thread(dataProcessor).start();
        selectButton.setDisable(true);
        generateButton.setDisable(true);
        templateButton.setDisable(true);
    }

    public void onSaveButtonClick(ActionEvent actionEvent) {
        File file = (File) showFileDialog(actionEvent, FileChooserType.SAVE);
        if (file != null) {
            dataProcessor.save(file.getPath());
        }
    }

    public void onSelectButtonClick(ActionEvent actionEvent) {
        List<?> files = (List<?>) showFileDialog(actionEvent, FileChooserType.OPEN_MULTIPLE);
        if (files != null && !files.isEmpty()) {
            dataProcessor = new DataProcessor(generateButton, saveButton, selectButton,
                    templateButton);
            files.forEach(dataProcessor::addToQueue);
            generateButton.setDisable(false);
        }
    }

    public void onTemplateButtonClick(ActionEvent actionEvent) {
        String exampleFilePath = System.getProperty("user.home") + "\\Documents\\PresumedCalculation";
        File exampleFileDir = new File(exampleFilePath);

        if (!exampleFileDir.exists()) exampleFileDir.mkdirs();

        File file = (File) showFileDialog(actionEvent, FileChooserType.OPEN);
        if (file != null) {
            Path destination = Paths.get(exampleFilePath + "\\example.xlsx");

            try {
                Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save template file!", e);
            }
        }
    }

    private boolean templateFileExists() {
        return new File(exampleFilePath).exists();
    }

    private Stage getStage(Event event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    private Object showFileDialog(Event event, FileChooserType type) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(type.getTitle());
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos Excel", "*.xlsx", "*.xls"));

        Stage stage = getStage(event);

        return switch (type) {
            case OPEN -> fileChooser.showOpenDialog(stage);
            case OPEN_MULTIPLE -> fileChooser.showOpenMultipleDialog(stage);
            case SAVE -> fileChooser.showSaveDialog(stage);
        };
    }
}
