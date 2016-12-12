package view.shapesFXImplementation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import logic.shapesAbstraction.Circle;
import logic.shapesAbstraction.Rectangle;
import logic.shapesAbstraction.Vector2D;
import logic.shapesAbstraction.interfaces.Shape;
import view.shapesFXImplementation.interfaces.FXShape;

public class FXCircle implements FXShape {
    private Circle shape;
    private Color color;
    private double lineWidth;

    public FXCircle(double xa, double ya, double xb, double yb) {
        shape = new Circle(new Vector2D(xa, ya), new Vector2D(xb, yb));
        this.color = FXShape.DEFAULT_COLOR;
        this.lineWidth = FXShape.DEFAULT_WIDTH;
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setStroke(color);
        graphicsContext.setLineWidth(lineWidth);
        graphicsContext.strokeOval(shape.getMiddlePoint().getX() - shape.getRadius(), shape.getMiddlePoint().getY() - shape.getRadius(), shape.getRadius() * 2, shape.getRadius() * 2);
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
        return true;
    }
}
