package physics;
import java.util.ArrayList;

public class Particle {
	private Vector3 oldPos;
	private Vector3 newPos;
	private Vector3 velocity;
	private Vector3 force;
	private Vector3 deltaP;
	private float mass;
	private float density;
	private float lambda;
	private float pConstraint;
	private ArrayList<Particle> neighbors;

	public Particle(Vector3 pos, float mass) {
		this.oldPos = pos;
		this.mass = mass;
		velocity = new Vector3(0f, 0f, 0f);
		force = new Vector3(0f, 0f, 0f);
		newPos = new Vector3(0f, 0f, 0f);
		neighbors = new ArrayList<Particle>();
	}

	public Vector3 getOldPos() {
		return oldPos;
	}

	public Vector3 getNewPos() {
		return newPos;
	}

	public Vector3 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector3 velocity) {
		this.velocity = velocity;
	}

	public Vector3 getForce() {
		return force;
	}

	public float getMass() {
		return mass;
	}

	public float getDensity() {
		return density;
	}

	public void setDensity(float density) {
		this.density = density;
	}

	public ArrayList<Particle> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(ArrayList<Particle> neighbors) {
		this.neighbors = neighbors;
	}

	public float getPConstraint() {
		return pConstraint;
	}

	public void setPConstraint(float f) {
		pConstraint = f;
	}

	public void setNewPos(Vector3 v) {
		newPos = v;
	}
	
	public void setOldPos(Vector3 v) {
		oldPos = v;
	}

	public Vector3 getDeltaP() {
		return deltaP;
	}

	public void setDeltaP(Vector3 deltaP) {
		this.deltaP = deltaP;
	}
}