package physics;

public class Vector3 {
	
	public float x;
	public float y;
	public float z;

	public Vector3(float x_, float y_, float z_) {
		x = x_;
		y = y_;
		z = z_;
	}
	
	public Vector3 add (Vector3 v) {
		return new Vector3 (x + v.x, y + v.y, z + v.z);
	}
	
	public Vector3 sub (Vector3 v) {
		return new Vector3 (x - v.x, y - v.y, z - v.z);
	}
	
	public Vector3 mul (float f) {
		return new Vector3 (x * f, y * f, z * f);
	}
	
	public Vector3 div (float f) {
		return new Vector3 (x / f, y / f, z / f);
	}
	
	public float magnitude() {
		return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}

}
