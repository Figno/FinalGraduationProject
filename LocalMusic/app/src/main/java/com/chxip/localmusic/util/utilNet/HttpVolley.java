package com.chxip.localmusic.util.utilNet;

import android.os.Handler;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.chxip.localmusic.application.MyApplication;
import com.chxip.localmusic.util.MLog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用volley框架进行http网络访问
 */
public class HttpVolley {

	// 获取请求队列RequestQueue对象
	public static RequestQueue requestQueue = MyApplication.getRequestQueue();

	/**
	 * volley 发送StringRequest登录
	 *
	 * @param url
	 *            访问路径
	 * @param map
	 *            参数
	 * @param ncbv
	 *            网络访问完成后 处理数据的接口
	 */
	public static void volleStringRequestPostLogin(String url, final HashMap<String, String> map, NetCallBackVolley ncbv) {

		StringRequest request = new StringRequest(Request.Method.POST, url, ncbv, ncbv) {
			/**
			 * 重写获取参数的函数  可以向服务器提交数据
			 * map里的键值对即为user里的数据
			 *
			 * @return
			 * @throws AuthFailureError
			 */
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				if (map == null) {
					return null;
				}
				return map;
			}
		};
//		MLog.i(url);
		//设置请求标签用于加入全局队列后，方便找到
		request.setTag(url);
		request.setRetryPolicy(new DefaultRetryPolicy(5000, // 默认超时时间
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // 默认最大尝试次数
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		requestQueue.add(request);

	}


	/**
	 * volley 发送StringRequest
	 *
	 * @param url
	 *            访问路径
	 * @param ncbv
	 *            网络访问完成后 处理数据的接口
	 */
	public static void volleStringRequestGetString(String url, NetCallBackVolley ncbv) {
		StringRequest request = new StringRequest(Method.GET, url, ncbv, ncbv) {
			@Override
			public String getBodyContentType() {
				// 请求参数x-www-form-urlencoded
				return String.format("application/x-www-form-urlencoded; charset=%s", "utf-8");
			}

		};

		request.setTag(url);
		request.setRetryPolicy(new DefaultRetryPolicy(5000, // 默认超时时间
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // 默认最大尝试次数
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		requestQueue.add(request);
	}



}
