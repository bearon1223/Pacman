package com.mypacman.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mypacman.game.entity.Blinky;
import com.mypacman.game.entity.Clyde;
import com.mypacman.game.entity.Inky;
import com.mypacman.game.entity.Pinky;
import com.mypacman.game.entity.Player;
import com.mypacman.game.tile.Tile;
import com.mypacman.game.tile.TileGrid;

public class MainScreen implements Screen {
	private final Pacman app;
	private OrthographicCamera camera;
	private Tile destinationTile;
	private boolean ready = false, gameOver = false, won = false;

	private TileGrid grid;
	private boolean showDebugTiles = false;
	public boolean settingDest = false;
	public Blinky blinky;
	public Pinky pinky;
	public Inky inky;
	public Clyde clyde;
	private Texture map, lives;
	private boolean entitySetup = false, invencible = false, eatingGhostMode = false;
	private Player pacman;
	private final Array<AtlasRegion> eyes;
	private long time = 0;
	private long time2 = 0;
	private long time3 = 0;
	private boolean debugSwitch = false;
	private int remainingLives = 3;
	private int livesSave = 3;
	private int scoreOffset = 0;

	public MainScreen(Pacman app) {
		this.app = app;
		camera = new OrthographicCamera();
		camera.setToOrtho(false);

		grid = new TileGrid(app, 29, 26);
		app.debugWindow.setMainScreen(this);

		float[] offset = { 30, 130 };
		TextureAtlas t = new TextureAtlas(Gdx.files.internal("pacman.atlas"));

		eyes = new Array<>();
		eyes.add(t.findRegion("u"));
		eyes.add(t.findRegion("d"));
		eyes.add(t.findRegion("l"));
		eyes.add(t.findRegion("r"));

		blinky = new Blinky(app, grid, t.findRegions("blink"), t.findRegions("f"), eyes, 0, 0, 0, 0, offset);
		pinky = new Pinky(app, grid, t.findRegions("inky"), t.findRegions("f"), eyes, 0, 0, 0, 0, offset);
		inky = new Inky(app, grid, t.findRegions("inky"), t.findRegions("f"), eyes, 0, 0, 0, 0, offset);
		clyde = new Clyde(app, grid, t.findRegions("clyde"), t.findRegions("f"), eyes, 0, 0, 0, 0, offset);
		pacman = new Player(app, grid, t, 13, 6, grid.getSize(), grid.getSize(), offset);

		map = new Texture(Gdx.files.internal("map/map.png"));
		lives = new Texture(Gdx.files.internal("pacman/move_1.png"));
	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		// update ghosts and player
		if (!won && !gameOver) {
			pacman.update(this);
		}
		if (ready && !won) {
			if (!invencible) {
				inky.checkPacman(pacman);
				clyde.checkPacman(pacman);
				pinky.checkPacman(pacman);
				blinky.checkPacman(pacman);
			}

			blinky.update(pacman);
			pinky.update(pacman);
			inky.update(pacman, blinky);
			clyde.update(pacman);
		} else {
			time = System.currentTimeMillis();
		}

		if (System.currentTimeMillis() - time > 20000 && System.currentTimeMillis() - time < 27000) {
			blinky.setScatter(true);
			inky.setScatter(true);
			pinky.setScatter(true);
			clyde.setScatter(true);
			if (!debugSwitch) {
				app.log("Scattering");
				debugSwitch = true;
			}
		} else {
			blinky.setScatter(false);
			inky.setScatter(false);
			pinky.setScatter(false);
			clyde.setScatter(false);
			if (System.currentTimeMillis() - time > 27000) {
				time = System.currentTimeMillis();
				app.log("No longer Scattering");
				debugSwitch = false;
			}
		}

		if (eatingGhostMode && System.currentTimeMillis() - time2 > 15000) {
			eatingGhostMode = false;
			pinky.setScared(eatingGhostMode);
			inky.setScared(eatingGhostMode);
			blinky.setScared(eatingGhostMode);
			clyde.setScared(eatingGhostMode);
		}

		// draw debug tiles
		ScreenUtils.clear(Color.BLACK);
		camera.update();

		app.renderer.setProjectionMatrix(camera.combined);
		app.renderer.begin(ShapeType.Filled);
		if (showDebugTiles)
			grid.render(app.renderer, 30, 130);
		app.renderer.end();

		// draw sprites
		app.batch.setProjectionMatrix(camera.combined);
		app.batch.begin();

		app.batch.draw(map, 30 - grid.getSize(), grid.getOffset()[1] - grid.getSize(), grid.getSize() * 28,
				grid.getSize() * 31);
		grid.render(app.batch, 30, 130);

		pacman.render(app.batch);
		if (pacman.isAlive()) {
			blinky.render(app.batch, pacman, System.currentTimeMillis() - time2);
			pinky.render(app.batch, pacman, System.currentTimeMillis() - time2);
			inky.render(app.batch, pacman, System.currentTimeMillis() - time2);
			clyde.render(app.batch, pacman, System.currentTimeMillis() - time2);
		}

		if (!ready && !gameOver) {
			app.bigUIFont.setColor(Color.YELLOW);
			app.bigUIFont.draw(app.batch, "Ready!", Gdx.graphics.getWidth() / 2 - 25f, Gdx.graphics.getHeight() / 2, 50,
					Align.center, false);
		}
		
		String scoreString = String.format("Score: %d", pacman.getScore());
		app.smallUIFont.draw(app.batch, scoreString, 10, Gdx.graphics.getHeight() - 50, 100f, Align.left, false);
		app.smallUIFont.draw(app.batch, "Lives: ", 10, 50);

		if (gameOver) {
			app.bigUIFont.setColor(new Color(0xed4747ff));
			app.bigUIFont.draw(app.batch, "GAME OVER!", Gdx.graphics.getWidth() / 2 - 25f, Gdx.graphics.getHeight() / 2,
					50, Align.center, false);
		}

		gameOver = remainingLives < 0;

		if (gameOver) {
			ready = false;
		}

		for (int i = 0; i < remainingLives; i++) {
			app.batch.draw(lives, 120 + grid.getSize() * i, 33, grid.getSize(), grid.getSize());
		}
		app.batch.end();

		// get blinky location if Q is pressed
		if (Gdx.input.isKeyJustPressed(Keys.Q)) {
			app.log("blinky tile loc = (%d, %d)", blinky.locationToTile().getLocation()[0],
					blinky.locationToTile().getLocation()[1]);
		}

		// setup entities again because they do be buggy if I don't
		if (!entitySetup) {
			float[] offset = grid.getOffset();
			TextureAtlas t = new TextureAtlas(Gdx.files.internal("pacman.atlas"));
			app.log("8: %d", remainingLives);

			pacman = new Player(app, grid, t, 13, 6, grid.getSize(), grid.getSize(), offset);
			pinky = new Pinky(app, grid, t.findRegions("pinky"), t.findRegions("f"), eyes, 14, 15, grid.getSize(),
					grid.getSize(), offset);
			blinky = new Blinky(app, grid, t.findRegions("blink"), t.findRegions("f"), eyes, 13, 15, grid.getSize(),
					grid.getSize(), offset);
			inky = new Inky(app, grid, t.findRegions("inky"), t.findRegions("f"), eyes, 12, 15, grid.getSize(),
					grid.getSize(), offset);
			clyde = new Clyde(app, grid, t.findRegions("clyde"), t.findRegions("f"), eyes, 11, 15, grid.getSize(),
					grid.getSize(), offset);
			pacman.setScore(scoreOffset);
			entitySetup = true;
			remainingLives = livesSave;
			app.log("9: %d", remainingLives);
		}

		if (grid.pelletCount() <= 0 && !won) {
			livesSave = remainingLives - 1;
			won = true;
			time3 = System.currentTimeMillis();
			pacman.die();
		}

		if (grid.pelletCount() <= 0 && System.currentTimeMillis() - time3 > 3000 && won) {
			won = false;
			scoreOffset = pacman.getScore();
			resetReady();
			grid = new TileGrid(app, 29, 26);
			entitySetup = false;
		}

		// check to see if the game has started
		if ((Gdx.input.isKeyJustPressed(Keys.LEFT) || Gdx.input.isKeyJustPressed(Keys.RIGHT)) && !gameOver) {
			ready = true;
		}
	}

	public void resetReady() {
		ready = false;
		remainingLives--;
	}

	public TileGrid getTileGrid() {
		return grid;
	}

	public void setEatGhosts(boolean setting) {
		eatingGhostMode = setting;
		pinky.setScared(eatingGhostMode);
		inky.setScared(eatingGhostMode);
		blinky.setScared(eatingGhostMode);
		clyde.setScared(eatingGhostMode);
		time2 = System.currentTimeMillis();
	}

	public void toggleEatGhosts() {
		eatingGhostMode = !eatingGhostMode;
		pinky.setScared(eatingGhostMode);
		inky.setScared(eatingGhostMode);
		blinky.setScared(eatingGhostMode);
		clyde.setScared(eatingGhostMode);
	}

	public Player getPacman() {
		return pacman;
	}

	public void setSearchDestinationTiles(Tile search, Tile destination) {
		this.destinationTile = destination;
		this.destinationTile.setColor(Color.RED);
	}

	public void toggleShowDebugTiles() {
		showDebugTiles = !showDebugTiles;
		app.log("Showing Debug Tiles: %b", showDebugTiles);
	}

	public void toggleInvincibility() {
		invencible = !invencible;
		app.log("Pacman Invicible: %b", invencible);
	}

	public void setTraveling(boolean traveling) {
		this.invencible = traveling;
	}

	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		grid.dispose();
		pinky.dispose();
		blinky.dispose();
		lives.dispose();
		map.dispose();
		pacman.dispose();
	}

}
