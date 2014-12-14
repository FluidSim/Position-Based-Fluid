#version 400

in vec2 screenPos;

uniform sampler2D tex;
uniform mat4 projection;
uniform vec2 screenSize;

out vec2 posTex;

void main() {
    posTex = screenPos;
    gl_Position = vec4(screenPos/screenSize, 0.0, 1.0);
}