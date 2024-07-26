package com.mypacman.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mypacman.game.UIHelper.Button;
import com.mypacman.game.UIHelper.Window;
import com.mypacman.game.tile.Tile;

public class DebugWindow extends Window {
	int logOffset = 0;
	long holdFrame = 0;

	Button moveEntityButton, resetButton;
	Button showDebugTilesButton, printPlayerLocButton;
	Button killGhostsModeButton;
	private MainScreen main;

	public DebugWindow(Pacman app) {
		super(app, 30, 400, 500, 330, "Debug Screen");
		moveEntityButton = new Button(this, 500 - 105, 5, 100, 50, "Invincibility");
		resetButton = new Button(this, 500 - 105, 60, 100, 50, "Reset");
		showDebugTilesButton = new Button(this, 500 - 105, 5 + 55 * 2, 100, 50, "Show Tiles");
		printPlayerLocButton = new Button(this, 500 - 105, 5 + 55 * 3, 100, 50, "Player Location");
		killGhostsModeButton = new Button(this, 500 - 105, 5 + 55 * 4, 100, 50, "Kill Ghosts");
	}

	public void setMainScreen(MainScreen m) {
		main = m;
	}

	@Override
	protected void render() {
		checkInputs();

		if (logOffset < 0)
			logOffset = 0;
		if (logOffset > app.getLogSize() - 16)
			logOffset = app.getLogSize() - 16;

		text(String.format("FPS: %d", Gdx.graphics.getFramesPerSecond()), 10, getHeight() - 10f, 100);
		text("Log:", 10, getHeight() - 30f, getWidth() - 112);
		text(app.getLog(logOffset), 12, getHeight() - 50f, 288);
		rect(new Color(0x2c2c2cff), 10, getHeight() - 48f - app.debugFont.getLineHeight() * 16, 288,
				app.debugFont.getLineHeight() * 16);

		if (moveEntityButton.clicked(mouseX, mouseY)) {
			main.toggleInvincibility();
		}

		if (showDebugTilesButton.clicked(mouseX, mouseY)) {
			main.toggleShowDebugTiles();
		}

		if (resetButton.clicked(mouseX, mouseY)) {
			app.setScreen(new MainScreen(app));
		}

		if (killGhostsModeButton.clicked(mouseX, mouseY)) {
			main.toggleEatGhosts();
		}

		if (printPlayerLocButton.clicked(mouseX, mouseY)) {
			Tile pacmanLoc = main.getPacman().locationToTile();
			app.log("Pacman loc: (row, col) = (%d, %d)", pacmanLoc.getLocation()[0], pacmanLoc.getLocation()[1]);
		}
	}

	private void checkInputs() {
		if (!shift) {
			if (Gdx.input.isKeyJustPressed(Keys.UP)) {
				logOffset++;
				holdFrame = Gdx.graphics.getFrameId();
			} else if (Gdx.input.isKeyJustPressed(Keys.DOWN)) {
				logOffset--;
				holdFrame = Gdx.graphics.getFrameId();
			}

			if (Gdx.graphics.getFrameId() - holdFrame > 30) {
				if (Gdx.input.isKeyPressed(Keys.UP)) {
					logOffset++;
				} else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
					logOffset--;
				}
			}
		} else {
			if (Gdx.input.isKeyJustPressed(Keys.UP)) {
				logOffset = app.getLogSize();
			} else if (Gdx.input.isKeyJustPressed(Keys.DOWN)) {
				logOffset = 0;
			}
		}
	}

	@Override
	protected void renderUIElements(ShapeRenderer r) {
		moveEntityButton.render(r);
		resetButton.render(r);
		showDebugTilesButton.render(r);
		printPlayerLocButton.render(r);
		killGhostsModeButton.render(r);
	}

	@Override
	protected void renderTextUIElements(SpriteBatch batch, BitmapFont font) {
		moveEntityButton.showText(batch, font);
		resetButton.showText(batch, font);
		showDebugTilesButton.showText(batch, font);
		printPlayerLocButton.showText(batch, font);
		killGhostsModeButton.showText(batch, font);
	}

}
