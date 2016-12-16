package com.klemstinegroup.googlemap;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.PixmapLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class Pair implements AssetErrorListener {
    int i, j, ii, jj;
    Texture blank;
    public Texture tex;
    public Texture texRd;
    AssetManager managerSat;
    AssetManager managerRoad;
    final long key;


    public Pair(int i, int j, int ii, int jj, String road, String sat) {
        this.i = i;
        this.j = j;
        this.ii = ii;
        this.jj = jj;
        key=j * 10000000l + i;
        blank = getBlank();
        managerSat = new AssetManager();
        managerSat.setLoader(Pixmap.class, new PixmapLoader(new URLHandle()));
        managerSat.setErrorListener(this);
        managerSat.load(sat, Pixmap.class);

        managerRoad = new AssetManager();
        managerRoad.setLoader(Pixmap.class, new PixmapLoader(new URLHandle()));
        managerRoad.setErrorListener(this);
        managerRoad.load(road, Pixmap.class);

    }

    public Pair(int i, int j) {
        this.i=i;
        this.j=j;
        key=j * 10000000l + i;
    }

    @Override
    public boolean equals(Object b) {
        //check for self-comparison
        if (this == b) return true;
        Pair a = (Pair) b;
        if (a.i == i && a.j == j) return true;
        return false;
    }

    static Color[] colors = new Color[]{Color.WHITE, Color.GREEN, Color.RED, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA};

    public static Texture getBlank() {
//        if (blank == null) {
        Pixmap pm = new Pixmap(GoogleMaps.WIDTH, GoogleMaps.HEIGHT, Pixmap.Format.RGBA8888);
        pm.setColor(Color.BLACK);
        pm.fill();
        pm.setColor(colors[(int) (Math.random() * colors.length)]);
        for (int i = 0; i < 5; i++) {
            pm.drawRectangle(i, i, GoogleMaps.WIDTH - 2 * i, GoogleMaps.HEIGHT - 2 * i);
        }
        return new Texture(pm);
//        }
//        return blank;
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        System.out.println("error:" + asset.fileName + "\t" + throwable.getMessage());
    }

    public boolean update() {
        return managerRoad.update() && managerSat.update();

    }

    public float y() {
        return j - GoogleMaps.HEIGHT / 2;
    }

    public float x() {
        return i - GoogleMaps.WIDTH / 2;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "i=" + i +
                ", j=" + j +
                '}';
    }
}
