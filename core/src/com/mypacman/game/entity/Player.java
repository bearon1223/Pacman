package com.mypacman.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mypacman.game.MainScreen;
import com.mypacman.game.Pacman;
import com.mypacman.game.tile.Tile;
import com.mypacman.game.tile.TileGrid;

public class Player {
	private final Pacman app;
	private TileGrid grid;
	private Vector2 location;
	private Animation<TextureRegion> moveAnimation;
	private Animation<TextureRegion> deathAnimation;
	private TextureAtlas pacmanAtlas;
	private float stateTime, stateTime2;
	private float w, h;
	private boolean UP, DOWN, LEFT, RIGHT, QU, QD, QL, QR, dead = false;
	private int sx, sy;
	private int pelletsCollected = 0;
	private int ghostKillCount = 0;
	private int score = 0;

	public Player(Pacman app, TileGrid grid, TextureAtlas pacmanAtlas, int x, int y, float w, float h, float[] offset) {
		this.app = app;
		this.grid = grid;
		this.location = indexToVector(x, y);
		this.w = w;
		this.h = h;

		this.sx = x;
		this.sy = y;

		this.pacmanAtlas = pacmanAtlas;
		moveAnimation = new Animation<TextureRegion>(0.033f, pacmanAtlas.findRegions("move"));
		deathAnimation = new Animation<TextureRegion>(0.033f, pacmanAtlas.findRegions("death"));
		stateTime = 0;
	}

	public void update(MainScreen screen) {
		// heh ded
		if (deathAnimation.isAnimationFinished(stateTime2) && dead)
			reset(screen);
		
		if(dead)
			return;
		Tile currentLocation = locationToTile();

		// warp to the other side
		if (currentLocation.getLocation()[0] == -1 && currentLocation.getLocation()[1] == -1
				&& !currentLocation.isObstical()) {
			if (LEFT)
				location.set(indexToVector(grid.getColCount() - 1, 15));

			if (RIGHT)
				location.set(indexToVector(0, 15));

			currentLocation = locationToTile();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.UP)) {
			QU = true;
			QL = false;
			QR = false;
			QD = false;
		} else if (Gdx.input.isKeyJustPressed(Keys.DOWN)) {
			QU = false;
			QL = false;
			QR = false;
			QD = true;
		} else if (Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
			QU = false;
			QL = false;
			QR = true;
			QD = false;
		} else if (Gdx.input.isKeyJustPressed(Keys.LEFT)) {
			QU = false;
			QL = true;
			QR = false;
			QD = false;
		}

		// set the facing direction (only valid if there is not an obstical in that
		// direction
		if ((Gdx.input.isKeyJustPressed(Keys.UP) | QU)
				&& !grid.getTile(currentLocation.getLocation()[0] + 1, currentLocation.getLocation()[1]).isObstical()) {
			UP = true;
			LEFT = false;
			RIGHT = false;
			DOWN = false;
		} else if ((Gdx.input.isKeyJustPressed(Keys.DOWN) | QD)
				&& !grid.getTile(currentLocation.getLocation()[0] - 1, currentLocation.getLocation()[1]).isObstical()) {
			UP = false;
			LEFT = false;
			RIGHT = false;
			DOWN = true;
		} else if ((Gdx.input.isKeyJustPressed(Keys.RIGHT) | QR)
				&& !grid.getTile(currentLocation.getLocation()[0], currentLocation.getLocation()[1] + 1).isObstical()) {
			UP = false;
			LEFT = false;
			RIGHT = true;
			DOWN = false;
		} else if ((Gdx.input.isKeyJustPressed(Keys.LEFT) | QL)
				&& !grid.getTile(currentLocation.getLocation()[0], currentLocation.getLocation()[1] - 1).isObstical()) {
			UP = false;
			LEFT = true;
			RIGHT = false;
			DOWN = false;
		}

		// move if the direction is set
		if (UP && !grid.getTile(currentLocation.getLocation()[0] + 1, currentLocation.getLocation()[1]).isObstical()) {
			move(indexToVector(currentLocation.getLocation()[1], currentLocation.getLocation()[0] + 1));
			stateTime += 0.5f; // Accumulate elapsed animation time
		} else if (LEFT
				&& !grid.getTile(currentLocation.getLocation()[0], currentLocation.getLocation()[1] - 1).isObstical()) {
			move(indexToVector(currentLocation.getLocation()[1] - 1, currentLocation.getLocation()[0]));
			stateTime += 0.5f; // Accumulate elapsed animation time
		} else if (DOWN
				&& !grid.getTile(currentLocation.getLocation()[0] - 1, currentLocation.getLocation()[1]).isObstical()) {
			move(indexToVector(currentLocation.getLocation()[1], currentLocation.getLocation()[0] - 1));
			stateTime += 0.5f; // Accumulate elapsed animation time
		} else if (RIGHT
				&& !grid.getTile(currentLocation.getLocation()[0], currentLocation.getLocation()[1] + 1).isObstical()) {
			move(indexToVector(currentLocation.getLocation()[1] + 1, currentLocation.getLocation()[0]));
			stateTime += 0.5f; // Accumulate elapsed animation time
		} else {
			move(indexToVector(currentLocation.getLocation()[1], currentLocation.getLocation()[0]));
		}

		// log the tile if Q is pressed
		if (Gdx.input.isKeyJustPressed(Keys.Q)) {
			if (UP) {
				Tile a = grid.getTile(currentLocation.getLocation()[0] + 1, currentLocation.getLocation()[1]);
				app.log("facing tile: (row, col) = (%d, %d), isObstical = %b", a.getLocation()[0], a.getLocation()[1],
						a.isObstical());
			} else if (DOWN) {
				Tile a = grid.getTile(currentLocation.getLocation()[0] - 1, currentLocation.getLocation()[1]);
				app.log("facing tile: (row, col) = (%d, %d), isObstical = %b", a.getLocation()[0], a.getLocation()[1],
						a.isObstical());
			} else if (LEFT) {
				Tile a = grid.getTile(currentLocation.getLocation()[0], currentLocation.getLocation()[1] - 1);
				app.log("facing tile: (row, col) = (%d, %d), isObstical = %b", a.getLocation()[0], a.getLocation()[1],
						a.isObstical());
			} else if (RIGHT) {
				Tile a = grid.getTile(currentLocation.getLocation()[0], currentLocation.getLocation()[1] + 1);
				app.log("facing tile: (row, col) = (%d, %d), isObstical = %b", a.getLocation()[0], a.getLocation()[1],
						a.isObstical());
			}
		}

		// score
		if (currentLocation.hasPellet()) {
			if (currentLocation.hasSuperPellet())
				screen.setEatGhosts(true);
			currentLocation.removePellet();
			pelletsCollected++;
		}
	}
	
	public boolean isAlive() {
		return !dead;
	}

	private Vector2 indexToVector(int x, int y) {
		float[] offset = grid.getOffset();
		return new Vector2(x * grid.getSize() + offset[0], y * grid.getSize() + offset[1]);
	}

	public Tile locationToTile() {
		float[] offset = grid.getOffset();
		return grid.getTile((int) Math.round((location.y - offset[1]) / grid.getSize()),
				(int) Math.round((location.x - offset[0]) / grid.getSize()));
	}

	public Tile locationToTile(Vector2 location) {
		float[] offset = grid.getOffset();
		return grid.getTile((int) Math.round((location.y - offset[1]) / grid.getSize()),
				(int) Math.round((location.x - offset[0]) / grid.getSize()));
	}

	public int pelletsCollected() {
		return pelletsCollected;
	}
	
	public void addToKillCount() {
		ghostKillCount++;
	}

	public Vector2 getLocation() {
		return location.cpy();
	}
	
	public int getScore() {
		return pelletsCollected*10 + ghostKillCount*200 + score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}

	public Tile getTileAhead(int distance) {
		Tile c = locationToTile();

		int row = c.getLocation()[0];
		int col = c.getLocation()[1];

		// adjust the row/col depending on where the player is facing and pull back
		// until a non wall has been found
		if (UP) {
			row += distance;
		} else if (DOWN) {
			row -= distance;
		} else if (LEFT) {
			col -= distance;
		} else if (RIGHT) {
			col += distance;
		}

		// return the gotten value
		if (row >= 0 && row < this.grid.getRowCount() && col >= 0 && col < this.grid.getColCount()) {
			c = this.grid.getTileGrid()[row * this.grid.getColCount() + col];
		}
		return c;
	}

	public void render(SpriteBatch batch) {
		if (!dead) {
			TextureRegion currentFrame = moveAnimation.getKeyFrame(stateTime, true);
			float angle = 0;
			if (UP)
				angle = -90f;
			else if (DOWN)
				angle = 90f;
			else if (LEFT)
				angle = 0;
			else if (RIGHT)
				angle = 180f;
			batch.draw(currentFrame, location.x, location.y, w / 2, h / 2, w, h, 1f, 1f, angle);
		} else {
			TextureRegion currentFrame = deathAnimation.getKeyFrame(stateTime2, false);
			stateTime2 += 0.005f; // Accumulate elapsed animation time
			batch.draw(currentFrame, location.x, location.y, w / 2, h / 2, w, h, 1f, 1f, 0f);
			UP = false;
			DOWN = false;
			LEFT = false;
			RIGHT = false;
		}
	}

	public void reset(MainScreen screen) {
		if (dead) {
			this.location = indexToVector(sx, sy);
			dead = false;
			QU = false;
			QD = false;
			QR = false;
			QL = false;
			stateTime2 = 0;
			screen.blinky.reset();
			screen.pinky.reset();
			screen.inky.reset();
			screen.clyde.reset();
			screen.resetReady();
		}
	}

	public void die() {
		if (!dead) {
			dead = true;
			QU = false;
			QD = false;
			QR = false;
			QL = false;
			stateTime2 = 0;
			deathAnimation = new Animation<TextureRegion>(0.033f, pacmanAtlas.findRegions("death"));
		}
	}

	/**
	 * moves the entity
	 * 
	 * @param destination
	 * @return is the entity still moving (optional)
	 */
	public boolean move(Vector2 destination) {
		if (!location.epsilonEquals(destination, 1))
			location.add(destination.cpy().sub(location).nor().scl(1.5f));
		else
			location.set(destination.cpy());

		return destination.cpy().sub(location).len() > 0.5f;
	}

	public void dispose() {
		pacmanAtlas.dispose();
	}
}
