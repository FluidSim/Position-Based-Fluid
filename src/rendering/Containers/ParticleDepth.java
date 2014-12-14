package rendering.Containers;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL40.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import rendering.RenderUtility;
import egl.math.Vector3;

public class ParticleDepth extends ShaderHelper {
	public int position;
	public int mView;
	public int projection;
	public int screenSize;
	public int color;
	public int depth;
	
	public int fbo;

	@Override
	public void initFields() {
		position = glGetAttribLocation(program, "vertexPos");
		mView = glGetUniformLocation(program, "mView");
		projection = glGetUniformLocation(program, "projection");
		screenSize = glGetUniformLocation(program, "screenSize");
		glBindFragDataLocation(program, 0, "outColor");
		glBindFragDataLocation(program, 1, "depth");
		color = glGetFragDataLocation(program, "outColor");
		depth = glGetFragDataLocation(program, "depth");
	}
	
	/**
	 * Create Vertex Array Object necessary to pass data to the shader
	 */
	public void particleDepthVAO(ArrayList<Vector3> points) {
		// Create color and position buffers
		FloatBuffer colorBuffer = RenderUtility.createColorBuffer(0.3f, 0.3f, 1.0f, (points.size()));
		FloatBuffer positionBuffer = RenderUtility.createPositionBuffer(points);

		// create VBO's
		int positionHandle = RenderUtility.bindBuffer(GL_ARRAY_BUFFER, positionBuffer, GL_STATIC_DRAW);
		int colorHandle = RenderUtility.bindBuffer(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);

		// Unbind VBO's
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// Create VA0
		int vao = glGenVertexArrays();
		glBindVertexArray(vao);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		// Assign vertex buffer to slot 0 of VAO
		glBindBuffer(GL_ARRAY_BUFFER, positionHandle);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

		// Assign color buffer to slot 0 of VAO
		glBindBuffer(GL_ARRAY_BUFFER, colorHandle);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

		// Unbind VBO's
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
}
