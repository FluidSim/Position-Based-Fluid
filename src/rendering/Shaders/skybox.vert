#version 400

in vec3 vp;

uniform mat4 mView;
uniform mat4 projection;

out vec3 texCoord;

void main() {
	texCoord = vp;
	gl_Position = projection * mView * vec4(vp, 1.0);
}