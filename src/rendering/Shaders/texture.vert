#version 400

in vec3 pos;

uniform sampler2D tex;

out vec2 uv;

void main() {
    uv = pos.xy;
    gl_Position = vec4(pos,1.0);
}