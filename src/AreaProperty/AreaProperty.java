package AreaProperty;

import Interop.Geometry.Point;

public class AreaProperty {

    private Point topLeft;
    private Point topRight;
    private Point botLeft;
    private Point botRight;

    public AreaProperty(Point topLeft, Point topRight, Point bottomLeft, Point bottomRight) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.botLeft = bottomLeft;
        this.botRight = bottomRight;
    }

    public Point getTopLeft() {
        return topLeft;
    }

    public void setP1(Point p1) {
        this.topLeft = p1;
    }

    public Point getTopRight() {
        return topRight;
    }

    public void setTopRight(Point topRight) {
        this.topRight = topRight;
    }

    public Point getBotLeft() {
        return botLeft;
    }

    public void setBotLeft(Point botLeft) {
        this.botLeft = botLeft;
    }

    public Point getBotRight() {
        return botRight;
    }

    public void setBotRight(Point botRight) {
        this.botRight = botRight;
    }
}
