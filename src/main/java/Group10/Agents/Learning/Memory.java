package Group10.Agents.Learning;

import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;
import Interop.Percept.Vision.ObjectPercepts;

import java.util.*;
import java.io.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Memory extends ArrayList {

    List<ObjectPercept> memory = new LinkedList<>();
    int agentID = this.hashCode();
    boolean isTargetReached = false;

    //add only TARGET and EMPTY.SPACES
    public Memory() {
        Point initPoint = new Point(0,0);
        ObjectPercept init = new ObjectPercept(ObjectPerceptType.EmptySpace, initPoint);
        memory.add(init);
    }

    public Memory(List<ObjectPercept> memory) {
        this.memory = memory;
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param o element to be appended to this list
     * @return {@code true}
     */
    public boolean add(ObjectPercept o) {
           // System.out.println(o + " has been added");
        if (o.getType() == ObjectPerceptType.TargetArea)isTargetReached=true;
        if (!this.contains(o)) return super.add(o);
            else System.out.println("Already in memory"); return  false;
    }

    /**
     *                  Returns the updated current location
     * @param currentLoc
     * @param o
     * @param currentDir
     * @return
     */
    public Point updateCurrentLoc(Point currentLoc, ObjectPercept o, Direction currentDir){
        Distance distance = findDistance(new Point(0,0), o.getPoint());
        double cos = Math.cos(currentDir.getRadians());
        double xbar = distance.getValue() * cos * -1;
        double sin = Math.sin(currentDir.getRadians());
        double ybar = distance.getValue() * sin;

        currentLoc = new Point(currentLoc.getX() + xbar,currentLoc.getY() + ybar);
        System.out.println("\n Current Loc: x: " + currentLoc.getX() + " y: " + currentLoc.getY());
        ObjectPercept memoryLoc = new ObjectPercept(ObjectPerceptType.EmptySpace,currentLoc);
        memory.add(memoryLoc);
        return  currentLoc;
    }

    public Direction updateCurrentDir(Direction currentDir, ObjectPercept o){
        Angle angle = Angle.fromRadians(findRelativeAngle(o.getPoint(), currentDir));
        currentDir = Direction.fromRadians((currentDir.getRadians()+angle.getRadians())%(2*Math.PI));
        return currentDir;
    }

    public double findRelativeAngle(Point point, Direction currentDir) {
        double theta =Math.atan2(point.getY(),point.getX()) - Math.PI/2; // to set direction North
        theta = (currentDir.getRadians() + theta) % (2*Math.PI);
        return theta;
    }

    public Distance findDistance(Point centerPt, Point targetPt){
        return new Distance(Math.sqrt((targetPt.getY()-centerPt.getY())*(targetPt.getY()-centerPt.getY()) + (targetPt.getX()-centerPt.getX()) * (targetPt.getX()-centerPt.getX())));
    }

    public Memory getMemory() {
        return this;
    }

    public void setTargetReached(boolean targetReached) {
        isTargetReached = targetReached;
    }

    public boolean isTargetReached() {
        return isTargetReached;
    }

    public static void exportToCsv(List<ObjectPercept> memory, String filename) {

        try (PrintWriter writer = new PrintWriter(new File(filename))) {

            StringBuilder sb = new StringBuilder();
            sb.append("Iteration, Point_X, Point_Y, Object Type\n");
            for (ObjectPercept o : memory) {
                sb.append(memory.indexOf(o));
                sb.append(",");
                sb.append(o.getPoint().getX());
                sb.append(",");
                sb.append(o.getPoint().getY());
                sb.append(",");
                sb.append(o.getType());
                sb.append("\n");
            }


            writer.write(sb.toString());


        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }


}
