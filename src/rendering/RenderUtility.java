package rendering;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL20;

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




public final class RenderUtility {

	private RenderUtility() { }
	
	/** Add a Vector3 as a uniform */
	public static void addVector3(ShaderHelper shader, Vector3 V, String name){
		int loc = glGetUniformLocation(shader.getProgram(), name);
		glUniform3f(loc, V.x, V.y, V.z);
	}
	
	/** Add a Vector2 as a uniform */
	public static void addVector2(ShaderHelper shader, Vector2 V, String name){
		int loc = glGetUniformLocation(shader.getProgram(), name);
		glUniform2f(loc, V.x, V.y);
	}
	
	/** Add an int as a uniform */
	public static void addInt(ShaderHelper shader, int x, String name){
		int loc = glGetUniformLocation(shader.getProgram(), name);
		glUniform1i(loc,x);
	}
	
	/** Add a float as a uniform */
	public static void addFloat(ShaderHelper shader, int f, String name){
		int loc = glGetUniformLocation(shader.getProgram(), name);
		glUniform1f(loc, f);
	}
	
	/** Add a Matrix as a uniform */
	public static void addMatrix(ShaderHelper shader, Matrix4 M, String name) {
		FloatBuffer Mbuffer = BufferUtils.createFloatBuffer(M.m.length);
		Mbuffer.put(M.m);
		Mbuffer.flip();
		int loc = glGetUniformLocation(shader.getProgram(), name);
		glUniformMatrix4(loc, true, Mbuffer);
	}
	
	public static FloatBuffer createPositionBuffer(ArrayList<Vector3> points){
		Matrix4 S = Matrix4.createScale((float)1 / 40);
		Matrix4 T = Matrix4.createTranslation((float)-0.5, (float)-0.3, (float)-0.5);
		float[] buffer = new float[points.size()*3];
		int i = 0;
		for(Vector3 point: points) {
			S.mulDir(point);
			T.mulPos(point);
			buffer[3*i]=(point.x); buffer[3*i+1]=(point.y); buffer[3*i+2]=(point.z);
			i = i+1;
		}
 
		// convert vertex array to buffer
		FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(buffer.length);
		positionBuffer.put(buffer);
		positionBuffer.flip();
		
		return positionBuffer;
	}
	
	public static FloatBuffer createColorBuffer(float x, float y, float z, int size) {
		float[] colors = new float[size*3];
		for(int j = 0; j < size; j++){
		colors[3*j]=(x); colors[3*j+1]=(y); colors[3*j+2]=(z);
		}
		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(colors.length);
		colorBuffer.put(colors);
		colorBuffer.flip();
		return colorBuffer;
	}
	
	/** Bind a buffer. 
	 * 
	 * @param type: target buffer object type
	 * @param data: data to be loaded into the buffer
	 * @param usage: expected usage pattern
	 */
	public static int bindBuffer(int type, FloatBuffer data, int usage){
		int buff = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, buff);
		glBufferData(GL_ARRAY_BUFFER, data ,GL_STATIC_DRAW);
		return buff;
	}
	
}
