package com.waiow.engine.graphic;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    public static int WIDTH;
    public static int HEIGHT;

    private AbstractPanel panel;

    public Window(String title, boolean undecorated) {
        this(title, undecorated, (int) SCREEN_SIZE.getWidth(), (int) SCREEN_SIZE.getHeight());
    }

    public Window(String title, boolean undecorated, int width, int height) {
        WIDTH = width;
        HEIGHT = height;
        setTitle(title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(undecorated);
        if (undecorated) {
            getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        }
    }

    public void construct(AbstractPanel panel) {
        this.panel = panel;
        add(this.panel);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    public Dimension getDimension() {
        return new Dimension(WIDTH, HEIGHT);
    }

    public AbstractPanel getPanel() {
        return panel;
    }
}
