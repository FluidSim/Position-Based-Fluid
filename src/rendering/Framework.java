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
import org.lwjgl.opengl.GL11;
import egl.math.*;

public class Framework {
	GLU vertexArray;
	
	public static void display() throws LWJGLException{
		int width = 600;
		int height = 600;
 
		// set up window and display
		Display.setDisplayMode(new DisplayMode(width, height));
		Display.setVSyncEnabled(true);
		Display.setTitle("Position Based Fluids");
 
		// set up OpenGL to run in forward-compatible mode
		// so that using deprecated functionality will
		// throw an error.
		Display.create(new PixelFormat(), new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true));
		// initialize basic OpenGL stuff
		GL11.glViewport(0, 0, width, height);
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearDepth(1.0f);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	
	public static void createBuffers() {
		GLU depthbuffer;
		GL30.glGenRenderbuffers();
	}
}
