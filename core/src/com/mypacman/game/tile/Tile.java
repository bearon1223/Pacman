package com.mypacman.game.tile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mypacman.game.UIHelper.Button;

public class Tile {
	private boolean isWall, hasPellet, isSuperPellet;
	private int row, col;
	private Color color;
	private float favorability = 0;
	private boolean isJunction, isInvalid;
	protected Button b;
	private TextureRegion t;

	public Tile(int row, int col, boolean isWall, boolean hasPellet, boolean isJunction, boolean isInvalid,
			boolean isSuperPellet, AtlasRegion pill, AtlasRegion superPill) {
		this.row = row;
		this.col = col;
		this.isWall = isWall;
		this.color = Color.WHITE;
		this.hasPellet = hasPellet || isSuperPellet;
		this.isJunction = isJunction;
		this.isInvalid = isInvalid;
		this.isSuperPellet = isSuperPellet;

		if (!isSuperPellet)
			this.t = pill;
		else
			this.t = superPill;

		b = new Button(0, 0, 10, 10);
	}

	public boolean isObstical() {
		return isWall;
	}

	public boolean hasPellet() {
		return hasPellet;
	}
	
	public boolean hasSuperPellet() {
		return isSuperPellet;
	}

	public void removePellet() {
		hasPellet = false;
	}

	public boolean isJunction() {
		return isJunction;
	}

	public boolean invalid() {
		return isInvalid;
	}

	/**
	 * @return location as [row, col]
	 */
	public int[] getLocation() {
		int[] loc = { row, col };
		return loc;
	}

	/**
	 * @param size
	 * @param offsetX
	 * @param offsetY
	 * @return coordinates of the rect in world space
	 */
	public Vector2 getLocationAsVector(float size, float offsetX, float offsetY) {
		return new Vector2(col * size + offsetX, row * size + offsetY);
	}

	public void render(ShapeRenderer renderer, float size, float offsetX, float offsetY) {
		if (isWall)
			renderer.setColor(new Color(0x0377fcff));
		else if (isJunction)
			renderer.setColor(Color.MAGENTA);
		else
			renderer.setColor(color);
		renderer.rect(col * size + offsetX, row * size + offsetY, size, size);
		b.setProperties(col * size + offsetX, row * size + offsetY, size, size);
		if (this.hasPellet && !isWall) {
			float x = col * size + offsetX;
			float y = row * size + offsetY;
			renderer.setColor(Color.GOLD);
			renderer.circle(x + size / 2, y + size / 2, size / 6);
		}
	}

	public void render(SpriteBatch batch, float size, float offsetX, float offsetY) {
		if (hasPellet && !isWall) {
//			batch.draw(t, col * size + offsetX, row * size + offsetY, size, size);
			if (isSuperPellet) {
				if (Gdx.graphics.getFrameId() % 50 > 20)
					batch.draw(t, col * size + offsetX, row * size + offsetY, size / 2, size / 2, size, size, 0.66f,
							0.66f, 0f);
			} else {
				batch.draw(t, col * size + offsetX, row * size + offsetY, size / 2, size / 2, size, size, 0.66f, 0.66f,
						0f);
			}
		}
	}

	public void setObstical(boolean isObstical) {
		isWall = isObstical;
	}

	public void resetColor() {
		setColor(Color.WHITE);
	}

	public float getFavorability() {
		return favorability;
	}

	public void setFavorability(float f) {
		favorability = f;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color newColor) {
		this.color = newColor;
	}

	public void dispose() {
	}
}
