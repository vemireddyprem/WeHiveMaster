
package uk.co.wehive.hive.utils.camera;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Image {

    /**
     * Calculate a sample size value that is a power of two based on a target width and height.
     *
     * @param options   Options that control sampling
     * @param reqWidth
     * @param reqHeight
     * @return Sample size
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int iAltoOriginal = options.outHeight;
        final int iLargoOriginal = options.outWidth;
        int inSampleSize = 1;
        if (iAltoOriginal > reqHeight || iLargoOriginal > reqWidth) {
            final int halfHeight = iAltoOriginal / 2;
            final int halfWidth = iLargoOriginal / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * Change the size of the image.
     *
     * @param res    The resources object containing the image data
     * @param resId  The resource id of the image data
     * @param factor Conversion factor
     * @return Resized bitmap
     */
    public static Bitmap changeSize(Resources res, int resId, float factor) {
        Bitmap tempBitmap;
        Bitmap finalBitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);
            int iWidth = options.outWidth;
            int iHeight = options.outHeight;
            options.inJustDecodeBounds = false;
            tempBitmap = BitmapFactory.decodeResource(res, resId);
            try {
                finalBitmap = Bitmap.createScaledBitmap(tempBitmap, (int) (iWidth / factor), (int) (iHeight / factor), true);
            } catch (OutOfMemoryError e) {
                finalBitmap = null;
            } catch (Exception e) {
                finalBitmap = null;
            }
            tempBitmap.recycle();
        } catch (Exception e) {
        }
        return finalBitmap;
    }

    /**
     * Change the size of the image.
     *
     * @param res    The resources object containing the image data
     * @param resId  The resource id of the image data
     * @param factor Conversion factor
     * @return Resized bitmap
     */
    public static Bitmap changeSize(Resources res, int resId, float factor, Matrix matrix) {
        Bitmap tempBitmap = null;
        Bitmap finalBitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);
            int iWidth = options.outWidth;
            int iHeight = options.outHeight;
            options.inJustDecodeBounds = false;
            tempBitmap = BitmapFactory.decodeResource(res, resId);
            try {
                finalBitmap = Bitmap.createScaledBitmap(tempBitmap, (int) (iWidth / factor), (int) (iHeight / factor), true);
                tempBitmap = Bitmap.createBitmap(finalBitmap, 0, 0, finalBitmap.getWidth(), finalBitmap.getHeight(), matrix, true);
            } catch (Exception e) {
                finalBitmap = null;
                tempBitmap = null;
            }
            finalBitmap.recycle();
        } catch (Exception e) {
        }
        return tempBitmap;
    }

    public static Bitmap changeSize(Resources res, int resId, int maxHeight, int maxWidth) {
        Bitmap tempBitmap;
        Bitmap finalBitmap = null;
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inScaled = false;

            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            tempBitmap = BitmapFactory.decodeResource(res, resId, options);
            try {
                finalBitmap = Bitmap.createScaledBitmap(tempBitmap, maxWidth, maxHeight, true);
            } catch (Exception e) {
                finalBitmap = null;
            }
            tempBitmap.recycle();

        } catch (Exception e) {
        }
        return finalBitmap;
    }

    /**
     * Calculate the conversion factor to sample an image
     *
     * @param res       The resources object containing the image data
     * @param resId     The resource id of the image data
     * @param iMaxWidth Max width of image
     * @return Conversion factor
     */
    public static float conversionFactor(Resources res, int resId, int iMaxWidth) {
        float factor = 1;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        factor = (float) (options.outWidth / (float) iMaxWidth);
        return factor;
    }

    public static int getHeightConvertion(Resources res, int resId, int iMaxWidth) {
        int height = 0;
        float factor = 1;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        factor = (float) (options.outWidth / (float) iMaxWidth);
        height = (int) (options.outHeight / factor);
        return height;
    }

    /**
     * Mapping coordinates from one range to another.
     *
     * @param x
     * @param in_min
     * @param in_max
     * @param out_min
     * @param out_max
     * @return
     */
    public static float map(float x, float in_min, float in_max, float out_min, float out_max) {
        return (((x) - (in_min)) * ((out_max) - (out_min)) / ((in_max) - (in_min)) + (out_min));
    }

    public static Bitmap getRoundedRectBitmap(Bitmap bitmap) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);

            int color = 0xff424242;
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, 200, 200);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(50, 50, 50, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

        } catch (NullPointerException e) {
        } catch (OutOfMemoryError o) {
        }
        return result;
    }

    public static Bitmap getImage(String sUrl, int iMaxHeight, int iMaxWidth) {
        Bitmap bitmapImg = null;
        InputStream input = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            URL url = new URL(sUrl);
            input = url.openStream();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, options);
            options.inSampleSize = calculateInSampleSize(options, iMaxWidth, iMaxHeight);
            options.inJustDecodeBounds = false;
            input.close();
            input = url.openStream();
            bitmapImg = BitmapFactory.decodeStream(input, null, options);
        } catch (Exception e) {
        } finally {
            try {
                input.close();
            } catch (Exception e) {
            }
        }
        return bitmapImg;
    }

    public static Bitmap getUserProfileBitmap(Bitmap bitmap) {
        Bitmap result = null;
        try {
            result = Bitmap.createScaledBitmap(bitmap, 105, 105, true);
        } catch (NullPointerException e) {
        } catch (OutOfMemoryError o) {
        }
        return result;
    }

    public static Bitmap getBitmapServer(String sUrl, int iMaxHeight, int iMaxWidth) {
        Bitmap bitmapImg = null;
        InputStream input = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            URL url = new URL(sUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            input = connection.getInputStream();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, options);
            options.inSampleSize = calculateInSampleSize(options, iMaxHeight, iMaxWidth);
            options.inJustDecodeBounds = false;
            input.close();
            input = url.openStream();
            bitmapImg = BitmapFactory.decodeStream(input, null, options);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (Exception e) {
            }
        }
        return bitmapImg;
    }

    public static Bitmap getBitmap2(String sUrl, int origin) {

        Bitmap mBitmapPhoto = BitmapFactory.decodeFile(sUrl);
        Bitmap result;
        Matrix matrix = new Matrix();

        switch (origin) {
            case 0:
                matrix.setRotate(90);
                break;
            case 1:
                matrix.setRotate(90);
                matrix.preScale(-1, 1);
                break;
        }

        result = Bitmap.createBitmap(mBitmapPhoto, 0, 0, mBitmapPhoto.getWidth(), mBitmapPhoto.getHeight(), matrix, false);
        mBitmapPhoto.recycle();
        matrix.reset();
        return result;
    }

    public static Bitmap loadBitmap(String url) {
        Bitmap bm = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            bm = BitmapFactory.decodeStream(bis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }
}
