package physics;
import java.util.ArrayList;

public class Cell {
	private ArrayList<Particle> particles;

	public Cell() {
		particles = new ArrayList<Particle>();
	}

	public void addParticle(Particle particle) {
		particles.add(particle);
	}

	public void clearParticles() {
		particles.clear();
	}

	public ArrayList<Particle> getParticles() {
		return particles;
	}

	public void setParticles(ArrayList<Particle> particles) {
		this.particles = particles;
	}
}