package rendering;

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

import egl.math.Matrix4;
import egl.math.Vector3;

public class Renderer {

	// CONSTANTS
	public static float xrot = 0.2f;
	public static float yrot = 0.2f;
	public static float scale = (float) 1 / 30;
	public static float trans = 0f;
	public static float transback = -5;

	public static ArrayList<Vector3> initialPoints = createBox(10, 10, 10, 1);
	public static ArrayList<Vector3> points = new ArrayList<Vector3>(
			initialPoints.size());
	public static double time = 0;

	/**
	 * General initialization stuff for OpenGL
	 */
	public void initGl() throws LWJGLException {
		// width and height of window and view port
		int width = 640;
		int height = 640;

		// set up window and display
		Display.setDisplayMode(new DisplayMode(width, height));
		Display.setVSyncEnabled(true);
		Display.setTitle("Shader Example");

		Display.create(new PixelFormat(), new ContextAttribs(3, 2)
				.withForwardCompatible(true).withProfileCore(true));

		// initialize basic OpenGL stuff
		GL11.glViewport(0, 0, width, height);
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

	}

	/** Run the shader */
	public void run() {
		copy(points, initialPoints);

		// compile and link vertex and fragment shaders into
		// a "program" that resides in the OpenGL driver
		ShaderProgram shader = new ShaderProgram();

		// do the heavy lifting of loading, compiling and linking
		// the two shaders into a usable shader program
		shader.init("src/rendering/particleDepth.vert",
				"src/rendering/particleDepth.frag");
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		// ArrayList<Vector3> points = createBox(15,15,15,15);

		int vaoHandle = constructVertexArrayObject(points);

		GL20.glBindAttribLocation(shader.getProgramId(), 0, "velocity");
		GL20.glBindAttribLocation(shader.getProgramId(), 1, "radius");

		while (Display.isCloseRequested() == false) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

			// Create Matrices
			Matrix4 M = Matrix4.createTranslation((float) 0, (float) 0,
					transback);
			Matrix4 R = Matrix4.createRotationY(yrot);
			Matrix4 R2 = Matrix4.createRotationX(xrot);
			Matrix4 V = Matrix4.createPerspective((float) 1, (float) 1,
					(float) 4, (float) 1);

			// tell OpenGL to use the shader
			GL20.glUseProgram(shader.getProgramId());

			addMatrix(shader, R2, "R2");
			addMatrix(shader, R, "R");
			addMatrix(shader, M, "M");
			addMatrix(shader, V, "V");

			// bind vertex and color data
			GL30.glBindVertexArray(vaoHandle);
			GL20.glEnableVertexAttribArray(0); // VertexPosition
			GL20.glEnableVertexAttribArray(1); // VertexColor

			// draw VAO
			GL11.glDrawArrays(GL11.GL_POINTS, 0, points.size());

			// check for errors
			if (glGetError() != GL_NO_ERROR) {
				throw new RuntimeException("OpenGL error: "
						+ GLU.gluErrorString(glGetError()));
			}

			// swap buffers and sync frame rate to 60 fps
			Display.update();
			Display.sync(60);

			updatePoints();
			vaoHandle = constructVertexArrayObject(points);
		}
		Display.destroy();
	}

	/**
	 * Create Vertex Array Object necessary to pass data to the shader
	 */
	private int constructVertexArrayObject(ArrayList<Vector3> points) {
		Matrix4 S = Matrix4.createScale(scale);
		Matrix4 T = Matrix4.createTranslation(trans, trans, trans);
		float[] buffer = new float[points.size() * 3];
		int i = 0;
		for (Vector3 point : points) {
			S.mulDir(point);
			T.mulPos(point);
			buffer[3 * i] = (point.x);
			buffer[3 * i + 1] = (point.y);
			buffer[3 * i + 2] = (point.z);
			i = i + 1;
		}

		// convert vertex array to buffer
		FloatBuffer positionBuffer = BufferUtils
				.createFloatBuffer(buffer.length);
		positionBuffer.put(buffer);
		positionBuffer.flip();

		// convert color array to buffer
		FloatBuffer colorBuffer = createColorBuffer((float) 0.6, (float) 0.6,
				(float) 0.8, points.size() * 3);

		// create vertex byffer object (VBO) for vertices
		int positionBufferHandle = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionBufferHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionBuffer,
				GL15.GL_STATIC_DRAW);

		// create VBO for color values
		int colorBufferHandle = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorBufferHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorBuffer,
				GL15.GL_STATIC_DRAW);

		// unbind VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		// create vertex array object (VAO)
		int vaoHandle = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoHandle);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		// assign vertex VBO to slot 0 of VAO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionBufferHandle);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

		// assign vertex VBO to slot 1 of VAO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorBufferHandle);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0);

		// unbind VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		return vaoHandle;
	}

	/** Add a Matrix as a uniform */
	public static void addMatrix(ShaderProgram shader, Matrix4 M, String name) {
		FloatBuffer Mbuffer = BufferUtils.createFloatBuffer(M.m.length);
		Mbuffer.put(M.m);
		Mbuffer.flip();
		int loc = GL20.glGetUniformLocation(shader.getProgramId(), name);
		GL20.glUniformMatrix4(loc, true, Mbuffer);
	}

	/** Create a x * y * z box of points */
	public static ArrayList<Vector3> createBox(int x, int y, int z,
			int closeness) {
		ArrayList<Vector3> points = new ArrayList<Vector3>();
		for (int i = -x; i < x; i++) {
			for (int j = -y; j < y; j++) {
				for (int k = -z; k < z; k++) {
					points.add(new Vector3((float) i / closeness, (float) j
							/ closeness, (float) k / closeness));
				}
			}
		}
		return points;
	}

	/** Create a colorBuffer with color (x,y,z) */
	public static FloatBuffer createColorBuffer(float x, float y, float z,
			int size) {
		float[] colors = new float[size * 3];
		for (int j = 0; j < size; j++) {
			colors[3 * j] = (x);
			colors[3 * j + 1] = (y);
			colors[3 * j + 2] = (z);
		}
		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(colors.length);
		colorBuffer.put(colors);
		colorBuffer.flip();
		return colorBuffer;
	}

	/**
	 * main method to run the example
	 */
	public static void main(String[] args) throws LWJGLException {
		ShaderExample example = new ShaderExample();
		example.initGl();
		// example.run();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		// set the color of the quad (R,G,B,A)
		GL11.glColor3f(0.5f, 0.5f, 1.0f);

		// draw quad
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(100, 100);
		GL11.glVertex2f(100 + 200, 100);
		GL11.glVertex2f(100 + 200, 100 + 200);
		GL11.glVertex2f(100, 100 + 200);
		GL11.glEnd();
	}

	public static void updatePoints() {
		time += .00;
		copy(points, initialPoints);
		for (Vector3 v : points) {
			double newX = Math.cos(time) * v.x - Math.sin(time) * v.y;
			double newY = Math.sin(time) * v.x + Math.cos(time) * v.y;
			v.x = (float) newX;
			v.y = (float) newY;
		}
	}

	public static void copy(ArrayList<Vector3> dest, ArrayList<Vector3> src) {
		dest.clear();
		for (Vector3 v : src) {
			dest.add(v.clone());
		}
	}

}
