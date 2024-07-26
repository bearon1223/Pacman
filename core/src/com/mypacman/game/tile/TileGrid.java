package com.mypacman.game.tile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.mypacman.game.Pacman;

public class TileGrid {
	private Tile[] grid;
	private int rowCount, colCount;
	private float offsetX, offsetY;

	public TileGrid(Pacman app, int rowCount, int colCount) {
		grid = new Tile[rowCount * colCount];
		for (int i = 0; i < rowCount; i++) { // row
			for (int j = 0; j < colCount; j++) { // col
				grid[i * colCount + j] = new Tile(i, j, Maps.pacmanMap[i * colCount + j] == 1 ? true : false,
						Maps.pacmanMap[i * colCount + j] == 0 || Maps.pacmanMap[i * colCount + j] == 3 ? true : false,
						Maps.pacmanMap[i * colCount + j] == 3 ? true : false,
						Maps.pacmanMap[i * colCount + j] == 4 ? true : false,
						Maps.pacmanMap[i * colCount + j] == 5 ? true : false);
			}
		}
		app.log("Generating Grid (size: %d)", grid.length);

		this.rowCount = rowCount;
		this.colCount = colCount;
	}

	public void render(ShapeRenderer r, float offsetX, float offsetY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;

		for (Tile t : grid) {
			r.setColor(t.getColor());
			t.render(r, getSize(), offsetX, offsetY);
		}
	}

	public void render(SpriteBatch batch, float offsetX, float offsetY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;

		for (Tile t : grid) {
			t.render(batch, getSize(), offsetX, offsetY);
		}
	}

	public Tile[] getTileGrid() {
		return grid;
	}
	
	public int pelletCount() {
		int pelletCount = 0;
		for(Tile t : grid) {
			if(!t.isObstical() && t.hasPellet()) {
				pelletCount++;
			}
		}
		return pelletCount;
	}

	public Array<Tile> getNeighbors(Tile tile) {
		Array<Tile> t = new Array<>();
		int[] loc = tile.getLocation();
		if (loc[0] > 0) {
			if (!getTile(loc[0] - 1, loc[1]).isObstical())
				t.add(getTile(loc[0] - 1, loc[1]));
		}
		if (loc[0] < rowCount - 1) {
			if (!getTile(loc[0] + 1, loc[1]).isObstical())
				t.add(getTile(loc[0] + 1, loc[1]));
		}
		if (loc[1] > 0) {
			if (!getTile(loc[0], loc[1] - 1).isObstical())
				t.add(getTile(loc[0], loc[1] - 1));
		}
		if (loc[1] < colCount - 1) {
			if (!getTile(loc[0], loc[1] + 1).isObstical())
				t.add(getTile(loc[0], loc[1] + 1));
		}
		return t;
	}

	/**
	 * Gets the tile at a certain row and col.
	 * 
	 * @param row
	 * @param col
	 * @return tile
	 */
	public Tile getTile(int row, int col) {
//		System.out.printf("row = %d, col = %d\n", row, col);
		if (row >= 0 && row < rowCount && col >= 0 && col < colCount) {
			return grid[row * colCount + col];
		}
		if (row == 15)
			return new Tile(-1, -1, false, false, false, true, false);
		return new Tile(-1, -1, true, false, false, true, false);
	}

	/**
	 * Prints the tile grid to the console (1 = obstacle, 0 = clear)
	 */
	public void print() {
		for (int i = 0; i < rowCount; i++) { // row
			for (int j = 0; j < colCount; j++) { // col
				System.out.printf("%d, ", grid[i * colCount + j].isObstical() ? 1 : 0);
			}
			System.out.println();
		}
	}

	/**
	 * @return size of an individual tile
	 */
	public float getSize() {
		return Math.min((Gdx.graphics.getHeight() - 2 * offsetY) / rowCount,
				(Gdx.graphics.getWidth() - 2 * offsetX) / colCount);
	}

	/**
	 * @return array [offsetX, offsetY]
	 */
	public float[] getOffset() {
		float[] offset = { offsetX, offsetY };
		return offset;
	}

	public int getRowCount() {
		return rowCount;
	}

	public int getColCount() {
		return colCount;
	}

	/**
	 * Gets the tile that was clicked on using the Button in the UIHelper class
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @return Tile
	 */
	public Tile getTileMouse(int mouseX, int mouseY) {
		for (Tile t : grid) {
			if (t.b.clicked(mouseX, mouseY)) {
				return t;
			}
		}
		return grid[0];
	}

	public void dispose() {
		for (Tile t : grid) {
			t.dispose();
		}
	}
}
