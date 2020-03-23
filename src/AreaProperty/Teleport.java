package AreaProperty;

import Interop.Geometry.Point;

public class Teleport extends AreaProperty{

    private Point teleportTo;

    public Teleport(Point upperleft, Point upperright, Point bottomleft, Point bottomright, Point telep) {
        super(upperleft, upperright, bottomleft, bottomright);
        this.setTeleportTo(telep);
    }

    public Point getTeleportTo() {
        return teleportTo;
    }

    public void setTeleportTo(Point teleportTo) {
        this.teleportTo = teleportTo;
    }
}
