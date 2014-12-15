#version 400

uniform sampler2D tex;

void main() {
    gl_FragColor = texture(tex,gl_FragCoord);
}