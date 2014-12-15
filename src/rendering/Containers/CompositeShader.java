package rendering.Containers;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glGetFragDataLocation;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;

import rendering.RenderUtility;

public class CompositeShader extends ShaderHelper {
	public int depthImage;
	public int thicknessImage;
	public int screenSize;
	public int color;
	public int projection;
	public int mView;

	@Override
	public void initFields() {
		depthImage = glGetAttribLocation(program, "depthImage");
		thicknessImage = glGetUniformLocation(program, "thicknessImage");
		color = glGetUniformLocation(program, "color");
		screenSize = glGetUniformLocation(program, "screenSize");
		projection = glGetUniformLocation(program, "projection");
		mView = glGetUniformLocation(program, "mView");
		glBindFragDataLocation(program, 0, "fragColor");

		fbo = glGenFramebuffers();
		glBindBuffer(GL_FRAMEBUFFER, fbo);
	}

	public void compositeVAO(int width, int height) {
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
