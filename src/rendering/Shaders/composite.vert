#version 400

layout (location = 0) in vec3 pos;

uniform vec3 color;
uniform sampler2D depthImage;
uniform sampler2D thicknessImage;
uniform vec2 screenSize;
uniform mat4 projection;
uniform mat4 mView;

out vec2 fPos;

void main() {
    fPos = (pos/2.0 + vec3(.5)).xy;
    gl_Position = vec4(pos,1.0);
}