package Environment;

import Interop.Geometry.Point;

public class Square {

    private Point topLeft;
    private Point bottomRight;

    private int x;
    private int y;
    private String type;
    private boolean walkable = true;
    private boolean explored = false;

    public Square(int x, int y) {
        this.x = x;
        this.y = y;
    }

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

    public boolean getVisited() {
        return explored;
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

}
