package com.mypacman.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;

public class Pacman extends Game {
	SpriteBatch batch;
	ShapeRenderer renderer;
	Texture img;
	DebugWindow debugWindow;
	BitmapFont debugFont;
	BitmapFont smallUIFont;
	BitmapFont bigUIFont;
	public Array<String> log = new Array<>();

	@Override
	public void create() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("calibri.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		generator = new FreeTypeFontGenerator(Gdx.files.internal("calibri.ttf"));
		parameter.size = 14;
		debugFont = generator.generateFont(parameter);
		
		generator = new FreeTypeFontGenerator(Gdx.files.internal("Emulogic-zrEw.ttf"));
		parameter = new FreeTypeFontParameter();
		parameter.size = 16;
		smallUIFont = generator.generateFont(parameter);
		
		parameter = new FreeTypeFontParameter();
		parameter.size = 48;
		bigUIFont = generator.generateFont(parameter);
		
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		renderer = new ShapeRenderer();
		debugWindow = new DebugWindow(this);
		debugWindow.close();
		setScreen(new MainScreen(this));
	}

	@Override
	public void setScreen(Screen screen) {
		log("Setting Screen to %s", screen.getClass().getSimpleName());
		super.setScreen(screen);
	}

	@Override
	public void render() {
		super.render();

		if (debugWindow.isOpen()) {
			renderer.begin(ShapeType.Filled);
			debugWindow.shapeRenderer(renderer);
			renderer.end();

			batch.begin();
			debugWindow.textRenderer(batch, debugFont);
			batch.end();
		}
		if (Gdx.input.isKeyJustPressed(Keys.D)) {
			debugWindow.reOpen();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.C)) {
			this.log.clear();
			log("Log Cleared");
		}
	}

	public void log(String s, Object... args) {
		log.add(String.format(s, args));
		System.out.printf(s, args);
		System.out.println();
	}

	public String getLog() {
		String log = "";
		for(int i = this.log.size - 16; i < this.log.size; i++) {
			if(i < 0 | i >= this.log.size)
				continue;
			log += "\n" + this.log.get(i);
		}
		return log;
	}
	
	public int getLogSize() {
		return this.log.size;
	}
	
	public String getLog(int offset) {
		String log = "";
		for(int i = this.log.size - 16 - offset; i < this.log.size - offset; i++) {
			if(i < 0 | i >= this.log.size)
				continue;
			log += this.log.get(i) + "\n";
		}
		return log;
	}

	@Override
	public void dispose() {
		batch.dispose();
		renderer.dispose();
		img.dispose();
		smallUIFont.dispose();
		bigUIFont.dispose();
		debugFont.dispose();
	}
}
