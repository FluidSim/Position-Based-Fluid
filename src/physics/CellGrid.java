package physics;

import java.util.ArrayList;

import egl.math.Vector3;

public class CellGrid {
	private int width;
	private int height;
	private int depth;
	private Cell cells[][][]; //fixed size uniform grid of cells

	public CellGrid(int width, int height, int depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		cells = new Cell[width][height][depth];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				for (int k = 0; k < depth; k++) {
					cells[i][j][k] = new Cell();
				}
			}
		}
	}

	public void updateCells(ArrayList<Particle> particles) {
		clearCells();
		for (Particle p: particles) {
			Vector3 pos = p.getNewPos();
			//assuming indices are always valid because the box keeps the particles contained
			//Cell cell = cells[(int) (pos.x*(15f/ParticleSystem.rangex))][(int) (15f/ParticleSystem.rangey)][(int) (15f/ParticleSystem.rangez)];
			Cell cell = cells[(int) (pos.x*(width/ParticleSystem.rangex))][(int) (pos.y*(height/ParticleSystem.rangey))][(int) (pos.z*(depth/ParticleSystem.rangez))];

			cell.addParticle(p);
			p.setCell(cell);
		}
	}

	public void clearCells() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				for (int k = 0; k < depth; k++) {
					cells[i][j][k].clearParticles();
				}
			}
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getDepth() {
		return depth;
	}
}