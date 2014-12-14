#version 400

in vec3 fColor;
in vec2 fPos;

uniform sampler2D depthImage;
uniform sampler2D thicknessImage;
uniform vec2 screenSize;

out vec4 fragColor;

void main() {
    float depth = texture(depthImage,fPos/screenSize).x;
    float thickness = texture(depthImage,fPos/screenSize).x;
    
    if (thickness == 0.0){
        fragColor = vec4(0.0,0.0,0.0,1.0);
    }
    else{
        fragColor = vec4(exp(-fColor*thickness),1.0)
    }
}