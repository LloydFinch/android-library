package com.example.HttpUtils;

import android.content.Context;
import android.os.Environment;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by VennUser on 2015/8/24.
 */
public final class FileCache {

	public static FileCache fileCache;
	private Context context;

	private FileCache(Context context) {
		this.context = context;
	}

	public static void creteInstance(Context context) {
		if (context != null) {
			if (fileCache == null) {
				fileCache = new FileCache(context);
			}
		} else {
			throw new IllegalArgumentException("Context must be set");
		}
	}

	public static FileCache getInstance() {
		if (fileCache != null) {
			return fileCache;
		} else {
			throw new IllegalArgumentException("You must invoke CreateInstance(Context) before this");
		}
	}

	//存放数据
	public void saveFile(String key, byte[] data) {
		if (key != null && data != null) {
			String name = ChangeMD5(key);
			File cacheDir = null;
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				cacheDir = context.getExternalCacheDir();
			} else {
				cacheDir = context.getCacheDir();
			}
			File file = new File(cacheDir, name);
			FileOutputStream fot = null;
			try {
				fot = new FileOutputStream(file);
				fot.write(data);
				fot.flush();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					fot.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//加载数据
	public byte[] loadFile(String key) {
		byte[] data = null;
		if (key != null) {
			String name = ChangeMD5(key);
			File cacheDir = null;
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				cacheDir = context.getExternalCacheDir();
			} else {
				cacheDir = context.getCacheDir();
			}

			File file = new File(cacheDir, name);
			FileInputStream fin = null;
			try {
				fin = new FileInputStream(file);
				fin.read(data);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return data;
	}

	private String ChangeMD5(String key) {
		String name = null;
		if (key != null) {
			try {
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				byte[] data = md5.digest(key.getBytes());
				StringBuilder sb = new StringBuilder();
				for (byte b : data) {
					int h = (b >> 4) & 0x0f;
					int l = b & 0x0f;
					sb.append(Integer.toHexString(h)).append(Integer.toHexString(l));
				}
				name = sb.toString();
				data = null;
				sb = null;
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}

		return name;
	}
}
