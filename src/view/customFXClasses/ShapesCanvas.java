package view.customFXClasses;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import logic.shapesAbstraction.Vector2D;
import logic.transformations.Transformation2D;
import view.shapesFXImplementation.FXBezierCurve;
import view.shapesFXImplementation.FXCircle;
import view.shapesFXImplementation.FXLine;
import view.shapesFXImplementation.FXRectangle;
import view.shapesFXImplementation.interfaces.FXShape;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ShapesCanvas extends Canvas {

    private interface ShapeMaintainer {
        FXShape createShape(double x, double y);

        FXShape modifyShape(FXShape shape, double x, double y);

    }

    public enum Mode {
        DRAW,
        EDIT,
    }

    public enum DrawnShape {
        RECTANGLE,
        CIRCLE,
        BEZIER,
        LINE
    }

    private List<FXShape> shapes;
    private Image image;
    private Map<DrawnShape, ShapeMaintainer> shapeMaintainers;
    private Color currentColor;
    private FXShape selectedShape;
    private FXShape drawnShape;
    private Mode mode;
    private DrawnShape drawnShapeType;
    private GraphicsContext context;
    private Vector2D createPoint;
    private Vector2D selectedPoint;

    public ShapesCanvas() {
        super();
        initializeListeners();
        initializeMaintainers();
        widthProperty().addListener((observable, oldValue, newValue) -> {
            drawAll();
        });
        heightProperty().addListener((observable, oldValue, newValue) -> {
            drawAll();
        });
        shapes = new LinkedList<>();
        context = getGraphicsContext2D();
        mode = Mode.DRAW;
        drawnShapeType = DrawnShape.RECTANGLE;
        currentColor = FXShape.DEFAULT_COLOR;
    }

    public ShapesCanvas(Pane bindingPane) {
        this();
        bindSizeTo(bindingPane);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    public void bindSizeTo(Pane pane) {
        this.widthProperty().bind(pane.widthProperty());
        this.heightProperty().bind(pane.heightProperty());
    }

    public void loadImage(Image image) {// TODO: 22.05.2016
        this.image = image;
        drawAll();
    }

    private void initializeListeners() {
        setOnMouseMoved(this::onMouseMove);
        setOnMouseClicked(this::onMouseClick);
        setOnMouseDragged(this::onMouseDrag);
        setOnMousePressed(this::onMousePress);
    }

    private void initializeMaintainers() {
        shapeMaintainers = new HashMap<>();
        shapeMaintainers.put(DrawnShape.RECTANGLE, new ShapeMaintainer() {
            @Override
            public FXShape createShape(double x, double y) {
                createPoint = new Vector2D(x, y);
                FXRectangle rectangle = new FXRectangle(x, y, x, y);
                rectangle.setColor(currentColor);
                return rectangle;
            }

            @Override
            public FXShape modifyShape(FXShape shape, double x, double y) {
                FXRectangle rectangle = new FXRectangle(createPoint.getX(), createPoint.getY(), x, y);
                rectangle.setColor(currentColor);
                return rectangle;
            }

        });
        shapeMaintainers.put(DrawnShape.BEZIER, new ShapeMaintainer() {
            @Override
            public FXShape createShape(double x, double y) {
                FXBezierCurve bezier = new FXBezierCurve(new Vector2D[]{new Vector2D(x, y)});
                bezier.setColor(currentColor);
                return bezier;
            }

            @Override
            public FXShape modifyShape(FXShape shape, double x, double y) {
                Vector2D[] oldPoints = drawnShape.getShape().getAllPoints();
                Vector2D[] newPoints = new Vector2D[oldPoints.length + 1];
                System.arraycopy(oldPoints, 0, newPoints, 0, oldPoints.length);
                newPoints[newPoints.length - 1] = new Vector2D(x, y);
                FXBezierCurve bezier = new FXBezierCurve(newPoints);
                bezier.setColor(currentColor);
                return bezier;
            }

        });
        shapeMaintainers.put(DrawnShape.LINE, new ShapeMaintainer() {
            @Override
            public FXShape createShape(double x, double y) {
                createPoint = new Vector2D(x, y);
                FXLine line = new FXLine(x, y, x, y);
                line.setColor(currentColor);
                return line;
            }

            @Override
            public FXShape modifyShape(FXShape shape, double x, double y) {
                FXLine line = new FXLine(createPoint.getX(), createPoint.getY(), x, y);
                line.setColor(currentColor);
                return line;
            }
        });
        shapeMaintainers.put(DrawnShape.CIRCLE, new ShapeMaintainer() {
            @Override
            public FXShape createShape(double x, double y) {
                createPoint = new Vector2D(x, y);
                FXCircle circle = new FXCircle(x, y, x, y);
                circle.setColor(currentColor);
                return circle;
            }

            @Override
            public FXShape modifyShape(FXShape shape, double x, double y) {
                FXCircle circle = new FXCircle(createPoint.getX(), createPoint.getY(), x, y);
                circle.setColor(currentColor);
                return circle;
            }
        });
    }

    private void onMouseMove(MouseEvent event) {
        switch (mode) {
            case DRAW:
                if (drawnShape != null && drawnShape.pointsFull()) {
                    drawnShape = shapeMaintainers.get(drawnShapeType).modifyShape(drawnShape, event.getX(), event.getY());
                }
                break;
            case EDIT:
                break;
        }
        drawAll();
    }

    private void onMouseClick(MouseEvent event) {
        switch (mode) {
            case DRAW:
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (drawnShape == null) {
                        drawnShape = shapeMaintainers.get(drawnShapeType).createShape(event.getX(), event.getY());
                    } else {
                        drawnShape = shapeMaintainers.get(drawnShapeType).modifyShape(drawnShape, event.getX(), event.getY());
                        if (drawnShape.pointsFull()) {
                            shapes.add(drawnShape);
                            drawnShape = null;
                            createPoint = null;
                        }
                    }
                } else {
                    shapes.add(drawnShape);
                    drawnShape = null;
                    createPoint = null;
                }
                break;
            case EDIT:
                this.selectedShape = getShapeByPosition(event.getX(), event.getY());
                break;
        }
        drawAll();
    }

    private void onMousePress(MouseEvent event) {
        switch (mode) {
            case DRAW:
                break;
            case EDIT:
                if (selectedShape != null) {
                    selectedPoint = selectedShape.getShape().getPointFromCoordinates(event.getX(), event.getY());
                }
                break;
        }
    }

    private void onMouseDrag(MouseEvent event) {
        switch (mode) {
            case DRAW:
                break;
            case EDIT:
                if (selectedPoint != null) {
                    selectedPoint.setX(event.getX());
                    selectedPoint.setY(event.getY());
                }
                break;
        }
        drawAll();
    }

    private void cancelAction() {
        drawnShape = null;
        selectedShape = null;
        createPoint = null;
        selectedPoint = null;
    }

    private FXShape getShapeByPosition(double x, double y) {
        for (FXShape shape : shapes) {
            if (shape.getBoundingBox().isPointInside(x, y)) {
                return shape;
            }
        }
        return null;
    }

    private Vector2D getPointByPosition(double x, double y) {
        return selectedShape.getShape().getPointFromCoordinates(x, y);
    }

    public void clear() {
        selectedShape = null;
        selectedPoint = null;
        image = null;
        shapes.clear();
        context.clearRect(0.0, 0.0, getWidth(), getHeight());
    }

    public void drawAll() {
        context.clearRect(0.0, 0.0, getWidth(), getHeight());
        context.drawImage(image, 0.0, 0.0);
        for (FXShape shape : shapes) {
            shape.draw(context);
        }
        switch (mode) {
            case DRAW:
                if (drawnShape != null) {
                    drawnShape.draw(context);
                }
                break;
            case EDIT:
                if (selectedShape != null) {
                    selectedShape.drawBoundingBox(context);
                    selectedShape.drawPoints(context);
                }
                break;
        }
    }

    public void translateSelectedShape(double x, double y) {
        if (checkSelectedShape()) {
            selectedShape.getShape().applyTransformation(new Transformation2D(Transformation2D.getTranslationMatrix(x, y)));
            drawAll();
        }
    }

    public void rotateSelectedShape(double angle) {
        if (checkSelectedShape()) {
            selectedShape.getShape().applyTransformation(new Transformation2D(Transformation2D.getRotationMatrix(angle)));
            drawAll();
        }
    }

    public void rotateSelectedShape(double x, double y, double angle) {
        if (checkSelectedShape()) {
            selectedShape.getShape().applyTransformation(new Transformation2D(Transformation2D.getRotationMatrixAroundPoint(x, y, angle)));
            drawAll();
        }
    }

    public void scaleSelectedShape(double sx, double sy) {
        if (checkSelectedShape()) {
            selectedShape.getShape().applyTransformation(new Transformation2D(Transformation2D.getScaleMatrix(sx, sy)));
            drawAll();
        }
    }

    public void scaleSelectedShape(double x, double y, double sx, double sy) {
        if (checkSelectedShape()) {
            selectedShape.getShape().applyTransformation(new Transformation2D(Transformation2D.getScaleMatrixAboutPoint(x, y, sx, sy)));
            drawAll();
        }
    }

    public void addLine(double xa, double ya, double xb, double yb) {
        FXLine addedLine = new FXLine(xa, ya, xb, yb);
        addedLine.setColor(currentColor);
        shapes.add(addedLine);
    }

    public void addCircle(double x, double y, double r) {
        FXCircle addedCircle = new FXCircle(x, y, x + r, y + r);
        addedCircle.setColor(currentColor);
        shapes.add(addedCircle);
    }

    public void addRectangle(double xa, double ya, double xb, double yb) {
        FXRectangle addedRectangle = new FXRectangle(xa, ya, xb, yb);
        addedRectangle.setColor(currentColor);
        shapes.add(addedRectangle);
    }

    private boolean checkSelectedShape() {
        if (selectedShape == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Nie zaznaczono kształtu.");
            alert.setContentText("Przed przekształceniem trzeba uprzednio zaznaczyć kształt");
            alert.show();
            return false;
        }
        return true;
    }

    public DrawnShape getDrawnShapeType() {
        return drawnShapeType;
    }

    public void setDrawnShapeType(DrawnShape drawnShapeType) {
        this.drawnShapeType = drawnShapeType;
        cancelAction();
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        cancelAction();
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(Color currentColor) {
        this.currentColor = currentColor;
    }
}
