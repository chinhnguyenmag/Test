package com.magrabbit.intelligentmenu.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

public class ProcessBitmap {

	/**
	 * Corner Bitmap
	 * 
	 * @param bitmap
	 * @param radius
	 * @return
	 */
	public static Bitmap roundBitmap(Bitmap bitmap, int radius) {
		if (bitmap == null) {
			return null;
		}

		Paint paintForRound = new Paint();
		paintForRound.setAntiAlias(true);
		paintForRound.setColor(0xff424242);
		paintForRound.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		canvas.drawARGB(0, 0, 0, 0);
		paintForRound.setXfermode(null);

		canvas.drawRoundRect(rectF, radius, radius, paintForRound);

		paintForRound.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paintForRound);

		return output;
	}

	/**
	 * Resize bitmap
	 * 
	 * @param context
	 * @param path
	 * @param uri
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeFile(Context context, Uri uri, int reqWidth,
			int reqHeight) {
		Bitmap bitmap = null;
		String path = null;
		try {
			if (uri != null) {
				path = getRealPathFromURI(context, uri);
			}

			if (path == null || path.length() <= 0) {
				return null;
			}

			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, o);

			// decode with inSampleSize
			o.inSampleSize = calculateInSampleSize(o, null, reqWidth, reqHeight);
			o.inJustDecodeBounds = false;

			bitmap = BitmapFactory.decodeFile(path, o);

			if (bitmap != null) {
				ExifInterface exif = new ExifInterface(path);
				int tag = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
						-1);
				int degree = 0;
				if (tag == ExifInterface.ORIENTATION_ROTATE_90) {
					degree = 90;
				} else if (tag == ExifInterface.ORIENTATION_ROTATE_180) {
					degree = 180;
				} else if (tag == ExifInterface.ORIENTATION_ROTATE_270) {
					degree = 270;
				}

				Bitmap temp = null;
				if (degree != 0) {
					Matrix matrix = new Matrix();
					matrix.setRotate(degree);

					temp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
							bitmap.getHeight(), matrix, true);
				}

				if (temp != null) {
					bitmap.recycle();
					bitmap = null;

					bitmap = temp;
				}
				temp = null;
			}
		} catch (Exception ex) {
			bitmap = null;
		} catch (OutOfMemoryError oome) {
			bitmap = null;
		}

		return bitmap;
	}

	/**
	 * Resize bitmap
	 * 
	 * @param aBitmap
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeFile(Bitmap aBitmap, int reqWidth, int reqHeight) {
		Bitmap bitmap = null;

		try {
			bitmap = aBitmap;
			if (bitmap == null) {
				return bitmap;
			}

			// decode with inSampleSize
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = calculateInSampleSize(null, bitmap,
					reqWidth, reqHeight);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
					byteArray.length, options);

			options = null;
			byteArray = null;
		} catch (Exception ex) {
			bitmap = null;
		} catch (OutOfMemoryError oome) {
			bitmap = null;
		}

		return bitmap;
	}

	/**
	 * Resize bitmap
	 * 
	 * @param File
	 *            f
	 * @param int reqWidth
	 * @param int reqHeight
	 * @return
	 */
	public static Bitmap decodeFile(File f, int reqWidth, int reqHeight) {
		Bitmap bitmap = null;

		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = calculateInSampleSize(o, null, reqWidth,
					reqHeight);
			bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null,
					o2);

			o = null;
			o2 = null;
		} catch (Exception ex) {
			bitmap = null;
		} catch (OutOfMemoryError oome) {
			bitmap = null;
		}

		return bitmap;
	}

	public static Bitmap decodeFile(Resources res, int resId, int reqWidth,
			int reqHeight) {
		Bitmap bitmap = null;

		try {
			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(res, resId, options);

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, null,
					reqWidth, reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeResource(res, resId, options);
		} catch (Exception ex) {
			bitmap = null;
		} catch (OutOfMemoryError oome) {
			bitmap = null;
		}

		return bitmap;
	}

	/**
	 * calculate a the sample size value based on a target width and height:
	 * 
	 * @param options
	 * @param bitmap
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options option,
			Bitmap bitmap, int reqWidth, int reqHeight) {
		int height = 0;
		int width = 0;

		if (option != null) {
			height = option.outWidth;
			width = option.outHeight;
		} else {
			height = bitmap.getHeight();
			width = bitmap.getWidth();
		}

		if (height == 0 || width == 0) {
			return 0;
		}

		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}

	/**
	 * get path from URI
	 * 
	 * @param context
	 * @param contentUri
	 * @return
	 */
	public static String getRealPathFromURI(Context context, Uri contentUri) {
		String result = null;

		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = ((Activity) context).managedQuery(contentUri, proj,
					null, null, null);
			if (cursor != null && !cursor.isClosed()) {
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				result = cursor.getString(column_index);
				if (Integer.parseInt(Build.VERSION.SDK) < 14) {
					cursor.close();
				}
			}
			cursor = null;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
