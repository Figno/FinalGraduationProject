/**
 * 
 */
package com.chxip.localmusic.util;

import android.content.Context;
import android.widget.Toast;

import com.chxip.localmusic.R;

/**
 * 对话框toast类
 */
public class ToastUtil {
	/**
	 * 弹出Toast
	 */
	public static void show(Context context, String info) {
		Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
	}

	public static void show(Context context, int info) {
		Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 网络访问失败 提示
	 */
	public static void showNetworkError(Context context) {
		Toast.makeText(context, context.getString(R.string.app_network_error), Toast.LENGTH_SHORT).show();
	}
}
