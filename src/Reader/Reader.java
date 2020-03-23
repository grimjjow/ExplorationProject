package Reader;


import Interop.Geometry.Point;
import Engine.GameInfo;
import AreaProperty.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Reader {

    private GameInfo gameInfo = new GameInfo();
    private ArrayList<AreaProperty> properties = new ArrayList<AreaProperty>();
    private SpawnGuards spawnGuards;
    private Shade shaded;
    private Wall wall;
    private Teleport teleport;
    private Window window;
    private Sentry sentry;
    private Door door;
    private String split[];
    private String doubleSplit[];

    public Reader(String path) {

        //basic reader stuff
        java.io.Reader reader = null;
        try {
            reader = new FileReader(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader buffReader = new BufferedReader(reader);

        //read the first line, so it is possible to enter the loop
        String inputLine = null;
        try {
            inputLine = buffReader.readLine();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        //here, call the storage class while needed and store the objects as we want to
        while (inputLine != null) {
            try {
                split = inputLine.split("=");
                for (int i = 0; i < split.length; i++) {
                    split[i] = split[i].trim();
                }
                switch (split[0]) {
                    case "gameMode":
                        gameInfo.setGameMode(Integer.parseInt(split[1]));
                        break;

                    case "height":
                        gameInfo.setHeight(Double.parseDouble(split[1]));
                        break;

                    case "width":
                        gameInfo.setWidth(Double.parseDouble(split[1]));
                        break;

                    case "numGuards":
                        gameInfo.setNumGuards(Integer.parseInt(split[1]));
                        break;

                    case "numIntruders":
                        gameInfo.setNumIntruders(Integer.parseInt(split[1]));
                        break;

                    case "captureDistance":
                        gameInfo.setCaptureDistance(Double.parseDouble(split[1]));
                        break;

                    case "winConditionIntruderRounds":
                        gameInfo.setWinConditionIntruderRounds(Integer.parseInt(split[1]));
                        break;

                    case "maxRotationAngle":
                        gameInfo.setMaxRotationAngle(Double.parseDouble(split[1]));
                        break;

                    case "maxMoveDistanceIntruder":
                        gameInfo.setMaxMoveDistanceIntruder(Double.parseDouble(split[1]));
                        break;

                    case "maxSprintDistanceIntruder":
                        gameInfo.setMaxSprintDistanceIntruder(Double.parseDouble(split[1]));
                        break;

                    case "maxMoveDistanceGuard":
                        gameInfo.setMaxMoveDistanceGuard(Double.parseDouble(split[1]));
                        break;

                    case "sprintCooldown":
                        gameInfo.setSprintCoolDown(Integer.parseInt(split[1]));
                        break;

                    case "pheromoneCooldown":
                        gameInfo.setPheromoneCoolDown(Integer.parseInt(split[1]));
                        break;

                    case "radiusPheromone":
                        gameInfo.setRadiusPheromone(Double.parseDouble(split[1]));
                        break;

                    case "slowDownModifierWindow":
                        gameInfo.setSlowDownModifierWindow(Double.parseDouble(split[1]));
                        break;

                    case "slowDownModifierDoor":
                        gameInfo.setSlowDownModifierDoor(Double.parseDouble(split[1]));
                        break;

                    case "slowDownModifierSentryTower":
                        gameInfo.setSlowDownModifierSentryTower(Double.parseDouble(split[1]));
                        break;

                    case "viewAngle":
                        gameInfo.setViewAngle(Double.parseDouble(split[1]));
                        break;

                    case "viewRays":
                        gameInfo.setViewRays(Integer.parseInt(split[1]));
                        break;

                    case "viewRangeIntruderNormal":
                        gameInfo.setViewRangeIntruderNormal(Double.parseDouble(split[1]));
                        break;

                    case "viewRangeIntruderShaded":
                        gameInfo.setViewRangeGuardShaded(Double.parseDouble(split[1]));
                        break;

                    case "viewRangeGuardNormal":
                        gameInfo.setViewRangeGuardNormal(Double.parseDouble(split[1]));
                        break;

                    case "viewRangeGuardShaded":
                        gameInfo.setViewRangeGuardShaded(Double.parseDouble(split[1]));
                        break;

                    case "viewRangeSentry":
                        doubleSplit = split[1].split(",");
                        for (int i = 0; i < doubleSplit.length; i++) {
                            doubleSplit[i].trim();
                        }
                        gameInfo.setViewRangeSentryShort(Double.parseDouble(doubleSplit[0]));
                        gameInfo.setViewRangeSentryLong(Double.parseDouble(doubleSplit[1]));
                        break;

                    case "yellSoundRadius":
                        gameInfo.setYellSoundRadius(Double.parseDouble(split[1]));
                        break;

                    case "maxMoveSoundRadius":
                        gameInfo.setMaxMoveSoundRadius(Double.parseDouble(split[1]));
                        break;

                    case "windowSoundRadius":
                        gameInfo.setWindowSoundRadius(Double.parseDouble(split[1]));
                        break;

                    case "doorSoundRadius":
                        gameInfo.setDoorSoundRadius(Double.parseDouble(split[1]));
                        break;

                    case "targetArea":
                        break;

                    case "spawnAreaIntruders":
                        break;

                    case "spawnAreaGuards":
                        doubleSplit = split[1].split(",");
                        for (int i = 0; i < doubleSplit.length; i++) {
                            doubleSplit[i] = doubleSplit[i].trim();
                        }
                        spawnGuards = new SpawnGuards(new Point(Double.parseDouble(doubleSplit[0]), Double.parseDouble(doubleSplit[1])), new Point(Double.parseDouble(doubleSplit[2]), Double.parseDouble(doubleSplit[3])), new Point(Double.parseDouble(doubleSplit[4]), Double.parseDouble(doubleSplit[5])), new Point(Double.parseDouble(doubleSplit[6]), Double.parseDouble(doubleSplit[7])));
                        properties.add(spawnGuards);
                        break;

                    case "wall":
                        doubleSplit = split[1].split(",");
                        for (int i = 0; i < doubleSplit.length; i++) {
                            doubleSplit[i] = doubleSplit[i].trim();
                        }
                        wall = new Wall(new Point(Double.parseDouble(doubleSplit[0]), Double.parseDouble(doubleSplit[1])), new Point(Double.parseDouble(doubleSplit[2]), Double.parseDouble(doubleSplit[3])), new Point(Double.parseDouble(doubleSplit[4]), Double.parseDouble(doubleSplit[5])), new Point(Double.parseDouble(doubleSplit[6]), Double.parseDouble(doubleSplit[7])));
                        properties.add(wall);
                        break;

                    case "teleport":
                        doubleSplit = split[1].split(",");
                        for (int i = 0; i < doubleSplit.length; i++) {
                            doubleSplit[i] = doubleSplit[i].trim();
                        }
                        teleport = new Teleport(new Point(Double.parseDouble(doubleSplit[0]), Double.parseDouble(doubleSplit[1])), new Point(Double.parseDouble(doubleSplit[2]), Double.parseDouble(doubleSplit[3])), new Point(Double.parseDouble(doubleSplit[4]), Double.parseDouble(doubleSplit[5])), new Point(Double.parseDouble(doubleSplit[6]), Double.parseDouble(doubleSplit[7])), new Point(Double.parseDouble(doubleSplit[8]), Double.parseDouble(doubleSplit[9])));
                        properties.add(teleport);
                        break;

                    case "shaded":
                        doubleSplit = split[1].split(",");
                        for (int i = 0; i < doubleSplit.length; i++) {
                            doubleSplit[i] = doubleSplit[i].trim();
                        }
                        shaded = new Shade(new Point(Double.parseDouble(doubleSplit[0]), Double.parseDouble(doubleSplit[1])), new Point(Double.parseDouble(doubleSplit[2]), Double.parseDouble(doubleSplit[3])), new Point(Double.parseDouble(doubleSplit[4]), Double.parseDouble(doubleSplit[5])), new Point(Double.parseDouble(doubleSplit[6]), Double.parseDouble(doubleSplit[7])));
                        properties.add(shaded);
                        break;

                    case "door":
                        doubleSplit = split[1].split(",");
                        for (int i = 0; i < doubleSplit.length; i++) {
                            doubleSplit[i] = doubleSplit[i].trim();
                        }
                        door = new Door(new Point(Double.parseDouble(doubleSplit[0]), Double.parseDouble(doubleSplit[1])), new Point(Double.parseDouble(doubleSplit[2]), Double.parseDouble(doubleSplit[3])), new Point(Double.parseDouble(doubleSplit[4]), Double.parseDouble(doubleSplit[5])), new Point(Double.parseDouble(doubleSplit[6]), Double.parseDouble(doubleSplit[7])));
                        properties.add(door);
                        break;

                    case "window":
                        doubleSplit = split[1].split(",");
                        for (int i = 0; i < doubleSplit.length; i++) {
                            doubleSplit[i] = doubleSplit[i].trim();
                        }
                        window = new Window(new Point(Double.parseDouble(doubleSplit[0]), Double.parseDouble(doubleSplit[1])), new Point(Double.parseDouble(doubleSplit[2]), Double.parseDouble(doubleSplit[3])), new Point(Double.parseDouble(doubleSplit[4]), Double.parseDouble(doubleSplit[5])), new Point(Double.parseDouble(doubleSplit[6]), Double.parseDouble(doubleSplit[7])));
                        properties.add(window);
                        break;

                    case "sentry":
                        doubleSplit = split[1].split(",");
                        for (int i = 0; i < doubleSplit.length; i++) {
                            doubleSplit[i] = doubleSplit[i].trim();
                        }
                        sentry = new Sentry(new Point(Double.parseDouble(doubleSplit[0]), Double.parseDouble(doubleSplit[1])), new Point(Double.parseDouble(doubleSplit[2]), Double.parseDouble(doubleSplit[3])), new Point(Double.parseDouble(doubleSplit[4]), Double.parseDouble(doubleSplit[5])), new Point(Double.parseDouble(doubleSplit[6]), Double.parseDouble(doubleSplit[7])));
                        properties.add(sentry);
                        break;

                    default:
                        System.out.println(split[0] + " has no been read correctly.");
                        break;
                }


                inputLine = buffReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GameInfo getInfo() {
        return gameInfo;
    }

    public ArrayList<AreaProperty> getProperties() {
        return properties;
    }
}
