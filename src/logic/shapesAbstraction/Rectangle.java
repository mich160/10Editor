package logic.shapesAbstraction;

import logic.shapesAbstraction.interfaces.Shape;
import logic.transformations.Transformation2D;
import logic.utilities.MathUtils;

import java.util.Iterator;

public class Rectangle implements Shape {
    private Vector2D[] points;

    public Rectangle(Vector2D[] points) {
        if (points.length != 4) {
            this.points = new Vector2D[4];
            System.arraycopy(points, 0, this.points, 0, 4);
        } else {
            this.points = points;
        }
    }

    public Rectangle(double aX, double aY, double bX, double bY) {
        double cY;
        double dY;
        double width = Math.abs(aX - bX);
        double height = Math.abs(aY - bY);
        if (aY > bY) {
            cY = aY - height;
            dY = bY + height;
        } else {
            cY = aY + height;
            dY = bY - height;
        }
        double[] xValues = {bX, aX, bX};
        double[] yValues = {bY, cY, dY};
        double minXYSum = aX + aY;
        double leftUpperX = aX;
        double leftUpperY = aY;
        for (int i = 0; i < xValues.length; i++) {
            double currentXYSum = xValues[i] + yValues[i];
            if (currentXYSum < minXYSum) {
                minXYSum = currentXYSum;
                leftUpperX = xValues[i];
                leftUpperY = yValues[i];
            }
        }
        points = new Vector2D[4];
        points[0] = new Vector2D(leftUpperX, leftUpperY);
        points[1] = new Vector2D(leftUpperX + width, leftUpperY);
        points[2] = new Vector2D(leftUpperX + width, leftUpperY + height);
        points[3] = new Vector2D(leftUpperX, leftUpperY + height);
    }

    @Override
    public Rectangle getBoundingBox() {
        return Shape.standardBoundingBox(points);
    }

    @Override
    public void applyTransformation(Transformation2D transformation) {
        for (int i = 0; i < 4; i++) {
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
    public Iterator<Vector2D> getPointsIterator() {
        return Shape.standardPointsIterator(this.points);
    }

    public boolean isPointInside(double x, double y) {
        return !(MathUtils.triangleArea(points[0].getX(), points[0].getY(), points[1].getX(), points[1].getY(), x, y) > 0
                || MathUtils.triangleArea(points[1].getX(), points[1].getY(), points[2].getX(), points[2].getY(), x, y) > 0
                || MathUtils.triangleArea(points[2].getX(), points[2].getY(), points[3].getX(), points[3].getY(), x, y) > 0
                || MathUtils.triangleArea(points[3].getX(), points[3].getY(), points[0].getX(), points[0].getY(), x, y) > 0);
    }

    public Vector2D getPoint(int index) {
        return points[index];
    }

    public Vector2D[] getAllPoints() {
        return points;
    }

    public double getHeight() {
        return points[0].distanceTo(points[1].getX(), points[1].getY());
    }

    public double getWidth() {
        return points[1].distanceTo(points[2].getX(), points[2].getY());
    }
}
