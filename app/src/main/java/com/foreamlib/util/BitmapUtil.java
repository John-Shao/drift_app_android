package com.foreamlib.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.View;

import com.foreamlib.imageloader.GoproDrawableFileCache;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//import com.drew.imaging.ImageMetadataReader;
//import com.drew.imaging.ImageProcessingException;
//import com.drew.metadata.Metadata;
//import com.drew.metadata.exif.ExifThumbnailDirectory;

public class BitmapUtil {

    public static Bitmap bytesToBitmap(byte[] data, Options opts) {
        if (data == null || data.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
    }

    /**
     * ���byte�������Bitmap����
     *
     * @param data byte����
     * @return ��Ӧ��Bitmap����
     */
    public static Bitmap bytesToBitmap(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    /**
     * ˮӡ ����ͼƬ��Ϊˮӡ��
     *
     * @param src       ��Ϊ������Bitmap����
     * @param watermark ��Ϊˮӡ��Bitmap����
     * @param x         ��ʼ��x���
     * @param y         ��ʼ��y���
     * @return ˮӡ��ɵ�Bitmap����
     */
    public static Bitmap waterMarkBitmap(Bitmap src, Bitmap watermark, int x, int y) {
        Bitmap bg = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Canvas canvas = new Canvas(bg);
        canvas.drawBitmap(src, 0, 0, paint);
        canvas.drawBitmap(watermark, x, y, paint);
        canvas.save();
        canvas.restore();
        return bg;
    }

    /**
     * ˮӡ �����ı���Ϊˮӡ��
     *
     * @param src       ��Ϊ������Bitmap����
     * @param watermark ��Ϊˮӡ��String����
     * @param x         ��ʼ��x���
     * @param y         ��ʼ��y���
     * @return ˮӡ��ɵ�Bitmap����
     */
    public static Bitmap waterMarkBitmap(Bitmap src, String watermark, int x, int y) {
        Bitmap bg = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Canvas canvas = new Canvas(bg);
        canvas.drawBitmap(src, 0, 0, paint);
        canvas.drawText(watermark, x, y, paint);
        canvas.save();
        canvas.restore();
        return bg;
    }

    /**
     * ˮӡ �����ı���Ϊˮӡ��
     *
     * @param src       ��Ϊ������Bitmap����
     * @param watermark ��Ϊˮӡ��String����
     * @param color     �ı���ɫ
     * @param x         ��ʼ��x���
     * @param y         ��ʼ��y���
     * @return ˮӡ��ɵ�Bitmap����
     */
    public static Bitmap waterMarkBitmap(Bitmap src, String watermark, int color, int x, int y) {
        Bitmap bg = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        Canvas canvas = new Canvas(bg);
        canvas.drawBitmap(src, 0, 0, paint);
        canvas.drawText(watermark, x, y, paint);
        canvas.save();
        canvas.restore();
        return bg;
    }

    /**
     * ˮӡ �����ı���Ϊˮӡ��
     *
     * @param src       ��Ϊ������Bitmap����
     * @param watermark ��Ϊˮӡ��String����
     * @param color     �ı���ɫ
     * @param textSize  �ı������С
     * @param x         ��ʼ��x���
     * @param y         ��ʼ��y���
     * @return ˮӡ��ɵ�Bitmap����
     */

    public static Bitmap waterMarkBitmap(Bitmap src, String watermark, int color, int textSize, int x, int y) {
        Bitmap bg = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);
        Canvas canvas = new Canvas(bg);
        canvas.drawBitmap(src, 0, 0, paint);
        canvas.drawText(watermark, x, y, paint);
        canvas.save();
        canvas.restore();
        return bg;
    }

    /*
     * ��ͼ��ͼƬ�ı�ע
     */
    public static Drawable waterMarkBitmap(Bitmap src, String watermark) {
        Bitmap bg = Bitmap.createBitmap(src.getWidth(),src.getHeight(), Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(18);
        paint.setAntiAlias(true);
        Canvas canvas = new Canvas(bg);
        canvas.drawBitmap(src, 0, 0, paint);
        canvas.drawText(watermark, src.getWidth()/2, src.getHeight() -30, paint);
        canvas.save();
        canvas.restore();
        return BitmapToDrawable(bg);
    }

    public static Bitmap waterMarkBitmap2(Bitmap src, String watermark) {
        Bitmap bg = Bitmap.createBitmap(src.getWidth(), src.getHeight()+10, Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(18);
        paint.setAntiAlias(true);
        Canvas canvas = new Canvas(bg);
        canvas.drawBitmap(src, 0, 0, paint);
        canvas.drawText(watermark, src.getWidth()/2-5, src.getHeight()-10, paint);
        canvas.save();
        canvas.restore();
        return bg;
    }

    // public static Bitmap rotateBitmap(Context mContext,String pathName, int
    // Angle) {
    // try {
    //
    // Bitmap orBitmap = ImageLoader.get(mContext).getBitmap(pathName, true);
    // Matrix mMatrix = new Matrix();
    // mMatrix.setRotate(Angle);
    // Bitmap temp = Bitmap.createBitmap(orBitmap, 0, 0,
    // orBitmap.getWidth(), orBitmap.getHeight(), mMatrix, true);
    // saveBitmap(temp, pathName);
    // return temp;
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return null;
    // }

    public static void saveBitmap(Bitmap orBitmap, String path) {
        try {

            File file = new File(path);
            if (file.exists())
                file.delete();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            orBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();

        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static void saveBitmap(Bitmap orBitmap, String path, int qulity) {
        try {

            File file = new File(path);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            orBitmap.compress(Bitmap.CompressFormat.JPEG, qulity, bos);
            bos.flush();
            bos.close();

        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * ˮӡ �����ı���Ϊˮӡ��
     *
     * @param src       ��Ϊ������Bitmap����
     * @param watermark ��Ϊˮӡ��String����
     * @param color     �ı���ɫ
     * @param textSize  �ı������С
     * @param Alpha     ͸����
     * @param x         ��ʼ��x���
     * @param y         ��ʼ��y���
     * @return ˮӡ��ɵ�Bitmap����
     */
    public static Bitmap waterMarkBitmap(Bitmap src, String watermark, int color, int textSize, int Alpha, int x, int y) {
        Bitmap bg = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setAlpha(Alpha);
        paint.setColor(color);
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);
        Canvas canvas = new Canvas(bg);
        canvas.drawBitmap(src, 0, 0, paint);
        canvas.drawText(watermark, x, y, paint);
        canvas.save();
        canvas.restore();
        return bg;
    }

    /**
     * ˮӡ �����ı���Ϊˮӡ��
     *
     * @param src       ��Ϊ������Bitmap����
     * @param watermark ��Ϊˮӡ��String����
     * @param paint     ����
     * @param x         ��ʼ��x���
     * @param y         ��ʼ��y���
     * @return
     */
    public static Bitmap waterMarkBitmap(Bitmap src, String watermark, Paint paint, int x, int y) {
        Bitmap bg = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bg);
        canvas.drawBitmap(src, 0, 0, paint);
        canvas.drawText(watermark, x, y, paint);
        canvas.save();
        canvas.restore();
        return bg;
    }

    /**
     * Drawableת��Ϊbitmap
     *
     * @param src Drawable��Դ�ļ�
     * @return
     */
    public static Bitmap DrawableToBitmap(Drawable src) {
        BitmapDrawable bd = (BitmapDrawable) src;
        Bitmap bitmap = bd.getBitmap();
        return bitmap;
    }

    /**
     * bitmapת��ΪDrawable
     *
     * @param src
     * @return
     */
    public static Drawable BitmapToDrawable(Bitmap src) {
        BitmapDrawable bd = new BitmapDrawable(src);
        return bd;
    }

    /**
     * ͨ��ID�õ�Drawable
     *
     * @param context
     * @param id
     * @return
     */
    public static Drawable getDrawableById(Context context, int id) {
        Drawable drawable = context.getResources().getDrawable(id);
        return drawable;
    }

    /**
     * ���ID�õ�BitMap����
     *
     * @param context
     * @param id
     * @return
     */
    public static Bitmap getBitmapById(Context context, int id) {
        return DrawableToBitmap(getDrawableById(context, id));
    }

    /**
     * ѹ��ͼƬ
     *
     * @param src    ԭʼͼƬ
     * @param scaleX X�����ѹ������
     * @param scaleY Y�����ѹ������
     * @return ѹ������ͼƬ
     */
    public static Bitmap smallBitmap(Bitmap src, float scaleX, float scaleY) {
        try {
            int bmpWidth = src.getWidth();
            int bmpHeight = src.getHeight();
            Matrix matrix = new Matrix();
            matrix.postScale(scaleX, scaleY);
            Bitmap bmp = Bitmap.createBitmap(src, 0, 0, bmpWidth, bmpHeight, matrix, true);
            return bmp;
        } catch (NullPointerException e) {
            // TODO: handle exception
        }
        return src;
    }

    /**
     * ѹ��ͼƬ
     *
     * @param src   ԭʼͼƬ
     * @param scale X��Y�����ѹ������
     * @return ѹ������ͼƬ
     */
    public static Bitmap smallBitmap(Bitmap src, float scale) {
        try {
            int bmpWidth = src.getWidth();
            int bmpHeight = src.getHeight();
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap bmp = Bitmap.createBitmap(src, 0, 0, bmpWidth, bmpHeight, matrix, true);
            return bmp;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return src;
    }

    /**
     * Զ������ͼƬ
     *
     * @param urlStr ͼƬ��ַ
     * @return ���غ��ͼƬ
     */

    public static Bitmap downLoadBitmap(String urlStr) {

        try {
            Bitmap bmp = GoproDrawableFileCache.getFileCache(urlStr);
            if (bmp != null)
                return bmp;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setConnectTimeout(10 * 1000);
            conn.setRequestMethod("GET");
            InputStream stream = conn.getInputStream();
            bmp = BitmapFactory.decodeStream(stream);
            stream.close();
            GoproDrawableFileCache.addFileCache(bmp, urlStr);
            return bmp;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Զ������ͼƬ�������Ǹ������ѹ������ͼƬ
     *
     * @param urlStr  ͼƬURL��ַ
     * @param xPixels x������������
     * @param yPixels y������������
     * @return
     */
    public static Bitmap downLoadBitmap(String urlStr, int xPixels, int yPixels) {

        try {
            Bitmap bitmap = null;
            byte[] imageByte = HttpConnection.getRemoteImageBytes(urlStr);
            Options options = new Options();
            options.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length, options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = 2; // ���ű���
            try {
                bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length, options);
            } catch (OutOfMemoryError err) {
                err.printStackTrace();
            }
            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    /**
     * ���߿ռ�ı���ͼƬ��Դ
     *
     * @param v view����
     * @return bitmapͼ�����
     */
    public static Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }

    /**
     * ��ͼƬ��������ˮӡ
     *
     * @param src ԭʼͼƬ��Դ
     * @param str ��Ҫ��ӵ�������Դ
     * @return ��ӹ���ͼƬ
     */
    public static Bitmap waterMarkBitmapWithStr(Bitmap src, String str) {

        try {
            Bitmap bmpTemp = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(bmpTemp);
            Paint p = new Paint();
            Rect src1 = new Rect(0, 0, src.getWidth(), src.getHeight());// ����һ��ָ�����¾��ε����
            Rect dst = new Rect(0, 0, src.getWidth(), src.getHeight());// ����һ��ָ�����¾��ε����
            p.setDither(true); // ��ȡ�������ͼ�����
            p.setFilterBitmap(true);// ����һЩ
            String familyName = "����";
            Typeface font = Typeface.create(familyName, Typeface.BOLD_ITALIC);
            p.setColor(Color.WHITE);
            p.setTypeface(font);
            p.setTextSize(16);
            canvas.drawBitmap(src, src1, dst, p);
            canvas.drawText(str, 2, src.getHeight() - 15, p);
            canvas.save();
            canvas.restore();
            return bmpTemp;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return src;
    }

    /**
     * ��ͼƬ��������ˮӡ
     *
     * @param src  ԭʼͼƬ��Դ
     * @param str  ��Ҫ��ӵ�������Դ
     * @param font ���ֵ�����
     * @return ��ӹ���ͼƬ
     */
    public static Bitmap waterMarkBitmapWithStr(Bitmap src, String str, Typeface font) {
        try {
            Bitmap bmpTemp = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(bmpTemp);
            Paint p = new Paint();
            Rect src1 = new Rect(0, 0, src.getWidth(), src.getHeight());// ����һ��ָ�����¾��ε����
            Rect dst = new Rect(0, 0, src.getWidth(), src.getHeight());// ����һ��ָ�����¾��ε����
            p.setDither(true); // ��ȡ�������ͼ�����
            p.setFilterBitmap(true);// ����һЩ
            p.setColor(Color.WHITE);
            p.setTypeface(font);
            p.setTextSize(16);
            canvas.drawBitmap(src, src1, dst, p);
            canvas.drawText(str, 2, src.getHeight() - 15, p);
            canvas.save();
            canvas.restore();
            return bmpTemp;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return src;
    }

    /**
     * ����ʡ�ڴ�ķ�ʽ��ȡ������Դ��ͼƬ
     *
     * @param context
     * @param resId   ��ԴID��
     * @return
     */
    public static Bitmap readLocalBitmap(Context context, int resId) {
        Options opts = new Options();
        opts.inPreferredConfig = Config.RGB_565;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        // ��ȡ��ԴͼƬ
        InputStream inStream = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(inStream, null, opts);
    }

    /**
     * ����ʡ�ڴ�ķ�ʽ��ȡ������Դ��ͼƬ
     *
     * @param path �ļ���·�������ǲ���SD����·��
     * @return
     */
    public static Bitmap readLocalImage(String path) {
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                Options opts = new Options();
                opts.inPreferredConfig = Config.RGB_565;
                opts.inPurgeable = true;
                opts.inInputShareable = true;
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + path);
                InputStream inStream = new FileInputStream(file);
                return BitmapFactory.decodeStream(inStream, null, opts);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ����ʡ�ڴ�ķ�ʽ��ȡ������Դ��ͼƬ
     *
     * @param path �ļ���·��
     * @return
     */
    public static Bitmap readLocalImage2(String path) {
        try {
            Options opts = new Options();
            opts.inPreferredConfig = Config.RGB_565;
            opts.inPurgeable = true;
            opts.inInputShareable = true;
            File file = new File(path);
            InputStream inStream = new FileInputStream(file);
            return BitmapFactory.decodeStream(inStream, null, opts);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ����ԴͼƬתΪ�Ҷ�ͼ
     *
     * @param ��ԴID
     * @return
     */
    public static Bitmap ConvertGrayImg(Bitmap bmp) {

        int w = bmp.getWidth(), h = bmp.getHeight();
        int[] pix = new int[w * h];
        bmp.getPixels(pix, 0, w, 0, 0, w, h);

        int alpha = 0xFF << 24;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                // ������ص���ɫ
                int color = pix[w * i + j];
                int red = ((color & 0x00FF0000) >> 16);
                int green = ((color & 0x0000FF00) >> 8);
                int blue = color & 0x000000FF;
                color = (red + green + blue) / 3;
                color = alpha | (color << 16) | (color << 8) | color;
                pix[w * i + j] = color;
            }
        }
        Bitmap result = Bitmap.createBitmap(w, h, Config.RGB_565);
        result.setPixels(pix, 0, w, 0, 0, w, h);
        return result;
    }

    // ��ʽ��ͼƬ��С
    public static Bitmap zoomBitmap(Bitmap bmp, int width, int height) {
        Bitmap result = null;
        if (bmp != null) {
            result = Bitmap.createScaledBitmap(bmp, width, height, false);
        } else {
        }
        return result;
    }

    public static Bitmap cutBitmap(Bitmap bmp, int st_x, int st_y, int width, int height) {
        Bitmap result = null;
        if (bmp != null) {
            result = Bitmap.createBitmap(bmp, st_x, st_y, width, height);
        } else {
        }
        return result;
    }

    public static byte[] bitmapToBytes(Bitmap src, Bitmap.CompressFormat format) {
        if (src == null) {
            return null;
        }
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        src.compress(format, 100, outStream);
        return outStream.toByteArray();
    }

    public static Bitmap loadBitmap(String path) {
        Options opts = new Options();
        opts.inPreferredConfig = Config.RGB_565;
        opts.inPurgeable = true;
        opts.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(path, opts);
        opts.inJustDecodeBounds = false;
        int be = (int) (opts.outHeight / (float) 720);
        if (be <= 0)
            be = 1;
        opts.inSampleSize = be;
        return BitmapFactory.decodeFile(path, opts);
    }

    public static Bitmap loadBitmap(String path, int width) {
        Options opts = new Options();
        opts.inPreferredConfig = Config.RGB_565;
        opts.inPurgeable = true;
        opts.inJustDecodeBounds = true;
        opts.inSampleSize = 10;
        //Bitmap bmp = BitmapFactory.decodeFile(path, opts);
        opts.inJustDecodeBounds = false;
        int be = (int) (opts.outHeight / (float) width);
        if (be <= 0)
            be = 1;
        opts.inSampleSize = be;
        return BitmapFactory.decodeFile(path, opts);
    }

    public static Bitmap getThumbnailFromMetaData(URL url) {
        // if (url != null && !"".equals(url)) {
        // byte[] imgArray = null;
        // Bitmap bitmap = null;
        // HttpURLConnection conn;
        // try {
        // conn = (HttpURLConnection) url.openConnection();
        //
        // conn.setDoInput(true);
        // conn.setConnectTimeout(10000);
        // conn.setRequestMethod("GET");
        // InputStream stream = conn.getInputStream();
        // BufferedInputStream buffInput = new BufferedInputStream(stream,
        // 1024 * 30);
        // Metadata reader = ImageMetadataReader.readMetadata(buffInput,
        // true);
        // ExifThumbnailDirectory thumb = reader
        // .getDirectory(ExifThumbnailDirectory.class);
        // if (thumb != null) {
        // imgArray = thumb.getThumbnailData();
        // }
        // BitmapFactory.Options opts = new BitmapFactory.Options();
        // opts.inPreferredConfig = Bitmap.Config.RGB_565;
        // opts.inPurgeable = true;
        // opts.inInputShareable = true;
        // if (imgArray != null) {
        // bitmap = BitmapFactory.decodeByteArray(imgArray, 0,
        // imgArray.length, opts);
        // }
        // stream.close();
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (ImageProcessingException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // return bitmap;
        //
        // }
        return null;
    }

    /**
     * 给图片添加圆角
     *
     * @param image          源图片
     * @param outerRadiusRat 圆角的大小。当为-1时，椭圆形.
     * @return 圆角图片
     */
    public static Bitmap createFramedPhoto(Bitmap image, int outerRadiusRat) {
        if (image == null) return null;
        int x = image.getWidth();
        int y = image.getHeight();
        // 根据源文件新建一个darwable对象
        Drawable imageDrawable = new BitmapDrawable(image);

        // 新建一个新的输出图片
        Bitmap output = Bitmap.createBitmap(x, y, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        // 新建一个矩形
        RectF outerRect = new RectF(0, 0, x, y);

        // 产生一个红色的圆角矩形
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        if (outerRadiusRat == -1) {
            canvas.drawOval(outerRect, paint);
        } else if (outerRadiusRat == -2) {
            RectF patchRect = new RectF(outerRect.left, outerRect.top, outerRect.right, 6 * 2);
            canvas.drawRect(patchRect, paint);
            canvas.drawRoundRect(outerRect, 6, 6, paint);
        } else {
            canvas.drawRoundRect(outerRect, outerRadiusRat, outerRadiusRat, paint);
        }
        // 将源图片绘制到这个圆角矩形上

        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        imageDrawable.setBounds(0, 0, x, y);
        canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG);
        imageDrawable.draw(canvas);
        canvas.restore();
        return output;
    }

    public static Bitmap getBitmapFromView(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap bitmap = view.getDrawingCache(true);
        view.destroyDrawingCache();
        return bitmap;
    }

    /**
     * 带shadow的图
     *
     * @param originalBitmap
     * @return
     */
    public static Bitmap createShadowBitmap(Bitmap originalBitmap) {
        BlurMaskFilter blurFilter = new BlurMaskFilter(5, BlurMaskFilter.Blur.OUTER);
        Paint shadowPaint = new Paint();
        shadowPaint.setMaskFilter(blurFilter);

        int[] offsetXY = new int[2];
//	    offsetXY[0] = 10;
//	    offsetXY[1] = 10;
        Bitmap shadowImage = originalBitmap.extractAlpha(shadowPaint, offsetXY);

	    /* Need to convert shadowImage from 8-bit to ARGB here. */
        Bitmap shadowImage32 = shadowImage.copy(Config.ARGB_8888, true);
        Canvas c = new Canvas(shadowImage32);
        c.drawBitmap(originalBitmap, -offsetXY[0], -offsetXY[1], null);

        return shadowImage32;
    }

    public static Bitmap loadBitmap(String path, boolean i) {
        Options opts = new Options();
        opts.inPreferredConfig = Config.ARGB_4444;
        opts.inPurgeable = true;
        opts.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(path, opts);
        opts.inJustDecodeBounds = false;
        int be = (int) (opts.outHeight / (float) 360);
        int be2 = (int) (opts.outWidth / (float) 640);
        if (be2 > be) be = be2;
        if (be <= 0)
            be = 1;
        opts.inSampleSize = be;
        return BitmapFactory.decodeFile(path, opts);
    }

    public static Bitmap drawTextToBitmap(Context gContext,
                                          int gResId,
                                          String gText) {
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap =
                BitmapFactory.decodeResource(resources, gResId);

        Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(61, 61, 61));
        // text size in pixels
        paint.setTextSize((int) (14 * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() + bounds.height()) / 2;
        canvas.drawText(gText, x * scale, y * scale, paint);
        return bitmap;
    }
}
