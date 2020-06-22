package Group10.GUI;

import Group10.Agents.Container.GuardContainer;
import Group10.Agents.Container.IntruderContainer;
import Group10.World.Dynamic.DynamicObject;
import javafx.application.Application;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class Gui extends Application {
    private File mapFile = new File("src/main/java/Group10/World/Maps/open.map");
    private Controller controller = new Controller(this,mapFile,true);
    private MainScene scene = new MainScene(new StackPane(), controller.getGame().getGameMap(),this);
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
        Thread thread = new Thread(controller);
        thread.start();
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Game over");
            controller.kill();
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

    public Controller getController() {
        return controller;
    }
    public void restartGame(boolean generateHistory){
        controller.kill();
        controller = new Controller(this,mapFile, generateHistory);
        Thread thread = new Thread(controller);
        thread.start();
    }

    public void setMapFile(File mapFile) {
        this.mapFile = mapFile;
    }

    public File getMapFile() {
        return mapFile;
    }
}
