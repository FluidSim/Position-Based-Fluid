import java.util.ArrayList;

public class Cell {
	private ArrayList<Particle> particles;
	private ArrayList<Cell> neighbors;

	public Cell() {
		particles = new ArrayList<Particle>();
		neighbors = new ArrayList<Cell>();
	}

	public void addParticle(Particle particle) {
		particles.add(particle);
	}

	public void clearParticle() {
		particles.clear();
	}

	public void addNeighbor(Cell cell){
		neighbors.add(cell);
	}

	public ArrayList<Cell> getNeighbors() {
		return neighbors;
	}

	public void setNeighbours(ArrayList<Cell> neighbours) {
		this.neighbors = neighbors;
	}

	public ArrayList<Particle> getParticles() {
		return particles;
	}

	public void setParticles(ArrayList<Particle> particles) {
		this.particles = particles;
	}
}