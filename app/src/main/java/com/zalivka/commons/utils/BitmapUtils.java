package com.zalivka.commons.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class BitmapUtils {

    // util dir for saving temporary data that could be passed between apps and processes
    public static File sUtilDir = new File(Environment.getExternalStorageDirectory(), "fingerpaint"); //StaticContextHolder.mCtx.getDir("fingerpaint", Context.MODE_WORLD_WRITEABLE);
    static  {sUtilDir.mkdirs();}

    public static Bitmap sOnePixel;
    static {
        sOnePixel = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    }

	public static Bitmap flip(Bitmap src) {
		Matrix m = new Matrix();
		m.setScale(1, -1);
		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);
	}
	
	public static boolean same(Bitmap b1, Bitmap b2) {
		return checksum(b1) == checksum(b2);
	}
	
	public static int checksum(Bitmap bm) {
		if (bm == null) {
			return -1;
		}
		int sum = 0;
		int pxs[] = new int[bm.getHeight() * bm.getWidth()];
		bm.getPixels(pxs, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight());
		for (int i : pxs ) {
			sum += i;
		}
		return sum;
	}
	
	/**
	 * 
	 * @param ctx
	 * @param data
	 * @param optimX2 - more optimizing
	 * @return
	 */
    @Deprecated
	public static Bitmap getSafeOptimizedBitmap(Context ctx, Uri data, boolean optimX2, boolean alpha) {
        String realpath = EnvUtils.getRealPathFromURI(data);
        if (TextUtils.isEmpty(realpath)) {
            return null;
        }
		File bitmapFile = new File(realpath);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), options);

		int tmpWidth = options.outWidth;
		int tmpHeight = options.outHeight;
		int sampleSize = 1;

		while (tmpWidth / 2 > 640 || tmpHeight / 2 > 640) {
			tmpWidth /= 2;
			tmpHeight /= 2;
			sampleSize *= 2;
		}
		
		Log.d("getSafeOptimizedBitmap", "SS: "+sampleSize);
		options.inJustDecodeBounds = false;
		options.inSampleSize = sampleSize;
        options.inMutable = true;

        return BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), options);
	}

    @Deprecated
	public static Bitmap getSafeOptimizedBitmap(Context ctx, Uri data, int minDesiredWidth, int minDesiredHeight, boolean alpha) {
        String path = EnvUtils.getRealPathFromURI(data);
        if (TextUtils.isEmpty(path)) {
            return null;
        }
		File bitmapFile = new File(path);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), options);

		int tmpWidth = options.outWidth;
		int tmpHeight = options.outHeight;
		int sampleSize = 1;

		while (tmpWidth / 2 > minDesiredWidth || tmpHeight / 2 > minDesiredHeight) {
			tmpWidth /= 2;
			tmpHeight /= 2;
			sampleSize *= 2;
		}
		
		if (minDesiredWidth > minDesiredHeight && options.outWidth < options.outHeight 
				|| minDesiredWidth < minDesiredHeight && options.outWidth > options.outHeight) {
			sampleSize /= 2;
		}
		
		
		Log.d("getSafeOptimizedBitmap", "SS: "+sampleSize);
		options.inJustDecodeBounds = false;
		options.inSampleSize = sampleSize;
        options.inMutable = true;
        return BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), options);
	}

    public static Bitmap getReasonableBitmap(Uri uri, int minDesiredWidth, int minDesiredHeight, Bitmap.Config config) {
        try {
            InputStream is = StaticContextHolder.mCtx.getContentResolver().openInputStream(uri);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);

            int tmpWidth = options.outWidth;
            int tmpHeight = options.outHeight;
            int sampleSize = 1;

            while (tmpWidth / 2 > minDesiredWidth || tmpHeight / 2 > minDesiredHeight) {
                tmpWidth /= 2;
                tmpHeight /= 2;
                sampleSize *= 2;
            }

            IOUtils.closeQuietly(is);
            is = StaticContextHolder.mCtx.getContentResolver().openInputStream(uri);

            Log.d("getSafeOptimizedBitmap", "SS: "+sampleSize);
            options.inJustDecodeBounds = false;
            options.inSampleSize = sampleSize;
            options.inMutable = true;
            options.inPreferredConfig = config;
            return BitmapFactory.decodeStream(is, null, options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Deprecated
    public static Bitmap getReasonableBitmap(String path, int minDesiredWidth, int minDesiredHeight, Bitmap.Config config) {
        try {
            File bitmapFile = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), options);

            int tmpWidth = options.outWidth;
            int tmpHeight = options.outHeight;
            int sampleSize = 1;

            while (tmpWidth / 2 > minDesiredWidth || tmpHeight / 2 > minDesiredHeight) {
                tmpWidth /= 2;
                tmpHeight /= 2;
                sampleSize *= 2;
            }

            Log.d("getSafeOptimizedBitmap", "SS: "+sampleSize);
            options.inJustDecodeBounds = false;
            options.inSampleSize = sampleSize;
            options.inMutable = true;
            options.inPreferredConfig = config;
            return BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getReasonableBitmapFromAssets(String path, int minDesiredWidth, int minDesiredHeight, Bitmap.Config config) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        try {
            InputStream is = StaticContextHolder.mCtx.getAssets().open(path);
            BitmapFactory.decodeStream(is, null, options);

            int tmpWidth = options.outWidth;
            int tmpHeight = options.outHeight;
            int sampleSize = 1;

            while (tmpWidth / 2 > minDesiredWidth || tmpHeight / 2 > minDesiredHeight) {
                tmpWidth /= 2;
                tmpHeight /= 2;
                sampleSize *= 2;
            }

            Log.d("getSafeOptimizedBitmap", "SS: " + sampleSize);
            options.inJustDecodeBounds = false;
            options.inSampleSize = sampleSize;
            options.inMutable = true;
            options.inPreferredConfig = config;

            is =  StaticContextHolder.mCtx.getAssets().open(path);
            return BitmapFactory.decodeStream(is, null, options);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Converts a immutable bitmap to a mutable bitmap. This operation doesn't allocates
     * more memory that there is already allocated.
     *
     * @param imgIn - Source image. It will be released, and should not be used more
     * @return a copy of imgIn, but muttable.
     */
    public static Bitmap convertToMutable(Bitmap imgIn) {
        try {
            //this is the file going to use temporally to save the bytes.
            // This file will not be a image, it will store the raw image data.
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // get the width and height of the source bitmap.
            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Bitmap.Config type = imgIn.getConfig();

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes()*height);
            imgIn.copyPixelsToBuffer(map);
            //recycle the source bitmap, this will be no longer used.
            imgIn.recycle();
            System.gc();// try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            //load it back from temporary
            imgIn.copyPixelsFromBuffer(map);
            //close the temporary file and channel , then delete that also
            channel.close();
            randomAccessFile.close();

            // delete the temp file
            file.delete();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgIn;
    }

    public static Bitmap rasterizeSvgFromAssets(String assetPath, int sceneWidth, int sceneHeight, int bgColor) {
        try {
            return SVGUtils.rasterSVG(sceneWidth, sceneHeight, assetPath, bgColor, 1f);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap rasterizeSvgFromString(String svgString, int sceneWidth, int sceneHeight, int bgColor) {
        try {
            return SVGUtils.rasterSVGFromString(sceneWidth, sceneHeight, svgString, bgColor, 1f);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getEmbeddedPngBg(String path) {
        try {
            InputStream is =  StaticContextHolder.mCtx.getAssets().open(path);
            Bitmap orig = BitmapFactory.decodeStream(is);
            return orig;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String saveTo(Bitmap bm, File dir, String fname) {
        try {
            File f = new File(dir, fname);
            FileOutputStream fos = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
            return f.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String debugSave(Bitmap bm, String name) {
        try {
            File f = new File(Environment.getExternalStorageDirectory(), name + ".png");
            f.delete();
            FileOutputStream fos = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
            return f.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
