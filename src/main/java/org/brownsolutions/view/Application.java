package org.brownsolutions.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws Exception {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("Application.fxml"));
            Pane layout = fxmlLoader.load();
            Scene scene = new Scene(layout, 264.0, 216.0);
            stage.setResizable(false);
            stage.initStyle(StageStyle.DECORATED);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
