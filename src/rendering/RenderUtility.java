package rendering;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import egl.math.Matrix4;
import egl.math.Vector2;
import egl.math.Vector3;

public final class RenderUtility {

	private RenderUtility() { }
	
	/** Add a Vector3 as a uniform */
	public static void addVector3(ShaderProgram shader, Vector3 V, String name){
		int loc = GL20.glGetUniformLocation(shader.getProgramId(), name);
		GL20.glUniform3f(loc, V.x, V.y, V.z);
	}
	
	/** Add a Vector2 as a uniform */
	public static void addVector2(ShaderProgram shader, Vector2 V, String name){
		int loc = GL20.glGetUniformLocation(shader.getProgramId(), name);
		GL20.glUniform2f(loc, V.x, V.y);
	}
	
	/** Add an int as a uniform */
	public static void addInt(ShaderProgram shader, int x, String name){
		int loc = GL20.glGetUniformLocation(shader.getProgramId(), name);
		GL20.glUniform1i(loc,x);
	}
	
	/** Add a float as a uniform */
	public static void addFloat(ShaderProgram shader, int f, String name){
		int loc = GL20.glGetUniformLocation(shader.getProgramId(), name);
		GL20.glUniform1f(loc, f);
	}
	
	/** Add a Matrix as a uniform */
	public static void addMatrix(ShaderProgram shader, Matrix4 M, String name) {
		FloatBuffer Mbuffer = BufferUtils.createFloatBuffer(M.m.length);
		Mbuffer.put(M.m);
		Mbuffer.flip();
		int loc = GL20.glGetUniformLocation(shader.getProgramId(), name);
		GL20.glUniformMatrix4(loc, true, Mbuffer);
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
	
}
