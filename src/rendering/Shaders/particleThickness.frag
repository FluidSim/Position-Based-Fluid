#version 400

in vec3 pos;
in float radius;
in vec3 fragColor;

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
	
	//Scale down alpha to make things more transparent
	//alpha = alpha / 5;
	
	//normal.z = -sqrt(1.0 - r2);
    
    //vec3 lightDir = pos - lightPos;
    //lightDir = normalize(lightDir);
    //normal = normalize(normal);
    //float diffuse = max(0.0, dot(normal, lightDir));
    
    //vec3 diffuseColor = diffuse * fragColor;
	//outColor = vec4(diffuseColor, alpha);
}