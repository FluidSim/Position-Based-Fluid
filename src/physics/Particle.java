package physics;
import java.util.ArrayList;

import egl.math.Vector3;

public class Particle {
	private Vector3 oldPos;
	private Vector3 newPos;
	private Vector3 velocity;
	private Vector3 force;
	private Vector3 deltaP;
	private Vector3 omega;
	private float mass;
	private float lambda;
	private float pConstraint;
	private ArrayList<Particle> neighbors;
	private Cell cell;

	public Particle(Vector3 pos, float mass) {
		this.oldPos = pos;
		this.mass = mass;
		newPos = new Vector3(0f, 0f, 0f);
		velocity = new Vector3(0f, 0f, 0f);
		force = new Vector3(0f, 0f, 0f);
		deltaP = new Vector3(0f, 0f, 0f);
		neighbors = new ArrayList<Particle>();
	}

	public void imposeConstraints() {

		if (this.newPos.x < 0f) {
			this.newPos.x = 0f;
		}
		if (this.newPos.y < 0f) {
			this.newPos.y = 0f;
		}
		if (this.newPos.z < 0f) {
			this.newPos.z = 0f;
		}
		if (this.newPos.x > 15f) {
			this.newPos.x = 15f -0.001f;
		}
		if (this.newPos.y > 15f) {
			this.newPos.y = 15f - 0.001f;;
		}
		if (this.newPos.z > 15f) {
			this.newPos.z = 15f -0.001f;;
		}
		
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
	
	//Resets the force to the gravitational force only
	//F = (0, -9.8, 0)
	public void resetToGravity() {
		force = new Vector3 (0, -9.8f, 0);
	}

	public Vector3 getForce() {
		return force;
	}

	public float getMass() {
		return mass;
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

	public float getLambda() {
		return lambda;
	}

	public void setLambda(float lambda) {
		this.lambda = lambda;
	}

	public Cell getCell() {
		return cell;
	}

	public void setCell(Cell cell) {
		this.cell = cell;
	}

	public Vector3 getOmega() {
		return omega;
	}

	public void setOmega(Vector3 curl) {
		this.omega = omega;
	}
}