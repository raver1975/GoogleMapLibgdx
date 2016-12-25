package com.klemstinegroup.googlemap;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class GoogleMapLite implements ApplicationListener, AssetErrorListener,
        InputProcessor {

    static float WIDTH = 480;
    static float HEIGHT = 320;
    int x = 0;
    int y = 0;
    int cnt = 100;


    private OrthographicCamera cam;

    ArrayList<PairLite> visibleList = new ArrayList<PairLite>();

//    ArrayList<Long> loaded = new ArrayList<Long>();

    private ArrayList<PairLite> autoList = new ArrayList<PairLite>();
    private ArrayList<PairLite> loadingList = new ArrayList<PairLite>();
    private ArrayList<PairLite> allList = new ArrayList<PairLite>();
    private ArrayList<PairLite> remove = new ArrayList<PairLite>();

    private float rotationSpeed;
    private GoogleMapGrabber gm = new GoogleMapGrabber();
    private SpriteBatch batch;
    private int movex;
    private int movey;
    Texture face;
    private int oldloaded;
    private int oldloading;
    static Texture loadedtile=null;
    static Texture blank = null;
    @Override
    public void create() {
        getLocation();
        auto();
        blank = PairLite.getBlank(1);
        loadedtile =PairLite.getBlank(2);

        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();
        rotationSpeed = 0.5f;
        Pixmap facepx = new Pixmap(20, 20, Pixmap.Format.RGBA8888);
        facepx.setColor(Color.CLEAR);
        facepx.fill();
        facepx.setColor(Color.RED);
        facepx.fillCircle(10, 10, 9);
        face = new Texture(facepx);
//        face = new Texture(
//                new URLHandle(
//                        "http://cdn1.sbnation.com/profile_images/592671/smiley_face_small.jpg"));
        cam = new OrthographicCamera(WIDTH, HEIGHT);
        cam.position.set(0, 0, 0);
        moveCamera(0, 0);
        batch = new SpriteBatch();

        Gdx.input.setInputProcessor(this);

    }

    private void auto() {
        System.out.println("starting");
        int x = 0, y = 0, dx = 0, dy = -1;
//        int t = Math.max(X,Y);
//        int maxI = t*t;
        int t = 0;

        while (cnt-- > 0) {
//            if ((-X/2 <= x) && (x <= X/2) && (-Y/2 <= y) && (y <= Y/2)) {
            System.out.println(x + "," + y);
            String sat = gm.getSatelliteUrl(x, y);
            PairLite p = new PairLite(x, y, sat);
            autoList.add(p);
            if ((x == y) || ((x < 0) && (x == -y)) || ((x > 0) && (x == 1 - y))) {
                t = dx;
                dx = -dy;
                dy = t;
            }
            x += dx;
            y += dy;
        }
    }

    private void getLocation() {
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

    static int max = 2;

    @Override
    public void render() {
//        int ready = 0;
//        for (PairLite p : loadingList) {
//            if (p.init) {
//                ready++;
//            }
//        }
//        if (ready < max) {
//            int cnt1 = max;
//            for (PairLite p : loadingList) {
//                if (!p.init) {
//                    cnt1--;
//                    p.init();
//                }
//                if (cnt1 <= 0) break;
//            }
//        }
        if (loadingList.size() <max && autoList.size() > 0) {
            loadingList.add(autoList.remove(0));
        }
        for (PairLite p : loadingList) {
            if (p.managerSat != null && p.managerSat.getProgress() != p.progress) {
                System.out.println(p + "\t" + p.managerSat.getProgress() + "%");
                p.progress = p.managerSat.getProgress();
            }
            if (p.update()) {
//                if (p.dataPix ==null)p.dataPix = getTexture(gm.getRoadMapUrl(p.pixelX, p.pixelY), p.managerRoad,true);
                if (p.satPix == null) {
                    p.satPix = getTexture(gm.getSatelliteUrl(p.pixelX, p.pixelY), p.managerSat, false);
                }
//                if (p.dataPix != null) p.dataTex = new Texture(p.dataPix);
                if (p.satPix != null) {
                    p.satTex = new Texture(p.satPix);
                    if (p.managerSat != null) {
                        System.out.println("writing pixmap:" + p);
//                    PixmapIO.writeCIM(Gdx.files.local("dataTex" + p.tileX + "_" + p.tileY +".cim"),p.dataPix);
                        PixmapIO.writeCIM(Gdx.files.local("satTex" + p.tileX + "_" + p.tileY + ".cim"), p.satPix);

//                    PixmapIO.writePNG(Gdx.files.local("dataTex" + p.tileX + "_" + p.tileY +".png"),p.dataPix);
                        PixmapIO.writePNG(Gdx.files.local("satTex" + p.tileX + "_" + p.tileY + ".png"), p.satPix);
                    }
                    visibleList.add(p);
                    allList.add(p);
                    remove.add(p);
                }


            }
        }
        while (remove.size() > 0) {
            loadingList.remove(remove.remove(remove.size() - 1));
        }


        handleInput();
        GL20 gl = Gdx.graphics.getGL20();
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);


        if (loadedtile != null) {
            for (PairLite p : allList) {
                batch.draw(loadedtile, p.x(), p.y());
            }
        }

        if (blank != null) {
            for (PairLite p : loadingList) {
                batch.draw(blank, p.x(), p.y());
            }
        }

        int xl = (int) (((cam.position.x - (WIDTH / 2)) / GoogleMapGrabber.SIZE) - .5f);
        int xh = (int) (((cam.position.x + (WIDTH / 2)) / GoogleMapGrabber.SIZE) + .5f);
        int yl = (int) (((cam.position.y - (HEIGHT / 2)) / GoogleMapGrabber.SIZE) - .5f);
        int yh = (int) (((cam.position.y + (HEIGHT / 2)) / GoogleMapGrabber.SIZE) + .5f);

//        System.out.println(xl+"\t"+xh+"\t"+yl+"\t"+yh);
        ArrayList<PairLite> draw = new ArrayList<PairLite>();
        for (int i = yl; i <= yh; i++) {
            for (int j = xl; j <= xh; j++) {
                PairLite test = new PairLite(j * GoogleMapGrabber.SIZE, i * GoogleMapGrabber.SIZE);
                for (PairLite p : visibleList) {
                    if (p.equals(test)) {
                        draw.add(p);
                        break;
                    }
                }
            }
        }


        for (PairLite p : visibleList) {
            if (!draw.contains(p)) remove.add(p);
        }
        for (PairLite p : remove) {
            p.dispose();
            visibleList.remove(p);
//            loaded.remove(p.tileY * 10000000l + p.tileX);
        }
        remove.clear();
        if (oldloaded != visibleList.size() || oldloading != loadingList.size()) {
            System.out.println("auto:"+autoList.size()+"\tvisible:" + visibleList.size() + "\t" + "loading:" + loadingList.size()+"\tall:"+ allList.size());
            oldloaded = visibleList.size();
            oldloading = loadingList.size();

        }

        for (PairLite mk : draw) {
            batch.setColor(1f, 1f, 1f, 1f);
//            if (mk.dataTex != null) {
//                batch.draw(mk.dataTex, mk.x(), mk.y());
//            }
            batch.setColor(1f, 1f, 1f, .8f);
            if (mk.satTex != null)
                batch.draw(mk.satTex, mk.x(), mk.y());
        }

        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(face, cam.position.x - 16, cam.position.y - 16);
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            cam.zoom += 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            cam.zoom -= 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            // if (cam.position.x > 0)
            moveCamera(-2, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            // if (cam.position.x < texture.getWidth()/2)
            moveCamera(2, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            // if (cam.position.y > 0)
            moveCamera(0, -2);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            // if (cam.position.y < texture.getHeight()/2)
            moveCamera(0, 2);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            cam.rotate(-rotationSpeed, 0, 0, 1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            cam.rotate(rotationSpeed, 0, 0, 1);
        }
    }

    @Override
    public void resize(int width, int height) {
        WIDTH = width;
        HEIGHT = height;
//       cam.setToOrtho(false,width,height);
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean keyDown(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == 'j' - 68) {
            shift(-GoogleMapGrabber.SIZE, 0);
        }
        if (keycode == 'l' - 68) {
            shift(GoogleMapGrabber.SIZE, 0);
        }
        if (keycode == 'k' - 68) {
            shift(0, -GoogleMapGrabber.SIZE);
        }
        if (keycode == 'i' - 68) {
            shift(0, GoogleMapGrabber.SIZE);
        }

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    private void shift(final int ii, final int jj) {
        int i = ii * GoogleMapGrabber.SIZE;
        int j = jj * GoogleMapGrabber.SIZE;
        String road = gm.getRoadMapUrl(i, j);
        String sat = gm.getSatelliteUrl(i, j);
        if (!visibleList.contains(new PairLite(i, j)) && !loadingList.contains(new PairLite(i, j))) {
            loadingList.add(new PairLite(ii, jj, sat));
        }
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        movex = x;
        movey = y;
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {

        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        moveCamera(movex - x, y - movey);
        movex = x;
        movey = y;
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void moveCamera(int x, int y) {
        cam.translate(x, y, 0);
        int xl = (int) (((cam.position.x - (WIDTH / 2f)) / GoogleMapGrabber.SIZE) - .5f);
        int xh = (int) (((cam.position.x + (WIDTH / 2f)) / GoogleMapGrabber.SIZE) + .5f);
        int yl = (int) (((cam.position.y - (HEIGHT / 2f)) / GoogleMapGrabber.SIZE) - .5f);
        int yh = (int) (((cam.position.y + (HEIGHT / 2f)) / GoogleMapGrabber.SIZE) + .5f);
        for (int i = yl; i <= yh; i++) {
            for (int j = xl; j <= xh; j++) {
                if (!visibleList.contains(new PairLite(j, i))) {
                    shift(j, i);
                }
            }
        }
    }

    public Pixmap getTexture(String bb, AssetManager manager, boolean b) {
//        System.out.println(bb);

        try {
            Pixmap pixmap = manager.get(bb, Pixmap.class);
            System.out.println(bb);
            Pixmap potPixmap = new Pixmap(GoogleMapGrabber.SIZE, GoogleMapGrabber.SIZE,
                    pixmap.getFormat());
            potPixmap.drawPixmap(pixmap, 0, 0, GoogleMapGrabber.SIZE, GoogleMapGrabber.SIZE, 0, 0,
                    GoogleMapGrabber.SIZE, GoogleMapGrabber.SIZE);
//            Texture nonPotTexture = new Texture(
//                    potPixmap);
            pixmap.dispose();
//            potPixmap.dispose();
            return potPixmap;
        } catch (GdxRuntimeException e) {
//            e.printStackTrace();
            return null;
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        System.out.println("Error with " + asset.fileName);
    }


}