package com.mypacman.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mypacman.game.Pacman;
import com.mypacman.game.tile.TileGrid;

public class Pinky extends Entity {

	public Pinky(Pacman app, TileGrid grid, Array<AtlasRegion> t, Array<AtlasRegion> scared, Array<AtlasRegion> eyes,
			int x, int y, float w, float h, float[] offset) {
		super(app, grid, t, scared, eyes, x, y, w, h, offset, 0);
		super.speed = 1.2f;
	}

	public void update(Player pacman) {
		if (!isScared && !scatter)
			super.update(pacman.getTileAhead(5).getLocationAsVector(grid.getSize(), grid.getOffset()[0],
					grid.getOffset()[1]), 1);
		else if (!scatter)
			super.update(new Vector2(0, Gdx.graphics.getHeight()), 0);
		else if (scatter)
			super.update(new Vector2(0, Gdx.graphics.getHeight()), 0);
	}

}
