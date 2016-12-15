package com.klemstinegroup.googlemap;

public class Pair{
	int i,j,ii,jj;

	public Pair(int i,int j,int ii,int jj) {
		this.i=i;
		this.j=j;
		this.ii=ii;
		this.jj=jj;
	}

	@Override public boolean equals(Object b) {
	    //check for self-comparison
	    if ( this == b) return true;
	    Pair a=(Pair)b;
	    if (a.i==i&&a.j==j&& a.ii==ii&& a.jj==jj)return true;
	    return false;
	}
}
