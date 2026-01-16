package com.foreamlib.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class HttpConnection {
	
	/**
	 * 
	 * @Title: isReachable
	 * @Description: TODO(���Է������Ƿ����ͨ)
	 * @param @param url
	 * @param @return
	 * @param @throws IOException    
	 * @return boolean    
	 * @throws
	 */

	public static boolean isReachable(String host){
		boolean isReachable = false;
		try {
			InetAddress inte = InetAddress.getByName(host);
			isReachable = inte.isReachable(3*1000);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return isReachable;
	}
	
	/**
	 * http������json��ʽ���Ͳ��� mainUrl ��Ҫurl������������� param ��������� ��url�����еĲ����岿��
	 */
	public static String HttpDoPostByJson(String homeUrl, JSONObject param) {
		String result = null;
		try {
			HttpPost httpPost = new HttpPost(homeUrl);
			// �󶨵����� Entry
			StringEntity se;
			se = new StringEntity(param.toString());
			httpPost.setEntity(se);
			HttpResponse httpResponse;
			httpResponse = new DefaultHttpClient().execute(httpPost);
			result = EntityUtils.toString(httpResponse.getEntity());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ����ͨ�Ĵ����ķ�ʽ����http ����
	 * 
	 * @param mainUrl
	 *            �������� ��url��ַ
	 * @param paramsList
	 *            �����б�
	 * @return ������Ӧ���ַ�
	 */

	public static String HttpDoPostByValuePair(String homeUrl,
			List<NameValuePair> paramsList) {
		try {

			BasicNameValuePair nameValuePair1 = new BasicNameValuePair(
					"source", "1000");
			BasicNameValuePair nameValuePair2 = new BasicNameValuePair(
					"status", "1000");

			HttpPost httpPost = new HttpPost(homeUrl);
			httpPost.setEntity(new UrlEncodedFormEntity(paramsList, HTTP.UTF_8));
			HttpResponse Response = new DefaultHttpClient().execute(httpPost);
			if (Response.getStatusLine().getStatusCode() == 200) // ״̬��
			{
				String result = EntityUtils.toString(Response.getEntity());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String sendGetRequest(String path,
			Map<String, Object> params, String enc) throws Exception {
		StringBuilder sb = new StringBuilder(path);
		sb.append('?');
		if (params != null) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				sb.append(entry.getKey())
						.append('=')
						.append(URLEncoder.encode(entry.getValue().toString(),
								enc)).append('&');
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		URL url = new URL(sb.toString());
		System.out.println("�����ַ-->" + sb.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(15 * 1000);
		conn.setReadTimeout(15 * 1000);
		if (conn.getResponseCode() == 200) {
			byte[] data = FileUtil.readInputStream(conn.getInputStream());
			return new String(data);
		}
		return null;
	}

	public static String getURL(String path, Map<String, Object> params,
			String enc) {

		StringBuilder sb = new StringBuilder(path);
		sb.append('?');
		if (params != null) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				try {
					sb.append(entry.getKey())
							.append('=')
							.append(URLEncoder.encode(entry.getValue()
									.toString(), enc)).append('&');
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}

		sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}

	public static InputStream sendGetRequest(String path,
			Map<String, Object> params) throws Exception {
		StringBuilder sb = new StringBuilder(path);
		sb.append('?');
		if (params != null) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				sb.append(entry.getKey())
						.append('=')
						.append(URLEncoder.encode(entry.getValue().toString(),
								"UTF-8")).append('&');
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		URL url = new URL(sb.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		if (conn.getResponseCode() == 200) {
			return conn.getInputStream();
		}
		return null;
	}

	public static String sendPostRequest(String path,
			Map<String, Object> params, String enc) throws Exception {
		StringBuilder sb = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				System.out.println(entry.getValue());
				sb.append(entry.getKey())
						.append('=')
						.append(URLEncoder.encode(entry.getValue().toString(),
								enc)).append('&');
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		byte[] entitydata = sb.toString().getBytes();
		System.out.println("�����ַ-->" + sb.toString());
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setConnectTimeout(15 * 1000);
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length",
				String.valueOf(entitydata.length));
		OutputStream outStream = conn.getOutputStream();
		outStream.write(entitydata);
		outStream.flush();
		outStream.close();
		if (conn.getResponseCode() == 200) {
			byte[] data = FileUtil.readInputStream(conn.getInputStream());
			return new String(data);
		}
		return null;
	}

	// �ϴ�ͼƬ��������

	public static String uploadImageToServer(String path,
			Map<String, Object> params, String enc, Bitmap bmp)
			throws Exception {
		StringBuilder sb = new StringBuilder(path);
		sb.append('?');
		if (params != null) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				sb.append(entry.getKey())
						.append('=')
						.append(URLEncoder.encode(entry.getValue().toString(),
								"UTF-8")).append('&');
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		URL url = new URL(sb.toString());
		HttpPost httpPost = new HttpPost(sb.toString());
		byte[] data = BitmapUtil.bitmapToBytes(bmp, Bitmap.CompressFormat.PNG);
		httpPost.setEntity(new ByteArrayEntity(data));
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);
		HttpResponse httpResponse = httpClient.execute(httpPost);
		String resultStr = null;
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			resultStr = EntityUtils.toString(httpResponse
					.getEntity());
			return resultStr;
		}else {
			return null;
		}		
	}
	// �ϴ��ļ���������
	public static String uploadFileToServer(String path,
			Map<String, Object> params, String enc,byte[] data )
			throws Exception {
		StringBuilder sb = new StringBuilder(path);
		sb.append('?');
		if (params != null) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				sb.append(entry.getKey())
						.append('=')
						.append(URLEncoder.encode(entry.getValue().toString(),
								"UTF-8")).append('&');
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		URL url = new URL(sb.toString());
		System.out.println("�����ַ-->" + sb.toString());
		HttpPost httpPost = new HttpPost(sb.toString());
		httpPost.setEntity(new ByteArrayEntity(data));
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);
		HttpResponse httpResponse = httpClient.execute(httpPost);
		String resultStr = null;
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			resultStr = EntityUtils.toString(httpResponse
					.getEntity());
			return resultStr;
		}else {
			return null;
		}		
	}

	public static boolean sendRequestFromHttpClient(String path,
			Map<String, Object> params, String enc) throws Exception {
		List<NameValuePair> paramPairs = new ArrayList<NameValuePair>();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				paramPairs.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue().toString()));
			}
		}
		UrlEncodedFormEntity entitydata = new UrlEncodedFormEntity(paramPairs,
				enc);
		HttpPost post = new HttpPost(path);
		post.setEntity(entitydata);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(post);
		if (response.getStatusLine().getStatusCode() == 200) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ���ͼƬ
	 */
	public static Bitmap loadImage(String imageUrl) {
		try {
			return HttpConnection.disposeImage(imageUrl);
		} catch (Exception e) {
			Log.e("HttpConnetion", e.toString());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * ���ͼƬ�����ַ��ȡͼƬ��byte[]�������
	 * 
	 * @param urlPath
	 *            ͼƬ�����ַ
	 * @return ͼƬ���
	 */
	public static byte[] getDataFromURL(String urlPath) {
		byte[] data = null;
		InputStream is = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlPath);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(6000);
			is = conn.getInputStream();
			if (conn.getResponseCode() == 200) {
				data = readInputStream(is);
			} else
				System.out.println("�����쳣��");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.disconnect();
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	/**
	 * ��ȡInputStream��ݣ�תΪbyte[]�������
	 * 
	 * @param is
	 *            InputStream���
	 * @return ����byte[]���
	 */
	public static byte[] readInputStream(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = -1;
		try {
			while ((length = is.read(buffer)) != -1) {
				baos.write(buffer, 0, length);
			}
			baos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] data = baos.toByteArray();
		try {
			is.close();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	public static int computeSampleSize(Options options,
                                        int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(Options options,
                                                int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static Bitmap disposeDefaltImage(String fyThumb) {
		try {

			Bitmap bitmap = null;
			byte[] imageByte = HttpConnection.getDataFromURL(fyThumb);
			bitmap = BitmapFactory.decodeByteArray(imageByte, 0,
					imageByte.length);
			return bitmap;
		} catch (NullPointerException e) {
			// return BitmapUtil.getBitmapById(context,
			// id)(R.drawable.expander_ic_folder);
			return null;
		}
	}

	public static Bitmap disposeImage(String fyThumb) {
		Bitmap bitmap = null;
		byte[] imageByte = HttpConnection.getDataFromURL(fyThumb);
		if (imageByte == null) {
			return null;
		}else {
			Options options = new Options();
			options.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length,
					options);
			options.inJustDecodeBounds = false;
			options.inSampleSize = HttpConnection.computeSampleSize(options, -1,
					128 * 128);
			try {
				bitmap = BitmapFactory.decodeByteArray(imageByte, 0,
						imageByte.length, null);
			} catch (OutOfMemoryError err) {
			}
		}
		return bitmap;
	}

	public static Bitmap disposeImage(String fyThumb, int width, int height) {
		try {
			Bitmap bitmap = null;
			try {
			
			byte[] imageByte = HttpConnection.getDataFromURL(fyThumb);
			Options options = new Options();
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

	/**
	 * http������json��ʽ���Ͳ���
	 * 
	 * @param homeUrl
	 *            ��������URL��ַ
	 * @param param
	 *            ����
	 * @return
	 */
	public static String httpDoPostByJson(String homeUrl, JSONObject param) {
		String result = null;
		try {
			HttpPost httpPost = new HttpPost(homeUrl);
			// �󶨵����� Entry
			StringEntity se;
			se = new StringEntity(param.toString());
			httpPost.setEntity(se);
			HttpResponse httpResponse;
			httpResponse = new DefaultHttpClient().execute(httpPost);
			result = EntityUtils.toString(httpResponse.getEntity());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ����ͨ�Ĵ����ķ�ʽ����http ����
	 * 
	 * @param mainUrl
	 *            �������� ��url��ַ
	 * @param paramsList
	 *            �����б�
	 * @return ������Ӧ���ַ�
	 */

	public static String httpDoPostByValuePair(String homeUrl,
			List<NameValuePair> paramsList) {
		try {
			HttpPost httpPost = new HttpPost(homeUrl);
			httpPost.setEntity(new UrlEncodedFormEntity(paramsList, HTTP.UTF_8));
			HttpResponse Response = new DefaultHttpClient().execute(httpPost);
			if (Response.getStatusLine().getStatusCode() == 200) // ״̬��
			{
				String result = EntityUtils.toString(Response.getEntity());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ��get��ʽ���������
	 * 
	 * @param homeUrl
	 *            ����ǰ�벿�ֵ�URL��ַ
	 * @param params
	 *            �����
	 * @param enc
	 *            ���뷽ʽ
	 * @return
	 * @throws Exception
	 */
	public static String httpGetByMapParams(String homeUrl,
			Map<String, Object> params, String enc) throws Exception {
		StringBuilder sb = new StringBuilder(homeUrl);
		sb.append('?');
		if (params != null) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				sb.append(entry.getKey())
						.append('=')
						.append(URLEncoder.encode(entry.getValue().toString(),
								enc)).append('&');
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		URL url = new URL(sb.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(6 * 1000);
		conn.setReadTimeout(6 * 1000);
		if (conn.getResponseCode() == 200) {
			byte[] data = FileUtil.readInputStream(conn.getInputStream());
			return new String(data);
		}
		return null;
	}

	/**
	 * ��ȡ��get��ʽ����� url��ַ
	 * 
	 * @param homeUrl
	 *            ����ǰ�벿�ֵ�URL��ַ
	 * @param params
	 *            ����
	 * @param enc
	 *            ���뷽ʽ
	 * @return
	 */
	public static String getHttpURL(String homeUrl, Map<String, Object> params,
			String enc) {
		StringBuilder sb = new StringBuilder(homeUrl);
		sb.append('?');
		if (params != null) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				try {
					sb.append(entry.getKey())
							.append('=')
							.append(URLEncoder.encode(entry.getValue()
									.toString(), enc)).append('&');
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	/**
	 * ��ȡ������������
	 * 
	 * @param homeUrl
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static InputStream getRequestStream(String homeUrl,
			Map<String, Object> params) throws Exception {
		StringBuilder sb = new StringBuilder(homeUrl);
		sb.append('?');
		if (params != null) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				sb.append(entry.getKey())
						.append('=')
						.append(URLEncoder.encode(entry.getValue().toString(),
								"UTF-8")).append('&');
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		URL url = new URL(sb.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		if (conn.getResponseCode() == 200) {
			return conn.getInputStream();
		}
		return null;
	}

	/**
	 * ��post��ʽ�������������ص��������
	 * 
	 * @param homeUrl
	 * @param params
	 * @param enc
	 * @return
	 * @throws Exception
	 */
	public static String httpDoPostByMapParams(String homeUrl,
			Map<String, String> params, String enc) throws Exception {
		StringBuilder sb = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				sb.append(entry.getKey()).append('=')
						.append(URLEncoder.encode(entry.getValue(), enc))
						.append('&');
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		byte[] entitydata = sb.toString().getBytes();
		URL url = new URL(homeUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setConnectTimeout(5 * 1000);
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length",
				String.valueOf(entitydata.length));
		OutputStream outStream = conn.getOutputStream();
		outStream.write(entitydata);
		outStream.flush();
		outStream.close();
		if (conn.getResponseCode() == 200) {
			byte[] data = FileUtil.readInputStream(conn.getInputStream());
			return new String(data);
		}
		return null;
	}

	/**
	 * ���ͼƬ�����ַ��ȡͼƬ��byte[]�������
	 * 
	 * @param urlPath
	 *            ͼƬ�����ַ
	 * @return ͼƬ���
	 */
	public static byte[] getRemoteImageBytes(String urlStr) {

		try {

			byte[] data = null;
			InputStream is = null;
			HttpURLConnection conn = null;
			try {
				URL url = new URL(urlStr);
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true);
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(6000);
				is = conn.getInputStream();
				if (conn.getResponseCode() == 200) {
					data = getBytes(is);
				} else
					System.out.println("�����쳣��");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (conn != null)
						conn.disconnect();
					if (is != null)
						is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return data;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ��ȡInputStream��ݣ�תΪbyte[]�������
	 * 
	 * @param is
	 *            InputStream���
	 * @return ����byte[]���
	 */
	public static byte[] getBytes(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = -1;
		try {
			while ((length = is.read(buffer)) != -1) {
				baos.write(buffer, 0, length);
			}
			baos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] data = baos.toByteArray();
		try {
			is.close();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * �������ͼƬ��ַ��������ȡ����ͼƬ
	 * 
	 * @param urlPath
	 *            ����ͼƬ��ַ����
	 * @return ����Bitmap������͵�����
	 */
	public static Bitmap[] getBitmapArray(String[] urlPath) {
		int length = urlPath.length;
		if (urlPath == null || length < 1) {
			return null;
		} else {
			Bitmap[] bitmaps = new Bitmap[length];
			for (int i = 0; i < length; i++) {
				byte[] imageByte = getRemoteImageBytes(urlPath[i].trim());
				// �����ǰ�ͼƬת��Ϊ����ͼ�ټ���
				Options options = new Options();
				options.inJustDecodeBounds = true;
				Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0,
						imageByte.length, options);
				options.inJustDecodeBounds = false;
				int be = (int) (options.outHeight / (float) 200);
				if (be <= 0)
					be = 1;
				options.inSampleSize = be;
				bitmaps[i] = BitmapFactory.decodeByteArray(imageByte, 0,
						imageByte.length, options);
			}
			return bitmaps;
		}
	}

	public static Bitmap httpDownLoadBitmap(String imageUrl) {
		try {
			byte[] imageByte = HttpConnection.getRemoteImageBytes(imageUrl);
			return BitmapFactory
					.decodeByteArray(imageByte, 0, imageByte.length);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap httpDownLoadBitmap(String imageUrl, Options options) {
		Bitmap bitmap = null;
		byte[] imageByte = HttpConnection.getRemoteImageBytes(imageUrl);
		try {
			bitmap = BitmapFactory.decodeByteArray(imageByte, 0,
					imageByte.length, options);
		} catch (OutOfMemoryError err) {
		}
		return bitmap;
	}
	/**
	 * 
	 * Desc:�ļ�����
	 * 
	 * @param downloadUrl
	 *            ����URL
	 * @param saveFilePath
	 *            �����ļ�·��
	 * @return ture:���سɹ� false:����ʧ��
	 */
	public static boolean downloadFile(String downloadUrl, File saveFilePath) {
		int fileSize = -1;
		int downFileSize = 0;
		boolean result = false;
		int progress = 0;
		try {
			URL url = new URL(downloadUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (null == conn) {
				return false;
			}
			// ��ȡ��ʱʱ�� ���뼶
			conn.setReadTimeout(10000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				fileSize = conn.getContentLength();
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(saveFilePath);
				byte[] buffer = new byte[1024];
				int i = 0;
				int tempProgress = -1;
				while ((i = is.read(buffer)) != -1) {
					downFileSize = downFileSize + i;
					// ���ؽ��
					progress = (int) (downFileSize * 100.0 / fileSize);
					fos.write(buffer, 0, i);
				}
				fos.flush();
				fos.close();
				is.close();
				result = true;
			} else {
				result = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}
}