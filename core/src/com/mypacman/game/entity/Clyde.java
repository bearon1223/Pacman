package com.mypacman.game.entity;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mypacman.game.Pacman;
import com.mypacman.game.tile.TileGrid;

public class Clyde extends Entity {
	int startingPelletCount;

	public Clyde(Pacman app, TileGrid grid, Array<AtlasRegion> t, Array<AtlasRegion> scared, Array<AtlasRegion> eyes,
			int x, int y, float w, float h, float[] offset) {
		super(app, grid, t, scared, eyes, x, y, w, h, offset, 0);
		this.startingPelletCount = grid.pelletCount();
	}

	public void update(Player pacman) {
		if (pacman.pelletsCollected() < startingPelletCount / 3)
			return;
		if (!isScared && !scatter)
			if (getLocation().sub(pacman.getLocation()).len() > 8*grid.getSize())
				super.update(pacman.getLocation(), 1);
			else
				super.update(new Vector2(0, 0), 1);
		else if (!scatter)
			super.update(new Vector2(0, 0), 1);
		else if (scatter)
			super.update(new Vector2(0, 0), 1);
	}
}
