package com.klemstinegroup.googlemap.desktop;

import com.klemstinegroup.googlemap.GoogleMapGrabber;
//import com.klemstinegroup.googlemap.PairLite;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;

/**
 * Created by Paul on 12/24/2016.
 */
public class Simple {
    private final String directory = "e:/GoogleMapImages/";
    //7000 seems max
    int tilesSqRoot=86;
    int tilesToDownLoad = tilesSqRoot*tilesSqRoot;
    GoogleMapGrabber gm = new GoogleMapGrabber();
    ArrayList<Data> datalist = new ArrayList<Data>();

    public Simple() {


        System.out.println("creating spiral list");
        int x = 0, y = 0, dx = 0, dy = -1;
        int t = 0;

        while (tilesToDownLoad-- > 0) {
            String sat = gm.getSatelliteUrl(x, y);
            String filename = gm.getFileName(x, y);
            datalist.add(new Data(sat, filename, x, y));
            if ((x == y) || ((x < 0) && (x == -y)) || ((x > 0) && (x == 1 - y))) {
                t = dx;
                dx = -dy;
                dy = t;
            }
            x += dx;
            y += dy;
        }

        int minx = Integer.MAX_VALUE, miny = Integer.MAX_VALUE, maxx = Integer.MIN_VALUE, maxy = Integer.MIN_VALUE;
        for (Data d : datalist) {
            minx = Math.min(minx, d.tileX);
            miny = Math.min(miny, d.tileY);
            maxx = Math.max(maxx, d.tileX);
            maxy = Math.max(maxy, d.tileY);
        }
        System.out.println(minx + "," + miny + "\t" + maxx + "," + maxy);
        int w = maxx - minx;
        int h = maxy - miny;
        w *= GoogleMapGrabber.SIZE;
        h *= GoogleMapGrabber.SIZE;
        System.out.println("BigPNG=" + w + "," + h + " = " + (w * h * 6l) / (1000000l) + "MB");
        BufferedImage bigPNG = new BufferedImage((int) w, (int) h, BufferedImage.TYPE_INT_RGB);
        Graphics bigPNGGraphics = bigPNG.getGraphics();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        ImagePane imagePane = new ImagePane(bigPNG);
        frame.add(imagePane);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds(0,0,screenSize.width, screenSize.height);
        frame.setVisible(true);

        for (Data d : datalist) {
            if (!new File(directory + d.filename).exists()) {
                try {
                    System.out.println(d.tileX + "," + d.tileY);
                    BufferedImage image = ImageIO.read(new URL(d.sat));
                    BufferedImage image2 = new BufferedImage(GoogleMapGrabber.SIZE, GoogleMapGrabber.SIZE, BufferedImage.TYPE_INT_RGB);
                    Graphics bg = image2.getGraphics();
                    bg.drawImage(image, 0, 0, null);
                    ImageIO.write(image2, "png", new File(directory + d.filename));
                    bigPNGGraphics.drawImage(image2, (w/2-gm.SIZE/2)+d.tileX * gm.SIZE, (h/2-gm.SIZE/2)-d.tileY * gm.SIZE, null);
                    imagePane.repaint();

                } catch (IOException e) {
                    System.out.println("retrying");
                    continue;
                }
            } else {
                try {
                    BufferedImage image2 = ImageIO.read(new File(directory + d.filename));
                    bigPNGGraphics.drawImage(image2, (w/2-gm.SIZE/2)+d.tileX * gm.SIZE, (h/2-gm.SIZE/2)-d.tileY * gm.SIZE, null);
                    imagePane.repaint();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            ImageIO.write(bigPNG, "png", new File(directory + "full.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Simple();
    }


    class ImagePane extends JLabel {

        private final BufferedImage bi;

        public ImagePane(BufferedImage bi) {
            this.bi=bi;
        }

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            g.drawImage(bi,0,0,getWidth(),getHeight(),this);
        }

    }

    class Data {
        String sat;
        String filename;
        int tileX;
        int tileY;

        public Data(String sat, String filename, int tileX, int tileY) {
            this.sat = sat;
            this.filename = filename;
            this.tileX = tileX;
            this.tileY = tileY;
        }
    }
}
