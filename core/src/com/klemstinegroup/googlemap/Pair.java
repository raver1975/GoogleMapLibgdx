package com.klemstinegroup.googlemap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.PixmapLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Pair implements AssetErrorListener {
    int i, j, ii, jj;
    Texture blank;
    public Pixmap texpm;
    public Pixmap texRdpm;

    public Texture tex;
    public Texture texRd;
    AssetManager managerSat;
    AssetManager managerRoad;


    public Pair(int i, int j, int ii, int jj, String road, String sat) {
        this.i = i;
        this.j = j;
        this.ii = ii;
        this.jj = jj;

        blank = getBlank();
        try {
            System.out.println("loading pixmap:"+this);
            texpm=PixmapIO.readCIM(Gdx.files.local("tex" + ii + "_" + jj));
            texRdpm=PixmapIO.readCIM(Gdx.files.local("texRd" + ii + "_" + jj));
            System.out.println("successfully loaded");
        }
        catch(GdxRuntimeException e){
            System.out.println("no pixmap saved");
            managerSat = new AssetManager();
            managerSat.setLoader(Pixmap.class, new PixmapLoader(new URLHandle()));
            managerSat.setErrorListener(this);
            managerSat.load(sat, Pixmap.class);

            managerRoad = new AssetManager();
            managerRoad.setLoader(Pixmap.class, new PixmapLoader(new URLHandle()));
            managerRoad.setErrorListener(this);
            managerRoad.load(road, Pixmap.class);
        }




    }

    public Pair(int i, int j) {
        this.i=i;
        this.j=j;
    }

    @Override
    public boolean equals(Object b) {
        //check for self-comparison
        if (this == b) return true;
        Pair a = (Pair) b;
        return (a.i == i && a.j == j);
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
        return (managerRoad==null||managerSat==null)||(managerRoad.update() && managerSat.update());

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

    public void dispose(){
        if (tex!=null)tex.dispose();
        if (texRd!=null)texRd.dispose();
        if (texpm!=null)texpm.dispose();
        if(texRdpm!=null)texRdpm.dispose();
//        if (managerSat!=null)managerSat.dispose();
//        if (managerRoad!=null)managerRoad.dispose();
        System.out.println("disposing:"+toString());
    }
}
