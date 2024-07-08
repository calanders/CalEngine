package com.calanders.calengine.engine;

import com.calanders.calengine.util.Time;
import org.lwjgl.Version;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private static Window instance;
    private int width;
    private int height;
    private String title;
    private long window;
    public float r, g, b, a;
    private boolean fadeToBlack;
    private static Scene scene;

    private Window() {
        width = 960;
        height = 540;
        title = "CalEngine";
        r = 1.0f;
        g = 1.0f;
        b = 1.0f;
        a = 1.0f;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion());

        init();
        loop();

        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }
        
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);

        window = GLFW.glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        GLFW.glfwSetCursorPosCallback(window, MouseListener::cursorPosCallback);
        GLFW.glfwSetMouseButtonCallback(window, MouseListener::mouseButtonCallback);
        GLFW.glfwSetScrollCallback(window, MouseListener::scrollCallback);
        GLFW.glfwSetKeyCallback(window, KeyListener::keyCallback);

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(window);
        GL.createCapabilities();

        setScene(0);
    }

    public void loop() {
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1.0f;

        while (!GLFW.glfwWindowShouldClose(window)) {
            GLFW.glfwPollEvents();

            GL11.glClearColor(r, g, b, a);
            GL11.glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {
                scene.update(dt);
            }

            GLFW.glfwSwapBuffers(window);

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    public static void setScene(int scene) {
        switch (scene) {
            case 0:
                Window.scene = new LevelEditorScene();
                Window.scene.init();
                break;
            case 1:
                Window.scene = new LevelScene();
                Window.scene.init();
                break;
            default:
                assert false : "Unknown scene '" + scene + "'";
                break;
        }
    }

    public static Window get() {
        if (instance == null) {
            instance = new Window();
        }
        return instance;
    }
}
