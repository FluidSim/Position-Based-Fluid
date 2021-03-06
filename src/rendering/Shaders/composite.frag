#version 400

in vec2 fPos;

uniform vec3 color;
uniform sampler2D depthImage;
uniform sampler2D thicknessImage;
uniform vec2 screenSize;
uniform mat4 projection;
uniform mat4 mView;

layout (location = 0) out vec4 fragColor;

vec3 normalOf(vec2 posTex) {
    vec2 normTex = posTex;
    
    //differential differences
    float deltaX = 1.0f / screenSize.x;
    float deltaY = 1.0f / screenSize.y;
    
    float particleDepth = texture(depthImage, normTex).x;
    
    //z value of point at (x,y)
    float z = texture(depthImage, normTex).x;
    //z value of point at (x + 1, y)
    float zXR = texture(depthImage, normTex + vec2(deltaX, 0)).x;
    //z value of point at (x - 1, y)
    float zXL = texture(depthImage, normTex + vec2(-deltaX, 0)).x;
    //z value of point at (x, y + 1)
    float zYT = texture(depthImage, normTex + vec2(0, deltaY)).x;
    //z value of point at (x, y - 1)
    float zYB = texture(depthImage, normTex + vec2(0, -deltaY)).x;
    
    //Focal lengths
    float fx = projection[0][0];
    float fy = projection[1][1];
    
    //Dimensions of viewport
    float vx = screenSize.x;
    float vy = screenSize.y;
    
    //The of the following four values, only one is definitely needed
    //All four are being used to improve the approximation
    
    //z value of point at (x + 1, y + 1)
    float zTR = texture(depthImage, normTex + vec2(deltaX, deltaY)).x;
    //z value of point at (x - 1, y + 1)
    float zTL = texture(depthImage, normTex + vec2(-deltaX, deltaY)).x;
    //z value of point at (x + 1, y - 1)
    float zBR = texture(depthImage, normTex + vec2(deltaX, -deltaY)).x;
    //z value of point at (x - 1, y - 1)
    float zBL = texture(depthImage, normTex + vec2(-deltaX, -deltaY)).x;
    
    //first derivates of sides
    float dzXRdx = (zXR - z) / deltaX;
    float dzXLdx = (z - zXL) / deltaX;
    float dzYTdy = (zYT - z) / deltaY;
    float dzYBdy = (z - zYB) / deltaY;
    
    //first derivatives of top right corner
    float dzTRdx = (zTR - zYT) / deltaX;
    float dzTRdy = (zTR - zXR) / deltaY;
    
    //first derivatives of top left corner
    float dzTLdx = (zYT - zTL) / deltaX;
    float dzTLdy = (zTL - zXL) / deltaY;
    
    //first derivatives of bottom right corner
    float dzBRdx = (zBR - zYB) / deltaX;
    float dzBRdy = (zXR - zBR) / deltaY;
    
    //first derivatives of bottom left corner
    float dzBLdx = (zYB - zBL) / deltaX;
    float dzBLdy = (zXL - zBL) / deltaY;
    
    //first derivatives at the middle
    float dx = (zXR - zXL) / (2 * deltaX);
    float dy = (zYT - zYB) / (2 * deltaY);
    
    //second derivates at middle
    float dx2 = (dzXRdx - dzXLdx) / (2 * deltaX);
    float dy2 = (dzYTdy - dzYBdy) / (2 * deltaY);
    
    //four mixed derivatives dxdy
    float dxdyA = (dzTRdy - dzYTdy) / deltaX;
    float dxdyB = (dzYTdy - dzTLdy) / deltaX;
    float dxdyC = (dzBRdy - dzYBdy) / deltaX;
    float dxdyD = (dzYBdy - dzBLdy) / deltaX;
    
    //four mixed derivatives dydx
    float dydxA = (dzTRdx - dzXRdx) / deltaY;
    float dydxB = (dzTLdx - dzXLdx) / deltaY;
    float dydxC = (dzXRdx - dzBRdx) / deltaY;
    float dydxD = (dzXLdx - dzBLdx) / deltaY;
    
    //Final mixed derivatives (averaged)
    float dxdy = (dxdyA + dxdyB + dxdyC + dxdyD ) / 4;
    float dydx = (dydxA + dydxB + dydxC + dydxD ) / 4;
    
    //Constants
    float Cx =  2 / (vx * fx);
    float Cy =  2 / (vy * fy);
    
    //D values
    float D = (Cy * Cy * dx * dx) + (Cx * Cx * dy * dy) + (Cx * Cx * Cy * Cy * z * z);
    
    float sqrtD = sqrt(D);
    
    return inverse(mat3(mView)) * vec3(-Cy * dx, -Cx * dy, Cx * Cy * z) / sqrtD;
}

vec3 position(vec2 screenPos, float depth) {
    //Focal lengths
    float fx = projection[0][0];
    float fy = projection[1][1];
    
    vec2 pos = screenPos/screenSize;
    //Convert from [0,1] to [-1,1]
    pos = (pos - vec2(.5)) * 2.0;
    return inverse(mat3(mView)) * vec3(-fx*pos.x*depth, -fy*pos.y*depth, depth);
}

void main() {
    float depth = texture(depthImage,fPos).x;
    float thickness = texture(thicknessImage,fPos).x;
    thickness = thickness/2.0;
    thickness = pow(thickness, 1.2);
    
    vec3 norm = normalize(normalOf(fPos));
    vec3 lightDir = normalize(vec3(1.0f,0.0f,-1.0f));
    vec3 pos = position(fPos, depth);
    vec3 view = normalize(-pos);
    float nDotV = -dot(norm, view);
    //Reflectance at Normal
    float reflectance = 0.96;
    float R = reflectance + (1 - reflectance) * pow((1 - nDotV), 5);
    
    vec3 H = normalize(lightDir + view);
    
    vec3 specular = max(0.0, dot(lightDir, H)) * vec3(1.0,1.0,1.0);
    specular = pow(specular,vec3(5.0));
    vec4 particleColor = exp(-1*vec4(.1f,.1f,.02f,1.5f) * thickness);

    if (thickness == 0.0){
        fragColor = vec4(1.0);
    }
    else{
        fragColor = particleColor;//vec4(specular,1.0);//vec4(diffuse,1.0);//vec4(diffuse,1.0) * 10;
        //fragColor = vec4(exp(-1*diffuse*thickness/10.0),1.0);
    }
    
}