package com.klemstinegroup.googlemap;

public class GoogleMaps {

	public static int WIDTH = 512;
	public static int HEIGHT = 512;

	public double lat = 44.253653;
	public double lon = -88.405523;
	public int zoom = 16;

	static final double GOOGLEOFFSET = 268435456;
	static final double GOOGLEOFFSET_RADIUS = GOOGLEOFFSET / Math.PI;
	static final double MATHPI_180 = Math.PI / 180;

	static private final double preLonToX1 = GOOGLEOFFSET_RADIUS * MATHPI_180;

	public final static double LonToX(double lon) {
		return Math.round(GOOGLEOFFSET + preLonToX1 * lon);
	}

	public final static double LatToY(double lat) {
		return Math.round(GOOGLEOFFSET
				- GOOGLEOFFSET_RADIUS
				* Math.log((1 + Math.sin(lat * MATHPI_180))
						/ (1 - Math.sin(lat * MATHPI_180))) / 2);
	}

	public final static double XToLon(double x) {
		return ((Math.round(x) - GOOGLEOFFSET) / GOOGLEOFFSET_RADIUS) * 180
				/ Math.PI;
	}

	public final static double YToLat(double y) {
		return (Math.PI / 2 - 2 * Math
				.atan(Math.exp((Math.round(y) - GOOGLEOFFSET)
						/ GOOGLEOFFSET_RADIUS)))
				* 180 / Math.PI;
	}

	public final static double adjustLonByPixels(double lon, int delta, int zoom) {
		return XToLon(LonToX(lon) + (delta << (21 - zoom)));
	}

	public final static double adjustLatByPixels(double lat, int delta, int zoom) {
		return YToLat(LatToY(lat) + (delta << (21 - zoom)));
	}

	public String getRoadMapUrl(int x, int y) {
		double templon = adjustLonByPixels(lon, x, zoom);
		double templat = adjustLatByPixels(lat, -y, zoom);
		return "http://maps.googleapis.com/maps/api/staticmap?" + "center="
				+ templat
				+ ","
				+ templon
				+ "&"
				+ "zoom="
				+ zoom
				+ "&"
				+ "format=png32&"
				+ "sensor=false&"
				+ "size="
				+ WIDTH
				+ "x"
				+ (HEIGHT + 25)
				+ "&"
				+ "maptype=roadmap&"
				+"style=feature:road|element:geometry|color:0xffffff&"
				+"style=feature:landscape.man_made|element:geometry|color:0xff0000&"
				+"style=feature:landscape.natural|element:geometry|color:0x00ff00&"
				+"style=feature:administrative|element:geometry|lightness:-100&"
				+"style=feature:poi|element:geometry|color:0xff00ff&&"
				+"style=feature:transit|element:geometry|lightness:-100&"
				+"style=feature:water|element:geometry|color:0x0000ff&"
				+"style=feature:all|element:labels|visibility:off&key=AIzaSyAsj-GadPbVpK0-G-HxHxbcPKuSLUme5xE";
	}

	String getSatelliteUrl(int x,int y) {
		double templon = adjustLonByPixels(lon, x, zoom);
		double templat = adjustLatByPixels(lat, -y, zoom);
		return "http://maps.googleapis.com/maps/api/staticmap?" + "center="
				+ templat + "," + templon + "&" + "zoom=" + zoom + "&"
				+ "format=png32&" + "sensor=false&" + "size=" + WIDTH + "x"
				+ (HEIGHT + 25) + "&" + "maptype=satellite&key=AIzaSyAsj-GadPbVpK0-G-HxHxbcPKuSLUme5xE";
	}

	
}
