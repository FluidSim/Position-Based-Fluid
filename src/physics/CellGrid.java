package physics;

import java.util.ArrayList;

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
			Cell cell = cells[(int) pos.x][(int) pos.y][(int) pos.z];
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
}