package logic.shapesAbstraction;

import logic.shapesAbstraction.interfaces.Shape;
import logic.transformations.Transformation2D;

import java.util.Iterator;

public class Circle implements Shape {
    private Vector2D middlePoint;
    private Vector2D tangentPoint;

    public Circle(double x, double y, double radius) {
        this.middlePoint = new Vector2D(x, y);
        this.tangentPoint = new Vector2D(x + radius, y + radius);
    }

    public Circle(Vector2D middlePoint, double radius) {
        this.middlePoint = middlePoint;
        this.tangentPoint = new Vector2D(middlePoint.getX() + radius, middlePoint.getY() + radius);
    }

    public Circle(Vector2D middlePoint, Vector2D tangentPoint) {
        this.middlePoint = middlePoint;
        this.tangentPoint = tangentPoint;
    }

    @Override
    public Rectangle getBoundingBox() {
        double radius = getRadius();
        return new Rectangle(middlePoint.getX() - radius, middlePoint.getY() + radius, middlePoint.getX() + radius, middlePoint.getY() - radius);
    }

    @Override
    public void applyTransformation(Transformation2D transformation) {
        middlePoint = middlePoint.applyTransformation(transformation);
        tangentPoint = tangentPoint.applyTransformation(transformation);
    }

    @Override
    public Vector2D getPointFromCoordinates(double x, double y) {
        if (middlePoint.distanceTo(x, y) < Shape.distanceTolerance) {
            return middlePoint;
        }
        return null;
    }

    @Override
    public Vector2D getMiddlePoint() {
        return middlePoint;
    }

    @Override
    public Vector2D getPoint(int index) {
        if (index == 0) {
            return middlePoint;
        } else if (index == 1) {
            return tangentPoint;
        }
        return null;
    }

    @Override
    public Iterator<Vector2D> getPointsIterator() {
        return new Iterator<Vector2D>() {
            boolean newIterator = true;

            @Override
            public boolean hasNext() {
                return newIterator;
            }

            @Override
            public Vector2D next() {
                newIterator = false;
                return middlePoint;
            }
        };
    }

    public Vector2D[] getAllPoints() {
        return new Vector2D[]{middlePoint};
    }

    public double getRadius() {
        return tangentPoint.distanceTo(middlePoint.getX(), middlePoint.getY());
    }

    public void setRadius(double radius) {
        tangentPoint.setX(middlePoint.getX() + radius);
        tangentPoint.setY(middlePoint.getY() + radius);
    }
}
