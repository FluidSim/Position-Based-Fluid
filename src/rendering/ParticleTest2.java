package rendering;

import physics.ParticleSystem;
import egl.math.Matrix4;
import egl.math.Vector2;
import egl.math.Vector3;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;

import egl.math.*;
 
public class ParticleTest2 {
	public static ParticleSystem system = new ParticleSystem(.1f, false);
	
	public static float xrot = 0.3f;
	public static float yrot = 0.3f;
	public static float scale = (float) 1 / 50;
	public static float trans = -0f;
	public static float transback = -5;


	public static final Vector3 lightPosition = new Vector3(10, 10, 10);
	
	/**
	 * General initialization stuff for OpenGL
	 */
	public void initGl() throws LWJGLException {
		// width and height of window and view port
		int width = 600;
		int height = 600;
 
		// set up window and display
		Display.setDisplayMode(new DisplayMode(width, height));
		Display.setVSyncEnabled(true);
		Display.setTitle("Fluid Simulation");
 
		// set up OpenGL to run in forward-compatible mode
		// so that using deprecated functionality will
		// throw an error.
		Display.create(new PixelFormat(), new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true));
		// initialize basic OpenGL stuff
		GL11.glViewport(0, 0, width, height);
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	}
	
	/** Run the shader */
	public void run() {
		ArrayList<Vector3> points = system.getPositions();
		// compile and link vertex and fragment shaders into
		// a "program" that resides in the OpenGL driver
		ShaderProgram shader = new ShaderProgram();
 
		// do the heavy lifting of loading, compiling and linking
		// the two shaders into a usable shader program
		shader.init("src/rendering/particleDepth.vert", "src/rendering/particleDepth.frag");	
		GL11.glEnable(GL11.GL_DEPTH_TEST);
 
		constructVertexArrayObject(points);
	
		while (Display.isCloseRequested() == false) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
 
			//Enable point size on Mac
			GL11.glEnable(0x8642);
			
			//Create Matrices
			Matrix4 M = Matrix4.createTranslation((float) 0, (float) 0,	transback);
			Matrix4 R = Matrix4.createRotationY(yrot);
			Matrix4 R2 = Matrix4.createRotationX(xrot);
			Matrix4 V = Matrix4.createPerspective((float) 1, (float) 1,	(float) 4, (float) 1);
			
			Matrix4 mViewProj = M.clone().mulBefore(R2).mulBefore(R).mulBefore(V);

			// tell OpenGL to use the shader
			GL20.glUseProgram(shader.getProgramId());

			RenderUtility.addMatrix(shader, mViewProj, "mViewProj");
			RenderUtility.addVector2(shader, new Vector2(Display.getWidth(), Display.getHeight()), "screenSize");
			RenderUtility.addVector3(shader, lightPosition, "lightPos");
	
			// draw VAO
			GL11.glDrawArrays(GL11.GL_POINTS, 0, points.size());
 
			// check for errors
			if (glGetError() != GL_NO_ERROR) {
				throw new RuntimeException("OpenGL error: "+GLU.gluErrorString(glGetError()));
			}
			
			// swap buffers and sync frame rate to 60 fps
			Display.update();
			Display.sync(60);
			
			system.update();
			points = system.getPositions();
			constructVertexArrayObject(points);
		}
		
		Display.destroy();
	}
 
	/**
	 * Create Vertex Array Object necessary to pass data to the shader
	 */
	private void constructVertexArrayObject(ArrayList<Vector3> points) {
		Matrix4 S = Matrix4.createScale((float)1 / 40);
		Matrix4 T = Matrix4.createTranslation((float)-0.5, (float)-0.3, (float)-0.5);
		float[] buffer = new float[points.size() * 3];
		
		int i = 0;
		for(Vector3 point : points) {
			S.mulDir(point);
			T.mulPos(point);
			buffer[3 * i] = (point.x); 
			buffer[3 * i+1] = (point.y); 
			buffer[3 * i+2] = (point.z);
			i++;
		}
 
		// convert vertex array to buffer
		FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(buffer.length + 6);
		positionBuffer.put(buffer);
		positionBuffer.put(0);positionBuffer.put(0);positionBuffer.put(0);
		positionBuffer.put(20);positionBuffer.put(20);positionBuffer.put(20);

		positionBuffer.flip();
 
		// convert color array to buffer
		FloatBuffer colorBuffer = createColorBuffer((float)0.6, (float)0.6, (float)0.8, (points.size() * 3) + 6);
		
		// create vertex byffer object (VBO) for vertices
		int positionBufferHandle = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionBufferHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionBuffer, GL15.GL_STATIC_DRAW);
 
		// create VBO for color values
		int colorBufferHandle = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorBufferHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorBuffer, GL15.GL_STATIC_DRAW);
 
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
 	}
	
	/** Add a Matrix as a uniform */
	public static void addMatrix(ShaderProgram shader, Matrix4 M, String name) {
		FloatBuffer Mbuffer = BufferUtils.createFloatBuffer(M.m.length);
		Mbuffer.put(M.m);
		Mbuffer.flip();
		int loc = GL20.glGetUniformLocation(shader.getProgramId(), name);
		GL20.glUniformMatrix4(loc,true,Mbuffer);
	}
	
	/** Create a x * y * z box of points */
	public static ArrayList<Vector3> createBox(int x, int y, int z, int closeness) {
		ArrayList<Vector3> points = new ArrayList<Vector3>();
		for(int i = 0; i < x; i++){
			for(int j = 0; j < y; j++){
				for(int k = 0; k < z; k++){
					points.add(new Vector3((float)i / closeness, (float)j / closeness, (float)k / closeness));
				}
			}
		}
		return points;
	}
	
	/** Create a colorBuffer with color (x,y,z) */
	public static FloatBuffer createColorBuffer(float x, float y, float z, int size) {
		float[] colors = new float[size * 3];
		for(int j = 0; j < size; j++){
			colors[3 * j] = (x);
			colors[3 * j+1] = (y);
			colors[3 * j+2] = (z);
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
		ParticleTest2 example = new ParticleTest2();
		example.initGl();
		example.run();
	}
	
	public static void copy(ArrayList<Vector3> dest, ArrayList<Vector3> src) {
		dest.clear();
		for (Vector3 v : src){
			dest.add(v.clone());
		}
	}
}

