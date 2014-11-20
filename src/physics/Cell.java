package physics;
import java.util.ArrayList;

public class Cell {
	private ArrayList<Particle> particles;
	private ArrayList<Cell> neighbors;

	public Cell() {
		particles = new ArrayList<Particle>();
		neighbors = new ArrayList<Cell>();
	}
	
	public void addNeighbor(Cell cell) {
		neighbors.add(cell);
	}
	
	public ArrayList<Cell> getNeighbors() {
		return neighbors;
	}
	
	public void setNeighbors(ArrayList<Cell> neighbors) {
		this.neighbors = neighbors;
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