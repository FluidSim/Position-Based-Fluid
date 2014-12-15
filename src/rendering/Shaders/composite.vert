#version 400

in vec2 pos;

uniform vec3 color;
uniform sampler2D depthImage;
uniform sampler2D thicknessImage;
uniform vec2 screenSize;

out fPos;

void main() {
    fColor = color;
    fPos = pos;
    gl_Position = vec4(pos/screenSize,0.0,1.0);
}