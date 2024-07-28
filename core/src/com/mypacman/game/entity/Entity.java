package com.mypacman.game.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mypacman.game.Pacman;
import com.mypacman.game.tile.PathFinder;
import com.mypacman.game.tile.Tile;
import com.mypacman.game.tile.TileGrid;

public abstract class Entity {
	protected TileGrid grid;
	protected Vector2 location;
	private Array<Tile> storedPath = new Array<>();
	protected final Pacman app;
	private final Animation<TextureRegion> defaultAnimation;
	private final Animation<TextureRegion> scaredAnimation;
	private final AtlasRegion eyesUp;
	private final AtlasRegion eyesDown;
	private final AtlasRegion eyesLeft;
	private final AtlasRegion eyesRight;
	protected float w, h;
	protected boolean isTraveling = false, debugOutputted = false;
	protected float speed = 1f;
	private float stateTime = 0f;
	protected int x, y;
	protected boolean UP, DOWN, LEFT, RIGHT, isAlive = true;
	protected boolean inBox = true;
	private boolean called = false;
	protected boolean isScared = false;
	private float timeout = 0f;
	private final float timeoutLength = 25f;
	protected boolean scatter = false;

	public Entity(Pacman app, TileGrid grid, Array<AtlasRegion> t, Array<AtlasRegion> scared, Array<AtlasRegion> eyes,
			int x, int y, float w, float h, float[] offset, float timeout) {
		this.grid = grid;
		location = new Vector2(x * grid.getSize() + offset[0], y * grid.getSize() + offset[1]);
		this.w = w;
		this.h = h;
		this.x = x;
		this.y = y;
		this.app = app;
		this.defaultAnimation = new Animation<TextureRegion>(0.033f, t);
		this.scaredAnimation = new Animation<TextureRegion>(0.033f, scared);
		this.timeout = timeoutLength - timeout;

		this.eyesUp = eyes.get(0);
		this.eyesDown = eyes.get(1);
		this.eyesLeft = eyes.get(2);
		this.eyesRight = eyes.get(3);

		stateTime = 0f;
	}

	public void reset() {
		float[] offset = grid.getOffset();
		location = new Vector2(x * grid.getSize() + offset[0], y * grid.getSize() + offset[1]);
		inBox = true;
		isAlive = true;
		UP = false;
		DOWN = false;
		RIGHT = false;
		LEFT = false;
	}

	public void setScatter(boolean scatter) {
		this.scatter = scatter;
	}

	public void postSetupUpdate(int x, int y, float w, float h) {
		float[] offset = grid.getOffset();
		location.set(x * grid.getSize() + offset[0], y * grid.getSize() + offset[1]);
		app.log("Moving Entity to %d, %d", x, y);
		this.w = w;
		this.h = h;
	}

	/**
	 * move to a tile with index loc[] where loc[0] = row, loc[1] = col and update
	 * width and height
	 * 
	 * @param loc location
	 * @param w   width
	 * @param h   height
	 */
	public void moveLocation(int[] loc, float w, float h) {
		float[] offset = grid.getOffset();
		location.set(loc[1] * grid.getSize() + offset[0], loc[0] * grid.getSize() + offset[1]);
		app.log("Moving Entity to %d, %d", loc[1], loc[0]);
		this.w = w;
		this.h = h;
	}

	public void setScared(boolean scared) {
		isScared = scared;
	}

	/**
	 * updates the "AI" of the ghosts to find a path to location
	 * 
	 * @param location the tile we should look for
	 * @param junction 1 = to the right, 0 = to the left (all other values will
	 *                 break it)
	 */
	public void update(Vector2 location, int junction) {
		if (timeout < timeoutLength) {
			timeout += 0.1f;
			return;
		}

		Tile currentLocation = locationToTile();

		if (currentLocation.getLocation()[0] == -1 && currentLocation.getLocation()[1] == -1
				&& !currentLocation.isObstical()) {
			if (LEFT)
				location.set(indexToVector(grid.getColCount() - 1, 15));

			if (RIGHT)
				location.set(indexToVector(0, 15));
		}
		int row = locationToTile().getLocation()[0];
		int col = locationToTile().getLocation()[1];

		if (inBox && isAlive) {
			isScared = false;
			travel(grid.getTile(18, 11 + junction * 3));
			if (locationToTile() == grid.getTile(18, 11 + junction * 3)) {
				inBox = false;
			}
		}
		if (isAlive && !inBox) {
			Tile chosenTile = locationToTile();

			// if on a junction choose the direction to go that gets closer to the target
			if (locationToTile().isJunction() && !called) {
				called = true;
				Array<Tile> neighbors = grid.getNeighbors(locationToTile());

				// remove tiles that we came from (doesn't work apparently)
				if (LEFT) {
					neighbors.removeValue(grid.getTile(row, col + 1), true);
				}
				if (RIGHT) {
					neighbors.removeValue(grid.getTile(row, col - 1), true);
				}
				if (UP) {
					neighbors.removeValue(grid.getTile(row - 1, col), true);
				}
				if (DOWN) {
					neighbors.removeValue(grid.getTile(row + 1, col), true);
				}

				// remove invalid tiles
				for (Tile t : neighbors) {
					if (t.isObstical() || t.invalid()) {
						neighbors.removeValue(t, true);
					}
				}

				// find the tile closest to the target (pacman)
				chosenTile = locationToTile();
				float dist = Float.MAX_VALUE;
				for (Tile t : neighbors) {
					t.setColor(Color.GREEN);
					if (t.getLocationAsVector(grid.getSize(), grid.getOffset()[0], grid.getOffset()[1]).cpy()
							.sub(location).len() < dist) {
						dist = t.getLocationAsVector(grid.getSize(), grid.getOffset()[0], grid.getOffset()[1]).cpy()
								.sub(location).len();

						chosenTile = t;
					}
				}

				// set the direction
				chosenTile.setColor(Color.CYAN);
				if (grid.getTile(row + 1, col) == chosenTile) {
					UP = true;
					DOWN = false;
					RIGHT = false;
					LEFT = false;
				}
				if (grid.getTile(row - 1, col) == chosenTile) {
					DOWN = true;
					UP = false;
					RIGHT = false;
					LEFT = false;
				}
				if (grid.getTile(row, col + 1) == chosenTile) {
					RIGHT = true;
					UP = false;
					DOWN = false;
					LEFT = false;
				}
				if (grid.getTile(row, col - 1) == chosenTile) {
					LEFT = true;
					UP = false;
					DOWN = false;
					RIGHT = false;
				}
			} else if (!locationToTile().isJunction()) {
				// ensure this only gets called once
				called = false;
			}

			// travel
			if (UP) {
				travel(grid.getTile(row + 1, col), false);
			} else if (DOWN) {
				travel(grid.getTile(row - 1, col), false);
			} else if (LEFT) {
				travel(grid.getTile(row, col - 1), false);
			} else if (RIGHT) {
				travel(grid.getTile(row, col + 1), false);
			}
		}
		if (!isAlive) {
			speed = 5f;
			travel(grid.getTile(y, x));
			if (locationToTile() == grid.getTile(y, x)) {
				reset();
				timeout = 0f;
			}
			inBox = true;
		} else if (isScared) {
			speed = 1.05f;
		} else {
			speed = 1.35f;
		}
	}

	public void checkPacman(Player pacman) {
		// kill pacman
		if (isOnPacman(pacman) && !isScared && isAlive) {
			pacman.die();
		} else if (isOnPacman(pacman)) {
			killEntity();
			pacman.addToKillCount();
		}
	}

	/**
	 * move to a tile with index loc[] where loc[0] = row, loc[1] = col
	 * 
	 * @param loc
	 */
	public void moveLocation(int[] loc) {
		float[] offset = grid.getOffset();
		location.set(loc[1] * grid.getSize() + offset[0], loc[0] * grid.getSize() + offset[1]);
		app.log("Moving Entity to %d, %d", loc[1], loc[0]);
	}

	/**
	 * move to index x and y on the tile grid
	 * 
	 * @param x
	 * @param y
	 */
	public void moveLocation(int x, int y) {
		float[] offset = grid.getOffset();
		location.set(x * grid.getSize() + offset[0], y * grid.getSize() + offset[1]);
	}

	/**
	 * Take a destination and use the PathFinder class to find a path using
	 * breadthFistSearch
	 * 
	 * @param destination
	 * @return A Tile array that represents the path which needs to be taken.
	 */
	public Array<Tile> findPath(Tile destination) {
		if (destination == null) {
			debugOutputted = false;
			return new Array<Tile>();
		}
		int[] destLocation = destination.getLocation();
		int[] currLocation = locationToTile().getLocation();

		if (!isTraveling && ((destLocation[0] != currLocation[0]) || (destLocation[1] != currLocation[1]))) {
//			app.log("Creating new Path");
			PathFinder pathfinder = new PathFinder(this.grid, locationToTile(), destination);
			pathfinder.breadthFirstSearch(app);
			storedPath = pathfinder.getPath();
		}
		return storedPath;

	}

	public void killEntity() {
		isScared = false;
		isAlive = false;
	}

	/**
	 * traverses the path
	 * 
	 * @param path
	 * @param updateDirs update the facing direction?
	 */
	public void travelPath(Array<Tile> path, boolean updateDirs) {
		if (path.notEmpty()) {
			debugOutputted = false;
			// if the first element in the path is the current tile, remove it (prevents the
			// entity from backtracking all the time)
			if (path.first() == locationToTile())
				path.removeValue(path.first(), false);

			if (path.notEmpty()) {
				if (!move(indexToVector(path.first().getLocation()[1], path.first().getLocation()[0]), speed,
						updateDirs)) {
					if (path.first() == path.peek()) {
						// if the entity has reached the destination point, teleport it to the
						// destination to prevent error buildup
						moveLocation(path.first().getLocation(), w, h);
					}
					path.removeValue(path.first(), false);
				}
			}
		}
		if (!debugOutputted && path.isEmpty()) {
			app.log("Path Traversed");
			debugOutputted = true;
		}
	}

	/**
	 * @param pacman
	 * @return is the entity on pacman
	 */
	public boolean isOnPacman(Player pacman) {
		return locationToTile() == pacman.locationToTile();
	}

	/**
	 * finds a path and traverses it using travelPath(path);
	 * 
	 * @param dest Destination Tile
	 */
	public void travel(Tile dest) {
		if (dest == null)
			return;
		if (locationToTile() != dest) {
			findPath(dest);
		}
		isTraveling = storedPath.notEmpty();
		if (isTraveling) {
			if (storedPath.peek() != dest) {
				isTraveling = false;
				findPath(dest);
			}
		}
		if (locationToTile() != dest || storedPath.notEmpty())
			travelPath(storedPath, true);
	}

	public void travel(Tile dest, boolean updateDirs) {
		if (dest == null)
			return;
		if (locationToTile() != dest) {
			findPath(dest);
		}
		isTraveling = storedPath.notEmpty();
		if (isTraveling) {
			if (storedPath.peek() != dest) {
				isTraveling = false;
				findPath(dest);
			}
		}
		if (locationToTile() != dest || storedPath.notEmpty())
			travelPath(storedPath, updateDirs);
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

	public void render(SpriteBatch batch, Player pacman, float timeLeft) {
		TextureRegion regularFrame = defaultAnimation.getKeyFrame(stateTime, true);
		TextureRegion scaredFrame = scaredAnimation.getKeyFrame(stateTime, true);
		stateTime += 0.002f;

		if (isAlive) {
			if (!inBox && isScared && timeLeft > 10000)
				batch.draw(scaredFrame, location.x, location.y, w, h);
			else if (!inBox && isScared) {
				// loop the animation just before the white flashing bits
				stateTime = Math.clamp(stateTime % (2 * 0.033f), 0, 2 * 0.033f);
				batch.draw(scaredFrame, location.x, location.y, w, h);
			} else {
				batch.draw(regularFrame, location.x, location.y, w, h);
			}
		}

		// Depending on direction of movement, draw the appropriate eyes
		if (!isScared || inBox) {
			if (UP) {
				batch.draw(eyesUp, location.x, location.y, w, h);
			} else if (DOWN) {
				batch.draw(eyesDown, location.x, location.y, w, h);
			} else if (LEFT) {
				batch.draw(eyesLeft, location.x, location.y, w, h);
			} else if (RIGHT) {
				batch.draw(eyesRight, location.x, location.y, w, h);
			} else {
				batch.draw(eyesLeft, location.x, location.y, w, h);
			}
		}
	}

	public boolean isTraveling() {
		return isTraveling;
	}

	/**
	 * moves the entity
	 * 
	 * @param destination
	 * @param speed
	 * @return is the entity still moving (optional)
	 */
	public boolean move(Vector2 destination, float speed, boolean updateDirs) {
		if (destination.cpy().sub(location).len() > 0.4f) {
			Vector2 dS = destination.cpy().sub(location).nor().scl(speed);
			location.add(dS);

			if (updateDirs) {
				UP = dS.y > 0.3f;
				DOWN = dS.y < -0.3f;
				LEFT = dS.x < -0.3f;
				RIGHT = dS.x > 0.3f;
			}
		} else
			location.set(destination.cpy());

		return destination.cpy().sub(location).len() > 0.1f;
	}

	public Vector2 getLocation() {
		return location.cpy();
	}

	public void dispose() {
	}
}
