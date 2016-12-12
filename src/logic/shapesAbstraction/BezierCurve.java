package logic.shapesAbstraction;

import logic.shapesAbstraction.interfaces.Shape;
import logic.transformations.Transformation2D;
import logic.utilities.MathUtils;

import java.util.Iterator;

public class BezierCurve implements Shape {
    private Vector2D[] points;
    private double[] newtonValues;

    public BezierCurve(Vector2D[] points) {
        this.points = points;
        calculateNewtonValues();
    }

    public Vector2D getCurvePoint(double t) {
        double resultX = 0.0;
        double resultY = 0.0;
        for (int i = 0; i < points.length; i++) {
            double coefficient = newtonValues[i] * Math.pow(1.0 - t, points.length - 1 - i) * Math.pow(t, i);
            resultX += coefficient * points[i].getX();
            resultY += coefficient * points[i].getY();
        }
        return new Vector2D(resultX, resultY);
    }

    @Override
    public Rectangle getBoundingBox() {
        return Shape.standardBoundingBox(this.points);
    }

    @Override
    public void applyTransformation(Transformation2D transformation) {
        for (int i = 0; i < points.length; i++) {
            points[i] = points[i].applyTransformation(transformation);
        }
    }

    @Override
    public Vector2D getPointFromCoordinates(double x, double y) {
        return Shape.coordinatesPoint(this.points, x, y);
    }

    @Override
    public Vector2D getMiddlePoint() {
        return Shape.middlePoint(this.points);
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

    private void calculateNewtonValues() {
        newtonValues = new double[points.length];
        for (long i = 0; i < points.length; i++) {
            newtonValues[(int) i] = MathUtils.newtonSymbol(points.length - 1, i);
        }
    }
}
