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
    int pixelX;
    int pixelY;
    int tileX;
    int tileY;
    Texture blank;
    public Pixmap dataPix;
    public Pixmap satPix;

    public Texture dataTex;
    public Texture satTex;
    AssetManager managerSat;
    AssetManager managerRoad;


    public Pair(int i, int j, int ii, int jj, String road, String sat) {
        this.pixelX = i;
        this.pixelY = j;
        this.tileX = ii;
        this.tileY = jj;

        blank = getBlank();
        try {
            System.out.println("loading pixmap:"+this);
            dataPix =PixmapIO.readCIM(Gdx.files.local("dataTex" + ii + "_" + jj+".cim"));
            satPix =PixmapIO.readCIM(Gdx.files.local("satTex" + ii + "_" + jj+".cim"));
            System.out.println("successfully loaded");
        }
        catch(GdxRuntimeException e){
            System.out.println("no pixmap found");
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
        this.pixelX =i;
        this.pixelY =j;
    }

    @Override
    public boolean equals(Object b) {
        //check for self-comparison
        if (this == b) return true;
        Pair a = (Pair) b;
        return (a.pixelX == pixelX && a.pixelY == pixelY);
    }

    static Color[] colors = new Color[]{Color.WHITE, Color.GREEN, Color.RED, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA};

    public static Texture getBlank() {
//        if (blank == null) {
        Pixmap pm = new Pixmap(GoogleMapGrabber.SIZE, GoogleMapGrabber.SIZE, Pixmap.Format.RGBA8888);
        pm.setColor(Color.BLACK);
        pm.fill();
        pm.setColor(colors[(int) (Math.random() * colors.length)]);
        for (int i = 0; i < 5; i++) {
            pm.drawRectangle(i, i, GoogleMapGrabber.SIZE - 2 * i, GoogleMapGrabber.SIZE- 2 * i);
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
        return pixelY - GoogleMapGrabber.SIZE/ 2;
    }

    public float x() {
        return pixelX - GoogleMapGrabber.SIZE / 2;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "pixelX=" + pixelX +
                ", pixelY=" + pixelY +
                '}';
    }

    public void dispose(){
        if (dataTex !=null) dataTex.dispose();
        if (satTex !=null) satTex.dispose();
        if (dataPix !=null) dataPix.dispose();
        if(satPix !=null) satPix.dispose();
//        if (managerSat!=null)managerSat.dispose();
//        if (managerRoad!=null)managerRoad.dispose();
        System.out.println("disposing:"+toString());
    }
}
