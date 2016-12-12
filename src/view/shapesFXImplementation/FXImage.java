package view.shapesFXImplementation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import logic.shapesAbstraction.Rectangle;
import logic.shapesAbstraction.interfaces.Shape;
import view.shapesFXImplementation.interfaces.FXShape;

public class FXImage implements FXShape {
    private Image image;

    public FXImage(Image image) {
        this.image = image;
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.drawImage(image, 0.0, 0.0);
    }

    @Override
    public void drawBoundingBox(GraphicsContext graphicsContext) {
        graphicsContext.setStroke(DEFAULT_BOUNDING_COLOR);
        graphicsContext.setLineWidth(1.0);
        graphicsContext.strokePolygon(new double[]{0.0, image.getWidth(), image.getWidth(), 0.0}, new double[]{0.0, 0.0, image.getHeight(), image.getHeight()}, 4);
    }

    @Override
    public void drawPoints(GraphicsContext graphicsContext) {
        graphicsContext.setFill(DEFAULT_POINT_COLOR);
        graphicsContext.fillOval(-1.5, -1.5, 3.0, 3.0);
        graphicsContext.fillOval(image.getWidth() - 1.5, -1.5, 3.0, 3.0);
        graphicsContext.fillOval(image.getWidth() - 1.5, image.getHeight() - 1.5, 3.0, 3.0);
        graphicsContext.fillOval(-1.5, image.getHeight() - 1.5, 3.0, 3.0);
    }

    @Override
    public Shape getShape() {
        return null;
    }

    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle(0.0, 0.0, image.getWidth(), image.getHeight());
    }

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public void setColor(Color color) {
    }

    @Override
    public double getLineWidth() {
        return 0;
    }

    @Override
    public void setLineWidth(double width) {
    }

    @Override
    public boolean pointsFull() {
        return true;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
