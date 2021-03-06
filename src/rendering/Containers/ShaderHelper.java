package rendering.Containers;

import rendering.RenderUtility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.ByteBuffer;

import org.lwjgl.util.glu.GLU;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL12.*;

public abstract class ShaderHelper {
	public int program;
	public int fbo;
	public int tex;
	public int vao;

	public void initProgram(String vertexShaderFile, String fragmentShaderFile) {
		program = glCreateProgram();
		
		int vertShader = compileShader(vertexShaderFile, GL_VERTEX_SHADER);
		int fragShader = compileShader(fragmentShaderFile, GL_FRAGMENT_SHADER);
		
		glAttachShader(program, vertShader);
		glAttachShader(program, fragShader);
		
		glLinkProgram(program);

		if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
			throw new RuntimeException("Could not link shader.");
		}

		glValidateProgram(program);
	}

	public void initTexture(int width, int height, int format, int internalFormat) {
		tex = glGenTextures();
		//glEnable(GL_TEXTURE_2D);
		
		glBindTexture(GL_TEXTURE_2D, tex);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);
		glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_FLOAT, (ByteBuffer)null);
	}

	public abstract void initFields();

	/*
	 * With the exception of syntax, setting up vertex and fragment shaders is
	 * the same.
	 * 
	 * @param the name and path to the vertex shader
	 */
	private int compileShader(String filename, int shaderType) {
		int shader = glCreateShader(shaderType);
		
		String source = loadFile(filename);
		glShaderSource(shader, source);
		glCompileShader(shader);
		
		if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
			throw new RuntimeException("Could not create shader.");
		}
		
		return shader;
	}

	private String loadFile(String filename) {
		StringBuilder vertexCode = new StringBuilder();
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			while ((line = reader.readLine()) != null) {
				vertexCode.append(line);
				vertexCode.append('\n');
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("unable to load shader from file [" + filename + "]", e);
		}

		return vertexCode.toString();
	}

	public int getProgram() {
		return program;
	}
}
