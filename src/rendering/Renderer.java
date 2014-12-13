package rendering;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL15.*;

import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

import physics.ParticleSystem;
import rendering.Containers.ParticleShader;
import rendering.Containers.ShaderHelper;
import egl.math.Matrix4;
import egl.math.Vector2;
import egl.math.Vector3;

public class Renderer {
	public static ParticleSystem system = new ParticleSystem(.1f, false);

	private double time = 0;

	public static final Vector3 lightPosition = new Vector3(10, 10, 10);

	public void initGl() throws LWJGLException {
		int width = 600;
		int height = 600;
		// set up window and display
		Display.setDisplayMode(new DisplayMode(width, height));
		Display.setVSyncEnabled(true);
		Display.setTitle("Position Based Fluids");

		// set up OpenGL to run in forward-compatible mode
		// so that using deprecated functionality will
		// throw an error.
		Display.create(new PixelFormat(), new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true));

		glViewport(0, 0, width, height);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

	}

	public void run() {
		ArrayList<Vector3> points = new ArrayList<Vector3>();
		resetPoints(points);

		ParticleShader particleShader = new ParticleShader();
		particleShader.initProgram("src/rendering/Shaders/particleDepth.vert", "src/rendering/Shaders/particleDepth.frag");
		particleShader.initFields();

		glEnable(GL_DEPTH_TEST);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		constructVertexArrayObject(points);

		while (Display.isCloseRequested() == false) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			// Enable point size on Mac
			glEnable(0x8642);

			glUseProgram(particleShader.program);

			// Create Matrices
			Vector3 eye = new Vector3(10f, 0f, -10f);
			Vector3 target = new Vector3(10f, 0f, 0f);
			Vector3 up = new Vector3(0, 1, 0);

			float zNear = 1e-2f;
			float zFar = 1e2f;

			Matrix4 projection = Matrix4.createPerspectiveFOV((float) (45*Math.PI/180), (float) Display.getWidth() / Display.getHeight(), zNear, zFar);
			Matrix4 mView = Matrix4.createLookAt(eye, target, up);
			
			RenderUtility.addMatrix(particleShader, mView, "mView");
			RenderUtility.addMatrix(particleShader, projection, "projection");
			RenderUtility.addVector2(particleShader, new Vector2(Display.getWidth(), Display.getHeight()), "screenSize");
			RenderUtility.addVector3(particleShader, lightPosition, "lightPos");

			// draw VAO
			glDrawArrays(GL_POINTS, 0, points.size());

			// swap buffers and sync frame rate to 60 fps
			Display.update();
			Display.sync(60);

			system.update();
			
			resetPoints(points);
			
			constructVertexArrayObject(points);
		}

		Display.destroy();
	}
	
	public void resetPoints(ArrayList<Vector3> points){
		points.clear();
		for (Vector3 p: system.getPositions()){
			points.add(p.clone());
		}
	}

	/**
	 * Create Vertex Array Object necessary to pass data to the shader
	 */
	private void constructVertexArrayObject(ArrayList<Vector3> points) {
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

	public static void main(String[] args) throws LWJGLException {
		Renderer r = new Renderer();
		r.initGl();
		r.run();
	}
}
