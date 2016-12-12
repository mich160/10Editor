package view.fxml;

import javafx.fxml.FXML;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import view.customFXClasses.TextureFactory;

public class CubeWindowController {
    private Stage stage;

    private Rotate rx, ry, rz;
    MeshView box;

    @FXML
    AmbientLight ambientLight;
    @FXML
    Group mainGroup;
    @FXML
    PerspectiveCamera camera;

    @FXML
    public void initialize() {
        Image boxTexture = TextureFactory.getRGBFaces(100);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(boxTexture);
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(
                0f, 0f, 0f,// A 0
                100f, 0f, 0f,// B 1
                100f, 0f, 100f,// C 2
                0f, 0f, 100f,// D 3
                0f, 100f, 0f,// E 4
                100f, 100f, 0f,// F 5
                100f, 100f, 100f,// G 6
                0f, 100f, 100f// H 7
        );
        mesh.getTexCoords().addAll(
                0f, 0f,//0
                1f / 2f, 0f,//1
                1f, 0f,//2
                0f, 1f / 3f,//3
                1f / 2f, 1f / 3f,//4
                1f, 1f / 3f,//5
                0f, 2f / 3f,//6
                1f / 2f, 2f / 3f,//7
                1f, 2f / 3f,//8
                0, 1f,//9
                1f / 2f, 1f,//10
                1f, 1f//11
        );
        mesh.getFaces().addAll( // p0, t0, p1, t1, p3, t3, where p0, p1, p2 and p3 are indices into the points array, and t0, t1, t2 and t3 are indices into the texCoords array.
                1, 7, 0, 6, 4, 3,
                4, 3, 5, 4, 1, 7,
                2, 5, 1, 4, 5, 1,
                5, 1, 6, 2, 2, 5,
                5, 10, 4, 9, 7, 6,
                7, 6, 6, 7, 5, 10,
                0, 8, 3, 7, 7, 4,
                7, 4, 4, 5, 0, 8,
                3, 4, 2, 3, 6, 0,
                6, 0, 7, 1, 3, 4,
                0, 11, 1, 10, 2, 7,
                2, 7, 3, 8, 0, 11

        );
        box = new MeshView(mesh);
        box.setMaterial(material);
        mainGroup.getChildren().add(box);
    }

    public void postInitializationRoutine() {
        double xPivot = 50;
        double yPivot = 50;
        double zPivot = 50;
        rx = new Rotate();
        rx.setAxis(Rotate.X_AXIS);
        rx.setPivotX(xPivot);
        rx.setPivotY(yPivot);
        rx.setPivotZ(zPivot);
        ry = new Rotate();
        ry.setAxis(Rotate.Y_AXIS);
        ry.setPivotX(xPivot);
        ry.setPivotY(yPivot);
        ry.setPivotZ(zPivot);
        rz = new Rotate();
        rz.setAxis(Rotate.Z_AXIS);
        rz.setPivotX(xPivot);
        rz.setPivotY(yPivot);
        rz.setPivotZ(zPivot);
        box.getTransforms().addAll(rx, ry, rz);
        ambientLight.setColor(Color.WHITE);
        stage.getScene().setCamera(camera);
        camera.setTranslateY(-190);
        camera.setTranslateX(-230);
        camera.setTranslateZ(-100);
        initializeListeners();
    }

    private void initializeListeners() {
        stage.getScene().setOnKeyPressed(event -> {
            KeyCode keyCode = event.getCode();
            switch (keyCode) {
                case RIGHT:
                    ry.setAngle(ry.getAngle() - 5);
                    break;
                case LEFT:
                    ry.setAngle(ry.getAngle() + 5);
                    break;
                case UP:
                    rx.setAngle(rx.getAngle() - 5);
                    break;
                case DOWN:
                    rx.setAngle(rx.getAngle() + 5);
                    break;
            }
        });
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
