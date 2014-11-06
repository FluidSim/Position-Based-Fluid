public class ParticleSystem {
	private ArrayList<Particle> particles;
	//private CellCube cube;

	private static final Vector3D GRAVITY = new Vector3D(0, -9.8f, 0);
	private static final float H = 1f;
	private static final float KPOLY = (315 / (64 * Math.PI * Math.pow(H, 9)));
	private static final float SPIKY = (15 / (Math.PI * Math.pow(H, 6)));
	private static final float REST_DENSITY; //not sure what this should be

	public ParticleSystem() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				for (int k = 0; k < 10; k++) {
					particles.add(new Particle(new Vector3D(i, j, k), 1));
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
				p.setPConstraint((density / REST_DENSITY) - 1)
			}

			//calculate gradient constraint
			for (Particle p : particles) {
				ArrayList<Particle> neighbors = p.getNeighbors();
				for (Particle n : neighbors) {
					Vector3D gradient = gradientConstraint(p, n);
				}
			}

			//calculate lambda

			for (Particle p : particles) {
				//update position - delta Pi - requires lambda
				//collision detection including with box
			}

			for (Particle p : particles) {
				//update x*i = x*i + delta Pi
			}
		}

		for (Particle p : particles) {
			//set new velocity vi = (1/delta T) * (x*i - xi)
			//apply vorticity confinement and XSPH viscosity
			//update position xi = x*i
		}
	}

	private void applyGravity() {
		for (Particle p : particles) {
			particle.getForce().add(GRAVITY);
		}
	}

	private float kernelCalc(Vector3D pi, Vector3D pj) {
		float rSquared = Vector3D.subtract(pi, pj).square(); //implement a vector3d subtraction and a square function
		if (rSquared > H) return 0;
		return (float) (KPOLY * Math.pow((H - rSquared), 3));
	}

	private Vector3D gradientConstraint(Particle p, Particle neighbor) {
		//first case k == i
		if (p.equals(neighbor)) {
			Vector3D sum = new Vector3D(0, 0, 0);
			ArrayList<Particle> neighbors = p.getNeighbors();
			for (Particle n : neighbors) {
				if (!n.equals(p)) {
					//Vector3D gradient = 
					//sum.add(gradient);
				}
			}

			sum.multiply(1 / REST_DENSITY);
			return sum;
		} else { //second case k == j

		}
	}

	private void lambda(Particle p) {

	}
}