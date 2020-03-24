package AreaProperty;

import Interop.Geometry.Point;

public class Wall extends AreaProperty {
    public Wall(Point topLeft, Point topRight, Point bottomLeft, Point bottomRight) {
        super(topLeft, topRight, bottomLeft, bottomRight);
    }
}
