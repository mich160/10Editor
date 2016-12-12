package logic.utilities;

public class MathUtils {
    public static final int A = 0, B = 1;

    public static long newtonSymbol(long n, long k) {
        if (k < 0 || k > n) {
            return 0;
        }
        if (k == 0 || k == n) {
            return 1;
        }
        long kMin = Math.min(k, n - k);
        long result = 1;
        for (int i = 1; i <= kMin; i++) {
            result = result * (n - i + 1) / i;
        }
        return result;
    }

    public static double[] getLinearCoefficients(double x1, double y1, double x2, double y2) {
        double a = (y1 - y2) / (x1 - x2);
        double b = y1 - a * x1;
        return new double[]{a, b};
    }

    public static double triangleArea(double x1, double y1, double x2, double y2, double x3, double y3) {
        return (x3 * y2 - x2 * y3) - (x3 * y1 - x1 * y3) + (x2 * y1 - x1 * y2);
    }

    public static double constraintValue(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static int constraintValue(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static double log2(double x) {
        return Math.log(x) / Math.log(2.0);
    }

    public static double shannons(double x) {
        if (Double.compare(x, 0) == 0 || Double.compare(x, 1.0) == 0) {
            return 0;
        }
        return -1.0 * x * log2(x) - (1 - x) * log2(1 - x);
    }
}
