package com.klemstinegroup.googlemap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class MapKeeper {

    public Texture tex;
    public Texture texRd;
    public int x;
    public int y;

    public void generateBlank() {
        Pixmap pm = new Pixmap(x, y, Pixmap.Format.RGBA8888);
        pm.setColor(Color.BLACK);
        pm.fill();
        pm.setColor(Color.WHITE);
        for (int i = 0; i < 5; i++) {
            pm.drawRectangle(i, i, x - 2 * i, y - 2 * i);
        }
        tex = new Texture(x, y, Pixmap.Format.RGBA8888);
        texRd = new Texture(x, y, Pixmap.Format.RGBA8888);
        tex.draw(pm, x, y);
        texRd.draw(pm, x, y);
    }
}
