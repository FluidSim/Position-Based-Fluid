#version 400

in vec2 fPos;

uniform vec3 color;
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

vec3 normal(vec2 posTex) {
    vec2 normTex = posTex / screenSize;
    
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
    
    //first derivatives at the middle (averaging the two sides)
    float dx = (dzXRdx + dzXLdx) / 2;
    float dy = (dzYTdy + dzYTdy) / 2;
    
    //second derivates at middle
    float dx2 = (dzXRdx - dzXLdx) / (2 * deltaX);
    float dy2 = (dzYTdy - dzYBdy) / (2 * deltaY);
    
    //four mixed derivatives dxdy
    float dxdyA = (dzTRdy - dzYTdy) / deltaX;
    float dxdyB = (dzYTdy - dzTLdy) / deltaX;
    float dxdyC = (dzBRdy - dzYTdy) / deltaX;
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
    
    return vec3(-Cy * dx, -Cx * dy, Cx * Cy * z) / sqrtD;
}