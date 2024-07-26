package com.mypacman.game.UIHelper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

/**
 * 
 */
public class Button {
	private float x, y, w, h;
	private float sx, sy; // Storing the original values for use in updating

	private String t;
	private Color color;
	private Color darkerColor;
	private Window window = null;
	private long holdFrame = 0;

	public Button(float x, float y, float w, float h, String text, Color c) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;

		this.sx = x;
		this.sy = y;

		this.t = text;
		this.color = c;
		this.darkerColor = new Color(c);
		darkerColor.sub(0.3f, 0.3f, 0.3f, 0.0f);
	}

	public Button(float x, float y, float w, float h, String text) {
		this(x, y, w, h, text, new Color(1, 1, 1, 1));
	}

	public Button(Window window, float x, float y, float w, float h, String text) {
		this(x, y, w, h, text, new Color(1, 1, 1, 1));
		this.window = window;
	}

	public Button(Window window, float x, float y, float w, float h, String text, Color c) {
		this(x, y, w, h, text, c);
		this.window = window;
	}

	public Button(float x, float y, float w, float h) {
		this(x, y, w, h, "Default Text");
	}

	/**
	 * Converts from Window Space to Screen Space using the current window location
	 * 
	 * @param x
	 * @param y
	 */
	public void updateData(float x, float y) {
		// Update the x, y, w, and h values to account for moving windows if it is in a
		// moving window
		this.x = this.sx + x;
		this.y = this.sy + y;
	}

	/**
	 * Sets the location and size of the button
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public void setProperties(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	/**
	 * If the mouse has been held down for at least 0.5 seconds, return true
	 * @param mouseX
	 * @param mouseY
	 * @return if the mouse is held down
	 */
	public boolean isHeld(float mouseX, float mouseY) {
		if(clicked(mouseX, mouseY)) {
			holdFrame = Gdx.graphics.getFrameId();
			return true;
		}
		if (mouseOver(mouseX, mouseY) && Gdx.input.isButtonPressed(0) && Gdx.graphics.getFrameId() - holdFrame > 30) {
			return true;
		}
		return false;
	}

	/**
	 * Check if the mouse position is inside the button and if it has just been
	 * clicked
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @return if the button has been clicked
	 */
	public boolean clicked(float mouseX, float mouseY) {
		boolean mousePressed = Gdx.input.isButtonJustPressed(0);
		// Check if the mouse position is inside of the button and the mouse is just
		// pressed.
		return mouseX > x && mouseX < x + w && Gdx.graphics.getHeight() - mouseY > y
				&& Gdx.graphics.getHeight() - mouseY < y + h && mousePressed;
	}

	/**
	 * Checks if the mouse is inside of the button
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @return If the mouse is inside the button
	 */
	public boolean mouseOver(float mouseX, float mouseY) {
		// Check if the mouse position is inside of the button.
		return mouseX > x && mouseX < x + w && Gdx.graphics.getHeight() - mouseY > y
				&& Gdx.graphics.getHeight() - mouseY < y + h;
	}

	public void render(ShapeRenderer renderer) {
		if (window != null)
			updateData(window.getX(), window.getY());
		if (mouseOver(Gdx.input.getX(), Gdx.input.getY()))
			renderer.setColor(darkerColor);
		else
			renderer.setColor(color);
		renderer.rect(x, y, w, h);
	}

	/**
	 * @return Button text
	 */
	public String getText() {
		return this.t;
	}

	public void setText(String t) {
		this.t = t;
	}

	public void showText(SpriteBatch batch, BitmapFont font) {
		font.setColor(Color.BLACK);
		font.draw(batch, t, x, y + h / 2 + font.getCapHeight() / 2, w, Align.center, true);
	}

	public void showText(Color c, SpriteBatch batch, BitmapFont font, int align) {
		font.setColor(c);
		font.draw(batch, t, x + 2, y + h / 2 + font.getCapHeight() / 2, w - 4, align, true);
	}
}
