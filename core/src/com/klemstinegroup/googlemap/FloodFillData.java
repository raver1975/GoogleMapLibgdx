package com.klemstinegroup.googlemap;

/**
 * Created by Paul on 12/28/2016.
 */
public class FloodFillData {
    public final int gloY;
    public final int gloX;
    int tileX;
    int tileY;
    int x;
    int y;


    public FloodFillData(int gloX, int gloY) {
        this.tileX = gloX / GoogleMapGrabber.SIZE;
        this.tileY = gloY / GoogleMapGrabber.SIZE;
        this.x = x % GoogleMapGrabber.SIZE;
        this.y = y % GoogleMapGrabber.SIZE;
        this.gloX = gloX;
        this.gloY = gloY;
    }


    public double dist(int ogloX, int ogloY) {
        return Math.sqrt((gloX - ogloX) * (gloX - ogloX) + (gloY - ogloY) * (gloY - ogloY));
    }
}
