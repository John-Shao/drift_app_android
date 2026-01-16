package com.foreamlib.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.foreamlib.util.BitmapUtil;
import com.foreamlib.util.HttpConnection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;


public class GoproDrawableFileCache {
	private static final String TempFileName = "tempDownload.dat";
	private static final String TAG = "GoproDrawableFileCache";
	private static String CACHE_DIR = "/FileCache/";
	private static Context mContext;
	private final static LinkedList<LoadFileTask> mTasks = new LinkedList<LoadFileTask>();
	private static int mActiveTaskCount = 0;
	private static int mMaxTaskCount = 1;
	private static Map<String,WeakReference<DownloadFileListener>> mapListener;
	private static Set<String> cacheSet;
	private static String cacheRootPath = null;
	
	public static void addFileCache(Bitmap bitmap, String filename){
		if(bitmap==null||filename==null)return;
		BitmapUtil.saveBitmap(bitmap,getCacheDir() + filterCacheID(filename));
	}
	public static String filterCacheID(String id){
		return id.replaceAll("[^(0-9a-zA-Z\\.)]", "");
	}
	public static void removeFileCache(String cacheId){
		String path = getFileCachePath(cacheId);
		if(path!=null){
			File file = new File(path);
			file.delete();
		}
	}

	public static Bitmap getFileCache(String fileName) {
		Bitmap result = null;
		if (fileName == null)
			return null;
		if (getFileCachePath(fileName) == null)
			return null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		opts.inPurgeable = true;
		opts.inJustDecodeBounds = false;
		try {
			result = BitmapFactory.decodeFile(getCacheDir() + filterCacheID(fileName), opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static String generateFileCachePath(String fileName)
	{
		return getCacheDir()+filterCacheID(fileName);
	}
	public static String getFileCachePath(String fileName)
	{
		if(fileName==null)return null;
		String cacheID = filterCacheID(fileName);
		File file = new File(getCacheDir()+cacheID);
		if(file.exists()){
			return getCacheDir()+cacheID;
		}else{
			return null;
		}
	}
	public static boolean initFileCache(Context context)
 {
		boolean internal = false;
		mContext = context;
		if (getCacheDir() == null)
			return false;
		File fileCache = new File(getCacheDir());
		cacheSet = new HashSet<String>();
		mapListener = new HashMap<String, WeakReference<DownloadFileListener>>();
		if (!fileCache.exists()) {
			fileCache.mkdirs();
		}
		File[] mFiles = fileCache.listFiles();
		// 超过200个文件后，隔张删除
		// if (mFiles.length > 200) {
		// for (File mFile:mFiles) {
		// if(internal)mFile.delete();
		// internal = !internal;
		// }
		// }
		mFiles = fileCache.listFiles();
		if (mFiles != null) {
			for (File mFile : mFiles) {
				cacheSet.add(mFile.getName());
			}
		}
		return true;

	}

	private static String getCacheDir()
	{
		if(cacheRootPath==null){

			if(mContext.getExternalCacheDir()==null){
				cacheRootPath = mContext.getCacheDir().getPath();
			}else{
				cacheRootPath = mContext.getExternalCacheDir().getPath();
			}
		}
		return cacheRootPath+CACHE_DIR;
	}
	public static void killAllTask(){
		Log.d(TAG,"killAllTask");
		while(!mTasks.isEmpty()){
			mTasks.poll().cancel();
		}
		mActiveTaskCount = 0;
	}
//	/**
//	 *
//	 * @param listener
//	 * @param bFromMetaData
//	 * @param image
//	 * @return if it has cache.
//	 */
//	public static boolean bindImage(String url,String cacheID,DownloadFileListener listener, boolean bFromMetaData,boolean bForceDownload) {
//		boolean bHaveCache = false;
//
//		String cachePath = getFileCachePath(cacheID);
//		Bitmap bitmap = null;
//		if (cachePath != null) {
//			try {
//				bitmap = BitmapUtil.readLocalImage2(cachePath);
//				if (bitmap != null && bitmap.getWidth() > 0
//						&& bitmap.getHeight() > 0) {
//					bHaveCache = true;
//				}
//			} catch (OutOfMemoryError e) {
//				// The VM does not always free-up memory as it should,
//				// so manually invoke the garbage collector
//				// and try loading the image again.
//				Log.e("Imageloader2", "out of memory.free-up now.");
//				System.gc();
//			}
//		}
//		if (!bHaveCache||bForceDownload) {
//
//			// run all binding function in another thread, to make UI smooth.
//			if(listener!=null){
//				listener.onPreExecute();
//				mapListener.put(url,new WeakReference<DownloadFileListener>(listener));
//			}
//
//			LoadFileTask task = new LoadFileTask(url,cacheID, mapListener, bFromMetaData,bForceDownload);
//			mTasks.add(0,task);// in this mode.We also load the latest file
//							// first.
//			flushTask();
//			return false;
//		} else {
//			//if(imageView!=null)imageView.reloadImageBitmap(bitmap);
//			listener.onPostExecute(bitmap, url);
//			return true;
//		}
//
//	}

	private static void flushTask(){
		if (mActiveTaskCount < mMaxTaskCount) {
			if (!mTasks.isEmpty()) {
				mActiveTaskCount++;
				LoadFileTask task = mTasks.getFirst();
				mTasks.remove(task);
				task.execute("");
				//Log.d(TAG,"Flush task:"+task.getImage().getMediaItemsData());
			}
		}
	}
	
	private static class LoadFileTask extends AsyncTask<String, Integer, Bitmap> {

		private final Map<String,WeakReference<DownloadFileListener>> mListener;
		//private final WeakReference<ImageView> mImageView;
		private final String mCacheID;
		private final String mediaItemsData;
		private final boolean bThumbnail;
		private final boolean bForceDL;
		private int preProgress = 0;
		private boolean cancelled = false;
		
		public LoadFileTask(String url,String cacheID,Map<String,WeakReference<DownloadFileListener>> listener,boolean bFromMetaData,boolean bForceDownload) {
			mCacheID = filterCacheID(cacheID);
			mListener = listener;
			//mImageView = new WeakReference<ImageView>(imageview);
			bThumbnail = bFromMetaData;
			bForceDL = bForceDownload;
			
			
			mediaItemsData = url;
			
		}
		public void cancel(){
			super.cancel(true);
			cancelled =true;
		}

		
		
		@Override
		protected void onPreExecute() {
			
			WeakReference<DownloadFileListener> listener= mListener.get(mediaItemsData);
			if(listener.get()!=null){
				listener.get().onPreExecute();
			}
			super.onPreExecute();
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bitmap = null;
			String cachePath = getFileCachePath(mCacheID);
			boolean bHaveCache = false;
			//load bitmap from local cache.
			if(cachePath!=null){
				try{
						bitmap = BitmapUtil.readLocalImage2(cachePath);

					
					if(bitmap!=null&&bitmap.getWidth()>0&&bitmap.getHeight()>0){
						bHaveCache = true;
					}
				}catch (OutOfMemoryError e) {
                    // The VM does not always free-up memory as it should,
                    // so manually invoke the garbage collector
                    // and try loading the image again.
                	Log.e(TAG,"out of memory.free-up now.");
                    System.gc();
                    bitmap = BitmapUtil.readLocalImage2(cachePath);
                    if(bitmap!=null&&bitmap.getWidth()>0&&bitmap.getHeight()>0){
						bHaveCache = true;
					}
                }
			} 
			//load bitmap from url.
			if (!bHaveCache||bForceDL) {
				try {
					if (bThumbnail) {
						bitmap = BitmapUtil.getThumbnailFromMetaData(new URL(
								mediaItemsData));

					} else {

						String tmpFilePath = getCacheDir()
								+ GoproDrawableFileCache.TempFileName;
						/*
						 * GoproDrawableFileCache.downLoadFile( mediaItemsData,
						 * tmpFilePath, mListener.get());
						 */
						
						try {
							
							URL url = new URL(mediaItemsData);
							HttpURLConnection connection = (HttpURLConnection) url
									.openConnection();
							//connection.addRequestProperty(field, newValue)
							connection.setReadTimeout(5000);
							InputStream input = connection.getInputStream();
							int size = connection.getContentLength();
							int progressPos = 0;
							OutputStream output = null;
							
							try {
								File file = new File(tmpFilePath); // �����ļ�
								output = new FileOutputStream(file);
								byte[] buffer = new byte[16 * 1024];
								int temp;
								
								while ((temp = input.read(buffer)) >0) {
									int nowProgress;
									
									output.write(buffer, 0, temp);
									
									progressPos += temp;
									nowProgress = progressPos * 100 / size;
									//Log.d(TAG,nowProgress+"");
									if(nowProgress>preProgress){
										preProgress = nowProgress;
										publishProgress(nowProgress);
									}
									if(cancelled){
										break;
									}
								}
								bitmap = BitmapUtil.loadBitmap(tmpFilePath);
								// return true;
							}catch (OutOfMemoryError e){
								 // The VM does not always free-up memory as it should,
			                    // so manually invoke the garbage collector
			                    // and try loading the image again.
								e.printStackTrace();
			                	Log.e(TAG,"out of memory.free-up now.");
			                    System.gc();
								bitmap = BitmapUtil.loadBitmap(tmpFilePath);
							} finally {
								input.close();
								output.flush();
								connection.disconnect();
								try {
									output.close();

								} catch (Exception e2) {
									e2.printStackTrace();
								}

							}
	
						} catch (Exception e) {
							e.printStackTrace();

						}
					
						
					}
				} catch (OutOfMemoryError e) {
					// The VM does not always free-up memory as it should,
					// so manually invoke the garbage collector
					// and try loading the image again.
					Log.e(TAG, "out of memory.free-up now.");
					System.gc();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (bitmap != null && bitmap.getWidth() > 0
						&& bitmap.getHeight() > 0&&!cancelled) {
						
					GoproDrawableFileCache.addFileCache(bitmap, mCacheID);
				}
			}
			return bitmap;
			// return loadBMP(params[0],params[1]);
		}
		@Override 
		protected void onProgressUpdate(Integer... values){
			super.onProgressUpdate(values);

			WeakReference<DownloadFileListener> listener= mListener.get(mediaItemsData);
			if(listener.get()!=null){
				//Log.d(TAG, "listener size:"+mListener.size());
				listener.get().onProgressUpdate(values[0], mediaItemsData);
			}else{
				Log.e(TAG,"mListener is null");
			}
		}
		

		@Override
		protected void onPostExecute(Bitmap result) {
			
			mActiveTaskCount --;
			WeakReference<DownloadFileListener> listener= mListener.get(mediaItemsData);
			if(listener.get()!=null){
				//if(result!=null&&result.getWidth()>0&&result.getHeight()>0){
					listener.get().onPostExecute(result, mediaItemsData);
				//}
			}else{
				Log.d(TAG,"mListener is null");
			}
			//if(mImageView.get()!=null){
			//	mImageView.get().setImageBitmap(result);
			//}
			flushTask();
			super.onPostExecute(result);
		}
	};
	public interface DownloadFileListener{
		
		void onPreExecute();
		void onProgressUpdate(int progress, String url);
		void onPostExecute(Bitmap result, String url);
	}
	/*
	public static boolean downLoadFile(String urlstr, String path,LoadFileTask task) {

		try {
			//System.out.println("�����Ƕ���-->" + urlstr + ",");
			URL url = new URL(urlstr);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			InputStream com.input = connection.getInputStream();
			int size = connection.getContentLength();
			int progressPos = 0;
			OutputStream output = null;
			try {
				File file = new File(path); // �����ļ�
				output = new FileOutputStream(file);
				byte[] buffer = new byte[4 * 1024];
				int temp;
				while ((temp = com.input.read(buffer)) != -1) {
					output.write(buffer, 0, temp);
					progressPos += temp;
					if(task!=null)task..onProgressUpdate(progressPos*100/size);
				}
				output.flush();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					output.close();
					
				} catch (Exception e2) {
					e2.printStackTrace();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return false;

	}
	*/
	public static Bitmap disposeImage(String fyThumb, int width, int height) {
		try {
			Bitmap bitmap = null;
			try {
			
			byte[] imageByte = HttpConnection.getDataFromURL(fyThumb);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeByteArray(imageByte, 0,
					imageByte.length, options);
			options.inJustDecodeBounds = false;
			options.inSampleSize = HttpConnection.computeSampleSize(options,
					-1, width * height);
			
				bitmap = BitmapFactory.decodeByteArray(imageByte, 0,
						imageByte.length, options);
			} catch (OutOfMemoryError err) {
				System.gc();
			}
			return bitmap;
		} catch (NullPointerException e) {
			// return
			// BitmapUtil.getBitmapById(R.drawable.news_top_default_icon);
			return null;
		}
	}
}
