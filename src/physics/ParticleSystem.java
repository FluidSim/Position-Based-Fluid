package physics;
import java.util.ArrayList;

public class ParticleSystem {
	private ArrayList<Particle> particles;
	//private CellCube cube;

	private static final Vector3 GRAVITY = new Vector3(0f, -9.8f, 0f);
	private static final float deltaT = 0.1f;
	private static final float H = 1f;
	private static final float KPOLY = (float) (315f / (64f * Math.PI * Math.pow(H, 9)));
	//We may want to damp the spikey density
	private static final float SPIKY = (float) (45f / (Math.PI * Math.pow(H, 6)));
	private static final float REST_DENSITY = 1f; //not sure what this should be

	public ParticleSystem() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				for (int k = 0; k < 10; k++) {
					particles.add(new Particle(new Vector3(i, j, k), 1));
				}
			}
		}

		//create cell cube
	}

	public void update() {
		applyGravity();
		//predict position x* = xi + delta T * vi

		//get neighbors

		//while sovler < iterations (they say that 2-4 is enough in the paper)
		for (int i = 0; i < 4; i++) {
			for (Particle p : particles) {
				//calculate c (density constraint)
				float density = 0;
				ArrayList<Particle> neighbors = p.getNeighbors();
				for (Particle n : neighbors) {
					density += kernelCalc(p.getNewPos(), n.getNewPos());
				}
				p.setDensity(density);
				p.setPConstraint((density / REST_DENSITY) - 1);
			}

			//calculate gradient constraint
			for (Particle p : particles) {
				ArrayList<Particle> neighbors = p.getNeighbors();
				for (Particle n : neighbors) {
					Vector3 gradient = gradientConstraint(p, n);
				}
			}

			//calculate lambda

			for (Particle p : particles) {
				//update position - delta Pi - requires lambda
				//collision detection including with box
			}

			for (Particle p : particles) {
				//update x*i = x*i + delta Pi
				p.setNewPos(p.getNewPos().add(p.getDeltaP()));
			}
		}

		for (Particle p : particles) {
			//set new velocity vi = (1/delta T) * (x*i - xi)
			p.setVelocity(p.getNewPos().sub(p.getOldPos()).div(deltaT));
			//TODO: apply vorticity confinement and XSPH viscosity
			//update position xi = x*i
			p.setOldPos(p.getNewPos());
		}
	}

	private void applyGravity() {
		for (Particle p : particles) {
			p.getForce().add(GRAVITY);
		}
	}

	//Poly6 Kernel
	private float kernelCalc(Vector3 pi, Vector3 pj) {
		float rSquared = (float) Math.pow(pi.sub(pj).magnitude(), 2);
		if (rSquared > H) return 0;
		return (float) (KPOLY * Math.pow((H - rSquared), 3));
	}

	//TODO
	private Vector3 gradientConstraint(Particle p, Particle neighbor) {
		//first case k == i
		if (p.equals(neighbor)) {
			Vector3 sum = new Vector3(0f, 0f, 0f);
			ArrayList<Particle> neighbors = p.getNeighbors();
			for (Particle n : neighbors) {
				if (!n.equals(p)) {
					//Vector3D gradient = 
					//sum.add(gradient);
				}
			}

			sum = sum.div(REST_DENSITY);
			return sum;
		} else { //second case k == j

		}
		
		//Suppress errors
		return null;
	}

	private void lambda(Particle p) {

	}
}
