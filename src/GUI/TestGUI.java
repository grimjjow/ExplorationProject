package GUI;

import Engine.GameEngine;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

public class TestGUI extends Application {

    public static GameEngine engine;
    String path;
    public void start(Stage primaryStage) throws Exception {
        try {

            Button startButton = new Button("Start");
            //Button pauseButton = new Button("Pause");

            BorderPane pane = new BorderPane();
            pane.setStyle("-fx-background-color: white ;");
            pane.setPadding(new Insets(20,50,50,20));
            pane.setRight(startButton);
            //pane.setRight(pauseButton);


            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            try {
                File f = new File("/Users/bertrand/IdeaProjects/ExplorationProject/src/testenv2.txt");
                String file = f.toString();
                this.path = file;
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }

            double envWidth = bounds.getWidth();
            double envHeight = (bounds.getHeight()*9)/10;
            GameEngine engine = new GameEngine(this.path);
            engine.setHeightBound(envHeight);
            engine.setWidthBound(envWidth);
            engine.createEnvironment(path);
            pane.setCenter(engine.getEnvPane());
            Scene scene = new Scene(pane, bounds.getWidth(), bounds.getHeight());
            primaryStage.setScene(scene);
            primaryStage.show();
            engine.update();


        } catch (Exception e) {
            System.out.println("Exception in creating GUI " + e.getMessage() + " " + Arrays.toString(e.getStackTrace()));
        }
    }
}
