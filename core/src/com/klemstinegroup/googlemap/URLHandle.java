package com.klemstinegroup.googlemap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class URLHandle extends FileHandle implements FileHandleResolver {
	final URL url;
	String name;

	public URLHandle() {
		url = null;
	}

	public URLHandle(String url) {
		name=url;
		try {
			this.url = new URL(url);
		} catch (Exception e) {
			throw new GdxRuntimeException("Couldn't create URLHandle for '"
					+ url + "'", e);
		}
		this.type = FileType.External;
		this.file = new File(url);
	}

	@Override
	public FileHandle child(String name) {
		return null;
	}

	@Override
	public FileHandle parent() {
		return null;
	}

	@Override
	public String toString() {
		return name;
	}

	public InputStream read() {
		try {
			return url.openStream();
		} catch (IOException e) {
			throw new GdxRuntimeException("Couldn't read URL '"
					+ url.toString() + "'");
		}
	}

	@Override
	public FileHandle resolve(String fileName) {
		//System.out.println(fileName);
		return new URLHandle(fileName);
	}
}