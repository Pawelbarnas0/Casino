package casino.gui;

import javax.swing.*;

public class ErrorPanel {
    public static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}