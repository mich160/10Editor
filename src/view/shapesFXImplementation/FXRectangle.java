package view.shapesFXImplementation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import logic.shapesAbstraction.Vector2D;
import logic.shapesAbstraction.interfaces.Shape;
import view.shapesFXImplementation.interfaces.FXShape;

public class FXRectangle implements FXShape {
    private logic.shapesAbstraction.Rectangle shape;
    private Color color;
    private double lineWidth;

    public FXRectangle(double x1, double y1, double x2, double y2) {
        shape = new logic.shapesAbstraction.Rectangle(x1, y1, x2, y2);
        color = DEFAULT_COLOR;
        lineWidth = DEFAULT_WIDTH;
    }

    public FXRectangle(Vector2D[] points) {
        shape = new logic.shapesAbstraction.Rectangle(points);
        color = Color.BLACK;
        lineWidth = 1.0;
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setStroke(color);
        graphicsContext.setLineWidth(lineWidth);
        Vector2D[] points = shape.getAllPoints();
        graphicsContext.strokePolygon(new double[]{points[0].getX(), points[1].getX(), points[2].getX(), points[3].getX()}, new double[]{points[0].getY(), points[1].getY(), points[2].getY(), points[3].getY()}, 4);
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

    public void setShape(logic.shapesAbstraction.Rectangle shape) {
        this.shape = shape;
    }

    @Override
    public logic.shapesAbstraction.Rectangle getBoundingBox() {
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
        return shape.getAllPoints().length == 4;
    }
}
