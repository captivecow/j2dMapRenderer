package io.github.captivecow;

import javax.swing.SwingUtilities;

public class J2dMapRenderer {
    public static void main(String[] args) {
        ScreenView screen = new ScreenView();
        SwingUtilities.invokeLater(screen);
    }
}
