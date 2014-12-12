package rendering.Containers;

import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;

public class ParticleShader extends ShaderHelper {
	
	public int position;
	
	/** model view matrix */
	public int mViewProj;
	
	public int screenSize;
	
	/** terrain texture */
	public int terrain;

	@Override
	public void initFields() {
		position = glGetAttribLocation(program, "vertexPos");
		mViewProj = glGetUniformLocation(program, "mViewProj");
		screenSize = glGetUniformLocation(program, "screenSize");	
	}
}
