package GUI;

import Environment.Grid;
import Environment.Square;
import Interop.Geometry.Point;
import Interop.Percept.Smell.SmellPerceptType;
import Perception.ExplorationPheromone;
import javafx.animation.FadeTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class PheromoneGUI {

    Grid grid;

    public void addPheromone(SmellPerceptType type, Point pos, Integer timer, double scalingFactor) {
        ExplorationPheromone pheromone = new ExplorationPheromone(type, pos, timer, scalingFactor);
        if (pheromone.getTurns() > 0) {
            double op = pheromone.getOpacity() - 0.05;
            if (grid.checkSquare((int)pos.getX(), (int)pos.getY())) {
                Rectangle rect = new Rectangle(grid.getSquare((int)pos.getX(), (int)pos.getY()).getSX(),grid.getSquare((int)pos.getX(), (int)pos.getY()).getSY(), grid.getCellSize(), grid.getCellSize());
                rect.setFill(Color.DARKGRAY);
                rect.setOpacity(op);
                pheromone.setOpacity(op);
            }
        }
    }
}
