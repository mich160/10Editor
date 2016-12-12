package logic.utilities;

public class ValidationUtils {
    public static boolean isValidDouble(String number) {
        try {
            Double.parseDouble(number);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }
}
