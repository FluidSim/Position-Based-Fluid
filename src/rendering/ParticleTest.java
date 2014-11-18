package rendering;

import physics.ParticleSystem;

import egl.math.Matrix4;
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
 
public class ParticleTest 
{
	public static ParticleSystem system = new ParticleSystem();
	
	/**
	 * General initialization stuff for OpenGL
	 */
	public void initGl() throws LWJGLException
	{
		
		// width and height of window and view port
		int width = 640;
		int height = 480;
 
		// set up window and display
		Display.setDisplayMode(new DisplayMode(width, height));
		Display.setVSyncEnabled(true);
		Display.setTitle("Shader Example");
 
		// set up OpenGL to run in forward-compatible mode
		// so that using deprecated functionality will
		// throw an error.
		PixelFormat pixelFormat = new PixelFormat();
		ContextAttribs contextAtrributes = new ContextAttribs(3, 2);
		contextAtrributes.withForwardCompatible(true);
		contextAtrributes.withProfileCore(true);
		Display.create(pixelFormat, contextAtrributes);
		// initialize basic OpenGL stuff
		GL11.glViewport(0, 0, width, height);
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
	}
	
	/** Run the shader */
	public void run()
	{
		ArrayList<Vector3> points = system.getPositions();
		// compile and link vertex and fragment shaders into
		// a "program" that resides in the OpenGL driver
		ShaderProgram shader = new ShaderProgram();
 
		// do the heavy lifting of loading, compiling and linking
		// the two shaders into a usable shader program
		shader.init("src/rendering/simple.vertex", "src/rendering/simple.fragment");	
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
 
		
		//ArrayList<Vector3> points = createBox(15,15,15,15);
		
		int vaoHandle = constructVertexArrayObject(points);
		
	    GL20.glBindAttribLocation(shader.getProgramId(), 0, "velocity");
	    GL20.glBindAttribLocation(shader.getProgramId(), 1, "radius");

 
		while( Display.isCloseRequested() == false )
		{
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
 
			//Create Matrices
			Matrix4 M = Matrix4.createTranslation((float)0,(float)0,(float)-5);
			Matrix4 R = Matrix4.createRotationY((float)0.3);
			Matrix4 R2 = Matrix4.createRotationX((float)0.3);
			Matrix4 V = Matrix4.createPerspective((float)1,(float)1,(float)3,(float)0);

			// tell OpenGL to use the shader
			GL20.glUseProgram( shader.getProgramId() );
			
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
			if( glGetError() != GL_NO_ERROR )
			{
				throw new RuntimeException("OpenGL error: "+GLU.gluErrorString(glGetError()));
			}
			
			// swap buffers and sync frame rate to 60 fps
			Display.update();
			Display.sync(60);
			
			system.update();
			points = system.getPositions();
			vaoHandle = constructVertexArrayObject(points);
		}
		Display.destroy();
	}
 
	/**
	 * Create Vertex Array Object necessary to pass data to the shader
	 */
	private int constructVertexArrayObject(ArrayList<Vector3> points)
	{
		Matrix4 S = Matrix4.createScale((float)1/15);
		Matrix4 T = Matrix4.createTranslation((float)-0.5,(float)-0.5,(float)-0.5);
		float[] buffer = new float[points.size()*3];
		int i = 0;
		for(Vector3 point: points){
			S.mulDir(point);
			T.mulPos(point);
			buffer[3*i]=(point.x); buffer[3*i+1]=(point.y); buffer[3*i+2]=(point.z);
			i = i+1;
		}
 
		// convert vertex array to buffer
		FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(buffer.length);
		positionBuffer.put(buffer);
		positionBuffer.flip();
 
		// convert color array to buffer
		FloatBuffer colorBuffer = createColorBuffer((float)0.6,(float)0.6,(float)0.8,points.size()*3);
		
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
 
		return vaoHandle;
	}
	
	/** Add a Matrix as a uniform */
	public static void addMatrix(ShaderProgram shader, Matrix4 M, String name){
		FloatBuffer Mbuffer = BufferUtils.createFloatBuffer(M.m.length);
		Mbuffer.put(M.m);
		Mbuffer.flip();
		int loc = GL20.glGetUniformLocation(shader.getProgramId(), name);
		GL20.glUniformMatrix4(loc,true,Mbuffer);
	}
	
	/** Create a x * y * z box of points */
	public static ArrayList<Vector3> createBox(int x, int y, int z, int closeness){
		ArrayList<Vector3> points = new ArrayList<Vector3>();
		for(int i = 0; i < x; i++){
			for(int j = 0; j < y; j++){
				for(int k = 0; k < z; k++){
					points.add(new Vector3((float)i/closeness,(float)j/closeness,(float)k/closeness));
				}
			}
		}
		return points;
	}
	
	/** Create a colorBuffer with color (x,y,z) */
	public static FloatBuffer createColorBuffer(float x, float y, float z, int size){
		float[] colors = new float[size*3];
		for(int j = 0; j < size; j++){
			colors[3*j]=(x); colors[3*j+1]=(y); colors[3*j+2]=(z);
		}
		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(colors.length);
		colorBuffer.put(colors);
		colorBuffer.flip();	
		return colorBuffer;
	}
	
	/**
	 * main method to run the example
	 */
	public static void main(String[] args) throws LWJGLException
	{
		ParticleTest example = new ParticleTest();
		example.initGl();
		example.run();
	}
	
	
	public static void copy(ArrayList<Vector3> dest, ArrayList<Vector3> src){
		dest.clear();
		for (Vector3 v: src){
			dest.add(v.clone());
		}
	}
	
	
}
