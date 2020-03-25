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
        Reader readEnv = new Reader(path);
        this.gameInfo = readEnv.getInfo();
        this.scalingFactor = getScalingFactor(screenWidth, screenHeight, readEnv.getInfo().getWidth() , readEnv.getInfo().getHeight());

        this.initialProperties = readEnv.getProperties();
    }

    public double getWidth() {	return width; }
    public double getHeight() { return height; }

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
