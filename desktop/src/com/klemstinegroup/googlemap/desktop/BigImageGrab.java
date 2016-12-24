package com.klemstinegroup.googlemap.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.klemstinegroup.googlemap.GoogleMap;
import com.klemstinegroup.googlemap.GoogleMapGrabber;
import com.klemstinegroup.googlemap.GoogleMapLite;
import com.klemstinegroup.googlemap.PairLite;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Paul on 12/24/2016.
 */
public class BigImageGrab {
    static GoogleMapGrabber gm = new GoogleMapGrabber();
    private ArrayList<PairLite> loading = new ArrayList<PairLite>();
    private ArrayList<PairLite> remove = new ArrayList<PairLite>();


    public static void main(String[] args) {

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(new GoogleMap(), config);


        getLocation();
        int x = 0, y = 0, dx = 0, dy = -1;
//        int t = Math.max(X,Y);
//        int maxI = t*t;
        int t = 0;
        int cnt = 10;
        while (cnt-- > 0) {
//            if ((-X/2 <= x) && (x <= X/2) && (-Y/2 <= y) && (y <= Y/2)) {
            System.out.println(x + "," + y);
            String sat = gm.getSatelliteUrl(x, y);
            PairLite p = new PairLite(x, y, sat);
            while (!p.update()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (p.satPix == null)
                p.satPix = getTexture(gm.getSatelliteUrl(p.pixelX, p.pixelY), p.managerSat, false);
//                if (p.dataPix != null) p.dataTex = new Texture(p.dataPix);
            if (p.satPix != null) p.satTex = new Texture(p.satPix);
            if (p.managerSat != null) {
                System.out.println("writing pixmap:" + p);
//                    PixmapIO.writeCIM(Gdx.files.local("dataTex" + p.tileX + "_" + p.tileY +".cim"),p.dataPix);
                PixmapIO.writeCIM(Gdx.files.local("satTex" + p.tileX + "_" + p.tileY + ".cim"), p.satPix);

//                    PixmapIO.writePNG(Gdx.files.local("dataTex" + p.tileX + "_" + p.tileY +".png"),p.dataPix);
                PixmapIO.writePNG(Gdx.files.local("satTex" + p.tileX + "_" + p.tileY + ".png"), p.satPix);
                System.out.println("written:"+x+","+y);
            }


            if ((x == y) || ((x < 0) && (x == -y)) || ((x > 0) && (x == 1 - y))) {
                t = dx;
                dx = -dy;
                dy = t;
            }
            x += dx;
            y += dy;
        }
    }

    private void update() {
        for (PairLite p : loading) {
            if (p.update()) {
                System.out.println("updating PairLite");
//                if (p.dataPix ==null)p.dataPix = getTexture(gm.getRoadMapUrl(p.pixelX, p.pixelY), p.managerRoad,true);
                if (p.satPix == null)
                    p.satPix = getTexture(gm.getSatelliteUrl(p.pixelX, p.pixelY), p.managerSat, false);
//                if (p.dataPix != null) p.dataTex = new Texture(p.dataPix);
                if (p.satPix != null) p.satTex = new Texture(p.satPix);
                if (p.managerSat != null) {
                    System.out.println("writing pixmap:" + p);
//                    PixmapIO.writeCIM(Gdx.files.local("dataTex" + p.tileX + "_" + p.tileY +".cim"),p.dataPix);
                    PixmapIO.writeCIM(Gdx.files.local("satTex" + p.tileX + "_" + p.tileY + ".cim"), p.satPix);

//                    PixmapIO.writePNG(Gdx.files.local("dataTex" + p.tileX + "_" + p.tileY +".png"),p.dataPix);
                    PixmapIO.writePNG(Gdx.files.local("satTex" + p.tileX + "_" + p.tileY + ".png"), p.satPix);


                }

                remove.add(p);
            }
        }
        while (remove.size() > 0) {
            loading.remove(remove.remove(remove.size() - 1));
        }

    }

    private static void getLocation() {
        URL url = null;
        try {
            url = new URL("http://api.ipinfodb.com/v3/ip-city/?key=c4a286f1ff1e0eba56a611b982696ecbb7c68a175cf92f16a826cebb4b0f8d5f");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStream is = null;
        try {
            is = url.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (is != null) {
            int ptr = 0;
            StringBuffer buffer = new StringBuffer();
            try {
                while ((ptr = is.read()) != -1) {
                    buffer.append((char) ptr);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String location = buffer.toString();
            String[] data = location.split(";");
            System.out.println(Arrays.toString(data));
//		"statusCode" : "OK",
//		"statusMessage" : "",
//		"ipAddress" : "74.125.45.100",
//		"countryCode" : "US",
//		"countryName" : "UNITED STATES",
//		"regionName" : "CALIFORNIA",
//		"cityName" : "MOUNTAIN VIEW",
//		"zipCode" : "94043",
//		"latitude" : "37.3956",
//		"longitude" : "-122.076",
//		"timeZone" : "-08:00"
            gm.lat = Double.parseDouble(data[8]);
            gm.lon = Double.parseDouble(data[9]);
        } else {
            gm.lat = 37.3956d;
            gm.lon = -122.076d;
        }
    }

    public static Pixmap getTexture(String bb, AssetManager manager, boolean b) {
//        System.out.println(bb);

        try {
            Pixmap pixmap = manager.get(bb, Pixmap.class);
            System.out.println(bb);
            Pixmap potPixmap = new Pixmap(GoogleMapGrabber.WIDTH, GoogleMapGrabber.HEIGHT,
                    pixmap.getFormat());
            potPixmap.drawPixmap(pixmap, 0, 0, GoogleMapGrabber.WIDTH, GoogleMapGrabber.HEIGHT, 0, 0,
                    GoogleMapGrabber.WIDTH, GoogleMapGrabber.HEIGHT);
//            Texture nonPotTexture = new Texture(
//                    potPixmap);
            pixmap.dispose();
//            potPixmap.dispose();
            return potPixmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
