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
        this.width = width+2;
        this.height = height+2;
        this.cellSize = cellSize;
        this.originPosition = originPosition;

        // Initialize gridArray
        gridArray = new Square[this.width][this.height];

        //Filling gridArray with new Squares
        for (int x = 0; x < gridArray.length; x++) {
            for (int y = 0; y < gridArray[0].length; y++) {
                Square square = new Square(x, y);
                square.setType("Empty");
                square.setWalkable(true);
                gridArray[x][y] = square;
            }
        }

        for (AreaProperty ap : readEnv.getProperties()) {

            // If the AreaProperty is a door
            if (ap instanceof Door) {
                Door door = (Door) ap;

                // Find the two opposite corner coordinates
                double maxX = Math.max(door.getTopLeft().getX(), Math.max(door.getTopRight().getX(), Math.max(door.getBotLeft().getX(), door.getBotRight().getX()))) + 1;
                double minX = Math.min(door.getTopLeft().getX(), Math.min(door.getTopRight().getX(), Math.min(door.getBotLeft().getX(), door.getBotRight().getX()))) + 1;
                double maxY = Math.max(door.getTopLeft().getY(), Math.max(door.getTopRight().getY(), Math.max(door.getBotLeft().getY(), door.getBotRight().getY()))) + 1;
                double minY = Math.min(door.getTopLeft().getY(), Math.min(door.getTopRight().getY(), Math.min(door.getBotLeft().getY(), door.getBotRight().getY()))) + 1;

                // Loop that goes over all Xs and Ys in a wall, setting type of the square of the gridArray to "Wall" and not walkable
                for (int x = (int) minX; x < maxX; x++) {
                    for (int y = (int) minY; y < maxY; y++) {
                        gridArray[x][y].setType("Door");
                    }
                }
            }

            // If the AreaProperty is a wall
            else if (ap instanceof Sentry) {
                Sentry sentry = (Sentry) ap;

                // Find the two opposite corner coordinates
                double maxX = Math.max(sentry.getTopLeft().getX(), Math.max(sentry.getTopRight().getX(), Math.max(sentry.getBotLeft().getX(), sentry.getBotRight().getX()))) + 1;
                double minX = Math.min(sentry.getTopLeft().getX(), Math.min(sentry.getTopRight().getX(), Math.min(sentry.getBotLeft().getX(), sentry.getBotRight().getX()))) + 1;
                double maxY = Math.max(sentry.getTopLeft().getY(), Math.max(sentry.getTopRight().getY(), Math.max(sentry.getBotLeft().getY(), sentry.getBotRight().getY()))) + 1;
                double minY = Math.min(sentry.getTopLeft().getY(), Math.min(sentry.getTopRight().getY(), Math.min(sentry.getBotLeft().getY(), sentry.getBotRight().getY()))) + 1;

                // Loop that goes over all Xs and Ys in a wall, setting type of the square of the gridArray to "Wall" and not walkable
                for (int x = (int) minX; x < maxX; x++) {
                    for (int y = (int) minY; y < maxY; y++) {
                        gridArray[x][y].setType("Sentry");
                    }
                }
            }

            // If the AreaProperty is a wall
            else if (ap instanceof Shade) {
                Shade shade = (Shade) ap;

                // Find the two opposite corner coordinates
                double maxX = Math.max(shade.getTopLeft().getX(), Math.max(shade.getTopRight().getX(), Math.max(shade.getBotLeft().getX(), shade.getBotRight().getX()))) + 1;
                double minX = Math.min(shade.getTopLeft().getX(), Math.min(shade.getTopRight().getX(), Math.min(shade.getBotLeft().getX(), shade.getBotRight().getX()))) + 1;
                double maxY = Math.max(shade.getTopLeft().getY(), Math.max(shade.getTopRight().getY(), Math.max(shade.getBotLeft().getY(), shade.getBotRight().getY()))) + 1;
                double minY = Math.min(shade.getTopLeft().getY(), Math.min(shade.getTopRight().getY(), Math.min(shade.getBotLeft().getY(), shade.getBotRight().getY()))) + 1;

                // Loop that goes over all Xs and Ys in a wall, setting type of the square of the gridArray to "Wall" and not walkable
                for (int x = (int) minX; x < maxX; x++) {
                    for (int y = (int) minY; y < maxY; y++) {
                        gridArray[x][y].setType("Shade");
                    }
                }
            }

            // If the AreaProperty is a wall
            else if (ap instanceof Teleport) {
                Teleport teleport = (Teleport) ap;

                // Find the two opposite corner coordinates
                double maxX = Math.max(teleport.getTopLeft().getX(), Math.max(teleport.getTopRight().getX(), Math.max(teleport.getBotLeft().getX(), teleport.getBotRight().getX()))) + 1;
                double minX = Math.min(teleport.getTopLeft().getX(), Math.min(teleport.getTopRight().getX(), Math.min(teleport.getBotLeft().getX(), teleport.getBotRight().getX()))) + 1;
                double maxY = Math.max(teleport.getTopLeft().getY(), Math.max(teleport.getTopRight().getY(), Math.max(teleport.getBotLeft().getY(), teleport.getBotRight().getY()))) + 1;
                double minY = Math.min(teleport.getTopLeft().getY(), Math.min(teleport.getTopRight().getY(), Math.min(teleport.getBotLeft().getY(), teleport.getBotRight().getY()))) + 1;

                // Loop that goes over all Xs and Ys in a wall, setting type of the square of the gridArray to "Wall" and not walkable
                for (int x = (int) minX; x < maxX; x++) {
                    for (int y = (int) minY; y < maxY; y++) {
                        gridArray[x][y].setType("Teleport");
                    }
                }
            }

            // If the AreaProperty is a wall
            else if (ap instanceof Window) {
                Window window = (Window) ap;

                // Find the two opposite corner coordinates
                double maxX = Math.max(window.getTopLeft().getX(), Math.max(window.getTopRight().getX(), Math.max(window.getBotLeft().getX(), window.getBotRight().getX()))) + 1;
                double minX = Math.min(window.getTopLeft().getX(), Math.min(window.getTopRight().getX(), Math.min(window.getBotLeft().getX(), window.getBotRight().getX()))) + 1;
                double maxY = Math.max(window.getTopLeft().getY(), Math.max(window.getTopRight().getY(), Math.max(window.getBotLeft().getY(), window.getBotRight().getY()))) + 1;
                double minY = Math.min(window.getTopLeft().getY(), Math.min(window.getTopRight().getY(), Math.min(window.getBotLeft().getY(), window.getBotRight().getY()))) + 1;

                // Loop that goes over all Xs and Ys in a wall, setting type of the square of the gridArray to "Wall" and not walkable
                for (int x = (int) minX; x < maxX; x++) {
                    for (int y = (int) minY; y < maxY; y++) {
                        gridArray[x][y].setType("Window");
                    }
                }
            }

            // If the AreaProperty is a wall
            else if (ap instanceof Wall) {
                Wall wall = (Wall) ap;

                // Find the two opposite corner coordinates
                double maxX = Math.max(wall.getTopLeft().getX(), Math.max(wall.getTopRight().getX(), Math.max(wall.getBotLeft().getX(), wall.getBotRight().getX()))) + 1;
                double minX = Math.min(wall.getTopLeft().getX(), Math.min(wall.getTopRight().getX(), Math.min(wall.getBotLeft().getX(), wall.getBotRight().getX()))) + 1;
                double maxY = Math.max(wall.getTopLeft().getY(), Math.max(wall.getTopRight().getY(), Math.max(wall.getBotLeft().getY(), wall.getBotRight().getY()))) + 1;
                double minY = Math.min(wall.getTopLeft().getY(), Math.min(wall.getTopRight().getY(), Math.min(wall.getBotLeft().getY(), wall.getBotRight().getY()))) + 1;

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
        return (int) Math.floor((worldPosition[0] - originPosition[0]) / cellSize);
    }

    public int getY(float[] worldPosition) //Returns Y with position
    {
        return (int) Math.floor((worldPosition[1] - originPosition[1]) / cellSize);
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

        if(x < 0) { x = 0; }
        if(x > width-1) { x = width-1; }
        if(y < 0) { x = 0; }
        if(y > height-1) { y = height-1; }

        return gridArray[x][y];
    }

    public float getCellSize() {
        return this.cellSize;
    }
}
