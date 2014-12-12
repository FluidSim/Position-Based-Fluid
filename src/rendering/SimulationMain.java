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

public class SimulationMain {
	public static ParticleSystem system = new ParticleSystem(.1f, false);

	public static void initObjects(){
		ArrayList<Vector3> particles = system.getPositions();
		
	}
	
}
