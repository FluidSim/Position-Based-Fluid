package physics;

public class Vector3 {
	
	public float x;
	public float y;
	public float z;

	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
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

	public Vector3 cross(Vector3 v) {
		float x = this.y * v.z - this.z * v.y;
		float y = this.z * v.x - this.x * v.z;
		float z = this.x * v.y - this.y * v.x;
		return new Vector3(x, y, z);
	}

}
