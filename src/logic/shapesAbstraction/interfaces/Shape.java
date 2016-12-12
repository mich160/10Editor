package logic.shapesAbstraction.interfaces;

import logic.shapesAbstraction.Rectangle;
import logic.shapesAbstraction.Vector2D;
import logic.transformations.Transformation2D;

import java.util.Iterator;

public interface Shape {
    static Vector2D middlePoint(Vector2D[] points) {
        double midX = 0.0;
        double midY = 0.0;
        for (Vector2D point : points) {
            midX += point.getX();
            midY += point.getY();
        }
        midX /= points.length;
        midY /= points.length;
        return new Vector2D(midX, midY);
    }

    static Vector2D coordinatesPoint(Vector2D[] points, double x, double y) {
        for (Vector2D point : points) {
            if (point.distanceTo(x, y) < Shape.distanceTolerance) {
                return point;
            }
        }
        return null;
    }

    static Iterator<Vector2D> standardPointsIterator(Vector2D[] points) {
        return new Iterator<Vector2D>() {
            private int position = 0;

            @Override
            public boolean hasNext() {
                return position < points.length;
            }

            @Override
            public Vector2D next() {
                return points[position++];
            }
        };
    }

    static Rectangle standardBoundingBox(Vector2D[] points) {
        double xMax = points[0].getX();
        double yMax = points[0].getY();
        double xMin = points[0].getX();
        double yMin = points[0].getY();
        for (Vector2D point : points) {
            if (point.getX() > xMax) {
                xMax = point.getX();
            } else if (point.getX() < xMin) {
                xMin = point.getX();
            }
            if (point.getY() > yMax) {
                yMax = point.getY();
            } else if (point.getY() < yMin) {
                yMin = point.getY();
            }
        }
        return new Rectangle(xMin, yMin, xMax, yMax);
    }

    double distanceTolerance = 20.0;

    Rectangle getBoundingBox();

    void applyTransformation(Transformation2D transformation);

    Vector2D getPointFromCoordinates(double x, double y);

    Vector2D getMiddlePoint();

    Vector2D getPoint(int index);

    Iterator<Vector2D> getPointsIterator();

    Vector2D[] getAllPoints();

    class PointsNumberException extends RuntimeException {
        public PointsNumberException(String message) {
            super(message);
        }
    }
}
