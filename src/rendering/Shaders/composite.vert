#version 400

in vec2 pos;

uniform vec3 color;
uniform sampler2D depthImage;
uniform sampler2D thicknessImage;
uniform vec2 screenSize;
uniform mat4 projection;

out vec2 fPos;

void main() {
    fPos = pos;
    gl_Position = vec4(pos/screenSize,0.0,1.0);
}