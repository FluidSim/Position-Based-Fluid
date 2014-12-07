#version 400

in vec3 pos;
in float radius;

uniform vec3 color;
uniform mat4 projectMatrix;
uniform vec2 screenSize;

out vec3 Color;
out float depth;
 
void main() {
	//calculate normal
	vec3 N;
	N.xy = gl_PointCoord * 2.0 - 1.0;
	float r2 = length(N);
	
	if (r2 > 1.0) {
		discard;
	}
	
	N.z = -sqrt(1 - r2);
	
	//calculate depth
	vec4 pixelPos = vec4(pos + N * radius, 1.0);
	vec4 clipSpacePos = pixelPos * projectMatrix;
	
	
}
