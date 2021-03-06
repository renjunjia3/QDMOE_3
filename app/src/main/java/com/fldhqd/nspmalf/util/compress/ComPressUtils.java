package com.fldhqd.nspmalf.util.compress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ComPressUtils {

    public static final int FIRST_GEAR = 1;
    public static final int THIRD_GEAR = 3;

    private static final String TAG = "ComPressUtils";
    public static String DEFAULT_DISK_CACHE_DIR = "/image/cache";

    private static volatile ComPressUtils INSTANCE;

    private final File mCacheDir;

    private OnCompressListener compressListener;
    private File mFile;
    private int gear = THIRD_GEAR;

    ComPressUtils(File cacheDir) {
        mCacheDir = cacheDir;
    }

    /**
     * Returns a directory with a default name in the private cache directory of the application to use to store
     * retrieved media and thumbnails.
     *
     * @param context A context.
     * @see #getPhotoCacheDir(Context, String)
     */
    public static File getPhotoCacheDir(Context context) {
        return getPhotoCacheDir(context, ComPressUtils.DEFAULT_DISK_CACHE_DIR);
    }

    /**
     * Returns a directory with the given name in the private cache directory of the application to use to store
     * retrieved media and thumbnails.
     *
     * @param context   A context.
     * @param cacheName The name of the subdirectory in which to store the cache.
     * @see #getPhotoCacheDir(Context)
     */
    public static File getPhotoCacheDir(Context context, String cacheName) {
        File cacheDir = context.getCacheDir();
        if (cacheDir != null) {
            File result = new File(cacheDir, cacheName);
            if (!result.mkdirs() && (!result.exists() || !result.isDirectory())) {
                // File wasn't able to create a directory, or the result exists but not a directory
                return null;
            }
            return result;
        }
        if (Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, "default disk cache dir is null");
        }
        return null;
    }

    public static ComPressUtils get(Context context) {
        if (INSTANCE == null) INSTANCE = new ComPressUtils(ComPressUtils.getPhotoCacheDir(context));
        return INSTANCE;
    }

    public ComPressUtils launch() {
        Preconditions.checkNotNull(mFile, "the image file cannot be null, please call .load() before this method!");

        if (gear == ComPressUtils.FIRST_GEAR)
            firstCompress(mFile);
        else if (gear == ComPressUtils.THIRD_GEAR)
            thirdCompress(mFile.getAbsolutePath());

        return this;
    }

    public ComPressUtils load(File file) {
        mFile = file;
        return this;
    }

    public ComPressUtils setCompressListener(OnCompressListener listener) {
        compressListener = listener;
        return this;
    }

    public ComPressUtils putGear(int gear) {
        this.gear = gear;
        return this;
    }

    private void thirdCompress(@NonNull String filePath) {
        String thumb = mCacheDir.getAbsolutePath() + "/" + System.currentTimeMillis();

        double s;

        int a = getImageSpinAngle(filePath);
        int i = getImageSize(filePath)[0];
        int j = getImageSize(filePath)[1];
        int k = i % 2 == 1 ? i + 1 : i;
        int l = j % 2 == 1 ? j + 1 : j;

        i = k > l ? l : k;
        j = k > l ? k : l;

        double c = ((double) i / j);

        if (c <= 1 && c > 0.5625) {
            if (j < 1664) {
                s = (i * j) / Math.pow(1664, 2) * 150;
                s = s < 60 ? 60 : s;
            } else if (j >= 1664 && j < 4990) {
                k = i / 2;
                l = j / 2;
                s = (k * l) / Math.pow(2495, 2) * 300;
                s = s < 60 ? 60 : s;
            } else if (j >= 4990 && j < 10240) {
                k = i / 4;
                l = j / 4;
                s = (k * l) / Math.pow(2560, 2) * 300;
                s = s < 100 ? 100 : s;
            } else {
                int multiple = j / 1280;
                k = i / multiple;
                l = j / multiple;
                s = (k * l) / Math.pow(2560, 2) * 300;
                s = s < 100 ? 100 : s;
            }
        } else if (c <= 0.5625 && c > 0.5) {
            int multiple = j / 1280;
            k = i / multiple;
            l = j / multiple;
            s = (k * l) / (1440.0 * 2560.0) * 200;
            s = s < 100 ? 100 : s;
        } else {
            int multiple = (int) Math.ceil(j / (1280.0 / c));
            k = i / multiple;
            l = j / multiple;
            s = ((k * l) / (1280.0 * (1280 / c))) * 500;
            s = s < 100 ? 100 : s;
        }

        compress(filePath, thumb, k, l, a, (long) s);
    }

    private void firstCompress(@NonNull File file) {
        int minSize = 60;
        int longSide = 720;
        int shortSide = 1280;

        String filePath = file.getAbsolutePath();
        String thumbFilePath = mCacheDir.getAbsolutePath() + "/" + System.currentTimeMillis();
        long size = 0;
        long maxSize = file.length() / 5;

        int angle = getImageSpinAngle(filePath);
        int[] imgSize = getImageSize(filePath);
        int width = 0, height = 0;
        if (imgSize[0] <= imgSize[1]) {
            double scale = (double) imgSize[0] / (double) imgSize[1];
            if (scale <= 1.0 && scale > 0.5625) {
                width = imgSize[0] > shortSide ? shortSide : imgSize[0];
                height = width * imgSize[1] / imgSize[0];
                size = minSize;
            } else if (scale <= 0.5625) {
                height = imgSize[1] > longSide ? longSide : imgSize[1];
                width = height * imgSize[0] / imgSize[1];
                size = maxSize;
            }
        } else {
            double scale = (double) imgSize[1] / (double) imgSize[0];
            if (scale <= 1.0 && scale > 0.5625) {
                height = imgSize[1] > shortSide ? shortSide : imgSize[1];
                width = height * imgSize[0] / imgSize[1];
                size = minSize;
            } else if (scale <= 0.5625) {
                width = imgSize[0] > longSide ? longSide : imgSize[0];
                height = width * imgSize[1] / imgSize[0];
                size = maxSize;
            }
        }

        compress(filePath, thumbFilePath, width, height, angle, size);
    }

    /**
     * obtain the image's width and height
     *
     * @param imagePath the path of image
     */
    public int[] getImageSize(String imagePath) {
        int[] res = new int[2];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        BitmapFactory.decodeFile(imagePath, options);

        res[0] = options.outWidth;
        res[1] = options.outHeight;

        return res;
    }

    /**
     * obtain the thumbnail that specify the size
     *
     * @param imagePath the target image path
     * @param width     the width of thumbnail
     * @param height    the height of thumbnail
     * @return {@link Bitmap}
     */
    private Bitmap compress(String imagePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        int outH = options.outHeight;
        int outW = options.outWidth;
        int inSampleSize = 1;

        if (outH > height || outW > width) {
            int halfH = outH / 2;
            int halfW = outW / 2;

            while ((halfH / inSampleSize) > height && (halfW / inSampleSize) > width) {
                inSampleSize *= 2;
            }
        }

        options.inSampleSize = inSampleSize;

        options.inJustDecodeBounds = false;

        int heightRatio = (int) Math.ceil(options.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(options.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio;
            } else {
                options.inSampleSize = widthRatio;
            }
        }
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(imagePath, options);
    }

    /**
     * obtain the image rotation angle
     *
     * @param path path of target image
     */
    private int getImageSpinAngle(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 指定参数压缩图片
     * create the thumbnail with the true rotate angle
     *
     * @param largeImagePath the big image path
     * @param thumbFilePath  the thumbnail path
     * @param width          width of thumbnail
     * @param height         height of thumbnail
     * @param angle          rotation angle of thumbnail
     * @param size           the file size of image
     */
    private void compress(String largeImagePath, String thumbFilePath, int width, int height, int angle, long size) {
        Bitmap thbBitmap = compress(largeImagePath, width, height);

        thbBitmap = rotatingImage(angle, thbBitmap);

        saveImage(thumbFilePath, thbBitmap, size);
    }

    /**
     * 旋转图片
     * rotate the image with specified angle
     *
     * @param angle  the angle will be rotating 旋转的角度
     * @param bitmap target image               目标图片
     */
    private static Bitmap rotatingImage(int angle, Bitmap bitmap) {
        //rotate image
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        //create a new image
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 保存图片到指定路径
     * Save image with specified size
     *
     * @param filePath the image file save path 储存路径
     * @param bitmap   the image what be save   目标图片
     * @param size     the file size of image   期望大小
     */
    private void saveImage(String filePath, Bitmap bitmap, long size) {
        Preconditions.checkNotNull(bitmap, TAG + "bitmap cannot be null");

        File result = new File(filePath.substring(0, filePath.lastIndexOf("/")));

        if (!result.exists() && !result.mkdirs()) return;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream);

        while (stream.toByteArray().length / 1024 > size) {
            stream.reset();
            options -= 6;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream);
        }
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(stream.toByteArray());
            fos.flush();
            fos.close();

            if (compressListener != null) compressListener.onSuccess(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}