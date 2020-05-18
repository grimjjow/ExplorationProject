package Group10.Pathfinding.HotCold;

public class PointNode {

    private double i;
    private double j;

    public PointNode(double x, double y) {
        this.i = x;
        this.j = y;
    }

    public double getI()
    {
        return i;
    }

    public double getJ() {
        return j;
    }
    public boolean equals(PointNode n) {

        if(i == n.getI() && j == n.getJ())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
