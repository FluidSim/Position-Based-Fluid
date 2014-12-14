package rendering;

import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL40.*;

import org.lwjgl.opengl.PixelFormat;

import physics.ParticleSystem;
import rendering.Containers.ParticleShader;
import rendering.Containers.ThicknessShader;
import egl.math.Matrix4;
import egl.math.Vector2;
import egl.math.Vector3;

public class Renderer {
	public static ParticleSystem system = new ParticleSystem(.1f, false);

	public static final Vector3 lightPosition = new Vector3(10, 10, 10);

	public void initGl() throws LWJGLException {
		int width = 600;
		int height = 600;
		
		Display.setDisplayMode(new DisplayMode(width, height));
		Display.setVSyncEnabled(true);
		Display.setTitle("Position Based Fluids");

		//Deprecated functions will throw an error
		Display.create(new PixelFormat(), new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true));

		glViewport(0, 0, width, height);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

	}

	public void run() {
		ArrayList<Vector3> points = new ArrayList<Vector3>();
		resetPoints(points);

		//Depth shader
		ParticleShader particleShader = new ParticleShader();
		particleShader.initProgram("src/rendering/Shaders/particleDepth.vert", "src/rendering/Shaders/particleDepth.frag");
		particleShader.initFields();
		
		//Thickness shader
		//ThicknessShader thicknessShader = new ThicknessShader();
		//thicknessShader.initProgram("src/rendering/Shaders/particleDepth.vert", "src/rendering/Shaders/particleThickness.frag");
		//thicknessShader.initFields();
		
		glEnable(GL_DEPTH_TEST);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		while (Display.isCloseRequested() == false) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			particleShader.particleDepthVAO(points);

			// Enable point size on Mac
			glEnable(0x8642);
			
			glDisable(GL_DEPTH_TEST);
			//glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

			glUseProgram(particleShader.program);

			// Create Camera
			Vector3 eye = new Vector3(10f, 4f, -5f);
			Vector3 target = new Vector3(10f, 2f, 0f);
			Vector3 up = new Vector3(0, 1, 0);

			float zNear = 1e-2f;
			float zFar = 1e2f;

			Matrix4 projection = Matrix4.createPerspectiveFOV((float) (60*Math.PI/180), (float) Display.getWidth() / Display.getHeight(), zNear, zFar);
			Matrix4 mView = Matrix4.createLookAt(eye, target, up);
			
			RenderUtility.addMatrix(particleShader, mView, "mView");
			RenderUtility.addMatrix(particleShader, projection, "projection");
			RenderUtility.addVector2(particleShader, new Vector2(Display.getWidth(), Display.getHeight()), "screenSize");
			RenderUtility.addVector3(particleShader, lightPosition, "lightPos");

			//Draw VAO
			glDrawArrays(GL_POINTS, 0, points.size());

			//Swap buffers and sync frame rate to 60 fps
			Display.update();
			Display.sync(60);

			system.update();
			
			resetPoints(points);
		}

		Display.destroy();
	}
	
	public void resetPoints(ArrayList<Vector3> points){
		points.clear();
		for (Vector3 p: system.getPositions()){
			points.add(p.clone());
		}
	}

	public static void main(String[] args) throws LWJGLException {
		Renderer r = new Renderer();
		r.initGl();
		r.run();
	}
}
