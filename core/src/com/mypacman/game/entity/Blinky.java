package com.mypacman.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mypacman.game.Pacman;
import com.mypacman.game.tile.TileGrid;

public class Blinky extends Entity {

	public Blinky(Pacman app, TileGrid grid, Array<AtlasRegion> t, Array<AtlasRegion> scared, Array<AtlasRegion> eyes,
			int x, int y, float w, float h, float[] offset) {
		super(app, grid, t, scared, eyes, x, y, w, h, offset, 0);
		super.speed = 1.2f;
	}

	public void update(Player pacman) {
		if (!isScared && !scatter)
			super.update(pacman.getLocation(), 0);
		else if (!scatter)
			super.update(new Vector2(0, 0), 0);
		else if (scatter)
			super.update(new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()),0);
	}

}
