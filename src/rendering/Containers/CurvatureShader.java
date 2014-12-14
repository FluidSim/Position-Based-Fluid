package rendering.Containers;

import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

import java.util.ArrayList;

import egl.math.Vector3;

public class CurvatureShader extends ShaderHelper {
	public int position;
	public int projection;
	public int screenSize;
	
	public int fbo;
	
	@Override
	public void initFields() {
		position = glGetAttribLocation(program, "vertexPos");
		projection = glGetUniformLocation(program, "projection");
		screenSize = glGetUniformLocation(program, "screenSize");
		glBindFragDataLocation(program, 0, "thickness");
	}
	
	public void curvatureVAO(ArrayList<Vector3> points) {
		
	}
}
