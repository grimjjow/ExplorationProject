package Environment;

import Interop.Geometry.Point;

public class Square {

    private Point topLeft;
    private Point bottomRight;
    private boolean explored = false;

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
}
