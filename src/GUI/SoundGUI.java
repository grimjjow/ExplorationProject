package GUI;

import Interop.Geometry.Point;
import Interop.Percept.Sound.SoundPerceptType;
import Perception.Sound;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class SoundGUI {

    Circle circle;

    public void addSound(SoundPerceptType type, Point point, Integer timer, Double radius) {
        Sound sound = new Sound(type, point, timer, radius);
        if (type.getClass().getName().equals("Yell")) {
            this.circle = new Circle(sound.getRadius());
            this.circle.setFill(Color.TRANSPARENT);
            this.circle.setCenterX(sound.getPosition().getX());
            this.circle.setCenterY(sound.getPosition().getY());
            if (sound.getTurns() > 0) {
                //todo
            }
        }
    }

}
