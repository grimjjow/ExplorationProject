package Group10.Tree;

import Group10.Engine.Game;
import Group10.Algebra.Vector;

import java.util.*;
import java.util.function.Function;

import static Group10.Algebra.Maths.geq;
import static Group10.Algebra.Maths.leq;

public abstract class PointContainer {

    /**
     * Move the container by applying `x_i = x_i + vector.x` and `y_i = y_i + vector.y`
     * @param vector
     */
    abstract public void translate(Vector vector);

    /**
     * Returns the center of the container.
     * @return
     */
    abstract public Vector getCenter();

    /**
     * Returns the area of the container.
     * @return
     */
    abstract public double getArea();

    @Override
    public PointContainer clone() throws CloneNotSupportedException {
        if(this instanceof Line)
        {
            return ((Line) this).clone();
        }
        else if(this instanceof Circle)
        {
            return ((Circle) this).clone();
        }
        else if(this instanceof Polygon)
        {
            return ((Polygon) this).clone();
        }
        throw new CloneNotSupportedException();
    }

    public PointContainer.Polygon getAsPolygon()
    {
        return (PointContainer.Polygon) this;
    }

    public PointContainer.Circle getAsCircle()
    {
        return (PointContainer.Circle) this;
    }

    /**
     * This class supports non-self-intersecting closed polygons of arbitrary size >= 3.
     */
    public static class Polygon extends PointContainer {

        private Vector[] points;
        private Line[] lines;
        private List<Vector[]> triangles = new ArrayList<>();

        private double area = -1;

        public Polygon(Vector ...points)
        {
            if(points.length < 3)
            {
                throw new IllegalArgumentException("A polygon must consist of at least 3 points");
            }

            this.points = points;
            this.lines = new Line[this.points.length];
            for(int i = 0; i < this.points.length; i++)
            {
                this.lines[i] = new Line(this.points[i], this.points[(i + 1) % this.points.length]);
            }
        }

        @Override
        public double getArea()
        {
            if(area == -1)
            {
                area = 0;
                for(int i = 0; i < points.length; i++)
                {
                    area += (points[i].getX() * points[(i + 1) % points.length].getY()
                            - points[(i + 1) % points.length].getX() * points[i].getY());
                }
                this.area = Math.abs(area) * 0.5;
            }

            return area;
        }

        public Vector[] getPoints() {
            return points;
        }

        public Line[] getLines()
        {
            return this.lines;
        }

        public List<Vector[]> getTriangles()
        {

            //---
            if(triangles.isEmpty())
            {
                //---
                //TODO implememt https://www.geometrictools.com/Documentation/TriangulationByEarClipping.pdf
                // very important
                if(false)
                {
                    for(Vector a : points)
                    {
                        for(Vector b : points)
                        {
                            if(a == b) continue;
                            for(Vector c : points)
                            {
                                if(a == c || b == c) continue;

                                if(new Vector(b.getX() - a.getX(), b.getY() - a.getY()).angle(
                                        new Vector(c.getX() - a.getX(), c.getY() - a.getY())
                                ) >= Math.PI)
                                {
                                    continue;
                                }

                                // TODO In theory we should do a proper check here whether or not the line is actually completely
                                //  inside the polygon but I am currently not aware of an easy way to do it, so this is only
                                //  for polygons with 4 points. ^^
                                Line _02 = new Line(a, c);
                                Vector randomPoint = new Vector(_02.getStart().getX(), _02.getStart().getY()).add(
                                        new Vector.Random().normalise().mul(
                                                _02.getEnd().getX() - _02.getStart().getX(),
                                                _02.getEnd().getY() - _02.getStart().getY()
                                        )
                                );

                                if(this.isPointInside(randomPoint))
                                {
                                    triangles.add(new Vector[] {a, b, c});
                                }

                            }
                        }
                    }
                }

                for(int i = 1; i <= this.points.length - 2; i++)
                {
                    this.triangles.add(new Vector[] {
                            this.points[0], this.points[i], this.points[(i + 1)]
                    });
                }
            }

            return triangles;
        }

        public Vector generateRandomLocation()
        {
            //--- follows: https://www.cs.princeton.edu/~funk/tog02.pdf @ 4.2

            // TODO if we wanted to make this actually uniform, we would need to calculate the area of the triangles
            //  and weight them appropriately... (low priority)
            List<Vector[]> triangles = getTriangles();
            Vector[] triangle = triangles.get(Math.round(Game._RANDOM.nextFloat() * (triangles.size() - 1)));

            Vector A = triangle[0];
            Vector B = triangle[1];
            Vector C = triangle[2];
            double r1 = Game._RANDOM.nextDouble();
            double r2 = Game._RANDOM.nextDouble();

            //--- P = A * sqrt(r1) + B * (1-r2)*sqrt(r1) + C * sqrt(r1)*r2
            final Vector r =  A.mul(1 - Math.sqrt(r1))
                    .add(B.mul((1 - r2) * Math.sqrt(r1)))
                    .add(C.mul(Math.sqrt(r1)*r2));
            //|| isInTriangle(q.points[0], q.points[2], q.points[3], point);

            assert isPointInside(r);
            return r;
        }

        /**
         * Splits the Polygon into triangles and uses a Barycentric technique to check whether it is in one of the
         * triangles.
         *
         * @link https://blackpawn.com/texts/pointinpoly/default.html
         * @param point
         * @return true, if inside, otherwise false.
         */
        public boolean isPointInside(Vector point)
        {
            for (Vector[] e : getTriangles()) {
                if (isInTriangle(e[0], e[1], e[2], point)) {
                    return true;
                }
            }
            return false;
        }

        private static boolean isInTriangle(Vector A, Vector B, Vector C, Vector P)
        {
            Vector v0 = C.sub(A);
            Vector v1 = B.sub(A);
            Vector v2 = P.sub(A);

            double dot00 = v0.dot(v0);
            double dot01 = v0.dot(v1);
            double dot02 = v0.dot(v2);
            double dot11 = v1.dot(v1);
            double dot12 = v1.dot(v2);

            double invDenom = 1D / (dot00 * dot11 - dot01 * dot01);
            double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
            double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

            return (u >= 0) && (v >= 0) && (u + v < 1);
        }

        @Override
        public void translate(Vector vector) {
            this.triangles.clear();
            for (int i = 0; i < this.points.length; i++) {
                this.points[i] = this.points[i].add(vector);
            }

            for(int i = 0; i < this.points.length; i++)
            {
                this.lines[i] = new Line(this.points[i], this.points[(i + 1) % this.points.length]);
            }
        }

        @Override
        public Vector getCenter() {
            final double divisor = 6D * this.getArea();
            double cx = 0;
            for(int i = 0; i < points.length; i++)
            {
                cx += (points[i].getX() + points[(i + 1) % points.length].getX())
                        * (points[i].getX() * points[(i + 1) % points.length].getY()
                        - points[(i + 1) % points.length].getX() * points[i].getY());
            }

            double cy = 0;
            for(int i = 0; i < points.length; i++)
            {
                cy += (points[i].getY() + points[(i + 1) % points.length].getY())
                        * (points[i].getX() * points[(i + 1) % points.length].getY()
                        - points[(i + 1) % points.length].getX() * points[i].getY());
            }


            return new Vector(cx/divisor, cy/divisor);
        }

        @Override
        public PointContainer.Polygon clone() {
            return new Polygon(
                    Arrays.stream(this.points).map(Vector::clone).toArray(Vector[]::new)
            );
        }

        @Override
        public String toString() {
            return "Polygon{" +
                    "points=" + Arrays.toString(points) +
                    ", lines=" + Arrays.toString(lines) +
                    '}';
        }
    }

    public static class Circle extends PointContainer
    {
        private Vector center;
        private double radius;
        private double area;

        public Circle(Vector center, double radius)
        {
            this.center = center;
            this.radius = radius;
            this.area = Math.pow(radius, 2) * Math.PI * 2D;
        }

        public boolean isInside(Vector point)
        {
            return center.distance(point) <= radius;
        }

        public double getRadius() {
            return radius;
        }

        @Override
        public Vector getCenter() {
            return center;
        }

        @Override
        public double getArea() {
            return this.area;
        }

        @Override
        public void translate(Vector vector) {
            this.center = this.center.add(vector);
        }

        @Override
        public Circle clone() {
            return new Circle(center.clone(), this.getRadius());
        }

        @Override
        public String toString() {
            return "Circle{" +
                    "center=" + center +
                    ", radius=" + radius +
                    '}';
        }
    }

    public static class Line extends PointContainer{

        private Vector start;
        private Vector end;

        public Line(Vector start, Vector end)
        {
            super();
            if(start.getX() <= end.getX())
            {
                this.start = start;
                this.end = end;
            }
            else if(start.getX() == end.getX())
            {
                if(start.getY() <= end.getY())
                {
                    this.start = start;
                    this.end = end;
                }
                else
                {
                    this.start = end;
                    this.end = end;
                }
            }
            else
            {
                this.start = end;
                this.end = start;
            }

        }

        public Vector getStart()
        {
            return this.start;
        }

        public Vector getEnd()
        {
            return this.end;
        }

        public boolean isPointOnLine(Vector p)
        {
            double length = this.start.distance(this.end);

            double start_p = this.start.distance(p);
            double end_p = this.end.distance(p);

            return Math.abs(start_p + end_p - length) < 1E-9;
        }

        public Vector getNormal()
        {
            double dx = end.getX() - start.getX();
            double dy = end.getY() - start.getY();
            return new Vector(-dy, dx).normalise();
        }

        @Override
        public String toString() {
            return "Line{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }

        @Override
        public void translate(Vector vector) {
            this.start = this.start.add(vector);
            this.end = this.end.add(vector);

        }

        @Override
        public Vector getCenter() {
            return this.start.add(this.end).mul(0.5);
        }

        @Override
        public double getArea() {
            return 0;
        }

        @Override
        public PointContainer.Line clone() {
            return new Line(this.start.clone(), this.end.clone());
        }
    }

    public static boolean intersect(PointContainer containerA, PointContainer containerB)
    {

        if (containerA instanceof Polygon || containerB instanceof Polygon)
        {

            Polygon polygon = (containerA instanceof Polygon) ? (Polygon) containerA : (Polygon) containerB;

            if(containerA instanceof Polygon && containerB instanceof Polygon)
            {
                Polygon other = (polygon == containerB) ? (Polygon) containerA : (Polygon) containerB;
                for(Line a : polygon.getLines()) {
                    for(Line b : other.getLines())
                    {
                        if(intersect(a, b))
                        {
                            return true;
                        }
                    }
                }

                return Arrays.stream(polygon.getPoints()).anyMatch(other::isPointInside)
                        || Arrays.stream(other.getPoints()).anyMatch(polygon::isPointInside);
            }
            else if(containerA instanceof Circle || containerB instanceof Circle)
            {
                Circle circle = (containerA instanceof Circle) ? (Circle) containerA : (Circle) containerB;

                if(polygon.isPointInside(circle.getCenter()))
                {
                    return true;
                }

                for(Line a : polygon.getLines())
                {
                    if(circleLineIntersect(circle, a).length != 0)
                    {
                        return true;
                    }
                }

                return false;
            }
            else if(containerA instanceof Line || containerB instanceof Line)
            {
                Line line = (containerA instanceof Line) ? (Line) containerA : (Line) containerB;
                for(Line a : polygon.getLines())
                {
                    if(intersect(a, line))
                    {
                        return true;
                    }
                }

                return (polygon.isPointInside(line.getStart()) || polygon.isPointInside(line.getEnd()));
            }

        }
        else if(containerA instanceof Circle && containerB instanceof Circle)
        {
            Circle a = (Circle) containerA;
            Circle b = (Circle) containerB;

            return a.getCenter().distance(b.getCenter()) < Math.max(a.getRadius(), b.getRadius());
        }
        else if(containerA instanceof Line || containerB instanceof Line)
        {
            Line line = (containerA instanceof Line) ? (Line) containerA : (Line) containerB;

            if(containerA instanceof Line && containerB instanceof Line)
            {
                Line other = (line == containerA) ? (Line) containerB : (Line) containerA;
                return PointContainer.twoLinesIntersect(line.getStart(), line.getEnd(), other.getStart(), other.getEnd()) != null;
            }
            else if(containerA instanceof Circle || containerB instanceof Circle)
            {
                Circle circle = (containerA instanceof Circle) ? (Circle) containerA : (Circle) containerB;
                return circleLineIntersect(circle, line).length != 0;
            }

        }

        throw new IllegalArgumentException();

    }

    public static Set<Vector> intersectionPoints(PointContainer pointContainer, Line l) {
        Set<Vector> intersectionPoints = new HashSet<>();

        if (pointContainer instanceof Line) {
            intersectionPoints.add(twoLinesIntersect((Line) pointContainer,l));
        } else if (pointContainer instanceof Circle) {
            Collections.addAll(intersectionPoints, circleLineIntersect((Circle) pointContainer, l));
        } else if (pointContainer instanceof Polygon) {
            Polygon q = (Polygon) pointContainer;

            for (Line ql : q.getLines()) {
                Vector intersectPoint = twoLinesIntersect(ql, l);
                if (intersectPoint != null) {
                    intersectionPoints.add(intersectPoint);
                }
            }
        }

        return intersectionPoints;
    }

    private static Vector[] circleLineIntersect(Circle circle, Line line)
    {
        Vector start = line.getStart().sub(circle.getCenter());
        Vector end = line.getEnd().sub(circle.getCenter());

        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();

        if(dx == 0 || dy == 0)
        {
            Line top = new Line(circle.getCenter(), circle.getCenter().add(new Vector(0, 1).mul(circle.getRadius())));
            Line right = new Line(circle.getCenter(), circle.getCenter().add(new Vector(1, 0).mul(circle.getRadius())));
            Line bottom = new Line(circle.getCenter(), circle.getCenter().add(new Vector(0, -1).mul(circle.getRadius())));
            Line left = new Line(circle.getCenter(), circle.getCenter().add(new Vector(-1, 0).mul(circle.getRadius())));

            if(twoLinesIntersect(line, top) != null)
            {
                return new Vector[] {top.getEnd()};
            }
            if(twoLinesIntersect(line, right) != null)
            {
                return new Vector[] {right.getEnd()};
            }
            if(twoLinesIntersect(line, bottom) != null)
            {
                return new Vector[] {bottom.getEnd()};
            }
            if(twoLinesIntersect(line, left) != null)
            {
                return new Vector[] {left.getEnd()};
            }

            /*Line a = new Line(circle.getCenter(), circle.getCenter().add(line.getStart().sub(circle.getCenter()).normalise().mul(circle.getRadius())));
            Line b = new Line(circle.getCenter(), circle.getCenter().add(line.getEnd().sub(circle.getCenter()).normalise().mul(circle.getRadius())));

            if(twoLinesIntersect(line, a) != null)
            {
                return new Vector2[] {circle.getCenter().add(line.getStart().sub(circle.getCenter()).normalise().mul(circle.getRadius()))};
            }

            if(twoLinesIntersect(line, b) != null)
            {
                return new Vector2[] {circle.getCenter().add(line.getEnd().sub(circle.getCenter()).normalise().mul(circle.getRadius()))};
            }*/

            return new Vector[0];
            //return new Vector2[0];
        }

        double dr = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

        double D = (start.getX() * end.getY()) - (end.getX() * start.getY());

        double discriminant = Math.pow(circle.getRadius(), 2) * Math.pow(dr, 2) - Math.pow(D, 2);

        if(discriminant < 0)
        {
            return new Vector[0];
        }

        Function<Double, Double> sgn = x -> (x < 0 ? -1D : 1D);

        if(discriminant == 0)
        {
            Vector candidate = new Vector(
                    (D * dy + sgn.apply(dy) * dx * Math.sqrt(discriminant)) / Math.pow(dr, 2),
                    (-D * dx + Math.abs(dy) * Math.sqrt(discriminant)) / Math.pow(dr, 2)
            ).add(circle.getCenter());

            if(line.isPointOnLine(candidate))
            {
                return new Vector[] { candidate };
            }
            return new Vector[0];
        }

        Vector candidateA = new Vector(
                (D * dy + sgn.apply(dy) * dx * Math.sqrt(discriminant)) / Math.pow(dr, 2),
                (-D * dx + Math.abs(dy) * Math.sqrt(discriminant)) / Math.pow(dr, 2)
        ).add(circle.getCenter());
        Vector candidateB = new Vector(
                (D * dy - sgn.apply(dy) * dx * Math.sqrt(discriminant)) / Math.pow(dr, 2),
                (-D * dx - Math.abs(dy) * Math.sqrt(discriminant)) / Math.pow(dr, 2)
        ).add(circle.getCenter());

        boolean a = line.isPointOnLine(candidateA);
        boolean b = line.isPointOnLine(candidateB);

        if(a && b)
        {
            return new Vector[] { candidateA, candidateB };
        }
        else if(a)
        {
            return new Vector[] { candidateA };
        }
        else if(b)
        {
            return new Vector[] { candidateB };
        }

        return new Vector[0];
    }

    /**
     * Calculate whether 2 lines intersect with each other
     * @param a_start start point of line 1.
     * @param a_end end point of line 1.
     * @param b_start start point of line 2.
     * @param b_end end point of line 2.
     * @return the vector that points to the intersection point, returns null when no intersection is found
     */
    private static Vector twoLinesIntersect(Vector a_start, Vector a_end, Vector b_start, Vector b_end){
        //http://mathworld.wolfram.com/Line-LineIntersection.html
        double x1 = a_start.getX();
        double y1 = a_start.getY();
        double x2 = a_end.getX();
        double y2 = a_end.getY();
        double x3 = b_start.getX();
        double y3 = b_start.getY();
        double x4 = b_end.getX();
        double y4 = b_end.getY();
        double parallelDenominator = determinant(x1-x2, y1-y2, x3-x4, y3-y4);

        if(parallelDenominator == 0.0){

            // Note: when the lines are parallel we have to check whether they contain one another
            // 1. First, we check if they share the same y-intercept, if they do not share the same intercept then
            //      they are parallel but can not intercept one another.
            // 2. Check if the start, end or both points are inside the other line.
            // mx+b=y -> b = y-mx
            double _a_y_intercept = a_start.getY() - (a_end.getY() - a_start.getY()) / (a_end.getX() - a_start.getX()) * a_start.getX();
            double _b_y_intercept = b_start.getY() - (b_end.getY() - b_start.getY()) / (b_end.getX() - b_start.getX()) * b_start.getX();

            //-- check y intercept
            if(_a_y_intercept != _b_y_intercept)
            {
                return null;
            }

            if(a_start.getX() >= b_start.getX() && a_end.getX() <= b_end.getX())
            {
                return a_start;
            }
            else if(a_start.getX() >= b_start.getX() && a_start.getX() <= b_end.getX() && a_end.getX() >= b_end.getX())
            {
                return a_start;
            }
            else if(a_start.getX() <= b_start.getX() && a_end.getX() >= b_start.getX() && a_end.getX() <= b_end.getX())
            {
                return b_end;
            }

            if (b_start.getX() >= a_start.getX() && b_end.getX() <= a_end.getX())
            {
                return b_start;
            }
            else if(b_start.getX() >= a_start.getX() && b_start.getX() <= a_end.getX() && b_end.getX() >= a_end.getX())
            {
                return b_start;
            }
            else if(b_start.getX() <= a_start.getX() && b_end.getX() >= a_start.getX() && b_end.getX() <= a_end.getX())
            {
                return b_end;
            }

            return null;
        }

        double determinantLine1 = determinant(x1, y1, x2, y2);
        double determinantLine2 = determinant(x3, y3, x4, y4);
        double xValue = determinant(determinantLine1, x1-x2, determinantLine2, x3-x4);
        double yValue = determinant(determinantLine1, y1-y2, determinantLine2, y3-y4);
        double xToCheck = xValue/parallelDenominator;
        double yToCheck = yValue/parallelDenominator;

        if (((geq(x1, xToCheck) && leq(x2, xToCheck)) || (geq(x2, xToCheck) && leq(x1, xToCheck))) && ((geq(y1, yToCheck) && leq(y2, yToCheck)) || (geq(y2, yToCheck) && leq(y1, yToCheck))))
        {
            if (((geq(x3, xToCheck) && leq(x4, xToCheck)) || (geq(x4, xToCheck) && leq(x3, xToCheck))) && ((geq(y3, yToCheck) && leq(y4, yToCheck)) || (geq(y4, yToCheck) && leq(y3, yToCheck)))) {
                return new Vector(xToCheck, yToCheck);
            }
        }

        return null;
    }

    private static Vector twoLinesIntersect(Line a, Line b) {
        return twoLinesIntersect(a.getStart(), a.getEnd(), b.getStart(), b.getEnd());
    }

    /**
     * matrix defined as
     * | a b |
     * | c d |
     * note,
     * @param x1 a or d
     * @param y1 c or b
     * @param x2 b or c
     * @param y2 d or a
     * @return The determinant of a 2x2 matrix
     */
    private static double determinant(double x1, double y1, double x2, double y2){
        return (x1*y2)-(x2*y1);
    }
}
