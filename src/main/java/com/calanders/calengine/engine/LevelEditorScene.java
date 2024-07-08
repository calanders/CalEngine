package com.calanders.calengine.engine;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class LevelEditorScene extends Scene {
    private String vertexShaderSrc = "#version 330 core\n" +
            "layout (location = 0) in vec3 aPos;\n" +
            "layout (location = 1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main() {\n" +
            "    color = fColor;\n" +
            "}\n";
    private int vertexID;
    private int fragmentID;
    private int shaderProgram;
    private float[] vertexArray = {
             0.5f, -0.5f, 0.0f,      1.0f, 0.0f, 0.0f, 1.0f,
            -0.5f,  0.5f, 0.0f,      0.0f, 1.0f, 0.0f, 1.0f,
             0.5f,  0.5f, 0.0f,      0.0f, 0.0f, 1.0f, 1.0f,
            -0.5f, -0.5f, 0.0f,      1.0f, 1.0f, 0.0f, 1.0f,
    };
    private int[] elementArray = {
            2, 1, 0,
            0, 1, 3
    };
    private int vaoID;
    private int vboID;
    private int eboID;

    public LevelEditorScene() {
    }

    @Override
    public void init() {
        // Compile vertex shaders and check for errors
        vertexID = GL20.glCreateShader(GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexID, vertexShaderSrc);
        GL20.glCompileShader(vertexID);
        if (GL20.glGetShaderi(vertexID, GL_COMPILE_STATUS) == GL_FALSE) {
            int length = GL20.glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tVertex shader compilation failed.");
            System.out.println(GL20.glGetShaderInfoLog(vertexID, length));
            assert false : "";
        }

        // Compile fragment shaders and check for errors
        fragmentID = GL20.glCreateShader(GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentID, fragmentShaderSrc);
        GL20.glCompileShader(fragmentID);
        if (GL20.glGetShaderi(fragmentID, GL_COMPILE_STATUS) == GL_FALSE) {
            int length = GL20.glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tFragment shader compilation failed.");
            System.out.println(GL20.glGetShaderInfoLog(fragmentID, length));
            assert false : "";
        }

        // Configure shader link and check for errors
        shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgram, vertexID);
        GL20.glAttachShader(shaderProgram, fragmentID);
        GL20.glLinkProgram(shaderProgram);
        if (GL20.glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE) {
            int length = GL20.glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tLinking shaders failed.");
            System.out.println(GL20.glGetProgramInfoLog(shaderProgram, length));
            assert false : "";
        }

        // Generate VAO
        vaoID = GL30.glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Generate VBO
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();
        vboID = GL30.glGenBuffers();
        GL30.glBindBuffer(GL_ARRAY_BUFFER, vboID);
        GL30.glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Generate EBO
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();
        eboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        GL15.glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        int positionSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionSize + colorSize) * floatSizeBytes;

        // Add vertex attribute pointers
        GL20.glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * floatSizeBytes);
        GL20.glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        // Set up program and VAO
        GL20.glUseProgram(shaderProgram);
        GL30.glBindVertexArray(vaoID);

        // Enable vertex attribute pointers
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Disable vertex attribute pointers
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        // Unbind program and VAO
        glUseProgram(0);
        glBindVertexArray(0);
    }
}
