package view.customFXClasses;

import javafx.scene.control.Alert;

public class ErrorUtils {
    public static void viewChannelInvalidValueAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText("Zła wartość liczbowa");
        alert.setContentText("Wartość powinna być liczbą z zakresu 0-255.");
        alert.show();
    }

    public static void viewPercentInvalidValueAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText("Zła wartość liczbowa");
        alert.setContentText("Wartość powinna być liczbą z zakresu 0-100.");
        alert.show();
    }

    public static void viewWrongCommandAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText("Złe polecenie");
        alert.setContentText("Nie ma takiego polecenia");
        alert.show();
    }

    public static boolean isValidChannelValue(String value) {
        int primitiveValue;
        try {
            primitiveValue = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return false;
        }
        return !(primitiveValue < 0 || primitiveValue > 255);
    }

    public static boolean isValidPercentValue(String value) {
        double primitiveValue;
        try {
            primitiveValue = Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return false;
        }
        return !(primitiveValue < 0.0 || primitiveValue > 100.0);
    }
}
