#version 400
 
in vec4 vertexPos
in vec3 color

uniform mat4 mViewProj;
uniform vec2 screenSize;
uniform vec3 lightPos;

out vec3 pos;
out float radius;
out vec3 fragColor;
 
void main() {
    vec4 viewPos = mViewProj * vec4(vertexPos.xyz, 1.0);
    pos = viewPos.xyz;
    gl_Position = viewPos;
    gl_PointSize = 20;
    radius = gl_PointSize;
    fragColor = color;
}
