package io.github.captivecow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputController implements KeyListener {
    private final Logger logger = LoggerFactory.getLogger(InputController.class);

    private final int RIGHT_KEY_CODE = 39;
    private final int LEFT_KEY_CODE = 37;
    private final int UP_KEY_CODE = 38;
    private final int DOWN_KEY_CODE = 40;

    private boolean pressingRight;
    private boolean pressingLeft;
    private boolean pressingUp;
    private boolean pressingDown;


    public InputController(){
        pressingRight = false;
        pressingLeft = false;
        pressingUp = false;
        pressingDown = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public synchronized void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()){
            case RIGHT_KEY_CODE: pressingRight = true;
            case LEFT_KEY_CODE: pressingLeft = true;
            case UP_KEY_CODE: pressingUp = true;
            case DOWN_KEY_CODE: pressingDown = true;
        }
    }

    @Override
    public synchronized void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()){
            case RIGHT_KEY_CODE: pressingRight = false;
            case LEFT_KEY_CODE: pressingLeft = false;
            case UP_KEY_CODE: pressingUp = false;
            case DOWN_KEY_CODE: pressingDown = false;
        }
    }

    public synchronized boolean isPressingRight() {
        return pressingRight;
    }

    public synchronized boolean isPressingLeft() {
        return pressingLeft;
    }

    public synchronized boolean isPressingUp() {
        return pressingUp;
    }

    public synchronized boolean isPressingDown() {
        return pressingDown;
    }
}
