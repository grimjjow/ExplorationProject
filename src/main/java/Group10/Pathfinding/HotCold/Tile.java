package Group10.Pathfinding.HotCold;

import java.util.LinkedList;

public class Tile {

    private double i;
    private double j;

    private double centerI;
    private double centerJ;

    private Tile top;
    private Tile bottom;
    private Tile left;
    private Tile right;
    private final static double size = 1;
    public static int totalTiles;
    private int exploredTemp;
    private boolean closed;

    private boolean wall = false;
    private boolean window = false;
    private boolean door = false;
    private boolean guard = false;
    private boolean sentryTower = false;
    private boolean target = false;
    private boolean teleport = false;

    public Tile(double i, double j) {
        top = null;
        bottom = null;
        left = null;
        right = null;
        setCenterI(i);
        setCenterJ(j);
        totalTiles++;
        closed = false;
    }

    public Tile() {

    }

    public LinkedList<Tile> getOpenTiles()
    {
        LinkedList<Tile> openTiles = new LinkedList<>();
        if (top == null)
        {
            addTop();
        }
        if (bottom == null)
        {
            addBottom();
        }
        if(left == null)
        {
            addLeft();
        }
        if(right == null)
        {
            addRight();
        }
        if (!top.getClosed())
        {
            openTiles.add(top);
        }
        if (!bottom.getClosed())
        {
            openTiles.add(bottom);
        }
        if (!left.getClosed())
        {
            openTiles.add(left);
        }
        if (!right.getClosed())
        {
            openTiles.add(right);
        }
        return openTiles;
    }

    public Tile findClosest(double x, double y) {

        x = Math.floor(x);
        y = Math.floor(y);
        Tile result = this;
        while(result.getCenterI() != x && result.getCenterJ() != y)
        {
            if(x > result.getCenterI())
            {
                if (result.getRight() != null)
                {
                    result = result.getRight();
                }
                else
                {
                    x = result.getI();
                }
            }
            else if(x < result.getCenterI())
            {
                if (result.getLeft() != null)
                {
                    result = result.getLeft();
                }
                else
                {
                    x = result.getCenterI();
                }
            }

            if(y > result.getCenterJ())
            {
                if (result.getBottom() != null)
                {
                    result = result.getBottom();
                }
                else
                {
                    y = result.getJ();
                }
            }
            else if(y < result.getCenterJ())
            {
                if (result.getTop() != null)
                {
                    result = result.getTop();
                }
                else
                {
                    y = result.getCenterJ();
                }
            }
        }
        return  result;
    }
    public boolean getClosed()
    {
        return closed;
    }
    public void setClosed(boolean b)
    {
        this.closed = b;
    }
    public void setTop(Tile top) {
        this.top = top;
    }

    public void setBottom(Tile bottom) {
        this.bottom = bottom;
    }

    public void setLeft(Tile left) {
        this.left = left;
    }

    public void setRight(Tile right) {
        this.right = right;
    }
    public Tile addTop() {
        Tile top = new Tile();
        setTop(top);
        top.setBottom(this);
        return top;
    }
    public Tile addBottom() {
        Tile bottom = new Tile();
        setBottom(bottom);
        bottom.setTop(this);
        return bottom;
    }
    public Tile addLeft() {
        Tile left = new Tile();
        setLeft(left);
        left.setRight(this);
        return left;
    }
    public Tile addRight() {
        Tile right = new Tile();
        setRight(right);
        right.setLeft(this);
        return right;
    }

    public void setCenterI(double centerI) {
        this.centerI = centerI;
        this.i = centerI - .5 * size;
    }

    public void setCenterJ(double centerJ) {
        this.centerJ = centerJ;
        this.j = centerJ - .5 * size;
    }

    public void setI(double i) {
        this.i = i;
        this.centerI = i + .5 * size;
    }

    public void setJ(double j) {
        this.j = j;
        this.centerJ = j + .5 * size;
    }

    public Tile getTop() {
        return top;
    }

    public Tile getBottom() {
        return bottom;
    }

    public Tile getLeft() {
        return left;
    }

    public Tile getRight() {
        return right;
    }

    public double getCenterI() {
        return centerI;
    }

    public double getCenterJ() {
        return centerJ;
    }

    public double getI() {
        return i;
    }

    public double getJ() {
        return j;
    }
    public PointNode[] getPoints() {
        PointNode[] points = {new PointNode(centerI + 0.5 * size, centerJ + 0.5 * size), new PointNode(centerI - 0.5 * size, centerJ + 0.5 * size),
                new PointNode(centerI - 0.5 * size, centerJ - 0.5 * size), new PointNode(centerI + 0.5 * size, centerJ - 0.5 * size)};
        return points;
    }
    public int getExploredTemp(){
        return exploredTemp;
    }

    public void addExploredTemp() {
        exploredTemp++;
    }

    public boolean hasWall(){
        return wall;
    }
    public boolean hasDoor(){
        return door;
    }

    public boolean hasWindow(){
        return window;
    }
    public boolean hasTower(){
        return sentryTower;
    }
    public boolean hasGuard(){
        return guard;
    }
    public boolean hasTarget(){
        return target;
    }

    public boolean hasTeleport(){
        return teleport;
    }

}
