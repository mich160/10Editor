package logic.transformations;

public class Transformation2D {
    private static double[][] getNewMatrix() {
        double[][] result = new double[3][];
        for (int i = 0; i < 3; i++) {
            result[i] = new double[3];
        }
        return result;
    }

    public static double[][] getRotationMatrix(double angle) {
        double[][] result = getNewMatrix();
        result[0][0] = Math.cos(angle);
        result[0][1] = -Math.sin(angle);
        result[0][2] = 0.0;
        result[1][0] = Math.sin(angle);
        result[1][1] = Math.cos(angle);
        result[1][2] = 0.0;
        result[2][0] = 0.0;
        result[2][1] = 0.0;
        result[2][2] = 1.0;
        return result;
    }

    public static double[][] getTranslationMatrix(double dx, double dy) {
        double[][] result = getNewMatrix();
        result[0][0] = 1.0;
        result[0][1] = 0.0;
        result[0][2] = dx;
        result[1][0] = 0.0;
        result[1][1] = 1.0;
        result[1][2] = dy;
        result[2][0] = 0.0;
        result[2][1] = 0.0;
        result[2][2] = 1.0;
        return result;
    }

    public static double[][] getScaleMatrix(double scaleX, double scaleY) {
        double[][] result = getNewMatrix();
        result[0][0] = scaleX;
        result[0][1] = 0.0;
        result[0][2] = 0.0;
        result[1][0] = 0.0;
        result[1][1] = scaleY;
        result[1][2] = 0.0;
        result[2][0] = 0.0;
        result[2][1] = 0.0;
        result[2][2] = 1.0;
        return result;
    }

    public static double[][] getRotationMatrixAroundPoint(double x, double y, double angle) {
        double[][] result = getNewMatrix();
        double sinAngle = Math.sin(angle);
        double cosAngle = Math.cos(angle);
        result[0][0] = cosAngle;
        result[0][1] = -sinAngle;
        result[0][2] = -(x * cosAngle) + y * sinAngle + x;
        result[1][0] = sinAngle;
        result[1][1] = cosAngle;
        result[1][2] = -(x * sinAngle) - y * cosAngle + y;
        result[2][0] = 0.0;
        result[2][1] = 0.0;
        result[2][2] = 1.0;
        return result;
    }

    public static double[][] getScaleMatrixAboutPoint(double x, double y, double scaleX, double scaleY) {
        double[][] result = getNewMatrix();
        result[0][0] = scaleX;
        result[0][1] = 0.0;
        result[0][2] = x - x * scaleX;
        result[1][0] = 0.0;
        result[1][1] = scaleY;
        result[1][2] = y - y * scaleY;
        result[2][0] = 0.0;
        result[2][1] = 0.0;
        result[2][2] = 1.0;
        return result;
    }

    public static double[][] getScaleMatrixAboutPoint(double x, double y, double scale) {
        return getScaleMatrixAboutPoint(x, y, scale, scale);
    }

    double[][] matrix;

    public Transformation2D(double[][] matrix) {
        this.matrix = matrix;
    }

    public Transformation2D(Transformation2D transformation2D) {
        this.matrix = transformation2D.copyMatrix();
    }

    public Transformation2D combine(Transformation2D transformation2D) {
        double[][] result = getNewMatrix();
        result[0][0] = matrix[0][0] * transformation2D.matrix[0][0] + matrix[0][1] * transformation2D.matrix[1][0] + matrix[0][2] * transformation2D.matrix[2][0];
        result[0][1] = matrix[0][0] * transformation2D.matrix[0][1] + matrix[0][1] * transformation2D.matrix[1][1] + matrix[0][2] * transformation2D.matrix[2][1];
        result[0][2] = matrix[0][0] * transformation2D.matrix[0][2] + matrix[0][1] * transformation2D.matrix[1][2] + matrix[0][2] * transformation2D.matrix[2][2];
        result[1][0] = matrix[1][0] * transformation2D.matrix[0][0] + matrix[1][1] * transformation2D.matrix[1][0] + matrix[1][2] * transformation2D.matrix[2][0];
        result[1][1] = matrix[1][0] * transformation2D.matrix[0][1] + matrix[1][1] * transformation2D.matrix[1][1] + matrix[1][2] * transformation2D.matrix[2][1];
        result[1][2] = matrix[1][0] * transformation2D.matrix[0][2] + matrix[1][1] * transformation2D.matrix[1][2] + matrix[1][2] * transformation2D.matrix[2][2];
        result[2][0] = matrix[2][0] * transformation2D.matrix[0][0] + matrix[2][1] * transformation2D.matrix[1][0] + matrix[2][2] * transformation2D.matrix[2][0];
        result[2][1] = matrix[2][0] * transformation2D.matrix[0][1] + matrix[2][1] * transformation2D.matrix[1][1] + matrix[2][2] * transformation2D.matrix[2][1];
        result[2][2] = matrix[2][0] * transformation2D.matrix[0][2] + matrix[2][1] * transformation2D.matrix[1][2] + matrix[2][2] * transformation2D.matrix[2][2];
        return new Transformation2D(result);
    }

    public double getValue(int i, int j) {
        if (i > 2 || j > 2) {
            throw new ArrayIndexOutOfBoundsException("Value out of bounds!");
        }
        return matrix[i][j];
    }

    public void setValue(int i, int j, double value) {
        if (i > 2 || j > 2) {
            throw new ArrayIndexOutOfBoundsException("Value out of bounds!");
        }
        matrix[i][j] = value;
    }

    public void setMatrix(double[][] matrix) {
        this.matrix = matrix;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    private double[][] copyMatrix() {
        double[][] result = new double[3][];
        for (int i = 0; i < 3; i++) {
            result[i] = new double[3];
            System.arraycopy(matrix[i], 0, result[i], 0, 3);
        }
        return result;
    }

}
