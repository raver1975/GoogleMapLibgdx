package com.klemstinegroup.googlemap;

import java.io.File;

public class GoogleMapGrabber {

    //7000 seems max
    //86 with 12G heap
    static int tilesSqRoot = 86;
    public static String key = "AIzaSyAsj-GadPbVpK0-G-HxHxbcPKuSLUme5xE";
    public double lat = 44.253653172740925;//31.778 ;
    public double lon = -88.4055233001709;//35.2354;
    public int zoom = 18;
    public String name = "Appleton";//"Jerusalem";
    public static String directory = null;

    static {
        if (directory != null && !directory.endsWith(File.separator)) directory += File.separator;
    }

    public static int SIZE = 512;
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
        int x1 = x * SIZE;
        int y1 = y * SIZE;
        double templon = adjustLonByPixels(lon, x1, zoom);
        double templat = adjustLatByPixels(lat, -y1, zoom);
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
                + SIZE
                + "x"
                + (SIZE + 25)
                + "&"
                + "maptype=roadmap&"
                + "style=feature:road|element:geometry|color:0xffffff&"
                + "style=feature:landscape.man_made|element:geometry|color:0xff0000&"
                + "style=feature:landscape.natural|element:geometry|color:0x00ff00&"
                + "style=feature:administrative|element:geometry|lightness:-100&"
                + "style=feature:poi|element:geometry|color:0xff00ff&&"
                + "style=feature:transit|element:geometry|color:0xffffff&"
                + "style=feature:water|element:geometry|color:0x0000ff&"
                + "style=feature:all|element:labels|visibility:off&"
                + "key=" + key;
    }

    public String getSatelliteUrl(int x, int y) {
        int x1 = x * SIZE;
        int y1 = y * SIZE;
        double templon = adjustLonByPixels(lon, x1, zoom);
        double templat = adjustLatByPixels(lat, -y1, zoom);
        return "http://maps.googleapis.com/maps/api/staticmap?" + "center="
                + templat + "," + templon + "&" + "zoom=" + zoom + "&"
                + "format=png32&" + "sensor=false&" + "size=" + SIZE + "x"
                + (SIZE + 25) + "&" + "maptype=satellite&key=" + key;
    }


    public String getFileNameSat(int x, int y) {
        int x1 = x * SIZE;
        int y1 = y * SIZE;
        double templon = adjustLonByPixels(lon, x1, zoom);
        double templat = adjustLatByPixels(lat, -y1, zoom);
        return "tile_" + name + "_" + zoom + "_" + templat + "_" + templon + "_" + x + "_" + y + ".png";
    }

    public String getFileNameData(int x, int y) {
        int x1 = x * SIZE;
        int y1 = y * SIZE;
        double templon = adjustLonByPixels(lon, x1, zoom);
        double templat = adjustLatByPixels(lat, -y1, zoom);
        return "tile_" + name + "RD_" + zoom + "_" + templat + "_" + templon + "_" + x + "_" + y + ".png";
    }
}
