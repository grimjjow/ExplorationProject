package Environment;

import java.util.ArrayList;

import AreaProperty.AreaProperty;
import Engine.GameInfo;
import Reader.Reader;

public class Environment {

    public static double width;
    public static double height;
    public final double scalingFactor;
    private ArrayList<AreaProperty> initialProperties;
    private GameInfo gameInfo;


    public String path;

    public Environment(String path, double screenWidth, double screenHeight) {
        this.path = path;
        Reader env = new Reader(path);
        this.gameInfo = env.getInfo();
        this.scalingFactor = getScalingFactor(screenWidth, screenHeight, env.getInfo().getWidth() , env.getInfo().getHeight());

        this.height = env.getInfo().getHeight()*scalingFactor;
        this.width = env.getInfo().getWidth()*scalingFactor;

        this.initialProperties = env.getProperties();
    }

    public double getWidth() {	return this.width; }
    public double getHeight() { return this.height; }

    public double getScalingFactor(double screenWidth, double screenHeight, double mapWidth, double mapHeight) {
        double scalingFactor;
        double heightFactor = screenHeight/mapHeight;
        double widthFactor = screenWidth/mapWidth;
        if(heightFactor >= widthFactor) {
            scalingFactor = widthFactor;
        }else {
            scalingFactor = heightFactor;
        }
        return scalingFactor;
    }
    public double getScalingFactor() {	return this.scalingFactor;	}
    public ArrayList<AreaProperty> getInitialProperties() {	return this.initialProperties;}

    public GameInfo getGameInfo() {
        return gameInfo;
    }
}
