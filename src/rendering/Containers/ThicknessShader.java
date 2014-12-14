package rendering.Containers;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glGetFragDataLocation;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import rendering.RenderUtility;
import egl.math.Vector3;

public class ThicknessShader extends ShaderHelper {
	public int position;
	public int mView;
	public int projection;
	public int screenSize;

	@Override
	public void initFields() {
		position = glGetAttribLocation(program, "vertexPos");
		mView = glGetUniformLocation(program, "mView");
		projection = glGetUniformLocation(program, "projection");
		screenSize = glGetUniformLocation(program, "screenSize");
		glBindFragDataLocation(program, 0, "thickness");
	}
	
	public void particleThicknessVAO(ArrayList<Vector3> points) {
		FloatBuffer positionBuffer = RenderUtility.createPositionBuffer(points);
		
		int positionHandle = RenderUtility.bindBuffer(GL_ARRAY_BUFFER, positionBuffer, GL_STATIC_DRAW);
		
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

		// Unbind VBO's
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
}
