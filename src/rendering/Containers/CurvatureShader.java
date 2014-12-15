package rendering.Containers;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;

import rendering.RenderUtility;
import egl.math.Vector3;

public class CurvatureShader extends ShaderHelper {
	public int position;
	public int projection;
	public int screenSize;

	public int fbo;
	
	@Override
	public void initFields() {
		position = glGetAttribLocation(program, "vertexPos");
		projection = glGetUniformLocation(program, "projection");
		screenSize = glGetUniformLocation(program, "screenSize");
		glBindFragDataLocation(program, 0, "thickness");
		fbo = glGenBuffers();
		glBindBuffer(GL_FRAMEBUFFER, fbo);
	}

	public void curvatureVAO(int width, int height) {
		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		float[] vertices = new float[] { 
				-1.0f, 1.0f, 0.0f,
				-1.0f, -1.0f, 0.0f, 
				1.0f, 1.0f, 0.0f,
				1.0f, -1.0f, 0.0f };
		FloatBuffer verts = BufferUtils.createFloatBuffer(vertices.length);
		verts.put(vertices);
		verts.flip();
		
		int vbo = glGenBuffers();
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, verts, GL_STATIC_DRAW);

		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(0);

		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
}
