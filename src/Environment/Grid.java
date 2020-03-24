package Environment;

import AreaProperty.AreaProperty;
import AreaProperty.Wall;
import GUI.TestGUI;


public class Grid {

    //Basic grid variables
    public int width;
    public int height;
    public float cellSize;
    private float[] originPosition = new float[3];

    //Matrix containing grid info
    public Square[][] gridArray;

    public Grid(int width, int height, float cellSize, float[] originPosition) {

        this.width = width;
        this.height = height;
        this.cellSize = cellSize;
        this.originPosition = originPosition;

        gridArray = new Square[width][height]; //Initializing gridArray with Squares

        for (int x = 0; x < gridArray.length; x++) {
            for (int y = 0; y < gridArray[0].length; y++) {
                gridArray[x][y] = new Square(x, y); //Filling gridarray with Squares
            }
        }

        for (AreaProperty ap : TestGUI.engine.readEnv.getProperties()) {
            //if Areaproperty is a wall
            if (ap instanceof Wall) {
                Wall w = (Wall) ap;
                //Loop that goes over all Xs and Ys in a wall, setting type of the square to "Wall"
                for (int x = (int) w.getP1().getX(); x < w.getP2().getX(); x++) {
                    for (int y = (int) w.getP1().getY(); y > w.getP3().getY(); y++) {
                        gridArray[x][y].setType("Wall");
                        gridArray[x][y].setWalkable(false);
                    }
                }

            }
        }

    }

    public float[] getWorldPosition(int x, int y) //Returns a vector(float[]) with position
    {
        return new float[]{x * cellSize + originPosition[0], y * cellSize + originPosition[1]};
    }

    public int getX(float[] worldPosition) //Returns X with position
    {
        return (int) ((worldPosition[0] - originPosition[0]) / cellSize);
    }

    public int getY(float[] worldPosition) //Returns Y with position
    {
        return (int) ((worldPosition[1] - originPosition[1]) / cellSize);
    }

    public void setSquare(int x, int y, Square value) //Set a Square using X and Y
    {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            gridArray[x][y] = value;
        }
    }

    public void setSquare(float[] worldPosition, Square value) //Set a Square using a vector(float[])
    {
        int x, y;
        x = getX(worldPosition);
        y = getY(worldPosition);
        setSquare(x, y, value);
    }

    public Square getSquare(int x, int y) //Get a Square using X and Y
    {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            return gridArray[x][y];
        } else {
            return null;
        }
    }

    public Square getSquare(float[] worldPosition) //Get a Square using a vector(float[])
    {
        int x, y;
        x = getX(worldPosition);
        y = getY(worldPosition);
        if (x >= 0 && y >= 0 && x < width && y < height) {
            return gridArray[x][y];
        } else {
            return null;
        }
    }
}