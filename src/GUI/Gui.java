package GUI;

import Agents.Container.GuardContainer;
import Agents.Container.IntruderContainer;
import World.Dynamic.DynamicObject;
import javafx.application.Application;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class Gui extends Application {
    private File mapFile = new File("src/World/Maps/test_1.map");
    private MainController mainController = new MainController(this,mapFile,true);
    private MainScene scene = new MainScene(new StackPane(), mainController.getGame().getGameMap(),this);
    private Stage primary = new Stage();

    public static void Gui(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setHeight(GuiSettings.defaultHeight);
        primaryStage.setWidth(GuiSettings.defaultWidth);
        primaryStage.setTitle("Guards vs Intruders");
        primaryStage.setScene(scene);
        primary = primaryStage;
        primaryStage.show();
        scene.rescale();
        Thread thread = new Thread(mainController);
        thread.start();
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("See Ya");
            mainController.kill();
        });
    }


    public void drawMovables(List<GuardContainer> guards, List<IntruderContainer> intruders, List<DynamicObject<?>> objects){
        scene.drawMovables(guards, intruders, objects);
    }
    public void activateHistory(){
        if(!scene.isHasHistory()){
            scene.activateHistory();
        }
    }
    public Stage getPrimary() {
        return primary;
    }

    public MainController getMainController() {
        return mainController;
    }
    public void restartGame(boolean generateHistory){
        mainController.kill();
        mainController = new MainController(this,mapFile, generateHistory);
        Thread thread = new Thread(mainController);
        thread.start();
    }

    public void setMapFile(File mapFile) {
        this.mapFile = mapFile;
    }

    public File getMapFile() {
        return mapFile;
    }
}
