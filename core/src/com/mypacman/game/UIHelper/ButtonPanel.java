package com.mypacman.game.UIHelper;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ButtonPanel {
    public Button[] buttons;
    float x, y, w, h, cx, cy;

    public ButtonPanel(float x, float y, float width, float height, int countX, int countY, float buffer, String[] text) {
        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;
        this.cx = countX;
        this.cy = countY;

        buttons = new Button[countX * countY];

        for (int i = 0; i < countX * countY; i++) {
            buttons[i] = new Button(x + ((i % countX) * width), y + (int) (Math.floor(i / countX)) * height,
            width - buffer, height - buffer, text[countX*countY - 1 -i]);
        }
    }

    public ButtonPanel(float x, float y, float width, float height, int countX, int countY, float buffer) {
        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;
        this.cx = countX;
        this.cy = countY;

        buttons = new Button[countX * countY];

        for (int i = 0; i < countX * countY; i++) {
            buttons[i] = new Button(x + ((i % countX) * width), y + (int) (Math.floor(i / countX)) * height,
                    width - buffer, height - buffer, String.valueOf(i+1));
        }
    }

    /**
     * @param mouseX
     * @param mouseY
     * @return Button the mouse is over
     */
    public Button getButtonOver(int mouseX, int mouseY) {
        for (Button b : buttons) {
            if (b.mouseOver(mouseX, mouseY)) {
                return b;
            }
        }
        return new Button(-100, -100, 0, 0);
    }

    /**
     * @param i
     * @return button i from the array of buttons
     */
    public Button getButton(int i){
        if (i > buttons.length){
            return null;
        }
        return buttons[i];
    }

    public void draw(ShapeRenderer renderer) {
        for (Button b : buttons) {
            b.render(renderer);
        }
    }

    public void drawText(SpriteBatch batch, BitmapFont font) {
        for (Button b : buttons) {
            b.showText(batch, font);
        }
    }
}
