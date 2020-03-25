package GUI;

import AreaProperty.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;


public class PropertyGUI {

    AreaProperty property;
    Polygon shape;
    Circle circleShape;

    public PropertyGUI(AreaProperty property, double scalingFactor) {
        this.property = property;
        this.shape = new Polygon();
        /*this.circleShape = new Circle();
        this.circleShape.setCenterX((property.getBotRight().getX()-property.getTopLeft().getX())/2);
        this.circleShape.setCenterY((property.getBotRight().getY()-property.getTopLeft().getY())/2);
        this.circleShape.setRadius((property.getBotRight().getX()-property.getTopLeft().getX())/2);*/
        this.shape.getPoints().addAll(new Double[] {
                (double)property.getTopLeft().getX()*scalingFactor, (double)property.getTopLeft().getY()*scalingFactor,
                (double)property.getTopRight().getX()*scalingFactor, (double)property.getTopRight().getY()*scalingFactor,
                (double)property.getBotLeft().getX()*scalingFactor, (double)property.getBotLeft().getY()*scalingFactor,
                (double)property.getBotRight().getX()*scalingFactor, (double)property.getBotRight().getY()*scalingFactor

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
            //circleShape.setFill(Color.BLUEVIOLET);
            shape.setFill(Color.BLUEVIOLET);
        }else if(property instanceof Shade) {
            shape.setFill(Color.rgb(0, 170, 70, 0.4));
        }else if(property instanceof SpawnGuards) {
            shape.setFill(Color.TRANSPARENT);
        }
    }

    public Polygon getShape(){	return  this.shape;	}

    public Circle getCircleShape(){	return  this.circleShape;	}
}
