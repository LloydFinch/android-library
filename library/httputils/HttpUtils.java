package com.example.HttpUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by VennUser on 2015/8/24.
 */
public final class HttpUtils {

	//基于http的GET请求
	public static byte[] httpGet(String path) throws IOException {
		byte[] data = null;
		URL url = new URL(path);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setConnectTimeout(5000);
		connection.setInstanceFollowRedirects(true);
		connection.setDoInput(true);
		if (connection.getResponseCode() == 200) {
			int length = 0;
			InputStream in = connection.getInputStream();
			while (true) {
				length = in.read(data);
				if (length == -1) {
					break;
				}
			}
		}

		return data;
	}

	//基于http的POST请求
	public static byte[] httpPost(String path, HashMap<String, String> params, String encode) throws IOException {
		byte[] data = null;

		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(entry.getKey())
					.append("=")
					.append(URLEncoder.encode(entry.getValue(), encode))
					.append("&");
		}
		sb.deleteCharAt(sb.length() - 1);

		URL url = new URL(path);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setConnectTimeout(5000);
		connection.setDoInput(true);
		connection.setDoOutput(true);

		byte request[] = sb.toString().getBytes();
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length", String.valueOf(request.length));

		OutputStream out = connection.getOutputStream();
		out.write(request, 0, request.length);
		out.close();

		if (connection.getResponseCode() == 200) {
			InputStream in = connection.getInputStream();
			int length = 0;
			while (true) {
				length = in.read(data);
				if (length == -1) {
					break;
				}
			}
		}

		return data;
	}

	//基于工具类的GET请求
	public static byte[] sendByGet(String path) throws IOException {
		byte[] data = null;

		HttpGet httpGet = new HttpGet(path);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(httpGet);

		if (response.getStatusLine().getStatusCode() == 200) {
			InputStream in = response.getEntity().getContent();
			int length = 0;
			while (true) {
				length = in.read(data);
				if (length == -1) {
					break;
				}
			}
		}

		return data;
	}

	//基于工具类的POST请求
	public static byte[] sendByPost(String path, HashMap<String, String> params, String encode) throws IOException {
		byte[] data = null;

		List<NameValuePair> list = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, encode);

		HttpPost httpPost = new HttpPost(path);
		httpPost.setEntity(entity);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(httpPost);

		if (response.getStatusLine().getStatusCode() == 200) {
			InputStream in = response.getEntity().getContent();
			int length = 0;
			while (true) {
				length = in.read(data);
				if (length == -1) {
					break;
				}
			}
		}

		return data;
	}
}
