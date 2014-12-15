#version 400

in vec3 pos;
in float radius;

uniform mat4 mView;
uniform mat4 projection;
uniform vec2 screenSize;
uniform vec3 lightPos;

out float thickness;

void main() {
	//calculate normal
	vec3 normal;
	normal.xy = gl_PointCoord * 2.0 - 1.0;
	float r2 = dot(normal.xy, normal.xy);
	
	if (r2 > 1.0) {
		discard;
	}
	
    thickness = 1.0 - r2;
}