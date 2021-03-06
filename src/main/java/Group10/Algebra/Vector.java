package Group10.Algebra;

import Group10.Engine.Game;
import Interop.Geometry.Direction;
import Interop.Geometry.Point;
import Interop.Utils.Utils;

import java.util.Objects;

public class Vector {

    private final double x, y;
    private final double length;

    public Vector(double x, double y)
    {
        this.x = x;
        this.y = y;
        this.length = Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
    }

    public Vector(Point point, Direction direction) {

        this.x = point.getX() + Math.cos(direction.getRadians());
        this.y = point.getY() + Math.sin(direction.getRadians());
        this.length = Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
    }

    public Vector() {
        this.x = 0;
        this.y = 0;
        this.length = 0;
    }


    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public Vector normalise() {

        if(this.length == 0)
        {
            return new Vector(0, 0);
        }
        return new Vector(this.x / this.length, this.y / this.length);
    }

    public double length()
    {
        return this.length;
    }

    public Vector mul(double x, double y)
    {
        return new Vector(this.x * x, this.y * y);
    }

    public Vector mul(double n){
        return new Vector(this.x * n, this.y * n);
    }

    public Vector div(double d) { return new Vector(x / d, y / d); }

    public Vector add(Vector add)
    {
        return this.add(add.getX(), add.getY());
    }

    public Vector add(double x, double y)
    {
        return new Vector(this.x + x, this.y + y);
    }

    public Vector sub(Vector sub)
    {
        return this.add(-sub.getX(), -sub.getY());
    }

    public Vector sub(double x, double y)
    {
        return this.add(-x, -y);
    }



    /**
     * See: https://matthew-brett.github.io/teaching/rotation_2d.html
     *      https://en.wikipedia.org/wiki/Rotation_matrix
     * @param radians
     * @return (new) vector rotated anticlockwise by 'angle' radians
     */
    public Vector rotated(double radians) {
        return new Vector(
                Math.cos(radians) * x  -  Math.sin(radians) * y,
                Math.sin(radians) * x  +  Math.cos(radians) * y
        );
    }

    public double dot(Vector other)
    {
        return (this.x * other.getX()) + (this.y * other.getY());
    }

    public double angle(Vector other)
    {
        return Math.atan2(other.getY(), other.getX()) - Math.atan2(this.getY(), this.getX());
    }

    public double angledSigned(Vector other)
    {
        return Math.atan2(other.getY(), other.getX()) - Math.atan2(this.getY(), this.getX());
    }


    public double distance(Vector other)
    {
        return Math.sqrt(Math.pow(this.x - other.getX(), 2) + Math.pow(this.y - other.getY(), 2));
    }

    public double getAngle()
    {
        double angle = Math.asin(y);
        if(x < 0)
        {
            angle = Math.PI - angle;
        }
        return angle;

    }

    public double getClockDirection() {
        return Utils.clockAngle(this.x, this. y);
    }

    public Point toVexing()
    {
        return new Point(this.x, this.y);
    }

    public static Vector from(Point point)
    {
        return new Vector(point.getX(), point.getY());
    }

    @Override
    public String toString() {
        return "Vector {" +
                "x = " + x +
                ", y = " + y +
                ", length = " + length +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector vector = (Vector) o;
        return Double.compare(vector.x, x) == 0 &&
                Double.compare(vector.y, y) == 0;
    }

    public Vector unitVector() {
        double length = length();
        if (length == 0) return new Vector();
        return this.div(length);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public Vector clone() {
        return new Vector(this.x, this.y);
    }

    public Point toPoint() {
        return new Point(x, y);
    }

    public Vector(Point point) {
        this(point.getX(), point.getY());
    }

    public static class Random extends Vector {
        public Random() {
            super(Game._RANDOM.nextDouble(), Game._RANDOM.nextDouble());
        }
    }

    public static class Origin extends Vector {
        public Origin() {
            super(0, 0);
        }
    }

}
