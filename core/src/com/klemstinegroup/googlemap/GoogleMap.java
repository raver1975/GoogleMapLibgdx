package com.klemstinegroup.googlemap;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

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

public class GoogleMap implements ApplicationListener, AssetErrorListener,
        InputProcessor {

    static float WIDTH = 480;
    static float HEIGHT = 320;
    private static Texture blank;
//    int x = 0;
//    int y = 0;

    private OrthographicCamera cam;

//    ArrayList<FloodFillData> queue = new ArrayList<FloodFillData>();

    ArrayList<Pair> tiles = new ArrayList<Pair>();

//    ArrayList<Long> loaded = new ArrayList<Long>();

    private ArrayList<Pair> loading = new ArrayList<Pair>();
    private ArrayList<Pair> remove = new ArrayList<Pair>();

    private float rotationSpeed;
    private GoogleMapGrabber gm = new GoogleMapGrabber();
    private SpriteBatch batch;
    private int movex;
    private int movey;
    //    Texture face;
    private int oldloaded;
    private int oldloading;


    @Override
    public void create() {

        getLocation();
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();
        rotationSpeed = 0.5f;
//        face = new Texture(
//                new URLHandle(
//                        "http://cdn1.sbnation.com/profile_images/592671/smiley_face_small.jpg"));
        cam = new OrthographicCamera(WIDTH, HEIGHT);
        cam.position.set(0, 0, 0);
        moveCamera(0, 0);
//        queue.add(new FloodFillData((int) cam.position.x, (int) cam.position.y));
        batch = new SpriteBatch();

        Gdx.input.setInputProcessor(this);

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
            gm.name = data[6];
        } else {
            gm.lat = 37.3956d;
            gm.lon = -122.076d;
            gm.name = "Jerusalem";
        }
    }


    float oldx = 1, oldy = 1;
    float angle = (float) Math.PI * 0f / 4f, speed = 1f;
    boolean on = false;
    boolean dir = false;
    boolean clear = true;

    @Override
    public void render() {
//        for (int i = 0; i < 100; i++) {
//            System.out.println(queue.size());
            FloodFillData ffd1 = new FloodFillData((int) cam.position.x, (int) cam.position.y);
            FloodFillData ffd = ffd1;
            double dist = Double.MAX_VALUE;
            double dist2 = Double.MIN_VALUE;
            FloodFillData ffd2 = null;
            double dist3 = Double.MIN_VALUE;
            FloodFillData ffd3 = null;

//            for (FloodFillData ff : queue) {
//                if (ff.dist(ffd1.gloX, ffd1.gloY) < dist) {
//                    dist = ff.dist(ffd1.gloX, ffd1.gloY);
//                    ffd = ff;
//                }
//
//                if (queue.size() > 1000) {
//                    if (ff.dist(ffd1.gloX, ffd1.gloY) > dist2) {
//                        dist2 = ff.dist(ffd1.gloX, ffd1.gloY);
//                        ffd2 = ff;
//                    }
//                }
//
//                if (queue.size() > 2000) {
//                    if (ff.dist(ffd1.gloX, ffd1.gloY) > dist3) {
//                        dist3 = ff.dist(ffd1.gloX, ffd1.gloY);
//                        ffd3 = ff;
//                    }
//                }
//
//
//            }
//            queue.remove(ffd);
//            queue.remove(ffd2);
//            queue.remove(ffd3);
            int ttx = (int) (ffd.gloX + GoogleMapGrabber.SIZE / 2) / GoogleMapGrabber.SIZE;
            int tty = (int) (ffd.gloY + GoogleMapGrabber.SIZE / 2) / GoogleMapGrabber.SIZE;
            int posx = (int) (((ffd.gloX + GoogleMapGrabber.SIZE / 2) % GoogleMapGrabber.SIZE) + GoogleMapGrabber.SIZE) % GoogleMapGrabber.SIZE;
            int posy = GoogleMapGrabber.SIZE - (int) (((ffd.gloY + GoogleMapGrabber.SIZE / 2) % GoogleMapGrabber.SIZE) + GoogleMapGrabber.SIZE) % GoogleMapGrabber.SIZE;

            for (Pair p : tiles) {
                if (p.tileX == ttx && p.tileY == tty) {
                    int pix = p.dataPix.getPixel(posx, posy);
                    if (!on) {
                        if (pix == -1) {
                            on = true;
                        } else {
                            moveCamera((float) Math.cos(angle) * speed, (float) Math.sin(angle) * speed);
                        }
                    } else {
                        if (pix != -1) {
                            cam.position.x = oldx;
                            cam.position.y = oldy;
                            angle += dir ? 0.1f : -0.1f;
                            clear = false;

                        } else {
//                            queue.add(new FloodFillData(ffd.gloX - 1, ffd.gloY));
//                            queue.add(new FloodFillData(ffd.gloX + 1, ffd.gloY));
//                            queue.add(new FloodFillData(ffd.gloX, ffd.gloY - 1));
//                            queue.add(new FloodFillData(ffd.gloX, ffd.gloY + 1));
//                            cam.position.x -= (cam.position.x - ffd.gloX) / 2f;
//                            cam.position.y -= (cam.position.y - ffd.gloY) / 2f;
//                            moveCamera(ffd.gloX-cam.position.x,ffd.gloY-cam.position.y);
                            if (!clear) dir = !dir;
                            clear = true;
                        }
                    }
//                    p.satPix.drawPixel(posx, posy, Color.rgba8888(Color.BLACK));
//
//                    p.dataPix.drawPixel(posx, posy, Color.rgba8888(Color.BLACK));


                    oldx = cam.position.x;
                    oldy = cam.position.y;

                    while (angle < 0f) angle += (float) Math.PI * 2f;
                    while (angle > (float) Math.PI * 2f) angle -= (float) Math.PI * 2f;
                    float d1 = Math.abs(angle - 0f);
                    float d2 = Math.abs(angle - (float) Math.PI / 2f);
                    float d3 = Math.abs(angle - (float) Math.PI);
                    float d4 = Math.abs(angle - (float) Math.PI * 3f / 2f);
                    float d5 = Math.abs(angle - (float) Math.PI * 2f);
                    float min = Math.min(d1, Math.min(d2, Math.min(d3, Math.min(d4, d5))));
                    float target = 0;
                    if (d1 == min) {
                        target = 0f;
                    }
                    if (d2 == min) {
                        target = (float) Math.PI / 2f;
                    }
                    if (d3 == min) {
                        target = (float) Math.PI;
                    }
                    if (d4 == min) {
                        target = (float) Math.PI * 3f / 2f;
                    }
                    if (d5 == min) {
                        target = (float) Math.PI * 2f;
                    }

                    if (target < angle) angle -= .01f;
                    if (target > angle) angle += .01f;
                moveCamera((float) Math.cos(angle) * speed, (float) Math.sin(angle) * speed);
                }
//                if (p.satTex != null) {
//                    p.satTex.dispose();}
//                    p.satTex = new Texture(p.satPix);
//
//                if (p.dataTex != null) {
//                    p.dataTex.dispose();}
//                    p.dataTex = new Texture(p.dataPix);




        }


        for (Pair p : loading) {
            if (p.update()) {
                System.out.println("updating pair");
                if (p.dataPix == null) p.dataPix = getPixmap(gm.getRoadMapUrl(p.tileX, p.tileY), p.managerRoad, true);
                if (p.satPix == null) p.satPix = getPixmap(gm.getSatelliteUrl(p.tileX, p.tileY), p.managerSat, false);
                if (p.dataPix != null) p.dataTex = new Texture(p.dataPix);
                if (p.satPix != null) p.satTex = new Texture(p.satPix);
                if (p.managerRoad != null) {
                    System.out.println("writing pixmap:" + p);
                    if (gm.directory == null) {
                        PixmapIO.writeCIM(Gdx.files.external(gm.getFileNameData(p.tileX, p.tileY) + ".cim"), p.dataPix);
                        PixmapIO.writeCIM(Gdx.files.external(gm.getFileNameSat(p.tileX, p.tileY) + ".cim"), p.satPix);

                        PixmapIO.writePNG(Gdx.files.external(gm.getFileNameData(p.tileX, p.tileY)), p.dataPix);
                        PixmapIO.writePNG(Gdx.files.external(gm.getFileNameSat(p.tileX, p.tileY)), p.satPix);

                    } else {
                        PixmapIO.writeCIM(Gdx.files.absolute(gm.directory + gm.getFileNameData(p.tileX, p.tileY) + ".cim"), p.dataPix);
                        PixmapIO.writeCIM(Gdx.files.absolute(gm.directory + gm.getFileNameSat(p.tileX, p.tileY) + ".cim"), p.satPix);

                        PixmapIO.writePNG(Gdx.files.absolute(gm.directory + gm.getFileNameData(p.tileX, p.tileY)), p.dataPix);
                        PixmapIO.writePNG(Gdx.files.absolute(gm.directory + gm.getFileNameSat(p.tileX, p.tileY)), p.satPix);
                    }

                }

                if (p.dataTex != null && p.satTex != null) {
                    tiles.add(p);
//                    loaded.add(p.tileX * 10000000l + p.tileY);


                }
                remove.add(p);
            }
        }
        while (remove.size() > 0) {
            loading.remove(remove.remove(remove.size() - 1));
        }


        handleInput();
        GL20 gl = Gdx.graphics.getGL20();
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);

        for (Pair p : loading) {
            batch.draw(p.blank, p.x(), p.y());
        }

        int xl = (int) (((cam.position.x - (WIDTH / 2)) / GoogleMapGrabber.SIZE) - .5f);
        int xh = (int) (((cam.position.x + (WIDTH / 2)) / GoogleMapGrabber.SIZE) + .5f);
        int yl = (int) (((cam.position.y - (HEIGHT / 2)) / GoogleMapGrabber.SIZE) - .5f);
        int yh = (int) (((cam.position.y + (HEIGHT / 2)) / GoogleMapGrabber.SIZE) + .5f);

//        System.out.println(xl+"\t"+xh+"\t"+yl+"\t"+yh);
        ArrayList<Pair> draw = new ArrayList<Pair>();
        for (int i = yl; i <= yh; i++) {
            for (int j = xl; j <= xh; j++) {
                Pair test = new Pair(j, i);
                for (Pair p : tiles) {
                    if (p.equals(test)) {
                        draw.add(p);
                        break;
                    }
                }
            }
        }


        for (Pair p : tiles) {
            if (!draw.contains(p)) remove.add(p);
        }
        for (Pair p : remove) {
            p.dispose();
            tiles.remove(p);
//            loaded.remove(p.tileY * 10000000l + p.tileX);
        }
        remove.clear();
        if (oldloaded != tiles.size() || oldloading != loading.size()) {
            System.out.println("loaded:" + tiles.size() + "\t" + "loading:" + loading.size());
            oldloaded = tiles.size();
            oldloading = loading.size();

        }

        for (Pair mk : draw) {
            batch.setColor(1f, 1f, 1f, 1f);
            if (mk.dataTex != null) {
                batch.draw(mk.dataTex, mk.x(), mk.y());
            }
            batch.setColor(1f, 1f, 1f, .8f);
            if (mk.satTex != null)
                batch.draw(mk.satTex, mk.x(), mk.y());
        }

        batch.setColor(1f, 1f, 1f, 1f);
        Pixmap p = new Pixmap(20, 20, Pixmap.Format.RGBA8888);
        p.setColor(Color.CLEAR);
        p.fill();
        p.setColor(Color.FIREBRICK);
        p.fillCircle(10, 10, 5);
        float len = 10;
        p.setColor(Color.RED);
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                p.drawLine(10 + i, 10 + j, (int) (10 + i + (float) Math.cos(angle) * len), (int) (10 + j - (float) Math.sin(angle) * len));
            }
        }


        batch.draw(new Texture(p), cam.position.x - 10, cam.position.y - 10);
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.O)) {
            angle += .1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            angle -= .1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.I)) {
            speed += .1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.K)) {
            speed -= .1f;
        }

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

    private void shift(final int i, final int j) {
//        int i = ii * GoogleMapGrabber.SIZE;
//        int j = jj * GoogleMapGrabber.SIZE;
        String road = gm.getRoadMapUrl(i, j);
        String sat = gm.getSatelliteUrl(i, j);
        if (!tiles.contains(new Pair(i, j)) && !loading.contains(new Pair(i, j))) {
            loading.add(new Pair(i, j, road, sat, gm));
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

    public void moveCamera(float x, float y) {
        cam.translate(x, y, 0);
        int xl = (int) (((cam.position.x - (WIDTH / 2f)) / GoogleMapGrabber.SIZE) - .5f);
        int xh = (int) (((cam.position.x + (WIDTH / 2f)) / GoogleMapGrabber.SIZE) + .5f);
        int yl = (int) (((cam.position.y - (HEIGHT / 2f)) / GoogleMapGrabber.SIZE) - .5f);
        int yh = (int) (((cam.position.y + (HEIGHT / 2f)) / GoogleMapGrabber.SIZE) + .5f);
        for (int i = yl; i <= yh; i++) {
            for (int j = xl; j <= xh; j++) {
                if (!tiles.contains(new Pair(j, i))) {
                    shift(j, i);
                }
            }
        }
    }


    public Pixmap getPixmap(String bb, AssetManager manager, boolean b) {
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
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        System.out.println("Error with " + asset.fileName);
    }


}