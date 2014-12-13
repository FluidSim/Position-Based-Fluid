#version 400

in vec3 pos;
in float radius;
in vec3 fragColor;

uniform mat4 mView;
uniform mat4 projection;
uniform vec2 screenSize;
uniform vec3 lightPos;

out vec4 outColor;

void main() {
	//calculate normal
	vec3 normal;
	normal.xy = gl_PointCoord * 2.0 - 1.0;
	float r2 = dot(normal.xy, normal.xy);
	
	if (r2 > 1.0) {
		discard;
	}
	
	float alpha = 1.0 - r2;
	
	normal.z = -sqrt(1.0 - r2);
    
    float diffuse = max(0.0, dot(normal, pos - lightPos));
    diffuse = diffuse/(length(normal) * length(pos-lightPos));
    
    diffuseColor = diffuse * fragColor;
	outColor = vec4(diffuseColor, alpha);
}