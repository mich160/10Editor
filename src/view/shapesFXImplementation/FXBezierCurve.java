package view.shapesFXImplementation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import logic.shapesAbstraction.Rectangle;
import logic.shapesAbstraction.Vector2D;
import logic.shapesAbstraction.interfaces.Shape;
import view.shapesFXImplementation.interfaces.FXShape;

public class FXBezierCurve implements FXShape {
    final static double DRAWING_STEP = 0.01;
    logic.shapesAbstraction.BezierCurve shape;
    Color color;
    double lineWidth;

    public FXBezierCurve(Vector2D[] points) {
        shape = new logic.shapesAbstraction.BezierCurve(points);
        color = Color.BLACK;
        lineWidth = 1.0;
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setStroke(color);
        graphicsContext.setLineWidth(lineWidth);
        Vector2D previous = shape.getCurvePoint(0.0);
        for (double t = DRAWING_STEP; t <= 1.0; t += DRAWING_STEP) {
            Vector2D current = shape.getCurvePoint(t);
            graphicsContext.strokeLine(previous.getX(), previous.getY(), current.getX(), current.getY());
            previous = current;
        }
    }

    @Override
    public void drawBoundingBox(GraphicsContext graphicsContext) {
        FXShape.drawBoundingBox(this, graphicsContext);
    }

    @Override
    public void drawPoints(GraphicsContext graphicsContext) {
        FXShape.drawPoints(this, graphicsContext);
    }

    @Override
    public Shape getShape() {
        return this.shape;
    }

    @Override
    public Rectangle getBoundingBox() {
        return shape.getBoundingBox();
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public double getLineWidth() {
        return lineWidth;
    }

    @Override
    public void setLineWidth(double width) {
        this.lineWidth = width;
    }

    @Override
    public boolean pointsFull() {
        return false;
    }

}
