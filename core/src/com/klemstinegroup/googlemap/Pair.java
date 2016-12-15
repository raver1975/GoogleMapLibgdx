package com.klemstinegroup.googlemap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class Pair{
	int i,j,ii,jj;
	Texture blank;

	public Pair(int i,int j,int ii,int jj) {
		this.i=i;
		this.j=j;
		this.ii=ii;
		this.jj=jj;
		blank=getBlank();
	}

	@Override public boolean equals(Object b) {
	    //check for self-comparison
	    if ( this == b) return true;
	    Pair a=(Pair)b;
	    if (a.i==i&&a.j==j&& a.ii==ii&& a.jj==jj)return true;
	    return false;
	}

	static Color[] colors=new Color[]{Color.WHITE,Color.GREEN,Color.RED,Color.BLUE,Color.YELLOW,Color.CYAN,Color.MAGENTA};
	public static Texture getBlank() {
//        if (blank == null) {
		Pixmap pm = new Pixmap(GoogleMaps.WIDTH, GoogleMaps.HEIGHT, Pixmap.Format.RGBA8888);
		pm.setColor(Color.BLACK);
		pm.fill();
		pm.setColor(colors[(int)(Math.random()*colors.length)]);
		for (int i = 0; i < 5; i++) {
			pm.drawRectangle(i, i, GoogleMaps.WIDTH - 2 * i, GoogleMaps.HEIGHT - 2 * i);
		}
		return new Texture(pm);
//        }
//        return blank;
	}
}
