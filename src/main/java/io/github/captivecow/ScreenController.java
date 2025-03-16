package io.github.captivecow;

import javax.swing.SwingUtilities;

public class ScreenController {
    private final ScreenView screen;

    public ScreenController() {
        screen = new ScreenView();
    }

    public void start() {
        SwingUtilities.invokeLater(screen);
    }
}
