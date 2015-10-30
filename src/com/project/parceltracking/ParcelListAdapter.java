package com.project.parceltracking;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ParcelListAdapter extends ArrayAdapter<ParcelItem> {

	private ArrayList<ParcelItem> objects;

	ImageDownloader imagedownloader = new ImageDownloader();

	CacheImage ci = CacheImage.getInstance();

	public ParcelListAdapter(Context context, int textViewResourceId,
			ArrayList<ParcelItem> objects) {

		super(context, textViewResourceId, objects);
		this.objects = objects;
	}

	public View getView(int position, View v, ViewGroup parent) {

		ViewHolder holder;

		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.parcel_list_item, null);

			holder = new ViewHolder();

			holder.parcelName = (TextView) v.findViewById(R.id.parcelName);
			holder.parcelImage = (ImageView) v.findViewById(R.id.parcelImage);
			holder.parcelPrice = (TextView) v.findViewById(R.id.parcelPrice);

			v.setTag(holder);
		} else {

			holder = (ViewHolder) v.getTag();

		}

		ParcelItem i = objects.get(position);

		if (i != null) {

			holder.parcelName.setText(i.getParcelName());

			holder.parcelPrice.setText("₹" + i.getParcelPrice());

			final Bitmap bitmap = ci
					.getBitmapFromMemCache(i.getParcelId() + "");
			if (bitmap != null) {

				Log.d("ParcelListAdapter",
						"Getting from cache image " + i.getParcelId());
				holder.parcelImage.setImageBitmap(bitmap);
			} else {

				imagedownloader.download(i.getParcelImage(),
						holder.parcelImage, i.getParcelId() + "");

			}
		}

		return v;

	}

	static class ViewHolder {

		TextView parcelName;
		ImageView parcelImage;
		TextView parcelPrice;

	}

}
