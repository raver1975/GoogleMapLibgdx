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
import com.badlogic.gdx.assets.loaders.PixmapLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GoogleMap implements ApplicationListener, AssetErrorListener,
		InputProcessor {

	static int WIDTH = 480;
	static int HEIGHT = 320;
	int x = 0;
	int y = 0;

	private OrthographicCamera cam;

	ArrayList<MapKeeper> tiles = new ArrayList<MapKeeper>();
	ArrayList<Pair> loading = new ArrayList<Pair>();

	ArrayList<Long> loaded = new ArrayList<Long>();

	private AssetManager manager;

	private float rotationSpeed;
	private GoogleMaps gm = new GoogleMaps();
	private SpriteBatch batch;
	private int movex;
	private int movey;
	Texture face;

	@Override
	public void create() {

		getLocation();

		manager = new AssetManager();
		manager.setLoader(Pixmap.class, new PixmapLoader(new URLHandle()));
		manager.setErrorListener(this);
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		rotationSpeed = 0.5f;
		face = new Texture(
				new URLHandle(
						"http://cdn1.sbnation.com/profile_images/592671/smiley_face_small.jpg"));
		// MapKeeper mk = new MapKeeper();
		// mk.tex = gm.getTexture(false);
		// mk.texRd = gm.getTexture(true);
		// mk.x = -GoogleMaps.WIDTH / 2;
		// mk.y = -GoogleMaps.HEIGHT / 2;
		// tiles.add(mk);
		// loaded.add(0l);

		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.position.set(0, 0, 0);
		moveCamera(0, 0);
		batch = new SpriteBatch();

		Gdx.input.setInputProcessor(this);

	}

	private void getLocation() {
		URL url=null;
		try {
			url = new URL("http://api.ipinfodb.com/v3/ip-city/?key=c4a286f1ff1e0eba56a611b982696ecbb7c68a175cf92f16a826cebb4b0f8d5f");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		InputStream is=null;
		try {
			is = url.openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int ptr = 0;
		StringBuffer buffer = new StringBuffer();
		try {
			while ((ptr = is.read()) != -1) {
				buffer.append((char)ptr);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String location=buffer.toString();
		String[] data=location.split(";");
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
		gm.lat=Double.parseDouble(data[8]);
		gm.lon=Double.parseDouble(data[9]);
	}

	@Override
	public void render() {
		if (manager.update()) {
			for (Pair p : loading) {
				MapKeeper mk = new MapKeeper();
				mk.x = p.i - GoogleMaps.WIDTH / 2;
				mk.y = p.j - GoogleMaps.HEIGHT / 2;
				mk.tex = getTexture(p.i, p.j, gm.getRoadMapUrl(p.i, p.j));
				mk.texRd = getTexture(p.i, p.j, gm.getSatelliteUrl(p.i, p.j));

				if (mk.tex != null && mk.texRd != null) {
					tiles.add(mk);
					loaded.add(p.ii * 10000000l + p.jj);
				}
			}
			loading.clear();
		}
		handleInput();
		GL20 gl = Gdx.graphics.getGL20();
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		cam.update();
		batch.setProjectionMatrix(cam.combined);

		batch.begin();
		batch.setColor(1f, 1f, 1f, 1f);
		for (MapKeeper mk : tiles) {
			if (mk.tex != null)
				batch.draw(mk.tex, mk.x, mk.y);
		}
		batch.setColor(1f, 1f, 1f, .8f);
		for (MapKeeper mk : tiles) {
			if (mk.texRd != null)
				batch.draw(mk.texRd, mk.x, mk.y);
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
		// TODO Auto-generated method stub
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
			shift(-GoogleMaps.WIDTH, 0);
		}
		if (keycode == 'l' - 68) {
			shift(GoogleMaps.HEIGHT, 0);
		}
		if (keycode == 'k' - 68) {
			shift(0, -GoogleMaps.WIDTH);
		}
		if (keycode == 'i' - 68) {
			shift(0, GoogleMaps.HEIGHT);
		}

		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	private void shift(final int ii, final int jj) {
		int i = ii * GoogleMaps.WIDTH;
		int j = jj * GoogleMaps.HEIGHT;
		String road = gm.getRoadMapUrl(i, j);
		String sat = gm.getSatelliteUrl(i, j);
		if (!loading.contains(new Pair(i, j, ii, jj))) {
			loading.add(new Pair(i, j, ii, jj));
			manager.load(road, Pixmap.class);
			manager.load(sat, Pixmap.class);
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
		int xl = (int) (((cam.position.x - (WIDTH / 2)) / GoogleMaps.WIDTH) - .5);
		int xh = (int) (((cam.position.x + (WIDTH / 2)) / GoogleMaps.WIDTH) + .5);
		int yl = (int) (((cam.position.y - (HEIGHT / 2)) / GoogleMaps.HEIGHT) - .5);
		int yh = (int) (((cam.position.y + (HEIGHT / 2)) / GoogleMaps.HEIGHT + .5));
		for (int i = yl; i <= yh; i++) {
			for (int j = xl; j <= xh; j++) {
				if (!loaded.contains(j * 10000000l + i)) {
					shift(j, i);
				}
			}
		}
	}

	public Texture getTexture(int x, int y, String bb) {
		System.out.println(bb);
		try {
			Pixmap pixmap = manager.get(bb, Pixmap.class);
			Pixmap potPixmap = new Pixmap(gm.WIDTH, gm.HEIGHT,
					pixmap.getFormat());
			potPixmap.drawPixmap(pixmap, 0, 0, gm.WIDTH, gm.HEIGHT, 0, 0,
					gm.WIDTH, gm.HEIGHT);
			TextureRegion nonPotTexture = new TextureRegion(new Texture(
					potPixmap), 0, 0, pixmap.getWidth(), pixmap.getHeight());
			pixmap.dispose();
			potPixmap.dispose();
			return nonPotTexture.getTexture();
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