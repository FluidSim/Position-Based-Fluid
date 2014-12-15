package rendering.Containers;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL40.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;

import rendering.RenderUtility;
import egl.math.Vector3;

public class ParticleDepth extends ShaderHelper {
	public int position;
	public int mView;
	public int projection;
	public int screenSize;
	public int color;
	public int depth;
	
	public int depthBuffer;

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
		
		fbo = glGenFramebuffers();
		glBindBuffer(GL_FRAMEBUFFER, fbo);
	}
	
	public void initDepthBuffer(int width, int height) {
		depthBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);
	}
	
	
	/**
	 * Create Vertex Array Object necessary to pass data to the shader
	 */
	public void particleDepthVAO(ArrayList<Vector3> points) {
		// Create position buffer
		FloatBuffer positionBuffer = RenderUtility.createPositionBuffer(points);

		// create VBO's
		int positionHandle = RenderUtility.bindBuffer(GL_ARRAY_BUFFER, positionBuffer, GL_STATIC_DRAW);

		// Unbind VBO's
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// Create VA0
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		glEnableVertexAttribArray(0);

		// Assign vertex buffer to slot 0 of VAO
		glBindBuffer(GL_ARRAY_BUFFER, positionHandle);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

		// Unbind VBO's
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
}
