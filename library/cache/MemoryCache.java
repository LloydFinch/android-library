package com.example.HttpUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by VennUser on 2015/8/24.
 */

//配合FileCache构成的图片三级缓存
public final class MemoryCache {

	private static MemoryCache memoryCache;

	public static MemoryCache getInstance() {
		if (memoryCache == null) {
			memoryCache = new MemoryCache();
		}
		return memoryCache;
	}

	//运行时最大内存
	private int maxSize;

	//第一级缓存
	private LruCache<String, Bitmap> lruCache;

	//第二级缓存
	private HashMap<String, SoftReference<Bitmap>> softCache;

	//第三级缓存
	private FileCache fileCache;

	//初始化三级缓存
	private MemoryCache() {
		maxSize = (int) Runtime.getRuntime().maxMemory() / 1024;

		lruCache = new LruCache<String, Bitmap>(maxSize / 8) {
			protected int sizeOf(String key, Bitmap value) {

				//API 12以上可用
				//return value.getByteCount();

				return value.getRowBytes() * value.getHeight();
			}
		};

		//采用链式表便于增删
		softCache = new LinkedHashMap<String, SoftReference<Bitmap>>();
		fileCache = FileCache.getInstance();
	}

	//将图片存放在缓存中
	public void putBitmap(String key, Bitmap bitmap) {
		if (key != null && bitmap != null) {
			lruCache.put(key, bitmap);
			softCache.put(key, new SoftReference<Bitmap>(bitmap));
			//TODO 文件缓存用于存放byte数据,在图片二次采样之前存放
		}
	}

	//从缓存中取图片
	public Bitmap getBitmap(String key) {
		Bitmap bitmap = lruCache.get(key);
		if (bitmap == null) {
			SoftReference<Bitmap> softReference = softCache.get(key);
			bitmap = softReference.get();
			if (bitmap == null) {
				byte[] data = fileCache.loadFile(key);
				if (data != null) {
					bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
					//TODO 可能需要二次采样
					if (bitmap != null) {
						lruCache.put(key, bitmap);
						softCache.put(key, new SoftReference<Bitmap>(bitmap));
					}
				}
			} else {
				lruCache.put(key, bitmap);
			}

		}

		return bitmap;
	}
}
