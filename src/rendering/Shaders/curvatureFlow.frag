in ivec2 posTex;

uniform sampler2D tex;
uniform mat4 projection;
uniform vec2 screenSize;

layout (location = 0) out float depth;

void main() {
    float particleDepth = texture(tex, posTex);
    
    //z value of point at (x,y)
    float z = texture(tex, posTex);
    //z value of point at (x + 1, y)
    float zXR = texture(tex, posTex + ivec2(1,0));
    //z value of point at (x - 1, y)
    float zXL = texture(tex, posTex + ivec2(-1,0));
    //z value of point at (x, y + 1)
    float zYT = texture(tex, posTex + ivec2(0,1));
    //z value of point at (x, y - 1)
    float zYB = texture(tex, posTex + ivec2(1,-1));
    
    //Focal lengths
    float fx = projection[0][0];
    float fy = projection[1][1];
    
    //Dimensions of viewport
    float vx = screenSize.x;
    float vy = screenSize.y;
    
    //The of the following four values, only one is definitely needed
    //All four are being used to improve the approximation
    
    //z value of point at (x + 1, y + 1)
    float zTR = 0;
    //z value of point at (x - 1, y + 1)
    float zTL = 0;
    //z value of point at (x + 1, y - 1)
    float zBR = 0;
    //z value of point at (x - 1, y - 1)
    float zBL = 0;
    
    //differential distances
    //TODO
    float deltaX = 1;
    float deltaY = 1;
    
    //first derivates of sides
    float dzXRdx = (zXR - z) / (deltaX);
    float dzXLdx = (z - zXL) / (deltaX);
    float dzYTdy = (zYT - z) / (deltaY);
    float dzYBdy = (z - zYB) / (deltaY);
    
    //first derivatives of top right corner
    float dzTRdx = (zTR - zYT) / (deltaX);
    float dzTRdy = (zTR - zXR) / (deltaY);
    
    //first derivatives of top left corner
    float dzTLdx = (zYT - zTL) / (deltaX);
    float dzTLdy = (zTL - zXL) / (deltaY);
    
    //first derivatives of bottom right corner
    float dzBRdx = (zBR - zYB) / (deltaX);
    float dzBRdy = (zXR - zBR) / (deltaY);
    
    //first derivatives of bottom left corner
    float dzBLdx = (zYB - zBL) / (deltaX);
    float dzBLdy = (zXL - zBL) / (deltaY);
    
    //first derivatives at the middle (averaging the two sides)
    float dx = (dzXRdx + dzXLdx) / 2;
    float dy = (dzYTdy + dzYTdy) / 2;
    
    //second derivates at middle
    float dx2 = (dzXRdx - dzXLdx) / (2 * deltaX);
    float dy2 = (dzYTdy - dzYBdy) / (2 * deltaY);
    
    //four mixed derivatives dxdy
    float dxdyA = (dzTRdy - dzYTdy) / (deltaX);
    float dxdyB = (dzYTdy - dzTLdy) / (deltaX);
    float dxdyC = (dzBRdy - dzYTdy) / (deltaX);
    float dxdyD = (dzYBdy - dzBLdy) / (deltaX);
    
    //four mixed derivatives dydx
    float dydxA = (dzTRdx - dzXRdx) / (deltaY);
    float dydxB = (dzTLdx - dzXLdx) / (deltaY);
    float dydxC = (dzXRdx - dzBRdx) / (deltaY);
    float dydxD = (dzXLdx - dzBLdx) / (deltaY);
    
    //Final mixed derivatives (averaged)
    float dxdy = (dxdyA + dxdyB + dxdyC + dxdyD ) / (4);
    float dydx = (dydxA + dydxB + dydxC + dydxD ) / (4);
    
    //Constants
    float Cx =  2 / (vx * fx);
    float Cy =  2 / (vy * fy);
    
    //D values
    float D = (Cy * Cy * dx * dx) + (Cx * Cx * dy * dy) + (Cx * Cx * Cy * Cy * z * z);
    float Ddx = (2 * Cy * Cy * dx * dx2) + (2 * Cx * Cx * Cy * Cy * z * dx) + (2 * Cx * Cx * dy * dxdy);
    float Ddy = (2 * Cx * Cx * dy * dy2) + (2 * Cy * Cy * Cx * Cx * z * dy) + (2 * Cy * Cy * dx * dydx);
    
    //E values
    float Ex = (0.5 * dx * Ddx) - (dx2 * D);
    float Ey = (0.5 * dy * Ddy) - (dy2 * D);
    
    //Finally we have an H value
    //Possibly have a catch for D = 0
    float  H = (Cy * Ex + Cx * Ey) / (2 * (pow(D, 1.5f)));
    
}
