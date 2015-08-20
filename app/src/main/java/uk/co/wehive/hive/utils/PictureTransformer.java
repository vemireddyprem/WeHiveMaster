package uk.co.wehive.hive.utils;

import android.graphics.*;

import com.squareup.picasso.Transformation;

public class PictureTransformer implements Transformation {

    @Override
    public Bitmap transform(Bitmap source) {

        //Old code for the different profile pic shape.

//        int targetWidth = source.getWidth();
//        int targetHeight = source.getHeight();
//        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(targetBitmap);
//
//        Path path = new Path();
//        float stdW = Math.min(targetWidth, targetHeight);
//        float stdH = Math.min(targetWidth, targetHeight);
//        float w3 = stdW / 2;
//        float h2 = stdH / 2;
//
//
//
//path.moveTo(0, (float) (h2 * Math.sqrt(3) / 2));
//path.rLineTo(w3 / 2, -(float) (h2 * Math.sqrt(3) / 2));
//path.rLineTo(w3, 0);
//path.rLineTo(w3 / 2, (float) (h2 * Math.sqrt(3) / 2));
//path.rLineTo(-w3 / 2, (float) (h2 * Math.sqrt(3) / 2));
//path.rLineTo(-w3, 0);
//path.rLineTo(-w3 / 2, -(float) (h2 * Math.sqrt(3) / 2));
//
//canvas.clipPath(path);
//
//
//        canvas.drawBitmap(source, new Rect(0, 0, source.getWidth(), source.getHeight()),
//                new Rect(0, 0, targetWidth, targetHeight), null);
//        source.recycle();
//        return targetBitmap;
//    }
//

        //Code for circular image. Modified by PREM
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size/2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}
