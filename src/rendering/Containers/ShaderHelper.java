package rendering.Containers;

import java.io.BufferedReader;
import java.io.FileReader;
 


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL40.*;

import static org.lwjgl.opengl.GL20.*;
 
public class ShaderHelper {
	private int program;
		
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
 
   /*
    * With the exception of syntax, setting up vertex and fragment shaders
    * is the same.
    * @param the name and path to the vertex shader
    */
	private int compileShader(String filename, int shaderType) {
		int shader = glCreateShader(shaderType);
		
		if (glGetShaderi(program, GL_LINK_STATUS) == GL_FALSE) {
			throw new RuntimeException("Could not create shader.");
		}
 
		String source = loadFile(filename);
 
		glShaderSource(shader, source); 
		glCompileShader(shader);
 
		return shader;
	}

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
