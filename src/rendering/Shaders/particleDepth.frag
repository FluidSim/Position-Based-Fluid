#version 400

in vec3 pos;
in float radius;
in vec3 fragColor;

uniform mat4 mView;
uniform mat4 projection;
uniform vec2 screenSize;
uniform vec3 lightPos;


out vec3 outColor;
out float depth;

void main() {
	//calculate normal
	vec3 normal;
	normal.xy = gl_PointCoord * 2.0 - 1.0;
	float r2 = dot(normal.xy, normal.xy);
	
	if (r2 > 1.0) {
		discard;
	}
	
	normal.z = -sqrt(1.0 - r2);

	//calculate depth
	vec4 pixelPos = vec4(pos + normal * radius, 1.0);
	vec4 clipSpacePos = pixelPos * projection;
	depth = clipSpacePos.z / clipSpacePos.w;
    
    float diffuse = max(0.0, dot(normal, pos - lightPos));
    
    outColor = diffuse * fragColor * .1;
}