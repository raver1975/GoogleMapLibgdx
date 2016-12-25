package com.klemstinegroup.googlemap.desktop;

import com.klemstinegroup.googlemap.GoogleMapGrabber;
//import com.klemstinegroup.googlemap.PairLite;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Paul on 12/24/2016.
 */
public class Simple {
    private final String directory = "e:/GoogleMapImages/";
    int tilesToDownLoad = 100;
    GoogleMapGrabber gm = new GoogleMapGrabber();

    public Simple() {

        System.out.println("creating spiral list");
        int x = 0, y = 0, dx = 0, dy = -1;
        int t = 0;

        while (tilesToDownLoad-- > 0) {
            System.out.println(x + "," + y);
            String sat = gm.getSatelliteUrl(x , y );
            String filename = gm.getFileName(x , y );
            try {
                BufferedImage image = ImageIO.read(new URL(sat));
                ImageIO.write(image, "png", new File(directory + filename));
            } catch (IOException e) {
                System.out.println("retrying");
                continue;
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

    public static void main(String[] args) {
        new Simple();
    }


}
