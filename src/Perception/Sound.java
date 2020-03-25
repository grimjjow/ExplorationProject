package Perception;

import Interop.Geometry.Point;
import Interop.Percept.Sound.SoundPerceptType;

import java.util.ArrayList;

public class Sound {

    private SoundPerceptType type;
    private Point position;
    private int turns;
    private double radius;
    private String yell;

    private ArrayList<Sound> sounds = new ArrayList<>();

    public ArrayList<Sound> getSounds() {
        return sounds;
    }

    public void setSounds(ArrayList<Sound> sounds) {
        this.sounds = sounds;
    }

    public Sound(SoundPerceptType type, Point position, int turns, double radius) {

        this.type = type;
        this.position = position;
        this.turns = turns;
        this.radius = radius;
        this.yell= yell;
    }

    public SoundPerceptType getType() {
        return type;
    }
    public void setType(SoundPerceptType type) {
        this.type = type;
    }

    public Point getPosition() {
        return position;
    }
    public void setPosition(Point position) {
        this.position = position;
    }

    public int getTurns() {
        return turns;
    }
    public void setTurns(int turns) {
        this.turns = turns;
    }

    public double getRadius() {
        return radius;
    }
    public void setRadius(double radius) {
        this.radius = radius;
    }

}
