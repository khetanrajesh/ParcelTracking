package com.project.parceltracking;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import com.project.parceltracking.ImageDownloader.DownloadedDrawable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class ImageDownloader {

	public void download(String url, ImageView imageView, String key) {
		if (cancelPotentialDownload(url, imageView)) {
			BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
			DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
			imageView.setImageDrawable(downloadedDrawable);
			String params[] = { url, key };
			task.execute(params);
		}
	}

	static class DownloadedDrawable extends ColorDrawable {
		private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

		public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
			super(Color.BLACK);
			bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(
					bitmapDownloaderTask);
		}

		public BitmapDownloaderTask getBitmapDownloaderTask() {
			return bitmapDownloaderTaskReference.get();
		}
	}

	private static boolean cancelPotentialDownload(String url,
			ImageView imageView) {
		BitmapDownloaderTask bitmapDownloaderTask = BitmapDownloaderTask
				.getBitmapDownloaderTask(imageView);

		if (bitmapDownloaderTask != null) {
			String bitmapUrl = bitmapDownloaderTask.url;
			if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
				bitmapDownloaderTask.cancel(true);
			} else {
				// The same URL is already being downloaded.
				return false;
			}
		}
		return true;
	}

}

class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
	String url;
	private final WeakReference<ImageView> imageViewReference;

	static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
		if (imageView != null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof DownloadedDrawable) {
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}

	public BitmapDownloaderTask(ImageView imageView) {
		imageViewReference = new WeakReference<ImageView>(imageView);
	}

	@Override
	// Actual download method, run in the task thread
	protected Bitmap doInBackground(String... params) {
		// params comes from the execute() call: params[0] is the url.

		Bitmap bitmap = downloadBitmap(params[0]);
		CacheImage ci = CacheImage.getInstance();
		ci.addBitmapToMemoryCache(String.valueOf(params[1]), bitmap);
		return bitmap;
	}

	@Override
	// Once the image is downloaded, associates it to the imageView
	protected void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
			bitmap = null;
		}

		if (imageViewReference != null) {
			ImageView imageView = imageViewReference.get();
			BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
			// Change bitmap only if this process is still associated with it
			if (this == bitmapDownloaderTask) {
				imageView.setImageBitmap(bitmap);

			}
		}
	}

	static Bitmap downloadBitmap(String url) {

		try {
			URL urlConnection = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlConnection
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			
			Log.d("height of bimap", " " + myBitmap.getHeight());
			Log.d("width of bimap", " " + myBitmap.getWidth());
			myBitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream);
			Log.d("height of bimap", " " + myBitmap.getHeight());
			Log.d("width of bimap", " " + myBitmap.getWidth());

			byte[] byteArray = stream.toByteArray();

			stream.close();
			stream = null;
			return myBitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int byt = read();
					if (byt < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

}
