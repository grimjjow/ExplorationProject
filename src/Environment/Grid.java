package Environment;

import AreaProperty.AreaProperty;
import AreaProperty.*;
import Reader.Reader;


public class Grid {

    // Grid Attributes
    private int width;
    private int height;
    private float cellSize;
    private float[] originPosition;
    public Square[][] gridArray;

    /**
     * @param width width of the grid (= width of the game)
     * @param height height of the grid (= width of the game)
     * @param cellSize
     * @param originPosition
     * @param readEnv reader of the file, use to check if a point is a wall
     */
    public Grid(int width, int height, float cellSize, float[] originPosition, Reader readEnv) {

        // Initialize attributes
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;
        this.originPosition = originPosition;

        // Initialize gridArray
        gridArray = new Square[width+2][height+2];

        //Filling gridArray with new Squares
        for (int x = 0; x < gridArray.length; x++) {
            for (int y = 0; y < gridArray[0].length; y++) {
                gridArray[x][y] = new Square(x, y);
            }
        }

        for (AreaProperty ap : readEnv.getProperties()) {

            // If the AreaProperty is a wall
            if (ap instanceof Wall) {
                Wall w = (Wall) ap;

                // Find the two opposite corner coordinates
                double maxX = Math.max(w.getTopLeft().getX(), Math.max(w.getTopRight().getX(), Math.max(w.getBotLeft().getX(), w.getBotRight().getX()))) + 1;
                double minX = Math.min(w.getTopLeft().getX(), Math.min(w.getTopRight().getX(), Math.min(w.getBotLeft().getX(), w.getBotRight().getX()))) + 1;
                double maxY = Math.max(w.getTopLeft().getY(), Math.max(w.getTopRight().getY(), Math.max(w.getBotLeft().getY(), w.getBotRight().getY()))) + 1;
                double minY = Math.min(w.getTopLeft().getY(), Math.min(w.getTopRight().getY(), Math.min(w.getBotLeft().getY(), w.getBotRight().getY()))) + 1;

                // Loop that goes over all Xs and Ys in a wall, setting type of the square of the gridArray to "Wall" and not walkable
                for (int x = (int) minX; x < maxX; x++) {
                    for (int y = (int) minY; y < maxY; y++) {
                        gridArray[x][y].setType("Wall");
                        gridArray[x][y].setWalkable(false);
                    }
                }
            }
        }
    }

    public Square[][] getGridArray(){ return this.gridArray; }

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

    public boolean checkSquare(int x, int y) //Get a Square using X and Y
    {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            return true;
        }
        return false;
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

    public float getCellSize() {
        return this.cellSize;
    }
}
