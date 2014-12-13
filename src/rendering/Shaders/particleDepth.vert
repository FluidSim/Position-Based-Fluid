#version 400

in vec3 vertexPos;
in vec3 color;

uniform mat4 projection;
uniform mat4 mView;
uniform vec2 screenSize;
uniform vec3 lightPos;

out vec3 pos;
out float radius;
out vec3 fragColor;

void main() {
	mat4 mViewProj = projection * mView;
	vec4 viewPos = mView * vec4(vertexPos, 1.0);
    pos = viewPos.xyz;
    gl_Position = viewPos;
    gl_PointSize = 15; //THIS IS WRONG, LOOK AT THE SLIDES
    radius = gl_PointSize;
    fragColor = color;
}