package view.shapesFXImplementation.interfaces;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import logic.shapesAbstraction.Rectangle;
import logic.shapesAbstraction.Vector2D;
import logic.shapesAbstraction.interfaces.Shape;

import java.util.Iterator;

public interface FXShape {
    Color DEFAULT_COLOR = Color.BLACK;
    Color DEFAULT_BOUNDING_COLOR = Color.BLUE;
    Color DEFAULT_POINT_COLOR = Color.RED;
    double DEFAULT_WIDTH = 1.0;

    static void drawBoundingBox(FXShape shape, GraphicsContext graphicsContext) {
        Vector2D[] points = shape.getBoundingBox().getAllPoints();
        graphicsContext.setStroke(DEFAULT_BOUNDING_COLOR);
        graphicsContext.setLineWidth(1.0);
        graphicsContext.strokePolygon(new double[]{points[0].getX(), points[1].getX(), points[2].getX(), points[3].getX()}, new double[]{points[0].getY(), points[1].getY(), points[2].getY(), points[3].getY()}, 4);
    }

    static void drawPoints(FXShape shape, GraphicsContext graphicsContext) {
        graphicsContext.setFill(DEFAULT_POINT_COLOR);
        Iterator<Vector2D> pointsIterator = shape.getShape().getPointsIterator();
        while (pointsIterator.hasNext()) {
            Vector2D current = pointsIterator.next();
            graphicsContext.fillOval(current.getX() - 1.5, current.getY() - 1.5, 3.0, 3.0);
        }
    }

    void draw(GraphicsContext graphicsContext);

    void drawBoundingBox(GraphicsContext graphicsContext);

    void drawPoints(GraphicsContext graphicsContext);

    Shape getShape();

    Rectangle getBoundingBox();

    Color getColor();

    void setColor(Color color);

    double getLineWidth();

    void setLineWidth(double width);

    boolean pointsFull();
}
