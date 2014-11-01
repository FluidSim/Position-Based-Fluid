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
		neighbors = new ArrayList<Particle>();
	}

	public Vector3D getOldPos() {
		return oldPos;
	}

	public Vector3D getNewPos() {
		return newPos;
	}

	public Vector3D getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector3D velocity) {
		this.velocity = velocity;
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

	public void setNeighbors(ArrayList<Particle> neighbors) {
		this.neighbors = neighbors;
	}

	public double getPConstraint() {
		return pConstraint;
	}
}