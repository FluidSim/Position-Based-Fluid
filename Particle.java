public class Particle {
	private Vector3D oldPos;
	private Vector3D newPos;
	private Vector3D velocity;
	private Vector3D force;
	private double mass;
	private double lambda;
	private double pConstraint;
	private ArrayList<Particle> neighbors;

	public Particle(Vector3D pos, float mass) {
		this.oldPos = pos;
		this.mass = mass;
		velocity = new Vector3D(0, 0, 0);
		force = new Vector3D(0, 0, 0);
		newPos = new Vector3D(0, 0, 0);
	}

	public Vector3D getOldPos() {
		return oldPos;
	}

	public Vector3D getNewPos() {
		return newPos;
	}

	public Vector3D getForce() {
		return force;
	}

	public double getMass() {
		return mass;
	}

	public ArrayList<Particle> getNeighbors() {
		return neighbors;
	}

	public double getPConstraint() {
		return pConstraint;
	}

	

}