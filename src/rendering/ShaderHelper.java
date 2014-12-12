package rendering;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.FloatBuffer;
 
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.GLU;

import static org.lwjgl.opengl.GL20.*;
 
public class ShaderHelper {
	private int program;
	
	public void initProgram(String vertexShaderFile, String fragmentShaderFile) {
		// create the shader program. If OK, create vertex and fragment shaders
		program = glCreateProgram();
		
		// load and compile the two shaders
		int vertShader = compileShader(vertexShaderFile, GL_VERTEX_SHADER);
		int fragShader = compileShader(fragmentShaderFile, GL_FRAGMENT_SHADER);
		
		glAttachShader(program, vertShader);
		glAttachShader(program, fragShader);
 
		glLinkProgram(program);
		
		// validate linking
		/*if (glGetProgramiv(program, GL_LINK_STATUS)) {
			throw new RuntimeException("could not link shader. Reason: " + glGetProgramInfoLog(program, 1000));
		}*/
		
		// perform general validation that the program is usable
		glValidateProgram(program);
	}
 
   /*
    * With the exception of syntax, setting up vertex and fragment shaders
    * is the same.
    * @param the name and path to the vertex shader
    */
	private int compileShader(String filename, int shaderType) {
		int shader = glCreateShader(shaderType);
 
		/*if (shader == 0) {
			throw new RuntimeException("Could not created shader of type " + shaderType + " for file " + filename + ". "+ glGetProgramInfoLog(program, 1000));
		}*/
 
		String source = loadFile(filename);
 
		glShaderSource(shader, source); 
		glCompileShader(shader);
 
		return shader;
	}
 
	/**
	 * Load a text file and return it as a String.
	 */
	private String loadFile(String filename) {
		StringBuilder vertexCode = new StringBuilder();
		String line = null ;
		try {
		    BufferedReader reader = new BufferedReader(new FileReader(filename));
		    while ((line = reader.readLine()) != null) {
		    	vertexCode.append(line);
		    	vertexCode.append('\n');
		    }
		} catch(Exception e) {
			throw new IllegalArgumentException("unable to load shader from file ["+filename+"]", e);
		}
 
		return vertexCode.toString();
	}
	
	public int getProgram() {
		return program;
	}
}
