package com.klemstinegroup.googlemap.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.klemstinegroup.googlemap.GoogleMap;
import com.klemstinegroup.googlemap.GoogleMapLite;

public class DesktopLauncherLite {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new GoogleMapLite(), config);
	}
}
