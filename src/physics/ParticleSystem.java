package physics;

import java.util.ArrayList;

import egl.math.Vector3;

public class ParticleSystem {
	private ArrayList<Particle> particles = new ArrayList<Particle>();
	private CellGrid cube;

	private static final Vector3 GRAVITY = new Vector3(0f, -9.8f, 0f);
	// deltaT is the timestep
	private float deltaT = 0.005f;
	// H is radius of influence
	// KPOLY and SPIKY are constant coefficients used in Density Estimation
	// Kernels
	// See Macklin slides or Muller 2003
	private static final float H = 2f;
	private static final float KPOLY = (float) (315f / (64f * Math.PI * Math.pow(H, 9)));
	private static final float SPIKY = (float) (15f / (Math.PI * Math.pow(H, 6)));
	private static final float REST_DENSITY = 1f;
	// Epsilon used in lambda calculation
	// See Macklin part 3
	private static final float EPSILON = 10f; // what value?
	private static final float C = 0.01f;
	// K and deltaQMag used in sCorr Calculation
	// See Macklin part 4
	private static final float K = 0.1f;
	private static final float deltaQMag = .3f * H;
	private static final float wQH = KPOLY * (H * H - deltaQMag * deltaQMag) * (H * H - deltaQMag * deltaQMag) * (H * H - deltaQMag * deltaQMag);
	// Used for bounds of the box
	public static int rangex = 50;
	public static int rangey = 100;
	public static int rangez = 10;

	public ParticleSystem(float deltaT, boolean randomStart) {
		this.deltaT = deltaT;
		if (!randomStart) {
			for (int i = 0; i < 30; i++) {
				for (int j = 25; j < 40; j++) {
					for (int k = 0; k < 5; k++) {
						particles.add(new Particle(new Vector3(i, j, k), 1));
					}
				}
			}
		} else {
			for (int i = 0; i < 5000; i++) {
				particles.add(new Particle(new Vector3((float) Math.random() * rangex, (float) Math.random() * rangey, (float) Math.random() * rangez), 1));
			}
		}
		// create cell cube
		cube = new CellGrid(rangex, rangey, rangez); // should be whatever the
														// size of our box is
	}

	public ArrayList<Vector3> getPositions() {
		ArrayList<Vector3> positions = new ArrayList<Vector3>();
		for (Particle part : particles) {
			Vector3 pos = part.getOldPos();
			positions.add(new Vector3(pos.x, pos.y, pos.z));
		}

		return positions;
	}

	public void update() {
		for (Particle p : particles) {
			applyGravity(p);
			p.setNewPos(p.getOldPos().clone());

			// update velocity vi = vi + delta T * fext
			p.getVelocity().add(p.getForce().mul(deltaT));

			// predict position x* = xi + delta T * vi
			p.getNewPos().add(p.getVelocity().mul(deltaT));

			imposeConstraints(p);
		}

		// get neighbors
		cube.updateCells(particles);
		for (Particle p : particles) {
			ArrayList<Particle> neighbors = new ArrayList<Particle>(p.getCell().getParticles());
			neighbors.remove(p);
			p.setNeighbors(neighbors);
		}

		// while solver < iterations (they say that 2-4 is enough in the paper)
		for (int i = 0; i < 4; i++) {
			// Set lambda
			for (Particle p : particles) {
				ArrayList<Particle> neighbors = p.getNeighbors();
				p.setLambda(lambda(p, neighbors));
			}
			// Calculate deltaP
			for (Particle p : particles) {
				Vector3 deltaP = new Vector3(0f, 0f, 0f);
				ArrayList<Particle> neighbors = p.getNeighbors();
				for (Particle n : neighbors) {
					float lambdaSum = p.getLambda() + n.getLambda();
					float sCorr = sCorr(p, n);
					// float sCorr = 0;
					deltaP.add((WSpiky(p.getNewPos(), n.getNewPos())).mul(lambdaSum + sCorr));
				}

				p.setDeltaP(deltaP.div(REST_DENSITY));
			}

			// Update position x*i = x*i + delta Pi
			for (Particle p : particles) {
				p.getNewPos().add(p.getDeltaP());
			}
		}

		for (Particle p : particles) {
			imposeConstraints(p);
			// set new velocity vi = (1/delta T) * (x*i - xi)
			p.setVelocity(((p.getNewPos().clone()).sub(p.getOldPos().clone())).div(deltaT));

			// apply vorticity confinement
			p.getVelocity().add(vorticityForce(p).mul(deltaT));
			
			// apply XSPH viscosity

			// update position xi = x*i
			p.setOldPos(p.getNewPos().clone());
		}
	}

	private void applyGravity(Particle p) {
		p.setForce(0f, 0f, 0f);
		p.getForce().add(GRAVITY);
	}

	// Poly6 Kernel
	private float WPoly6(Vector3 pi, Vector3 pj) {
		// Check if particles are in the same place
		if (pi.equalsApprox(pj)) {
			pj.add((float) Math.random() * 1e-3f);
		}

		Vector3 r = new Vector3(pi.clone().sub(pj.clone()));
		float rLen = r.len();
		if (rLen > H) {
			return 0;
		}
		return (float) (KPOLY * Math.pow((H * H - r.lenSq()), 3));
	}

	// Spiky Kernel
	private Vector3 WSpiky(Vector3 pi, Vector3 pj) {
		// Check if particles are in the same place
		if (pi.equalsApprox(pj)) {
			pj.add((float) Math.random() * 1e-3f);
		}

		Vector3 r = new Vector3(pi.clone().sub(pj.clone()));
		float rLen = r.len();
		if (rLen > H) {
			return new Vector3(0f, 0f, 0f);
		}

		float coeff = (H - rLen) * (H - rLen);
		coeff *= SPIKY;
		coeff /= rLen;
		return r.mul(coeff);
	}

	private float lambda(Particle p, ArrayList<Particle> neighbors) {
		float densityConstraint = calcDensityConstraint(p, neighbors);
		Vector3 gradientI = new Vector3(0f, 0f, 0f);
		float sumGradients = 0;
		for (Particle n : neighbors) {
			// Calculate gradient with respect to j
			Vector3 gradientJ = new Vector3((WSpiky(p.getNewPos(), n.getNewPos())).div(REST_DENSITY));
			// Add magnitude squared to sum
			sumGradients += gradientJ.lenSq();
			// Continue calculating particle i gradient
			gradientI.add(gradientJ);
		}
		// Add the particle i gradient magnitude squared to sum
		sumGradients += gradientI.lenSq();
		return ((-1f) * densityConstraint) / (sumGradients + EPSILON);

		
		/*float sum = 0; 
		for (Particle n : neighbors) { //k == i 
			Vector3 v = new Vector3(0f, 0f, 0f); 
			if (p.equals(n)) { 
				ArrayList<Particle> ns = p.getNeighbors();
				for (Particle ne : ns) { 
					if (!ne.equals(p)) {
						Vector3 gradient = WSpiky(p.getNewPos(), ne.getNewPos());
						v.add(gradient); 
					}
				}
				v.div(REST_DENSITY); 
				sum += v.lenSq(); 
			} else {
				Vector3 gradient = WSpiky(p.getNewPos(), n.getNewPos());
				gradient.div(-1 * REST_DENSITY); 
				sum += gradient.lenSq(); 
			}
		}
		
		return ((-1f) * densityConstraint) / (sum + EPSILON);*/
	}

	private float calcDensityConstraint(Particle p, ArrayList<Particle> neighbors) {
		float sum = 0f;
		for (Particle n : neighbors) {
			sum += n.getMass() * WPoly6(p.getNewPos(), n.getNewPos());
		}

		return (sum / REST_DENSITY) - 1;
	}

	private Vector3 vorticity(Particle p) {
		Vector3 vorticity = new Vector3(0, 0, 0);
		Vector3 velocityDiff;
		Vector3 gradient;

		ArrayList<Particle> neighbors = p.getNeighbors();
		for (Particle n : neighbors) {
			velocityDiff = new Vector3(n.getVelocity().clone().sub(p.getVelocity().clone()));
			gradient = WSpiky(p.getNewPos(), n.getNewPos());
			vorticity.add(velocityDiff.cross(gradient));
		}

		return vorticity;
	}

	private Vector3 eta(Particle p, float vorticityMag) {
		ArrayList<Particle> neighbors = p.getNeighbors();
		Vector3 eta = new Vector3(0, 0, 0);
		for (Particle n : neighbors) {
			eta.add(WSpiky(p.getNewPos(), n.getNewPos()).mul(vorticityMag));
		}

		return eta;
	}

	private Vector3 vorticityForce(Particle p) {
		Vector3 vorticity = vorticity(p);
		if (vorticity.len() == 0) {
			// No direction for eta
			return new Vector3 (0f, 0f, 0f);
		}
		Vector3 eta = eta(p, vorticity.len());
		// Same epsilon?
		Vector3 n = eta.clone().normalize();
		return (n.cross(vorticity)).mul(EPSILON);
		
		/*
		Vector3 N;
		Vector3 w = p.getVorticity();
		Vector3 gradient = new Vector3(0, 0, 0);
		Vector3 vorticity;
		ArrayList<Particle> neighbors = p.getNeighbors();
		for (Particle n : neighbors) {
			Vector3 d = n.getNewPos().sub(p.getNewPos());
			Vector3 mw = n.getVorticity().sub(w);
			float magnitudeW = mw.len();
			gradient.x += magnitudeW / d.x;
			gradient.y += magnitudeW / d.y;
			gradient.z += magnitudeW / d.z;
		}
		
		N = gradient.div(gradient.len());
		vorticity = (N.cross(w)).mul(EPSILON);
		p.getForce().add(vorticity);
		*/
	}

	// Make sure that particle does not leave the cube grid
	private void imposeConstraints(Particle p) {
		if (outOfRange(p.getNewPos().x, 0, rangex)) {
			p.getVelocity().x = 0;
		}

		if (outOfRange(p.getNewPos().y, 0, rangey)) {
			p.getVelocity().y = 0;
		}

		if (outOfRange(p.getNewPos().z, 0, rangez)) {
			p.getVelocity().z = 0;
		}

		p.getNewPos().x = clampedConstraint(p.getNewPos().x, rangex);
		p.getNewPos().y = clampedConstraint(p.getNewPos().y, rangey);
		p.getNewPos().z = clampedConstraint(p.getNewPos().z, rangez);
	}

	private float clampedConstraint(float x, float max) {
		if (x < 0) {
			return (float) (Math.random() * 1e-3);
		} else if (x >= max) {
			return (float) (max - 1e-3f - Math.random() * 1e-3f);
		} else {
			return x;
		}
	}

	private float sCorr(Particle pi, Particle pj) {
		// Get Density from WPoly6 and divide by constant from paper
		float corr = WPoly6(pi.getNewPos(), pj.getNewPos()) / wQH;
		// take to power of 4
		corr *= corr * corr * corr;
		return -K * corr;
	}

	private static boolean outOfRange(float x, float min, float max) {
		return x < min || x >= max;
	}

}
