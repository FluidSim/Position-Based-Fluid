package rendering.Containers;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL40.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class TextureShader extends ShaderHelper {

	public int texUniform;
	
	@Override
	public void initFields() {
		texUniform = glGetUniformLocation(program, "tex");
	}
	
	public void textureVAO() {
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
