package rendering;

import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL40.*;

import org.lwjgl.opengl.PixelFormat;

import physics.ParticleSystem;
import rendering.Containers.CompositeShader;
import rendering.Containers.CurvatureShader;
import rendering.Containers.ParticleDepth;
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

		// Deprecated functions will throw an error
		Display.create(new PixelFormat(), new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true));

		glViewport(0, 0, width, height);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

	}

	public void run() {
		ArrayList<Vector3> points = new ArrayList<Vector3>();
		resetPoints(points);

		int width = Display.getWidth();
		int height = Display.getHeight();

		//Depth shader
		ParticleDepth depthShader = new ParticleDepth();
		depthShader.initProgram("src/rendering/Shaders/particle.vert", "src/rendering/Shaders/particleDepth.frag");
		depthShader.initFields();

		//Thickness shader
		ThicknessShader thicknessShader = new ThicknessShader();
		thicknessShader.initProgram("src/rendering/Shaders/particle.vert", "src/rendering/Shaders/particleThickness.frag");
		thicknessShader.initFields();

		//Curvature shader
		CurvatureShader curvatureShader = new CurvatureShader();
		curvatureShader.initProgram("src/rendering/Shaders/passThrough.vert", "src/rendering/Shaders/curvatureFlow.frag");
		curvatureShader.initFields();
		
		//Composite Shader
		CompositeShader compositeShader = new CompositeShader();
		compositeShader.initProgram("src/rendering/Shaders/composite.vert", "src/rendering/Shaders/composite.frag");
		compositeShader.initFields();

		glEnable(GL_DEPTH_TEST);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		//Create Camera
		Vector3 eye = new Vector3(10f, 4f, -10f);
		Vector3 target = new Vector3(10f, 2f, 0f);
		Vector3 up = new Vector3(0, 1, 0);

		float zNear = 1e-2f;
		float zFar = 1e2f;

		Matrix4 projection = Matrix4.createPerspectiveFOV((float) (60 * Math.PI / 180), (float) Display.getWidth() / Display.getHeight(), zNear, zFar);
		Matrix4 mView = Matrix4.createLookAt(eye, target, up);

		while (Display.isCloseRequested() == false) {
			//Particle Depth
			depthShader.initTexture(width, height, GL_R32F, GL_RED);
			depthShader.initDepthBuffer(width, height);

			glBindFramebuffer(GL_FRAMEBUFFER, depthShader.fbo);

			glUseProgram(depthShader.program);
			
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, depthShader.tex, 0);
			glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthShader.depthBuffer);

			depthShader.particleDepthVAO(points);

			//Enable point size on Mac
			glEnable(0x8642);
			glDisable(GL_BLEND);
			glEnable(GL_DEPTH_TEST);

			RenderUtility.addMatrix(depthShader, mView, "mView");
			RenderUtility.addMatrix(depthShader, projection, "projection");
			RenderUtility.addVector2(depthShader, new Vector2(Display.getWidth(), Display.getHeight()), "screenSize");
			RenderUtility.addVector3(depthShader, lightPosition, "lightPos");

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			//Draw VAO
			glBindVertexArray(depthShader.vao);
			glDrawArrays(GL_POINTS, 0, points.size());

			//Particle Thickness
			glBindFramebuffer(GL_FRAMEBUFFER, thicknessShader.fbo);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glUseProgram(thicknessShader.program);
			thicknessShader.initTexture(width, height, GL_RED, GL_R32F);

			thicknessShader.particleThicknessVAO(points);

			RenderUtility.addMatrix(thicknessShader, mView, "mView");
			RenderUtility.addMatrix(thicknessShader, projection, "projection");
			RenderUtility.addVector2(thicknessShader, new Vector2(width, height), "screenSize");
			RenderUtility.addVector3(thicknessShader, lightPosition, "lightPos");
			
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, thicknessShader.tex, 0);

			glEnable(GL_BLEND);
			glBlendFunc(GL_ONE, GL_ONE);
			glDisable(GL_DEPTH_TEST);

			glBindVertexArray(thicknessShader.vao);
			glDrawArrays(GL_POINTS, 0, points.size());

			glDisable(GL_BLEND);
			glEnable(GL_DEPTH_TEST);
			
			//Particle curvature
			glUseProgram(curvatureShader.program);
			curvatureShader.initTexture(width, height, GL_RED, GL_R32F);
			
			glDisable(GL_DEPTH_TEST);
			
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, curvatureShader.tex, 0);
			
			curvatureShader.curvatureVAO(width, height);
			
			RenderUtility.addTexture(curvatureShader, thicknessShader.tex);
			RenderUtility.addMatrix(curvatureShader, projection, "projection");
			RenderUtility.addVector2(curvatureShader, new Vector2(width, height) , "screenSize");
			
			glBindFramebuffer(GL_FRAMEBUFFER, curvatureShader.fbo);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			glBindVertexArray(curvatureShader.vao);
			glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
			
			for (int i = 0; i < 10; i++){
				int oldTex = curvatureShader.tex;
				
				curvatureShader.initTexture(width, height, GL_RED, GL_R32F);
				
				glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, curvatureShader.tex, 0);
				//glBindTexture?
				
//				curvatureShader.curvatureVAO(width, height);
//				
//				RenderUtility.addTexture(curvatureShader, oldTex);
//				RenderUtility.addMatrix(curvatureShader, projection, "projection");
//				RenderUtility.addVector2(curvatureShader, new Vector2(width, height) , "screenSize");
				
				glBindFramebuffer(GL_FRAMEBUFFER, curvatureShader.fbo);
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				
				glBindVertexArray(curvatureShader.vao);
				glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
			}
			
			glEnable(GL_DEPTH_TEST);
			
			//Composite everything
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			glUseProgram(compositeShader.program);
			
			compositeShader.compositeVAO(width, height);
			
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, depthShader.tex);
			glUniform1i(compositeShader.tex, 0);
			
			glActiveTexture(GL_TEXTURE1);
			glBindTexture(GL_TEXTURE_2D, thicknessShader.tex);
			glUniform1i(compositeShader.tex, 1);
			
			RenderUtility.addMatrix(compositeShader, projection, "projection");
			RenderUtility.addMatrix(compositeShader, mView, "mView");
			RenderUtility.addVector2(compositeShader, new Vector2(width, height), "screenSize");
			RenderUtility.addVector3(compositeShader, new Vector3(0.3f, 0.3f, 0.8f), "color");
						
			glDisable(GL_DEPTH_TEST);
			
			glBindVertexArray(compositeShader.vao);
			glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

			glEnable(GL_DEPTH_TEST);
			
			glViewport(0, 0, width, height);
			
			// Swap buffers and sync frame rate to 60 fps
			Display.update();
			Display.sync(60);

			system.update();

			resetPoints(points);
		}

		Display.destroy();
	}

	public void resetPoints(ArrayList<Vector3> points) {
		points.clear();
		for (Vector3 p : system.getPositions()) {
			points.add(p.clone());
		}
	}

	public static void main(String[] args) throws LWJGLException {
		Renderer r = new Renderer();
		r.initGl();
		r.run();
	}
}
