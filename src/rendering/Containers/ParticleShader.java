package rendering.Containers;

import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL40.*;

public class ParticleShader extends ShaderHelper {
	
	public int position;
	
	/** model view matrix */
	public int mView;
	
	public int projection;
	
	public int screenSize;
	
	/** terrain texture */
	public int terrain;
	
	/** out color */
	public int color;
	
	/** out depth */
	public int depth;

	@Override
	public void initFields() {
		position = glGetAttribLocation(program, "vertexPos");
		mView = glGetUniformLocation(program, "mView");
		projection = glGetUniformLocation(program, "projection");
		screenSize = glGetUniformLocation(program, "screenSize");
		glBindFragDataLocation(program, 0, "outColor");
		glBindFragDataLocation(program, 1, "depth");
		color = glGetFragDataLocation(program, "outColor");
		depth = glGetFragDataLocation(program, "depth");
	}
}
