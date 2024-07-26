package com.mypacman.game.tile;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Array;
import com.mypacman.game.Pacman;

public class PathFinder {
	private Tile startingTile, destinationTile;
	private TileGrid grid;
	private Array<Tile> frontier = new Array<>();
	private Array<Tile> reached = new Array<>();
	private Map<Tile, Tile> cameFrom = new HashMap<Tile, Tile>();
	private Array<Tile> path = new Array<>();

	public PathFinder(TileGrid grid, Tile startingTile, Tile destinationTile) {
		this.grid = grid;
		this.startingTile = startingTile;
		this.destinationTile = destinationTile;
		frontier.add(startingTile);
		reached.add(startingTile);
	}

	public void breadthFirstSearch(Pacman app) {
		// clear the buffers
		frontier.clear();
		cameFrom.clear();
		path.clear();
		reached.clear();
		
		// add the starting tile
		frontier.add(startingTile);
		reached.add(startingTile);

		// while the frontiers exist, continue the while loop
		while (frontier.notEmpty() && path.isEmpty()) {
			Tile current = frontier.first();
			frontier.removeValue(current, true);

			// get neighbors and search add it to the frontier
			Array<Tile> neighbors = grid.getNeighbors(current);
			for (Tile next : neighbors) {
				if (!reached.contains(next, false)) {
					reached.add(next);
					frontier.add(next);
					cameFrom.put(next, current);
				}
			}

			// if the current tile is the destination,
			if (current == destinationTile) {
				path.add(destinationTile);
				while (current != startingTile) {
					path.add(cameFrom.get(current));
					current = cameFrom.get(current);
				}
				path.reverse();
				break;
			}
		}
	}

	public Array<Tile> getPath() {
		return path;
	}

	public float heuristic(Tile a, Tile b) {
		float abx = a.getLocation()[0] - b.getLocation()[0];
		float aby = a.getLocation()[1] - b.getLocation()[1];
		return abx * abx + aby * aby;
	}

	public boolean isFavorable(float a, float b, float c, float d) {
		return a <= b && a <= c && a <= d;
	}
}
