package logic.shapesAbstraction;

import logic.shapesAbstraction.interfaces.Shape;
import logic.transformations.Transformation2D;

import java.util.Iterator;

public class Line implements Shape {
    private static final int a = 0, b = 1;
    private Vector2D[] points;

    public Line(double xa, double ya, double xb, double yb) {
        points = new Vector2D[]{new Vector2D(xa, ya), new Vector2D(xb, yb)};
    }

    public Line(Vector2D a, Vector2D b) {
        points = new Vector2D[]{a, b};
    }

    @Override
    public Rectangle getBoundingBox() {
        return Shape.standardBoundingBox(points);
    }

    @Override
    public void applyTransformation(Transformation2D transformation) {
        for (int i = 0; i < points.length; i++) {
            points[i] = points[i].applyTransformation(transformation);
        }
    }

    @Override
    public Vector2D getPointFromCoordinates(double x, double y) {
        return Shape.coordinatesPoint(points, x, y);
    }

    @Override
    public Vector2D getMiddlePoint() {
        return Shape.middlePoint(points);
    }

    @Override
    public Vector2D getPoint(int index) {
        return points[index];
    }

    @Override
    public Iterator<Vector2D> getPointsIterator() {
        return Shape.standardPointsIterator(points);
    }

    @Override
    public Vector2D[] getAllPoints() {
        return points;
    }
}
