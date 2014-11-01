public class ParticleSystem {
	private ArrayList<Particle> particles;
	//private CellCube cube;

	private static final Vector3D GRAVITY = new Vector3D(0, -9.8f, 0);

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
				//calculate gradient constraint
				//calculate lambda
			}

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

	public void applyGravity() {
		for (Particle p : particles) {
			particle.getForce().add(GRAVITY);
		}
	}

	public Vector3D gradientConstraint(Particle p, Particle neighbor) {
		//first case k == i
		if (p.equals(neighbor)) {
			
		} else { //second case k == j

		}
	}

	public void lambda(Particle p) {

	}
}