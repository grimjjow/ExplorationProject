package GUI;

import AreaProperty.*;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class PropertyGUI {

    AreaProperty property;
    Polygon shape;

    public PropertyGUI(AreaProperty property, double scalingFactor) {
        this.property = property;
        this.shape = new Polygon();
        this.shape.getPoints().addAll(new Double[] {
                (double)property.getP1().getX()*scalingFactor, (double)property.getP1().getY()*scalingFactor,
                (double)property.getP2().getX()*scalingFactor, (double)property.getP2().getY()*scalingFactor,
                (double)property.getP3().getX()*scalingFactor, (double)property.getP3().getY()*scalingFactor,
                (double)property.getP4().getX()*scalingFactor, (double)property.getP4().getY()*scalingFactor
        });
        if(property instanceof Wall) {
            shape.setFill(Color.DIMGRAY);
        }else if(property instanceof Window) {
            shape.setFill(Color.AZURE);
        }else if(property instanceof Door) {
            shape.setFill(Color.MAROON);
        }else if(property instanceof Teleport) {
            shape.setFill(Color.PURPLE);
        }else if(property instanceof Sentry) {
            shape.setFill(Color.BLUEVIOLET);
        }else if(property instanceof Shade) {
            shape.setFill(Color.SEAGREEN);
        }else if(property instanceof SpawnGuards) {
            shape.setFill(Color.CORNFLOWERBLUE);
        }

    }
    public Polygon getShape(){	return  this.shape;	}
}
