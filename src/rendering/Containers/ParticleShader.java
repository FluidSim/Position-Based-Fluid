package rendering.Containers;

public class ParticleShader extends ShaderHelper {
	
	/** shader program */
	public int program;
	
	public int position;
	
	/** model view matrix */
	public int mView;
	
	/** projection matrix */
	public int mProj;
	
	public int screenSize;
	
	/** terrain texture */
	public int terrain;
}
