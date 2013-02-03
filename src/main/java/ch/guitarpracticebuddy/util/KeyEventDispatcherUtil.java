package ch.guitarpracticebuddy.util;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created with IntelliJ IDEA.
 * User: mjunker
 * Date: 2/2/13
 * Time: 12:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class KeyEventDispatcherUtil {


    public static void addKeyListener(KeyEventListener keyEventListener) {


        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher(keyEventListener));

    }


    public static class KeyEventListener {

        public void onKeyPressed(KeyEvent e) {
        }

        public void onKeyReleased(KeyEvent e) {
        }
    }

    private static class MyDispatcher implements KeyEventDispatcher {

        private KeyEventListener keyEventListener;

        private MyDispatcher(KeyEventListener keyEventListener) {
            this.keyEventListener = keyEventListener;
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                keyEventListener.onKeyPressed(e);
            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                keyEventListener.onKeyReleased(e);
            } else if (e.getID() == KeyEvent.KEY_TYPED) {
            }
            return false;
        }

    }
}
