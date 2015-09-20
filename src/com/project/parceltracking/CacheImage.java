package com.project.parceltracking;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class CacheImage {

	private static CacheImage cacheImage = new CacheImage();

	private LruCache<String, Bitmap> mMemoryCache;

	public CacheImage() {

		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@SuppressLint("NewApi")
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}

		};

	}

	public static CacheImage getInstance() {
		return cacheImage;
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

}
