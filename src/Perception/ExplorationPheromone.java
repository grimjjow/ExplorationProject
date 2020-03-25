package Perception;

import Interop.Geometry.Point;
import Interop.Percept.Smell.SmellPerceptType;

public class ExplorationPheromone {

    private SmellPerceptType expPheromone;
    private Point position;
    private int turns;
    double scalingFactor;
    double opacity;

    public ExplorationPheromone(SmellPerceptType expPheromone, Point position, int turns, double scalingFactor) {

        this.expPheromone = expPheromone;
        this.position = position;
        this.turns = turns;
        this.scalingFactor = scalingFactor;
        this.opacity = 1.0;
    }

    public SmellPerceptType getExpPheromone() {
        return expPheromone;
    }

    public void setExpPheromone(SmellPerceptType expPheromone) {
        this.expPheromone = expPheromone;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public double getOpacity() {
        return this.opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }
    public int getTurns() { return this.turns; }
}
