package com.calanders.calengine.engine;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX;
    private double scrollY;
    private double xPos;
    private double yPos;
    private double lastX;
    private double lastY;
    private boolean mouseButtonPressed[] = new boolean[3];
    private boolean isDragging;

    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    public static void cursorPosCallback(long window, double xPos, double yPos) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xPos;
        get().yPos = yPos;
        get().isDragging = get().mouseButtonPressed[0]
                || get().mouseButtonPressed[1]
                || get().mouseButtonPressed[2];
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (button < get().mouseButtonPressed.length) {
            if (action == GLFW_PRESS) {
                get().mouseButtonPressed[button] = true;
            } else if (action == GLFW_RELEASE) {
                get().mouseButtonPressed[button] = false;
                get().isDragging = false;
            }
        }
    }

    public static void scrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static void endFrame() {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    public static MouseListener get() {
        if (instance == null) {
            instance = new MouseListener();
        }
        return instance;
    }

    public static float getX() {
        return (float) get().xPos;
    }

    public static float getY() {
        return (float) get().yPos;
    }

    public static float getDx() {
        return (float) (get().lastX - get().xPos);
    }

    public static float getDy() {
        return (float) (get().lastY - get().yPos);
    }

    public static float getScrollX() {
        return (float) get().scrollX;
    }

    public static float getScrollY() {
        return (float) get().scrollY;
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int button) {
        return button < get().mouseButtonPressed.length && get().mouseButtonPressed[button];
    }
}
