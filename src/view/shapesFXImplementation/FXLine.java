package view.shapesFXImplementation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import logic.shapesAbstraction.Line;
import logic.shapesAbstraction.Rectangle;
import logic.shapesAbstraction.Vector2D;
import logic.shapesAbstraction.interfaces.Shape;
import view.shapesFXImplementation.interfaces.FXShape;

public class FXLine implements FXShape {
    private Line shape;
    private Color color;
    private double lineWidth;

    public FXLine(double xa, double ya, double xb, double yb) {
        shape = new Line(xa, ya, xb, yb);
        color = DEFAULT_COLOR;
        lineWidth = DEFAULT_WIDTH;
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setStroke(color);
        graphicsContext.setLineWidth(lineWidth);
        Vector2D[] points = shape.getAllPoints();
        graphicsContext.strokeLine(points[0].getX(), points[0].getY(), points[1].getX(), points[1].getY());
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
        return shape;
    }

    @Override
    public Rectangle getBoundingBox() {
        return shape.getBoundingBox();
    }

    @Override
    public Color getColor() {
        return color;
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
        return this.shape.getAllPoints().length == 2;
    }
}
