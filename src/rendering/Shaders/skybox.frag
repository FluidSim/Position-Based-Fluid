#version 400

in vec3 texCoord;

uniform samplerCube cubeMap;

out vec4 fragColor;

void main() {
	fragColor = texture(cubeMap, texCoord);
}