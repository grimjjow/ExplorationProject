package Environment;

import AreaProperty.AreaProperty;
import Interop.Geometry.Point;

import java.awt.geom.Area;

public class Square {

    private Point topLeft;
    private Point bottomRight;

    private int x;
    private int y;
    private String type;
    private boolean walkable = true;
    private boolean explored = false;
    private AreaProperty areaProperty = null;

    public Square(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public AreaProperty getAreaProperty(){ return this.areaProperty;}

    public void setAreaProperty(AreaProperty areaProperty){ this.areaProperty = areaProperty; }

    public Square(Point topLeft, Point bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    public void checkExplored(Point point) {
        if (point.getX() >= topLeft.getX() && point.getX() <= bottomRight.getX()) {
            if (point.getY() <= topLeft.getY() && point.getY() >= bottomRight.getY()) {
                this.explored = true;
            }
        }
    }

    public boolean pointInSquare(Point point) {
        if (point.getX() >= topLeft.getX() && point.getX() <= bottomRight.getX()) {
            if (point.getY() <= topLeft.getY() && point.getY() >= bottomRight.getY()) {
                return true;
            }
        }
        return false;
    }

    public boolean getExplored() {
        return explored;
    }

    public void setExplored(boolean BOOL) {
        explored = BOOL;
    }

    public boolean getWalkable() {
        return walkable;
    }

    public void setWalkable(boolean BOOL) {
        walkable = BOOL;
    }

    public int getSX() {
        return x;
    }

    public int getSY() {
        return y;
    }

    public String getType() {
        return type;
    }

    public void setType(String typeName) {
        type = typeName;
    }

    public String toString() {
        return "Square{" +
                "type=" + type +
                ", x=" + x +
                ", y=" + y +
                ", walkable=" + walkable +
                '}';
    }

}
