package physics;

import java.util.ArrayList;

import egl.math.Vector3;

public class ParticleSystem {
	private ArrayList<Particle> particles = new ArrayList<Particle>();
	private CellGrid cube;

	// private static final Vector3 GRAVITY = new Vector3(0f, -9.8f, 0f);
	private float deltaT = 0.1f;
	private static final float H = 2f;
	private static final float KPOLY = (float) (315f / (64f * Math.PI * Math
			.pow(H, 9)));
	// We may want to damp the spiky density
	private static final float SPIKY = (float) (45f / (Math.PI * Math.pow(H, 6)));
	private static final float REST_DENSITY = 1f;
	private static final float EPSILON = .1f; // what value?
	private static final float C = 0.01f;

	public ParticleSystem(float deltaT, boolean randomStart) {
		this.deltaT = deltaT;
		if (!randomStart) {
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 10; j++) {
					for (int k = 0; k < 10; k++) {
						particles.add(new Particle(new Vector3(i, j, k), 1));
					}
				}
			}
		} else {
			for (int i = 0; i < 5000; i++) {
				particles.add(new Particle(new Vector3(
						(float) Math.random() * 10, (float) Math.random() * 10,
						(float) Math.random() * 10), 1));
			}
		}
		// create cell cube
		cube = new CellGrid(15, 15, 15); // should be whatever the size of our
											// box is
	}

	public ArrayList<egl.math.Vector3> getPositions() {
		ArrayList<egl.math.Vector3> positions = new ArrayList<egl.math.Vector3>();
		for (Particle part : particles) {
			Vector3 pos = part.getOldPos();
			positions.add(new egl.math.Vector3(pos.x, pos.y, pos.z));
		}
		return positions;
	}

	public static void main(String args[]) {
		ParticleSystem ps = new ParticleSystem(0.1f,true);
		while (true) {
			ps.update();
		}
	}

	public void update() {
		// Removed apply gravity in favor of p.resetToGravity()
		// applyGravity();

		for (Particle p : particles) {
			
			p.setNewPos(p.getOldPos().clone());

			// Reset force and apply gravity which is constant
			p.resetToGravity();

			// update velocity vi = vi + delta T * fext
			p.getVelocity().add(p.getForce().mul(deltaT));

			// predict position x* = xi + delta T * vi
			p.getNewPos().add(p.getVelocity().mul(deltaT));
			
			imposeConstraints(p);
		}

		// get neighbors
		
		cube.updateCells(particles);
		for (Particle p : particles) {
			ArrayList<Particle> neighbors = new ArrayList<Particle>(p.getCell()
					.getParticles());
			neighbors.remove(p);
			p.setNeighbors(neighbors);

		}

		// while sovler < iterations (they say that 2-4 is enough in the paper)
		for (int i = 0; i < 4; i++) {

			// Set lambda
			for (Particle p : particles) {
				ArrayList<Particle> neighbors = p.getNeighbors();
				p.setLambda(lambda(p, neighbors));

			}
			// Calculate deltaP
			// TODO:Perform collision and detection response
			for (Particle p : particles) {
				Vector3 deltaP = new Vector3(0f, 0f, 0f);
				ArrayList<Particle> neighbors = p.getNeighbors();
				for (Particle n : neighbors) {
					float lambdaSum = p.getLambda() + n.getLambda();
					deltaP.add((WSpiky(p, n)).mul(lambdaSum));
				}
				p.setDeltaP(deltaP.div(REST_DENSITY));
			}
			// Update position x*i = x*i + delta Pi
			for (Particle p : particles) {
				p.getNewPos().add(p.getDeltaP());
			}
		}

		for (Particle p : particles) {
			// set new velocity vi = (1/delta T) * (x*i - xi)

			p.setVelocity(((p.getNewPos().clone()).sub(p.getOldPos().clone()))
					.div(deltaT));
			// apply vorticity confinement
			// omega(p);
			// applyVorticity(p);

			// p.setVelocity(p.getVelocity().add(p.getForce().mul(deltaT)));
			// p.setNewPos(p.getOldPos().add(p.getVelocity().mul(deltaT)));

			// apply XSPH viscosity

			// update position xi = x*i
			

			p.setOldPos(p.getNewPos().clone());
		}
	}

	// Poly6 Kernel
	private float WPoly6(Particle pi, Particle pj) {
		// Check if particles are in the same place
		if (pi.getNewPos().equalsApprox(pj.getNewPos())) {
			pj.getNewPos().add((float) Math.random() * 1e-3f);
		}
		Vector3 r = new Vector3(pi.getNewPos().clone()
				.sub(pj.getNewPos().clone()));
		if (r.len() > H)
			return 0;
		return (float) (KPOLY * Math.pow((Math.pow(H, 2.0) - r.lenSq()), 3));
	}

	// Spiky Kernel
	private Vector3 WSpiky(Particle pi, Particle pj) {
		// Check if particles are in the same place
		if (pi.getNewPos().equalsApprox(pj.getNewPos())) {
			pj.getNewPos().add((float) Math.random() * 1e-3f);
		}
		Vector3 r = new Vector3(pi.getNewPos().clone()
				.sub(pj.getNewPos().clone()));
		if (r.len() > H)
			return new Vector3(0f, 0f, 0f);
		float coeff = (float) Math.pow(H - r.len(), 2);
		coeff *= SPIKY;
		coeff /= r.len();
		return r.mul(coeff);
	}

	private float lambda(Particle p, ArrayList<Particle> neighbors) {
		float densityConstraint = calcDensityConstraint(p, neighbors);
		Vector3 gradientI = new Vector3(0f, 0f, 0f);
		float sumGradients = 0;
		for (Particle n : neighbors) {
			// Calculate gradient with respect to j
			Vector3 gradientJ = new Vector3((WSpiky(p, n)).div(REST_DENSITY));
			// Add magnitude squared to sum
			sumGradients += gradientJ.lenSq();
			// Continue calculating particle i gradient
			gradientI.add(gradientJ);
		}
		// Add the particle i gradient magnitude squared to sum
		sumGradients += gradientI.lenSq();
		return ((-1f) * densityConstraint) / (sumGradients + EPSILON);
	}

	private float calcDensityConstraint(Particle p, ArrayList<Particle> neighbors) {
		float sum = 0f;
		for (Particle n : neighbors) {
			sum += n.getMass() * WPoly6(p, n);
		}
		return (sum / REST_DENSITY) - 1;
	}
	
	private void omega(Particle p) {
		Vector3 w = new Vector3(0, 0, 0);
		Vector3 velocityDiff;
		Vector3 gradient;
		ArrayList<Particle> neighbors = p.getNeighbors();
		for (Particle n : neighbors) {
			velocityDiff = new Vector3(n.getVelocity().clone()
					.sub(p.getVelocity().clone()));
			gradient = WSpiky(p, n);
			w.add(velocityDiff.cross(gradient));
		}
		// I don't think there's a reason for this
		// let's just output the answer or abstract all of vorticity force into
		// a function
		p.setOmega(w);
	}

	// I don't really follow the reasoning for this method -Steve
	private void applyVorticity(Particle p) {
		Vector3 N;
		Vector3 w = p.getOmega();
		Vector3 gradient = new Vector3(0, 0, 0);
		Vector3 vorticity;
		ArrayList<Particle> neighbors = p.getNeighbors();
		for (Particle n : neighbors) {
			Vector3 d = n.getNewPos().sub(p.getNewPos());
			// Why do you need the curl?
			Vector3 mw = n.getOmega().sub(w);
			float magnitudeW = mw.len();
			gradient.x += magnitudeW / d.x;
			gradient.y += magnitudeW / d.y;
			gradient.z += magnitudeW / d.z;
		}

		N = gradient.div(gradient.len());
		vorticity = (N.cross(w)).mul(EPSILON);
		p.getForce().add(vorticity);
	}
	
	//Make sure that particle does not leave the cube grid
	private void imposeConstraints(Particle p) {
		if (p.getNewPos().x < 0f) {
			p.getNewPos().x = (float)(0f + Math.random()*1e-3);
		}
		if (p.getNewPos().y < 0f) {
			p.getNewPos().y = (float)(0f + Math.random()*1e-3);
		}
		if (p.getNewPos().z < 0f) {
			p.getNewPos().z = (float)(0f + Math.random()*1e-3);
		}
		if (p.getNewPos().x >= cube.getWidth()) {
			p.getNewPos().x = (float) (cube.getWidth() - Math.random()*1e-3);
		}
		if (p.getNewPos().y >= cube.getHeight()) {
			p.getNewPos().y = (float) (cube.getHeight() - Math.random()*1e-3);;
		}
		if (p.getNewPos().z >= cube.getDepth()) {
			p.getNewPos().z = (float) (cube.getDepth() - Math.random()*1e-3);;
		}
	}
}
