package logic.shapesAbstraction;

import logic.transformations.Transformation2D;

public class Vector2D {
    private double x, y;

    public Vector2D() {
        this.x = 0.0;
        this.y = 0.0;
    }

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vector2D vector2D) {
        this.x = vector2D.x;
        this.y = vector2D.y;
    }

    public void add(double x, double y) {
        this.x += x;
        this.y += y;
    }

    public void add(Vector2D vector2D) {
        this.x += vector2D.x;
        this.y += vector2D.y;
    }

    public void scale(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
    }

    public void normalize() {
        double thisLength = getLength();
        this.x /= thisLength;
        this.y /= thisLength;
    }

    public double distanceTo(double x, double y) {
        return Math.sqrt(Math.pow(this.x - x, 2.0) + Math.pow(this.y - y, 2.0));
    }

    public double getLength() {
        return Math.sqrt(Math.pow(this.x, 2.0) + Math.pow(this.y, 2.0));
    }

    public Vector2D applyTransformation(Transformation2D transformation) {
        return new Vector2D(transformation.getValue(0, 0) * this.x + transformation.getValue(0, 1) * this.y + transformation.getValue(0, 2), transformation.getValue(1, 0) * this.x + transformation.getValue(1, 1) * this.y + transformation.getValue(1, 2));
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Vector2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
