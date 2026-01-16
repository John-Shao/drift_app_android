/*-
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreamlib.imageloader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifThumbnailDirectory;
import com.foreamlib.util.BitmapUtil;
//import com.foreamlib.util.ImageFilterModel.BrightContrastFilter;
//import com.foreamlib.util.ImageFilterModel.HslModifyFilter;
//import com.foreamlib.util.ImageFilterModel.IImageFilter;
//import com.foreamlib.util.ImageFilterModel.Image;
//import com.foreamlib.util.ImageFilterModel.ParamEdgeDetectFilter;
//import com.foreamlib.util.ImageFilterModel.VintageFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ContentHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;

import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * A helper class to load images asynchronously.
 */
public final class ImageLoader {

    public static final String LOG_TAG = "ImageLoader";
    private static final boolean DEBUG_MODE = false;

    /**
     * The default maximum number of active tasks.
     */

    public static final int DEFAULT_TASK_LIMIT = 1;
    /**
     * The default cache size (in bytes).
     */
    // 25% of available memory, up to a maximum of 32MB
    public static final long DEFAULT_CACHE_SIZE = Math.min(Runtime.getRuntime().maxMemory() / 4, 32 * 1024 * 1024);

    /**
     * Use with {@link Context#getSystemService(String)} to retrieve an
     * {@link ImageLoader} for loading images.
     * <p/>
     * Since {@link ImageLoader} is not a standard system service, you must
     * create a custom {@link Application} subclass implementing
     * {@link Application#getSystemService(String)} and add it to your
     * {@code AndroidManifest.xml}.
     * <p/>
     * Using this constant is optional and it is only provided for convenience
     * and to promote consistency across deployments of this component.
     */
    public static final String IMAGE_LOADER_SERVICE = "com.google.android.imageloader.imageloader2";// "com.google.android.imageloader.imageloader2";

    /**
     * Gets the {@link ImageLoader} from a {@link Context}.
     *
     * @throws IllegalStateException if the {@link Application} does not have an
     *                               {@link ImageLoader}.
     * @see #IMAGE_LOADER_SERVICE
     */
    public static ImageLoader get(Context context) {
        ImageLoader loader = (ImageLoader) context.getSystemService(IMAGE_LOADER_SERVICE);
        if (loader == null) {
            context = context.getApplicationContext();
            loader = (ImageLoader) context.getSystemService(IMAGE_LOADER_SERVICE);
        }
        if (loader == null) {
            throw new IllegalStateException("ImageLoader not available");
        }
        return loader;
    }

    public void cancelImageLoading() {
        // imageCnt=0;
        cancelTaskInEqueue();
        cancelTaskRun();
        mActiveTaskCount = 0;
    }

    private void cancelTaskRun() {
        while (!mTasks.isEmpty()) {
            mActiveTaskCount--;

            boolean result = mTasks.poll().cancel(true);
            Log.d(LOG_TAG, "Cancel running task:" + result);
        }
    }

    private void cancelTaskInEqueue() {
        Log.d(LOG_TAG, "cancelTaskInEqueue");
        mRequests.clear();
    }

    public int getTaskSize() {
        return mRequests.size() + mTasks.size();
    }

    /**
     * Callback interface for load and error events.
     * <p/>
     * This interface is only applicable when binding a stand-alone
     * {@link ImageView}. When the target {@link ImageView} is in an
     * {@link AdapterView},
     * {@link ImageLoader#bind(BaseAdapter, ImageView, String)} will be called
     * implicitly by {@link BaseAdapter#notifyDataSetChanged()}.
     */
    public interface Callback {
        /**
         * Notifies an observer that an image was loaded.
         * <p/>
         * The bitmap will be assigned to the {@link ImageView} automatically.
         * <p/>
         * Use this callback to dismiss any loading indicators.
         *
         * @param view the {@link ImageView} that was loaded.
         * @param url  the URL that was loaded.
         */
        void onImageLoaded(ImageView view, String url);

        /**
         * Notifies an observer that an image could not be loaded.
         *
         * @param view  the {@link ImageView} that could not be loaded.
         * @param url   the URL that could not be loaded.
         * @param error the exception that was thrown.
         */
        void onImageError(ImageView view, String url, Throwable error);
    }

    public static enum BindResult {
        /**
         * Returned when an image is bound to an {@link ImageView} immediately
         * because it was already loaded.
         */
        OK,
        /**
         * Returned when an image needs to be loaded asynchronously.
         * <p/>
         * Callers may wish to assign a placeholder or show a progress spinner
         * while the image is being loaded whenever this value is returned.
         */
        LOADING,
        /**
         * Returned when an attempt to load the image has already been made and
         * it failed.
         * <p/>
         * Callers may wish to show an error indicator when this value is
         * returned.
         *
         * @see Callback
         */
        ERROR
    }

    private static String getProtocol(String url) {
        Uri uri = Uri.parse(url);
        return uri.getScheme();
    }

    private final ContentHandler mBitmapContentHandler;

    private final ContentHandler mPrefetchContentHandler;

    private final URLStreamHandlerFactory mURLStreamHandlerFactory;

    private final HashMap<String, URLStreamHandler> mStreamHandlers;

    private final LinkedList<ImageRequest> mRequests;

    private final LinkedList<ImageTask> mTasks;
    /**
     * A cache containing recently used bitmaps.
     * <p/>
     * Use soft references so that the application does not run out of memory in
     * the case where one or more of the bitmaps are large.
     */
    private final Map<String, Bitmap> mBitmaps;

    /**
     * Recent errors encountered when loading bitmaps.
     */
    private final Map<String, ImageError> mErrors;

    /**
     * Tracks the last URL that was bound to an {@link ImageView}.
     * <p/>
     * This ensures that the right image is shown in the case where a new URL is
     * assigned to an {@link ImageView} before the previous asynchronous task
     * completes.
     * <p/>
     * This <em>does not</em> ensure that an image assigned with
     * {@link ImageView#setImageBitmap(Bitmap)},
     * {@link ImageView#setImageDrawable(Drawable)},
     * {@link ImageView#setImageResource(int)}, or
     * {@link ImageView#setImageURI(Uri)} is not replaced. This
     * behavior is important because callers may invoke these methods to assign
     * a placeholder when a bind method returns {@link BindResult#LOADING} or
     * {@link BindResult#ERROR}.
     */
    private final Map<ImageView, String> mImageViewBinding;

    /**
     * The maximum number of active tasks.
     */
    private final int mMaxTaskCount;

    /**
     * The current number of active tasks.
     */
    private int mActiveTaskCount;
    private int mVideoThumbTaskCount = 0;

    public int getVideoThumbTaskCount() {
        return mVideoThumbTaskCount;
    }

    /**
     * Creates an {@link ImageLoader}.
     *
     * @param taskLimit       the maximum number of background tasks that may be active at
     *                        one time.
     * @param streamFactory   a {@link URLStreamHandlerFactory} for creating connections to
     *                        special URLs such as {@code content://} URIs. This parameter
     *                        can be {@code null} if the {@link ImageLoader} only needs to
     *                        load images over HTTP or if a custom
     *                        {@link URLStreamHandlerFactory} has already been passed to
     *                        {@link URL#setURLStreamHandlerFactory(URLStreamHandlerFactory)}
     * @param bitmapHandler   a {@link ContentHandler} for loading images.
     *                        {@link ContentHandler#getContent(URLConnection)} must either
     *                        return a {@link Bitmap} or throw an {@link IOException}. This
     *                        parameter can be {@code null} to use the default
     *                        {@link BitmapContentHandler}.
     * @param prefetchHandler a {@link ContentHandler} for caching a remote URL as a file,
     *                        without parsing it or loading it into memory.
     *                        {@link ContentHandler#getContent(URLConnection)} should always
     *                        return {@code null}. If the URL passed to the
     *                        {@link ContentHandler} is already local (for example,
     *                        {@code file://}), this {@link ContentHandler} should do
     *                        nothing. The {@link ContentHandler} can be {@code null} if
     *                        pre-fetching is not required.
     * @param cacheSize       the maximum size of the image cache (in bytes).
     * @param handler         a {@link Handler} identifying the callback thread, or {@code}
     *                        null for the main thread.
     * @throws NullPointerException if the factory is {@code null}.
     */
    public ImageLoader(int taskLimit, URLStreamHandlerFactory streamFactory, ContentHandler bitmapHandler, ContentHandler prefetchHandler, long cacheSize, Handler handler) {
        if (taskLimit < 1) {
            throw new IllegalArgumentException("Task limit must be positive");
        }
        if (cacheSize < 1) {
            throw new IllegalArgumentException("Cache size must be positive");
        }
        mMaxTaskCount = taskLimit;
        mURLStreamHandlerFactory = streamFactory;
        mStreamHandlers = streamFactory != null ? new HashMap<String, URLStreamHandler>() : null;
        mBitmapContentHandler = bitmapHandler != null ? bitmapHandler : new BitmapContentHandler();
        mPrefetchContentHandler = prefetchHandler;

        mImageViewBinding = new WeakHashMap<ImageView, String>();

        mRequests = new LinkedList<ImageRequest>();
        mTasks = new LinkedList<ImageTask>();
        // Use a LruCache to prevent the set of keys from growing too large.
        // The Maps must be synchronized because they are accessed
        // by the UI thread and by background threads.
        mBitmaps = Collections.synchronizedMap(new BitmapCache<String>(cacheSize));
        mErrors = Collections.synchronizedMap(new LruCache<String, ImageError>());
    }

    /**
     * Creates a basic {@link ImageLoader} with support for HTTP URLs and
     * <p/>
     * in-memory caching. Persistent caching and content:// URIs are not
     * supported when this constructor is used.
     */
    public ImageLoader() {
        this(DEFAULT_TASK_LIMIT, null, null, null, DEFAULT_CACHE_SIZE, null);
    }

    /**
     * Creates a basic {@link ImageLoader} with support for HTTP URLs and
     * in-memory caching.
     * <p/>
     * Persistent caching and content:// URIs are not supported when this
     * constructor is used.
     *
     * @param taskLimit the maximum number of background tasks that may be active at a
     *                  time.
     */
    public ImageLoader(int taskLimit) {
        this(taskLimit, null, null, null, DEFAULT_CACHE_SIZE, null);
    }

    /**
     * Creates a basic {@link ImageLoader} with support for HTTP URLs and
     * in-memory caching.
     * <p/>
     * Persistent caching and content:// URIs are not supported when this
     * constructor is used.
     *
     * @param cacheSize the maximum size of the image cache (in bytes).
     */
    public ImageLoader(long cacheSize) {
        this(DEFAULT_TASK_LIMIT, null, null, null, cacheSize, null);
    }

    /**
     * Creates an {@link ImageLoader} with support for pre-fetching.
     *
     * @param bitmapHandler   a {@link ContentHandler} that reads, caches, and returns a
     *                        {@link Bitmap}.
     * @param prefetchHandler a {@link ContentHandler} for caching a remote URL as a file,
     *                        without parsing it or loading it into memory.
     *                        {@link ContentHandler#getContent(URLConnection)} should always
     *                        return {@code null}. If the URL passed to the
     *                        {@link ContentHandler} is already local (for example,
     *                        {@code file://}), this {@link ContentHandler} should return
     *                        {@code null} immediately.
     */
    public ImageLoader(ContentHandler bitmapHandler, ContentHandler prefetchHandler) {
        this(DEFAULT_TASK_LIMIT, null, bitmapHandler, prefetchHandler, DEFAULT_CACHE_SIZE, null);
    }

    /**
     * Creates an {@link ImageLoader} with support for http:// and content://
     * URIs.
     * <p/>
     * Prefetching is not supported when this constructor is used.
     *
     * @param resolver a {@link ContentResolver} for accessing content:// URIs.
     */
    public ImageLoader(ContentResolver resolver) {
        this(DEFAULT_TASK_LIMIT, new ContentURLStreamHandlerFactory(resolver), null, null, DEFAULT_CACHE_SIZE, null);
    }

    /**
     * Creates an {@link ImageLoader} with a custom
     * {@link URLStreamHandlerFactory}.
     * <p/>
     * Use this constructor when loading images with protocols other than
     * {@code http://} and when a custom {@link URLStreamHandlerFactory} has not
     * already been installed with
     * {@link URL#setURLStreamHandlerFactory(URLStreamHandlerFactory)}. If the
     * only additional protocol support required is for {@code content://} URIs,
     * consider using {@link #ImageLoader(ContentResolver)}.
     * <p/>
     * Prefetching is not supported when this constructor is used.
     */
    public ImageLoader(URLStreamHandlerFactory factory) {
        this(DEFAULT_TASK_LIMIT, factory, null, null, DEFAULT_CACHE_SIZE, null);
    }

    private URLStreamHandler getURLStreamHandler(String protocol) {
        URLStreamHandlerFactory factory = mURLStreamHandlerFactory;
        if (factory == null) {
            return null;
        }
        HashMap<String, URLStreamHandler> handlers = mStreamHandlers;
        synchronized (handlers) {
            URLStreamHandler handler = handlers.get(protocol);
            if (handler == null) {
                handler = factory.createURLStreamHandler(protocol);
                if (handler != null) {
                    handlers.put(protocol, handler);
                }
            }
            return handler;
        }
    }

    /**
     * Creates tasks to service any pending requests until {@link #mRequests} is
     * empty or {@link #mMaxTaskCount} is reached.
     */
    void flushTasks() {
        // Log.d(LOG_TAG,"MaxtaskCount"+mMaxTaskCount);
        while (mActiveTaskCount < mMaxTaskCount && !mRequests.isEmpty()) {
            if (mRequests.isEmpty()) {
                Log.e(LOG_TAG, "multi-thread error,check mRequests.clear()func");
                break;
            }
            ImageTask task = new ImageTask();
            mTasks.add(task);
            task.executeOnThreadPool(mRequests.poll());

            // task.cancel(true);

        }

    }

    private void enqueueRequest(ImageRequest request) {
        mRequests.add(request);
        flushTasks();
    }

    private void insertRequestAtFrontOfQueue(ImageRequest request) {
        mRequests.add(0, request);
        flushTasks();

    }

    /**
     * Binds a URL to an {@link ImageView} within an
     * {@link AdapterView}.
     *
     * @param adapter the adapter for the {@link AdapterView}.
     * @param view    the {@link ImageView}.
     * @param url     the image URL.
     * @return a {@link BindResult}.
     * @throws NullPointerException if any of the arguments are {@code null}.
     */
    public BindResult bind(BaseAdapter adapter, ImageView view, String url) {
        if (adapter == null) {
            throw new NullPointerException("Adapter is null");
        }
        if (view == null) {
            throw new NullPointerException("ImageView is null");
        }
        if (url == null) {
            throw new NullPointerException("URL is null");
        }
        Bitmap bitmap = getBitmap(url);
        ImageError error = getError(url);
        if (bitmap != null) {
            view.setImageBitmap(bitmap);
            return BindResult.OK;
        } else {
            // Clear the ImageView by default.
            // The caller can set their own placeholder
            // based on the return value.
            view.setImageDrawable(null);

            if (error != null) {
                return BindResult.ERROR;
            } else {
                ImageRequest request = new ImageRequest(adapter, url);

                // For adapters, post the latest requests
                // at the front of the queue in case the user
                // has already scrolled past most of the images
                // that are currently in the queue.
                insertRequestAtFrontOfQueue(request);

                return BindResult.LOADING;
            }
        }
    }

    /**
     * Binds a URL to an {@link ImageView} within an
     * {@link android.widget.ExpandableListView}.
     *
     * @param adapter the adapter for the {@link android.widget.ExpandableListView}.
     * @param view    the {@link ImageView}.
     * @param url     the image URL.
     * @return a {@link BindResult}.
     * @throws NullPointerException if any of the arguments are {@code null}.
     */
    public BindResult bind(BaseExpandableListAdapter adapter, ImageView view, String url) {
        if (adapter == null) {
            throw new NullPointerException("Adapter is null");
        }
        if (view == null) {
            throw new NullPointerException("ImageView is null");
        }
        if (url == null) {
            throw new NullPointerException("URL is null");
        }
        Bitmap bitmap = getBitmap(url);
        ImageError error = getError(url);
        if (bitmap != null) {
            view.setImageBitmap(bitmap);
            return BindResult.OK;
        } else {
            // Clear the ImageView by default.
            // The caller can set their own placeholder
            // based on the return value.
            view.setImageDrawable(null);

            if (error != null) {
                return BindResult.ERROR;
            } else {
                ImageRequest request = new ImageRequest(adapter, url);

                // For adapters, post the latest requests
                // at the front of the queue in case the user
                // has already scrolled past most of the images
                // that are currently in the queue.
                insertRequestAtFrontOfQueue(request);

                return BindResult.LOADING;
            }
        }
    }

    /**
     * Binds an image at the given URL to an {@link ImageView}.
     * <p/>
     * If the image needs to be loaded asynchronously, it will be assigned at a
     * later time, replacing any existing {@link Drawable} unless
     * {@link #unbind(ImageView)} is called or
     * {@link #bind(ImageView, String, Callback)} is called with the same
     * {@link ImageView}, but a different URL.
     * <p/>
     * Use {@link #bind(BaseAdapter, ImageView, String)} instead of this method
     * when the {@link ImageView} is in an {@link AdapterView} so
     * that the image will be bound correctly in the case where it has been
     * assigned to a different position since the asynchronous request was
     * started.
     *
     * @param view     the {@link ImageView} to bind.
     * @param url      the image URL.s
     * @param callback invoked after the image has finished loading or after an
     *                 error. The callback may be executed before this method returns
     *                 when the result is cached. This parameter can be {@code null}
     *                 if a callback is not required.
     * @return a {@link BindResult}.
     * @throws NullPointerException if a required argument is {@code null}
     */
    public BindResult bind(ImageView view, String url, Callback callback) {
        if (view == null) {
            throw new NullPointerException("ImageView is null");
        }
        if (url == null) {
            throw new NullPointerException("URL is null");
        }
        mImageViewBinding.put(view, url);
        Bitmap bitmap = getBitmap(url);
        ImageError error = getError(url);
        if (bitmap != null) {
            view.setImageBitmap(bitmap);
            if (callback != null) {
                callback.onImageLoaded(view, url);
            }
            return BindResult.OK;
        } else {
            // Clear the ImageView by default.
            // The caller can set their own placeholder
            // based on the return value.
            view.setImageDrawable(null);

            if (error != null) {
                if (callback != null) {
                    callback.onImageError(view, url, error.getCause());
                }
                return BindResult.ERROR;
            } else {
                ImageRequest request = new ImageRequest(view, url, callback);
                enqueueRequest(request);
                return BindResult.LOADING;
            }
        }
    }

    /**
     * Cancels an asynchronous request to bind an image URL to an
     * {@link ImageView} and clears the {@link ImageView}.
     *
     * @see #bind(ImageView, String, Callback)
     */
    public void unbind(ImageView view) {
        mImageViewBinding.remove(view);
        view.setImageDrawable(null);
    }

    /**
     * clear cache to refresh image.
     */
    public void removeCache(String cacheId) {
        // mImageViewBinding.remove(view);
        if (cacheId != null) {
            mBitmaps.remove(cacheId);
            GoproDrawableFileCache.removeFileCache(cacheId);
            mErrors.remove(cacheId);
        }

    }

    /**
     * Clears any cached errors.
     * <p/>
     * Call this method when a network connection is restored, or the user
     * invokes a manual refresh of the screen.
     */
    public void clearErrors() {
        mErrors.clear();
    }

    /**
     * sheng add for preload photo thumb nail
     */
    public void preload(String url, int imageWidth, int imageHeight, int iRadius, boolean isQuickLoad) {
        if (url == null) {
            throw new NullPointerException();
        }
        if (null != getBitmap(url)) {
            // The image is already loaded
            return;
        }
        if (null != getError(url)) {
            // A recent attempt to load the image failed,
            // therefore this attempt is likely to fail as well.
            return;
        }
        ImageRequest task = new ImageRequest(null, null, url, imageWidth, imageHeight, iRadius, null, isQuickLoad);
        if (isQuickLoad) {
            insertRequestAtFrontOfQueue(task);
        } else {
            enqueueRequest(task);
        }



    }

    /**
     * Pre-loads an image into memory.
     * <p/>
     * The image may be unloaded if memory is low. Use {@link #prefetch(String)}
     * and a file-based cache to pre-load more images.
     *
     * @param url the image URL
     * @throws NullPointerException if the URL is {@code null}
     */
    public void preload(String url) {
        if (url == null) {
            throw new NullPointerException();
        }
        if (null != getBitmap(url)) {
            // The image is already loaded
            return;
        }
        if (null != getError(url)) {
            // A recent attempt to load the image failed,
            // therefore this attempt is likely to fail as well.
            return;
        }
        boolean loadBitmap = true;
        ImageRequest task = new ImageRequest(url, loadBitmap);
        enqueueRequest(task);
    }

    /**
     * Pre-loads a range of images into memory from a {@link Cursor}.
     * <p/>
     * Typically, an {@link Activity} would register a {@link DataSetObserver}
     * and an {@link AdapterView.OnItemSelectedListener}, then
     * call this method to prime the in-memory cache with images adjacent to the
     * current selection whenever the selection or data changes.
     * <p/>
     * Any invalid positions in the specified range will be silently ignored.
     *
     * @param cursor      a {@link Cursor} containing the image URLs.
     * @param columnIndex the column index of the image URL. The column value may be
     *                    {@code NULL}.
     * @param start       the first position to load. For example,
     *                    {@code selectedPosition - 5}.
     * @param end         the first position not to load. For example,
     *                    {@code selectedPosition + 5}.
     * @see #preload(String)
     */
    public void preload(Cursor cursor, int columnIndex, int start, int end) {
        for (int position = start; position < end; position++) {
            if (cursor.moveToPosition(position)) {
                String url = cursor.getString(columnIndex);
                if (!TextUtils.isEmpty(url)) {
                    preload(url);
                }
            }
        }
    }

    /**
     * Pre-fetches the binary content for an image and stores it in a file-based
     * cache (if it is not already cached locally) without loading the image
     * data into memory.
     * <p/>
     * Pre-fetching should not be used unless a {@link ContentHandler} with
     * support for persistent caching was passed to the constructor.
     *
     * @param url the URL to pre-fetch.
     * @throws NullPointerException if the URL is {@code null}
     */
    public void prefetch(String url) {
        if (url == null) {
            throw new NullPointerException();
        }
        if (null != getBitmap(url)) {
            // The image is already loaded, therefore
            // it does not need to be prefetched.
            return;
        }
        if (null != getError(url)) {
            // A recent attempt to load or prefetch the image failed,
            // therefore this attempt is likely to fail as well.
            return;
        }
        boolean loadBitmap = false;
        ImageRequest request = new ImageRequest(url, loadBitmap);
        enqueueRequest(request);
    }

    /**
     * Pre-fetches the binary content for images referenced by a {@link Cursor},
     * without loading the image data into memory.
     * <p/>
     * Pre-fetching should not be used unless a {@link ContentHandler} with
     * support for persistent caching was passed to the constructor.
     * <p/>
     * Typically, an {@link Activity} would register a {@link DataSetObserver}
     * and call this method from {@link DataSetObserver#onChanged()} to load
     * off-screen images into a file-based cache when they are not already
     * present in the cache.
     *
     * @param cursor      the {@link Cursor} containing the image URLs.
     * @param columnIndex the column index of the image URL. The column value may be
     *                    {@code NULL}.
     * @see #prefetch(String)
     */
    public void prefetch(Cursor cursor, int columnIndex) {
        for (int position = 0; cursor.moveToPosition(position); position++) {
            String url = cursor.getString(columnIndex);
            if (!TextUtils.isEmpty(url)) {
                prefetch(url);
            }
        }
    }

    private static int imageCnt = 0;

    private void putBitmap(String url, Bitmap bitmap) {
        mBitmaps.put(url, bitmap);
    }

    private void putError(String url, ImageError error) {
        mErrors.put(url, error);
    }

    public Bitmap getBitmap(String url) {
        return mBitmaps.get(url);
    }

    private ImageError getError(String url) {
        ImageError error = mErrors.get(url);
        return error != null && !error.isExpired() ? error : null;
    }

    /**
     * Returns {@code true} if there was an error the last time the given URL
     * was accessed and the error is not expired, {@code false} otherwise.
     */
    private boolean hasError(String url) {
        return getError(url) != null;
    }

    private class ImageRequest {

        private final ImageCallback mCallback;

        private final ImageView mView;

        private final String mUrl;

        private final String mCacheID;

        private final boolean mLoadBitmap;

        private Bitmap mBitmap;

        private boolean mIsThumbnail = false;
        private boolean mIsLocal = false;
        private boolean misPreviewImage = false;

        private ImageError mError;

        final private int mWidth;

        final private int mHeight;

        private int mRadius = 0;
        private long[][] sec = {{-1, -1}};

        private ImageRequest(String url, ImageCallback callback, String cacheID, boolean loadBitmap) {
            mUrl = url;
            mCallback = callback;
            mView = null;
            mLoadBitmap = loadBitmap;
            mCacheID = cacheID;
            mWidth = mHeight = 1000;
        }

        private ImageRequest(String url, ImageView view) {
            mUrl = url;
            mView = view;
            mCallback = null;
            mLoadBitmap = true;
            mCacheID = url;
            mWidth = mHeight = -1;
        }

        /**
         * @param adapter
         * @param url
         * @param cacheID TODO
         * @param mWidth
         * @param mHeight
         */
        public ImageRequest(BaseAdapter adapter, ImageView view, String url, int iWidth, int iHeight, int iRadius, String cacheID, boolean isThumb) {
            mUrl = url;
            mView = view;
            mCallback = null;
            mLoadBitmap = true;
            mWidth = iWidth;
            mHeight = iHeight;
            mIsThumbnail = isThumb;
            mCacheID = cacheID;
            mRadius = iRadius;
        }

        public ImageRequest(BaseAdapter adapter, ImageView view, String url, int iWidth, int iHeight, int iRadius, String cacheID, boolean isThumb, boolean isPreviewImage) {
            mUrl = url;
            mView = view;
            mCallback = null;
            mLoadBitmap = true;
            mWidth = iWidth;
            mHeight = iHeight;
            mIsThumbnail = isThumb;
            mCacheID = cacheID;
            mRadius = iRadius;
        }

        public ImageRequest(BaseAdapter adapter, ImageView view, String url, int iWidth, int iHeight, int iRadius, String cacheID, boolean isThumb, boolean isPreviewImage, ImageCallback imageCallback) {
            mUrl = url;
            mView = view;
            mCallback = imageCallback;
            mLoadBitmap = true;
            mWidth = iWidth;
            mHeight = iHeight;
            mIsThumbnail = isThumb;
            mCacheID = cacheID;
            mRadius = iRadius;
            misPreviewImage = isPreviewImage;
        }

        public ImageRequest(BaseAdapter adapter, ImageView view, String url, int iWidth, int iHeight, int iRadius, String cacheID, boolean isThumb, long[][] sec, ImageCallback imageCallback) {
            mUrl = url;
            mView = view;
            mCallback = imageCallback;
            mLoadBitmap = true;
            mWidth = iWidth;
            mHeight = iHeight;
            mIsThumbnail = isThumb;
            mCacheID = cacheID;
            mRadius = iRadius;
            this.sec = sec;
        }

        /**
         * Creates an {@link ImageTask} to load a {@link Bitmap} for an
         * {@link ImageView} in an {@link AdapterView}.
         */
        public ImageRequest(BaseAdapter adapter, String url) {
            this(url, new BaseAdapterCallback(adapter), url, true);
        }

        /**
         * Creates an {@link ImageTask} to load a {@link Bitmap} for an
         * {@link ImageView} in an {@link android.widget.ExpandableListView}.
         */
        public ImageRequest(BaseExpandableListAdapter adapter, String url) {
            this(url, new BaseExpandableListAdapterCallback(adapter), url, true);
        }

        /**
         * Creates an {@link ImageTask} to load a {@link Bitmap} for an
         * {@link ImageView}.
         */
        public ImageRequest(ImageView view, String url, Callback callback) {
            this(url, new ImageViewCallback(view, callback), url, true);
        }

        /**
         * Creates an {@link ImageTask} to prime the cache.
         */
        public ImageRequest(String url, boolean loadBitmap) {
            this(url, null, url, loadBitmap);
        }

        private Bitmap loadImage(URL url) throws IOException

        {
            URLConnection connection = url.openConnection();
            return (Bitmap) mBitmapContentHandler.getContent(connection);
        }

        private Bitmap loadImage(String urlPath) throws IOException {

            if (urlPath == null || "".equals(urlPath))
                return null;
            if (mCacheID != null) {
                String cachePath = GoproDrawableFileCache.getFileCachePath(mCacheID);
                Bitmap cacheBmp = loadImageByPath(cachePath, mWidth, mHeight);
                if (cacheBmp != null) {
                    if (DEBUG_MODE)
                        Log.d(LOG_TAG, "GetBmp From LocalCache(" + cacheBmp.getWidth() + "," + cacheBmp.getHeight() + "):" + mCacheID);
                    return cacheBmp;
                }
            }
            Bitmap result = null;
            if (mIsThumbnail) {
                Log.d(LOG_TAG, "begin task " + mVideoThumbTaskCount);
                if (urlPath.toUpperCase(Locale.getDefault()).matches(".*\\.JPG")) {
                    byte[] imgArray = null;
                    Bitmap bitmap = null;
                    HttpURLConnection conn = (HttpURLConnection) new URL(urlPath).openConnection();
                    conn.setDoInput(true);
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    InputStream stream = conn.getInputStream();
                    // BufferedInputStream buffInput = new
                    // BufferedInputStream(stream, 1024 * 300);
                    Metadata reader;
                    try {
                        reader = ImageMetadataReader.readMetadata(stream);
                        reader.getDirectories();

                        Collection<ExifThumbnailDirectory> thumbs = reader.getDirectoriesOfType(ExifThumbnailDirectory.class);
                        if (thumbs != null) {
                            for (ExifThumbnailDirectory thumb : thumbs) {
                                byte[] tmp = thumb.getThumbnailData();
                                if (tmp == null)
                                    continue;
                                if (imgArray == null || imgArray.length > tmp.length) {
                                    imgArray = tmp;
                                }
                            }

                        }
                        BitmapFactory.Options opts = new BitmapFactory.Options();
                        opts.inPreferredConfig = Bitmap.Config.RGB_565;
                        opts.inPurgeable = true;
                        opts.inInputShareable = true;
                        if (imgArray != null) {
                            bitmap = BitmapFactory.decodeByteArray(imgArray, 0, imgArray.length, opts);
                        }
                    } catch (ImageProcessingException e) {
                        e.printStackTrace();
                    }
                    stream.close();
                    result = bitmap;

                } else {
                    mVideoThumbTaskCount++;
                    Bitmap bitmap = null;
                    // 硬解
                    // if (Build.VERSION.SDK_INT < 10) {
                    // bitmap = ThumbnailUtils.createVideoThumbnail(urlPath,
                    // Thumbnails.MINI_KIND);
                    // }else{
                    // bitmap = createVideoThumbnail(urlPath);
                    // }

                    // 硬解失败后软解
                    // *
                    if (bitmap == null) {
                        bitmap = createVideoThumbnailFromVimatio(mView.getContext(), urlPath, sec);
                        int scale = calSampleSize(bitmap.getWidth(), bitmap.getHeight(), mWidth, mHeight);
                        if (scale > 1) {
                            Bitmap tmpbitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / scale, bitmap.getHeight() / scale, true);
                            bitmap.recycle();
                            bitmap = tmpbitmap;
                        }
                    }
                    // */

                    mVideoThumbTaskCount--;
                    Log.d(LOG_TAG, "end task " + mVideoThumbTaskCount);
                    result = bitmap;// ThumbnailUtils.extractThumbnail(bitmap,
                    // mWidth, mHeight);
                }
            } else if (misPreviewImage) {
                String protocol = ImageLoader.getProtocol(urlPath);
                URLStreamHandler streamHandler = getURLStreamHandler(protocol);
                URL url = new URL(null, urlPath, streamHandler);
                // Log.d(LOG_TAG,"Load Image URL:"+urlPath);

                result = loadImageByUrl2(url, mWidth, mHeight);
            } else if (urlPath.startsWith("/") || !urlPath.startsWith("http")) {
                // Log.d(LOG_TAG,"local file:"+urlPath);
                result = loadImageByPath(urlPath, mWidth, mHeight);
            } else {

                // try
                // {
                String protocol = ImageLoader.getProtocol(urlPath);
                URLStreamHandler streamHandler = getURLStreamHandler(protocol);
                URL url = new URL(null, urlPath, streamHandler);
                // Log.d(LOG_TAG,"Load Image URL:"+urlPath);

                result = loadImageByUrl(url, mWidth, mHeight);

            }
            // 后期合成.
            if (mRadius != 0) {
                result = BitmapUtil.createFramedPhoto(result, mRadius);
            }
//            if (sec[0][1] != -1) {
//                switch (new Long(sec[0][1]).intValue()) {
//                    case 0:
//                        break;
//                    case 1:
//                        result = imagefilter(result, new HslModifyFilter(20f));
//                        break;
//                    case 2:
//                        result = imagefilter(result, new HslModifyFilter(60f));
//                        break;
//                    case 3:
//                        result = imagefilter(result, new BrightContrastFilter());
//                        break;
//                    case 4:
//                        result = imagefilter(result, new VintageFilter());
//                        break;
//                    case 5:
//                        result = imagefilter(result, new ParamEdgeDetectFilter());
//                        break;
//                    default:
//                        break;
//                }
//
//            }
            if (result != null) {
                if (DEBUG_MODE)
                    Log.d(LOG_TAG, "GetBmp From RealUrl(" + result.getWidth() + "," + result.getHeight() + "):" + mCacheID);
                GoproDrawableFileCache.addFileCache(result, mCacheID);
            }

            return result;
            /*
			 * } catch(IOException e) { e.printStackTrace(); }
			 */
            // return null;
        }

        // private Bitmap loadImageByUrl(URL url)
        // {
        // return loadImageByUrl(url, 196, 148);
        // }
        private Bitmap loadImageByUrl(URL url, int width, int height) {
            try {

                {
                    long time = System.currentTimeMillis();
                    com.foreamlib.log.Log.d(LOG_TAG, "Image:" + url);

                    if (url != null && !"".equals(url)) {
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        conn.setConnectTimeout(10000);
                        conn.setRequestMethod("GET");

                        int t = conn.getResponseCode();
                        if (t == HttpURLConnection.HTTP_NOT_FOUND) {
                            mError = new ImageError(new Throwable("404 HTTP_NOT_FOUND"));
                            return null;
                        }
                        com.foreamlib.log.Log.d(LOG_TAG, "get response Time " + (System.currentTimeMillis() - time));
                        time = System.currentTimeMillis();
                        int contentLength = conn.getContentLength();

                        InputStream stream = conn.getInputStream();

                        BitmapFactory.Options options = new BitmapFactory.Options();

                        options.inJustDecodeBounds = true;
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        options.inPurgeable = true;
                        BitmapFactory.decodeStream(stream, null, options);
                        options.inSampleSize = calSampleSize(options.outWidth, options.outHeight, width, height);

                        options.inJustDecodeBounds = false;
                        stream.close();
                        stream = ((HttpURLConnection) url.openConnection()).getInputStream();

                        Bitmap img = BitmapFactory.decodeStream(stream, null, options);
                        stream.close();
                        long duration = (System.currentTimeMillis() - time);
                        float speed = (float) contentLength / (float) duration * 1000;
                        com.foreamlib.log.Log.d(LOG_TAG, "Download Image Time " + duration + ",Speed:" + (int) speed + " Bps");
                        if(img.getWidth()<=0||img.getHeight()<=0)return null;
                        return img;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private Bitmap loadImageByUrl2(URL url, int width, int height) {
            try {

                {
                    long time = System.currentTimeMillis();
                    com.foreamlib.log.Log.d(LOG_TAG, "Image:" + url);

                    if (url != null && !"".equals(url)) {
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        conn.setConnectTimeout(10000);
                        conn.setRequestMethod("GET");

                        int t = conn.getResponseCode();
                        if (t == HttpURLConnection.HTTP_NOT_FOUND) {
                            mError = new ImageError(new Throwable("404 HTTP_NOT_FOUND"));
                            return null;
                        }
                        com.foreamlib.log.Log.d(LOG_TAG, "get response Time " + (System.currentTimeMillis() - time));
                        time = System.currentTimeMillis();
                        int contentLength = conn.getContentLength();

                        InputStream stream = conn.getInputStream();
                        int length = 0;
                        LinkedList<byte[]> list = new LinkedList<>();
                        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
                        int h1, h2;
                        LinkedList<Integer> link = new LinkedList<>();
                        link.add(0, stream.read());
                        link.add(1, stream.read());
                        while (true) {
                            if (link.get(0) == 0xFF) {
                                if (link.get(1) == 0xE2) {
                                    do {
                                        int iByteLengthHigh = stream.read();
                                        int iByteLengthLow = stream.read();
                                        System.out.println("GetSectionLen(" + iByteLengthHigh + ", " + iByteLengthLow + ")");
                                        int iBlockLen = (((iByteLengthHigh << 8) + iByteLengthLow) & 0xffff) - 2;
                                        System.out.println("Section length: " + iBlockLen + " bytes");
                                        length = length + iBlockLen;
                                        int readBytes = 0;
                                        byte[] b = new byte[iBlockLen];//1024可改成任何需要的值
                                        int len = b.length;
                                        while (readBytes < len) {
                                            int read = stream.read(b, readBytes, len - readBytes);
                                            //判断是不是读到了数据流的末尾 ，防止出现死循环。
                                            if (read == -1) {
                                                break;
                                            }
                                            readBytes += read;
                                        }
                                        swapStream.write(b, 0, b.length);
                                        h1 = stream.read();
                                        h2 = stream.read();
                                    } while (h1 == 0xFF && h2 == 0xE2);
                                    break;
                                } else {
                                    link.remove(0);
                                    link.add(stream.read());
                                }
                            } else {
                                link.remove(0);
                                link.add(stream.read());
                            }


                        }
                        System.out.println("success" + swapStream.toByteArray().length);
                        byte[] data = swapStream.toByteArray();
                        BitmapFactory.Options opts = new BitmapFactory.Options();
                        opts.inPreferredConfig = Bitmap.Config.RGB_565;

                        Bitmap img = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
                        stream.close();
                        //stream.close();

                        long duration = (System.currentTimeMillis() - time);
                        float speed = (float) contentLength / (float) duration * 1000;
                        com.foreamlib.log.Log.d(LOG_TAG, "Download Image Time " + duration + ",Speed:" + (int) speed + " Bps");
                        return img;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 计算需要缩放的图片倍数
         *
         * @param options
         * @param width
         * @param height
         * @return
         */
        private int calSampleSize(int org_width, int org_height, int width, int height) {
            // 需要缩放的倍数
            int zoom = 1;
            if (height > 0 && width > 0) {
                float be = ((float) org_height / (float) height) <= ((float) org_width / (float) width) ? ((float) org_width / (float) width) : ((float) org_height / (float) height);
                if (be <= 0)
                    be = 1;

                zoom = (int) Math.ceil(be);
            }

            // 在最大2X1MB RGB_565 BUFFER下，最小的缩放倍数.
            float maxPixcels = 1000000f;
            int minzoom = 1;
            float nowPixcels = org_width * org_height;
            while (maxPixcels * minzoom * minzoom < nowPixcels) {
                minzoom = minzoom << 1;
            }
            if (zoom < minzoom) {
                zoom = minzoom;
            }
            return zoom;
        }

        private Bitmap loadImageByPath(String path, int width, int height) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            opts.inPurgeable = true;
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opts);

            opts.inSampleSize = calSampleSize(opts.outWidth, opts.outHeight, width, height);

            opts.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(path, opts);
        }

        /**
         * Executes the {@link ImageTask}.
         *
         * @return {@code true} if the result for this {@link ImageTask} should
         * be posted, {@code false} otherwise.
         */
        public boolean execute() {
            try {
                if (mCallback != null) {
                    if (mCallback.unwanted()) {
                        return false;
                    }
                }
                // Check if the last attempt to load the URL had an error
                mError = getError(mCacheID);
                if (mError != null) {
                    return true;
                }

                // Check if the Bitmap is already cached in memory
                mBitmap = getBitmap(mCacheID);
                if (mBitmap != null) {

                    // Keep a hard reference until the view has been notified.
                    return true;
                }

                // String protocol = getProtocol(mUrl);
                // URLStreamHandler streamHandler =
                // getURLStreamHandler(protocol);
                // URL url = new URL(null, mUrl, streamHandler);

                if (mLoadBitmap) {
                    try {
                        mBitmap = loadImage(mUrl);
                    } catch (OutOfMemoryError e) {
                        // The VM does not always free-up memory as it should,
                        // so manually invoke the garbage collector
                        // and try loading the image again.
                        Log.e(LOG_TAG, "out of memory.free-up now.");
                        System.gc();
                        // mBitmap = loadImage(mUrl);
                    }
                    // if (mBitmap == null) {
                    // throw new
                    // NullPointerException("ContentHandler returned null");
                    // }
                    return true;
                } else {
                    if (mPrefetchContentHandler != null) {
                        // Cache the URL without loading a Bitmap into memory.
                        String protocol = getProtocol(mUrl);
                        URLStreamHandler streamHandler = getURLStreamHandler(protocol);
                        URL url = new URL(null, mUrl, streamHandler);
                        URLConnection connection = url.openConnection();
                        mPrefetchContentHandler.getContent(connection);
                    }
                    mBitmap = null;
                    return false;
                }
            } catch (IOException e) {
                mError = new ImageError(e);
                return true;
            } catch (RuntimeException e) {
                mError = new ImageError(e);
                return true;
            } catch (Error e) {
                mError = new ImageError(e);
                return true;
            }
        }

        public void publishResult() {
            // imageCnt++;
            // Log.d("ImageLoader2","publishResult testRtCnt="+String.valueOf(imageCnt));
            if (mBitmap != null) {
                putBitmap(mCacheID, mBitmap);

            } else if (mError != null && !hasError(mCacheID)) {
                Log.e(LOG_TAG, "Failed to load " + mUrl);
                mError.getCause().printStackTrace();
                // Log.e(LOG_TAG, "Failed to load " + mUrl, mError.getCause());
                putError(mCacheID, mError);
            }
            if (mCallback != null) {
                mCallback.send(mUrl, mBitmap, mError);
            }
            if (mView != null && mBitmap != null) {
                String binding = mImageViewBinding.get(mView);
                // Log.d(LOG_TAG,"bind:"+binding+":"+mUrl);
                if (!TextUtils.equals(binding, mUrl)) {
                    // The ImageView has been unbound or bound to a
                    // different URL since the task was started.
                    return;
                }
                setFadeInImageBitmap(mView, mBitmap);
                // mView.setImageBitmap(mBitmap);
            }
        }
    }

    private interface ImageCallback {
        boolean unwanted();

        void send(String url, Bitmap bitmap, ImageError error);
    }

    private final class ImageViewCallback implements ImageCallback {

        // TODO: Use WeakReferences?

        private final WeakReference<ImageView> mImageView;
        private final Callback mCallback;

        public ImageViewCallback(ImageView imageView, Callback callback) {
            mImageView = new WeakReference<ImageView>(imageView);
            mCallback = callback;
        }

        /**
         * {@inheritDoc}
         */
        public boolean unwanted() {
            // Always complete the callback
            return false;
        }

        /**
         * {@inheritDoc}
         */
        public void send(String url, Bitmap bitmap, ImageError error) {
            ImageView imageView = mImageView.get();
            if (imageView == null)
                return;
            String binding = mImageViewBinding.get(imageView);
            Log.d("Imagelaoder2", "ImageViewCallback send function");
            if (!TextUtils.equals(binding, url)) {
                // The ImageView has been unbound or bound to a
                // different URL since the task was started.
                return;
            }
            if (bitmap != null) {
                setFadeInImageBitmap(imageView, bitmap);
                // mImageView.setImageBitmap(bitmap);
                if (mCallback != null) {
                    mCallback.onImageLoaded(imageView, url);
                }
            } else if (error != null) {
                if (mCallback != null) {
                    mCallback.onImageError(imageView, url, error.getCause());
                }
            }
        }
    }

    private static final class BaseAdapterCallback implements ImageCallback {
        private final WeakReference<BaseAdapter> mAdapter;

        public BaseAdapterCallback(BaseAdapter adapter) {
            mAdapter = new WeakReference<BaseAdapter>(adapter);
        }

        /**
         * {@inheritDoc}
         */
        public boolean unwanted() {
            return mAdapter.get() == null;
        }

        /**
         * {@inheritDoc}
         */
        public void send(String url, Bitmap bitmap, ImageError error) {
            BaseAdapter adapter = mAdapter.get();
            if (adapter == null) {
                // The adapter is no longer in use
                return;
            }
            if (!adapter.isEmpty()) {
                Log.d(LOG_TAG, " notifid in sending function");
                adapter.notifyDataSetChanged();
            } else {
                // The adapter is empty or no longer in use.
                // It is important that BaseAdapter#notifyDataSetChanged()
                // is not called when the adapter is empty because this
                // may indicate that the data is valid when it is not.
                // For example: when the adapter cursor is deactivated.
            }
        }
    }

    private static final class BaseExpandableListAdapterCallback implements ImageCallback {

        private final WeakReference<BaseExpandableListAdapter> mAdapter;

        public BaseExpandableListAdapterCallback(BaseExpandableListAdapter adapter) {
            mAdapter = new WeakReference<BaseExpandableListAdapter>(adapter);
        }

        /**
         * {@inheritDoc}
         */
        public boolean unwanted() {
            return mAdapter.get() == null;
        }

        /**
         * {@inheritDoc}
         */
        public void send(String url, Bitmap bitmap, ImageError error) {
            BaseExpandableListAdapter adapter = mAdapter.get();
            if (adapter == null) {
                // The adapter is no longer in use
                return;
            }
            if (!adapter.isEmpty()) {

                adapter.notifyDataSetChanged();
            } else {
                // The adapter is empty or no longer in use.
                // It is important that BaseAdapter#notifyDataSetChanged()
                // is not called when the adapter is empty because this
                // may indicate that the data is valid when it is not.
                // For example: when the adapter cursor is deactivated.
            }
        }
    }

    private class ImageTask extends AsyncTask<ImageRequest, ImageRequest, Void> {

        public final AsyncTask<ImageRequest, ImageRequest, Void> executeOnThreadPool(ImageRequest... params) {
            if (Build.VERSION.SDK_INT < 4) {
                // Thread pool size is 1
                return execute(params);
            } else if (Build.VERSION.SDK_INT < 11) {
                // The execute() method uses a thread pool
                return execute(params);
            } else {
                // The execute() method uses a single thread,
                // so call executeOnExecutor() instead.
                try {
                    Method method = AsyncTask.class.getMethod("executeOnExecutor", Executor.class, Object[].class);
                    Field field = AsyncTask.class.getField("THREAD_POOL_EXECUTOR");
                    Object executor = field.get(null);
                    method.invoke(this, executor, params);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("Unexpected NoSuchMethodException", e);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException("Unexpected NoSuchFieldException", e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Unexpected IllegalAccessException", e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("Unexpected InvocationTargetException", e);
                }
                return this;
            }
        }

        @Override
        protected void onPreExecute() {
            mActiveTaskCount++;
        }

        @Override
        protected Void doInBackground(ImageRequest... requests) {
            for (ImageRequest request : requests) {
                if (request.execute()) {
                    publishProgress(request);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(ImageRequest... values) {
            for (ImageRequest request : values) {
                request.publishResult();
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            mActiveTaskCount--;
            mTasks.remove(this);
            flushTasks();
        }
    }

    private static class ImageError {
        private static final int TIMEOUT = 2 * 60 * 1000; // Two minutes

        private final Throwable mCause;

        private final long mTimestamp;

        public ImageError(Throwable cause) {
            if (cause == null) {
                throw new NullPointerException();
            }
            mCause = cause;
            mTimestamp = now();
        }

        public boolean isExpired() {
            return (now() - mTimestamp) > TIMEOUT;
        }

        public Throwable getCause() {
            return mCause;
        }

        private static long now() {
            return SystemClock.elapsedRealtime();
        }
    }

    /**
     * sheng add.
     *
     * @param imageAdapter
     * @param iv_iamge
     * @param data
     * @param defaultBitmap
     * @param mWith
     * @param mHeight
     * @return
     */
    public BindResult bind(BaseAdapter adapter, ImageView view, String url, int defaultBitmap, int iWidth, int iHeight, boolean isQuickLoad) {
        return bind(adapter, view, url, defaultBitmap, iWidth, iHeight, url, false, isQuickLoad);

    }

    public BindResult bind(BaseAdapter adapter, ImageView view, String url, int defaultBitmap, int iWidth, int iHeight, String cacheID, boolean isThumb, boolean isQuickLoad) {
        return bind(adapter, view, url, defaultBitmap, iWidth, iHeight, 0, cacheID, isThumb, isQuickLoad);
    }

    public BindResult bind(BaseAdapter adapter, ImageView view, String url, int defaultBitmap, int iWidth, int iHeight, int iRadius, String cacheID, boolean isThumb, boolean isQuickLoad) {
        return bind(adapter, view, url, defaultBitmap, null, iWidth, iHeight, iRadius, cacheID, isThumb, isQuickLoad);
    }

    public BindResult bind(ImageView view, String url, int defaultBitmap, String thmCacheId, String cacheID, boolean isThumb) {
        return bind(null, view, url, defaultBitmap, thmCacheId, -1, -1, 0, cacheID, isThumb, true);
    }

    /**
     * @param adapter       载入完图片后通知adapter
     * @param view          载入图片的IMAGEVIEW
     * @param url           图片的地址
     * @param defaultBitmap 载入完图片前，默认显示的图片.若为空，则不使用
     * @param thmCacheId    载入完图片前，从cache里取缩略图先显示.若为空，则不使用
     * @param iWidth        最大的图片宽度，若为-1,使用原尺寸.
     * @param iHeight       最大的图片高度，若为-1,使用原尺寸.
     * @param iRadius       特殊图片处理,0为不处理，-1为圆角图片
     * @param cacheID       图片载入完后的cache id.
     * @param isThumb       是否通知META DATA里取缩略图
     * @param isQuickLoad   当前载入任务是否排在队列的最前面.
     * @return
     */
    public BindResult bind(BaseAdapter adapter, ImageView view, String url, int defaultBitmap, String thmCacheId, int iWidth, int iHeight, int iRadius, String cacheID, boolean isThumb,
                           boolean isQuickLoad) {

        // if (adapter == null) {
        // return null;
        // throw new NullPointerException("Adapter is null");
        // }
        if (view == null) {
            return null;
            // throw new NullPointerException("ImageView is null");
        }
        if (url == null) {
            if (defaultBitmap != -1) {
                view.setImageResource(defaultBitmap);
            }
            return null;
            // throw new NullPointerException("URL is null");
        }
        if (cacheID == null)
            cacheID = url;
        mImageViewBinding.put(view, url);
        Bitmap bitmap = getBitmap(cacheID);
        ImageError error = getError(cacheID);
        if (bitmap != null) {
            if (DEBUG_MODE)
                Log.d(LOG_TAG, "GetBmp From MemCache(" + bitmap.getWidth() + "," + bitmap.getHeight() + "):" + url);
            // Log.d(LOG_TAG,"Image in cache mem:"+bitmap.getWidth()+","+bitmap.getHeight());
            view.setImageBitmap(bitmap);
            return BindResult.OK;
        } else {
            // Log.d(LOG_TAG,"Image not in memory:"+url);
            // Clear the ImageView by default.
            // The caller can set their own placeholder
            // based on the return value.

            // view.setImageDrawable(null);
            Bitmap thmBmp = null;
            if (thmCacheId != null) {
                thmBmp = getBitmap(thmCacheId);
                if (thmBmp == null) {
                    thmBmp = GoproDrawableFileCache.getFileCache(thmCacheId);
                    if (thmBmp != null)
                        putBitmap(thmCacheId, thmBmp);
                }
            }
            if (thmBmp != null) {
                view.setImageBitmap(thmBmp);
            } else {
                if (defaultBitmap != -1) {
                    view.setImageResource(defaultBitmap);
                }
            }
            if (error != null) {
                return BindResult.ERROR;
            } else {
                ImageRequest request = new ImageRequest(adapter, view, url, iWidth, iHeight, iRadius, cacheID, isThumb);
                // For adapters, post the latest requests
                // at the front of the queue in case the user
                // has already scrolled past most of the images
                // that are currently in the queue.
                // insertRequestAtFrontOfQueue(request);
                if (isQuickLoad) {
                    insertRequestAtFrontOfQueue(request);
                } else {
                    enqueueRequest(request);
                }

                return BindResult.LOADING;
            }
        }

    }

    /**
     * sheng add.
     *
     * @param imageAdapter
     * @param iv_iamge
     * @param data
     * @param defaultBitmap
     */
    public void bindlLocal(BaseAdapter imageAdapter, ImageView iv_iamge, String data, int defaultBitmap, boolean isQuickLoad) {
        bind(imageAdapter, iv_iamge, data, defaultBitmap, 150, 120, isQuickLoad);

    }

    public void bindlLocalThumbnail(BaseAdapter videoAdapter, ImageView iv_thumb, String data, int commonDefualtVideo, boolean isQuickLoad) {
        bind(videoAdapter, iv_thumb, data, commonDefualtVideo, 150, 120, null, true, isQuickLoad);

    }

    private final static int FADE_IN_TIME_MS = 500;

    private void setFadeInImageBitmap(ImageView view, Bitmap bm) {
        if (view.getDrawable() == null) {
            view.setImageBitmap(bm);
        } else {

            TransitionDrawable td = new TransitionDrawable(new Drawable[]{view.getDrawable(),
                    // new ColorDrawable(android.R.color.transparent),
                    new BitmapDrawable(view.getContext().getResources(), bm)});

            view.setImageDrawable(td);
            td.startTransition(FADE_IN_TIME_MS);
        }
    }

    public static Bitmap createVideoThumbnailFromVimatio(Context ct, String path) {
        return createVideoThumbnailFromVimatio(ct, path, new long[][]{{-1, 2}});
    }

    public static Bitmap createVideoThumbnailFromVimatio(Context ct, String path, long[][] sec) {
        Bitmap bitmap = null;
//        io.vov.vitamio.MediaMetadataRetriever retriever = new io.vov.vitamio.MediaMetadataRetriever(ct);
//        try {
//            retriever.setDataSource(path);
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        try {
//			/*
//			 * if(sec[0][0]>-1) bitmap = retriever.getFrameAtTime(sec[0][0]);
//			 * else
//			 */
//            bitmap = retriever.getFrameAtTime(-1);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        retriever.release();
        FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
        mmr.setDataSource(path);
        mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
        mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
        bitmap = mmr.getFrameAtTime(2000000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST); // frame at 2 seconds
//        byte [] artwork = mmr.getEmbeddedPicture();

        mmr.release();
        return bitmap;
    }

    @SuppressLint("NewApi")
    public static Bitmap createVideoThumbnail(String filePath) {
        if (Build.VERSION.SDK_INT < 10)
            return null;
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            if (filePath.toLowerCase(Locale.getDefault()).startsWith("http")) {
                retriever.setDataSource(filePath, new HashMap<String, String>());
            } else {
                retriever.setDataSource(filePath);
            }
            bitmap = retriever.getFrameAtTime(1);
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                try {
                    retriever.release();
                }
                catch (IOException ex)
                {
                    // Ignore failures while cleaning up.
                }
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }

        if (bitmap == null)
            return null;

        // if (kind == Images.Thumbnails.MINI_KIND) {
        // Scale down the bitmap if it's too large.
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int max = Math.max(width, height);
        if (max > 512) {
            float scale = 512f / max;
            int w = Math.round(scale * width);
            int h = Math.round(scale * height);
            bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
        }
        // } else if (kind == Images.Thumbnails.MICRO_KIND) {
        // bitmap = extractThumbnail(bitmap,
        // TARGET_SIZE_MICRO_THUMBNAIL,
        // TARGET_SIZE_MICRO_THUMBNAIL,
        // OPTIONS_RECYCLE_INPUT);
        // }
        return bitmap;
    }

    public interface BindStateListener {
        public void bindState(BindResult bindResult);
    }

    // public BindStateListener bindStateListener;
	/*
	 * public void setBindStateListener(BindStateListener bindStateListener){
	 * this.bindStateListener=bindStateListener; }
	 */

    public BindResult bind(BaseAdapter adapter, ImageView view, String url, int defaultBitmap, int iWidth, int iHeight, boolean isQuickLoad, BindStateListener bindStateListener) {
        return bind(adapter, view, url, defaultBitmap, iWidth, iHeight, url, false, isQuickLoad, bindStateListener);

    }

    public BindResult bind(BaseAdapter adapter, ImageView view, String url, int defaultBitmap, int iWidth, int iHeight, String cacheID, boolean isThumb, boolean isQuickLoad,
                           BindStateListener bindStateListener) {
        return bind(adapter, view, url, defaultBitmap, iWidth, iHeight, 0, cacheID, isThumb, isQuickLoad, bindStateListener);
    }

    public BindResult bind(BaseAdapter adapter, ImageView view, String url, int defaultBitmap, int iWidth, int iHeight, int iRadius, String cacheID, boolean isThumb, boolean isQuickLoad,
                           BindStateListener bindStateListener) {
        return bind(adapter, view, url, defaultBitmap, null, iWidth, iHeight, iRadius, cacheID, isThumb, isQuickLoad, false, bindStateListener);
    }

    public BindResult bind(ImageView view, String url, int defaultBitmap, String thmCacheId, String cacheID, boolean isThumb, BindStateListener bindStateListener) {
        return bind(null, view, url, defaultBitmap, thmCacheId, -1, -1, 0, cacheID, isThumb, true, false, bindStateListener);
    }

    public BindResult bind(ImageView view, String url, int defaultBitmap, String cacheID, boolean isPreviewImage, BindStateListener bindStateListener) {
        return bind(null, view, url, defaultBitmap, null, -1, -1, 0, cacheID, false, true, isPreviewImage, bindStateListener);
    }


    public BindResult bind(BaseAdapter adapter, ImageView view, String url, int defaultBitmap, String thmCacheId, int iWidth, int iHeight, int iRadius, String cacheID, boolean isThumb,
                           boolean isQuickLoad, boolean isPreviewImage, final BindStateListener bindStateListener) {

        // if (adapter == null) {
        // return null;
        // throw new NullPointerException("Adapter is null");
        // }
        if (view == null) {
            return null;
            // throw new NullPointerException("ImageView is null");
        }
        if (url == null) {
            return null;
            // throw new NullPointerException("URL is null");
        }
        if (cacheID == null)
            cacheID = url;
        mImageViewBinding.put(view, url);
        Bitmap bitmap = getBitmap(cacheID);
        ImageError error = getError(cacheID);
        if (bitmap != null) {
            if (DEBUG_MODE)
                Log.d(LOG_TAG, "GetBmp From MemCache(" + bitmap.getWidth() + "," + bitmap.getHeight() + "):" + url);
            // Log.d(LOG_TAG,"Image in cache mem:"+bitmap.getWidth()+","+bitmap.getHeight());
            view.setImageBitmap(bitmap);

            if (null != bindStateListener)
                bindStateListener.bindState(BindResult.OK);
            return BindResult.OK;
        } else {
            // Log.d(LOG_TAG,"Image not in memory:"+url);
            // Clear the ImageView by default.
            // The caller can set their own placeholder
            // based on the return value.

            // view.setImageDrawable(null);
            Bitmap thmBmp = null;
            if (thmCacheId != null) {
                thmBmp = getBitmap(thmCacheId);
                if (thmBmp == null) {
                    thmBmp = GoproDrawableFileCache.getFileCache(thmCacheId);
                    if (thmBmp != null)
                        putBitmap(thmCacheId, thmBmp);
                }
            }
            if (thmBmp != null) {
                view.setImageBitmap(thmBmp);
                if (null != bindStateListener)
                    bindStateListener.bindState(BindResult.OK);
            } else {
                if (defaultBitmap != -1) {
                    view.setImageResource(defaultBitmap);
                    if (null != bindStateListener)
                        bindStateListener.bindState(BindResult.LOADING);
                }
            }
            if (error != null) {
                if (null != bindStateListener)
                    bindStateListener.bindState(BindResult.ERROR);
                return BindResult.ERROR;
            } else {
                ImageRequest request = new ImageRequest(adapter, view, url, iWidth, iHeight, iRadius, cacheID, isThumb, isPreviewImage, new ImageViewCallback(view, new Callback() {

                    @Override
                    public void onImageLoaded(ImageView view, String url) {
                        if (null != bindStateListener)
                            bindStateListener.bindState(BindResult.OK);
                    }

                    @Override
                    public void onImageError(ImageView view, String url, Throwable error) {

                    }
                }));

                // For adapters, post the latest requests
                // at the front of the queue in case the user
                // has already scrolled past most of the images
                // that are currently in the queue.
                // insertRequestAtFrontOfQueue(request);
                if (isQuickLoad) {
                    insertRequestAtFrontOfQueue(request);
                } else {
                    enqueueRequest(request);
                }
                if (null != bindStateListener)
                    bindStateListener.bindState(BindResult.LOADING);
                return BindResult.LOADING;
            }
        }

    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public BindResult bind(TextView view, String url, Drawable defaultDrawable, String thmCacheId, int iWidth, int iHeight, int iRadius, String cacheID, boolean isThumb, boolean isQuickLoad,
                           final BindStateListener bindStateListener) {

        // if (adapter == null) {
        // return null;
        // throw new NullPointerException("Adapter is null");
        // }
        if (view == null) {
            return null;
            // throw new NullPointerException("ImageView is null");
        }
        if (url == null) {
            return null;
            // throw new NullPointerException("URL is null");
        }
        if (cacheID == null)
            cacheID = url;
        Bitmap bitmap = getBitmap(cacheID);
        ImageError error = getError(cacheID);
        if (bitmap != null) {
            if (DEBUG_MODE)
                Log.d(LOG_TAG, "GetBmp From MemCache(" + bitmap.getWidth() + "," + bitmap.getHeight() + "):" + url);
            // Log.d(LOG_TAG,"Image in cache mem:"+bitmap.getWidth()+","+bitmap.getHeight());
            Drawable draw = new BitmapDrawable(bitmap);
            view.setBackground(draw);

            if (null != bindStateListener)
                bindStateListener.bindState(BindResult.OK);
            return BindResult.OK;
        } else {
            // Log.d(LOG_TAG,"Image not in memory:"+url);
            // Clear the ImageView by default.
            // The caller can set their own placeholder
            // based on the return value.

            // view.setImageDrawable(null);
            Bitmap thmBmp = null;
            if (thmCacheId != null) {

                thmBmp = getBitmap(thmCacheId);
                if (thmBmp == null) {
                    thmBmp = GoproDrawableFileCache.getFileCache(thmCacheId);
                    if (thmBmp != null)
                        putBitmap(thmCacheId, thmBmp);
                }
            }
            if (thmBmp != null) {
                Drawable draw = new BitmapDrawable(thmBmp);
                view.setBackground(draw);
                if (null != bindStateListener)
                    bindStateListener.bindState(BindResult.OK);
            } else {
                if (defaultDrawable != null) {
                    view.setBackground(defaultDrawable);
                    if (null != bindStateListener)
                        bindStateListener.bindState(BindResult.LOADING);
                }
            }
            if (error != null) {
                if (null != bindStateListener)
                    bindStateListener.bindState(BindResult.ERROR);
                return BindResult.ERROR;
            } else {

                if (null != bindStateListener)
                    bindStateListener.bindState(BindResult.LOADING);
                return BindResult.LOADING;
            }
        }

    }

    public BindResult bind(BaseAdapter adapter, ImageView view, String url, int defaultBitmap, String thmCacheId, int iWidth, int iHeight, int iRadius, String cacheID, boolean isThumb,
                           boolean isQuickLoad, long[][] sec, final BindStateListener bindStateListener) {

        if (view == null) {
            return null;
        }
        if (url == null) {
            return null;
        }
        if (cacheID == null)
            cacheID = url;
        mImageViewBinding.put(view, url);
        Bitmap bitmap = getBitmap(cacheID);
        ImageError error = getError(cacheID);
        if (bitmap != null) {
            if (DEBUG_MODE)
                Log.d(LOG_TAG, "GetBmp From MemCache(" + bitmap.getWidth() + "," + bitmap.getHeight() + "):" + url);
            view.setImageBitmap(bitmap);
            if (null != bindStateListener)
                bindStateListener.bindState(BindResult.OK);
            return BindResult.OK;
        } else {
            Bitmap thmBmp = null;
            if (thmCacheId != null) {
                thmBmp = getBitmap(thmCacheId);
                if (thmBmp == null) {
                    thmBmp = GoproDrawableFileCache.getFileCache(thmCacheId);
                    if (thmBmp != null)
                        putBitmap(thmCacheId, thmBmp);
                }
            }
            if (thmBmp != null) {
                view.setImageBitmap(thmBmp);
                if (null != bindStateListener)
                    bindStateListener.bindState(BindResult.OK);
            } else {
                if (defaultBitmap != -1) {
                    view.setImageResource(defaultBitmap);
                    if (null != bindStateListener)
                        bindStateListener.bindState(BindResult.LOADING);
                }
            }
            if (error != null) {
                if (null != bindStateListener)
                    bindStateListener.bindState(BindResult.ERROR);
                return BindResult.ERROR;
            } else {
                ImageRequest request = new ImageRequest(adapter, view, url, iWidth, iHeight, iRadius, cacheID, isThumb, sec, new ImageViewCallback(view, new Callback() {

                    @Override
                    public void onImageLoaded(ImageView view, String url) {
                        if (null != bindStateListener)
                            bindStateListener.bindState(BindResult.OK);
                    }

                    @Override
                    public void onImageError(ImageView view, String url, Throwable error) {

                    }
                }));

                // For adapters, post the latest requests
                // at the front of the queue in case the user
                // has already scrolled past most of the images
                // that are currently in the queue.
                // insertRequestAtFrontOfQueue(request);
                if (isQuickLoad) {
                    insertRequestAtFrontOfQueue(request);
                } else {
                    enqueueRequest(request);
                }
                if (null != bindStateListener)
                    bindStateListener.bindState(BindResult.LOADING);
                return BindResult.LOADING;
            }
        }
    }

//    private Bitmap imagefilter(Bitmap bitmap, IImageFilter filter) {
//        Image img = null;
//        try {
//            img = new Image(bitmap);
//            if (filter != null) {
//                img = filter.process(img);
//                img.copyPixelsFromBuffer();
//            }
//            return img.getImage();
//        } catch (Exception e) {
//            if (img != null && img.destImage.isRecycled()) {
//                img.destImage.recycle();
//                img.destImage = null;
//                System.gc();
//            }
//        } finally {
//            if (img != null && img.image.isRecycled()) {
//                img.image.recycle();
//                img.image = null;
//                System.gc();
//            }
//        }
//        return null;
//    }


}
